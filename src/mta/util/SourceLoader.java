package mta.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.tools.*;

public class SourceLoader {
	public static void loadFile(String file) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		TempClassFileManager fileManager = new TempClassFileManager(compiler.getStandardFileManager(
				diagnostics, null, null));
		
		Iterable<? extends JavaFileObject> fobjects
			= fileManager.getJavaFileObjectsFromStrings(Arrays.asList(file));
		
		compiler.getTask(
				null,
				fileManager,
				diagnostics,
				null,
				null,
				fobjects).call();
		for (JavaFileObject obj : fobjects)
			System.out.println(fileManager.inferBinaryName(StandardLocation.CLASS_PATH, obj));
	}
	

	static Path tempdir = null;
	private static Path getTempdir() {
		if (tempdir == null) {
			try {
				tempdir = Files.createTempDirectory("MTA_Classes");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return tempdir;
	}
	
	static ClassLoader loader = null;
	private static ClassLoader getLoader() {
		if (loader == null) {
			try {
				loader = new URLClassLoader(new URL[]{getTempdir().toUri().toURL()});
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		return loader;
	}

	private static class TempClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
		protected TempClassFileManager(StandardJavaFileManager fileManager) {
			super(fileManager);
		}
		
		Iterable<? extends JavaFileObject>
			getJavaFileObjectsFromStrings(Iterable<String> names) {
			return fileManager.getJavaFileObjectsFromStrings(names);
		}
		
		@Override
		public ClassLoader getClassLoader(Location location) {
			return getLoader();
		}
		
		@Override
		public String inferBinaryName(Location location, JavaFileObject file) {
			System.out.println("inferBinaryName(" + location.toString() + ", "
					+ file.toString() + ") = " + super.inferBinaryName(location, file));
			return super.inferBinaryName(location, file);
		}
		
		@Override
		public JavaFileObject getJavaFileForOutput(Location location,
				String className, JavaFileObject.Kind kind, FileObject sibling)
				throws IOException {
			String outdir = getTempdir().resolve(className.replace('.', '/')) + ".class";
			FileObject classHint = fileManager.getJavaFileObjects(outdir).iterator().next();
			if (location.equals(StandardLocation.CLASS_OUTPUT))
				return fileManager.getJavaFileForOutput(location, className, kind, classHint);
			else
				return fileManager.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

}