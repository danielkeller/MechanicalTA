package mta.util;

import java.io.IOException;
import java.util.*;
import javax.tools.*;

public class SourceLoader {
	public static void loadFile(String file) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		InMemoryClassLoader loader = new InMemoryClassLoader(Thread.currentThread().getContextClassLoader());
		
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		InMemoryFileManager fileManager = new InMemoryFileManager(compiler.getStandardFileManager(
				diagnostics, null, null), loader);
		
		Iterable<? extends JavaFileObject> fobjects
			= fileManager.getJavaFileObjectsFromStrings(Arrays.asList(file));
		
		compiler.getTask(
				null,
				fileManager,
				diagnostics,
				null,
				null,
				fobjects).call();
		
		System.out.println("For " + file);
		for (Class<?> clazz : loader.getClasses())
			System.out.println(clazz.isAnonymousClass());
	}

	private static class InMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
		private InMemoryClassLoader loader;
		
		protected InMemoryFileManager(StandardJavaFileManager fileManager,
				InMemoryClassLoader l) {
			super(fileManager);
			loader = l;
		}
		
		Iterable<? extends JavaFileObject>
			getJavaFileObjectsFromStrings(Iterable<String> names) {
			return fileManager.getJavaFileObjectsFromStrings(names);
		}
		
		@Override
		public ClassLoader getClassLoader(Location location) {
			return loader;
		}
		
		@Override
		public JavaFileObject getJavaFileForOutput(Location location,
				String className, JavaFileObject.Kind kind, FileObject sibling)
				throws IOException {
			if (location == StandardLocation.CLASS_OUTPUT
					&& kind == JavaFileObject.Kind.CLASS)
				return loader.newFileObject(className);
			else
				return fileManager.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

}