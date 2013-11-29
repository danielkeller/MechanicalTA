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
		
	public InMemoryFileManager load(String folder) throws IOException {
		Files.walkFileTree(Paths.get(folder), new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (matcher.matches(file))
					javafiles.add(file.toString());
				return FileVisitResult.CONTINUE;
			}
		});
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		InMemoryFileManager fileManager = new InMemoryFileManager(compiler);
		
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
		
		return fileManager;
	}

	public InMemoryFileManager load(InputStream in) {
		try (ZipInputStream zip = new ZipInputStream(in);) {
			
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			InMemoryFileManager fileManager = new InMemoryFileManager(compiler, diagnostics);

			ZipEntry entry;
			
			while((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".java"))
					fileManager.newSourceFrom(entry.getName(), zip);
				zip.closeEntry();
			}
			
			compiler.getTask(
					null,
					fileManager,
					diagnostics,
					null,
					null,
					fileManager.getSources())
					.call();
			
			return fileManager;
			
		} catch (ZipException e) {
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}