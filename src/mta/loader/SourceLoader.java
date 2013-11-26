package mta.loader;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.*;

import javax.tools.*;

import mta.util.Errors;

public class SourceLoader {
	List<String> javafiles = new ArrayList<String>();
	PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.java");
		
	public List<Class<?>> loadFolder(String folder) throws IOException {
		Files.walkFileTree(Paths.get(folder), new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (matcher.matches(file))
					javafiles.add(file.toString());
				return FileVisitResult.CONTINUE;
			}
		});
		
		if (javafiles.size() == 0)
			return new ArrayList<Class<?>>();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		InMemoryClassLoader loader = new InMemoryClassLoader(Thread.currentThread().getContextClassLoader());
		
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		InMemoryFileManager fileManager = new InMemoryFileManager(compiler.getStandardFileManager(
				diagnostics, null, null), loader);
		
		Iterable<? extends JavaFileObject> fobjects
			= fileManager.getJavaFileObjectsFromPaths(javafiles);
		
		compiler.getTask(
				null,
				fileManager,
				diagnostics,
				null,
				null,
				fobjects).call();
		
		if (diagnostics.getDiagnostics().size() != 0)
			Errors.DisplayErrorBox(diagnostics.getDiagnostics());
		
		return loader.getClasses();
	}

	public List<Class<?>> loadZipStream(InputStream in) {
		try (ZipInputStream zip = new ZipInputStream(in);) {
			
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			
			InMemoryClassLoader loader = new InMemoryClassLoader(Thread.currentThread().getContextClassLoader());
			
			InMemoryFileManager fileManager = new InMemoryFileManager(compiler.getStandardFileManager(
					null, null, null), loader);

			ZipEntry entry;
			
			while((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".java") && !entry.getName().endsWith("package-info.java") )
					fileManager.newSourceFrom(entry.getName(), zip);
				zip.closeEntry();
			}
			
			compiler.getTask(
					null,
					fileManager,
					null,
					null,
					null,
					fileManager.getSources())
					.call();
			
			//if (diagnostics.getDiagnostics().size() != 0)
			//	Errors.DisplayErrorBox(diagnostics.getDiagnostics());
			
			return loader.getClasses();
			
		} catch (ZipException e) {
			return new ArrayList<Class<?>>();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}