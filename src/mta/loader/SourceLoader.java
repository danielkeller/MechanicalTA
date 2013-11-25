package mta.loader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import javax.tools.*;

import mta.util.Errors;

public class SourceLoader {
	List<String> javafiles = new ArrayList<String>();
	PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.java");
		
	public void loadFolder(String folder) {
		try {
			Files.walkFileTree(Paths.get(folder), new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (matcher.matches(file))
						javafiles.add(file.toString());
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			Errors.dieGracefully(e);
		}
		
		if (javafiles.size() == 0)
			return; //display an error?
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		InMemoryClassLoader loader = new InMemoryClassLoader(Thread.currentThread().getContextClassLoader());
		
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		InMemoryFileManager fileManager = new InMemoryFileManager(compiler.getStandardFileManager(
				diagnostics, null, null), loader);
		
		Iterable<? extends JavaFileObject> fobjects
			= fileManager.getJavaFileObjectsFromStrings(javafiles);
		
		compiler.getTask(
				null,
				fileManager,
				null, //diagnostics,
				null,
				null,
				fobjects).call();
		
		System.out.println("For " + folder);
		for (Class<?> clazz : loader.getClasses())
			System.out.println(clazz.getName());
	}

}