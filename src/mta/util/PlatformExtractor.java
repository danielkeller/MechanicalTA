package mta.util;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;

public class PlatformExtractor implements AutoCloseable {
	private Path tempdir;
	
	public PlatformExtractor() throws IOException {
		tempdir = Files.createTempDirectory("MTA_Jars");

		ArrayList<URL> jarUrls = new ArrayList<URL>();
		for (String jar : pfJars) {
			ResourceExtractor.extractResource(tempdir.resolve(jar), jar);
			jarUrls.add(tempdir.resolve(jar).toUri().toURL());
		}
		//replace the current class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader urlClassLoader = new URLClassLoader(jarUrls.toArray(new URL[0]), currentThreadClassLoader);
		Thread.currentThread().setContextClassLoader(urlClassLoader);
	}
	
	private static String[] pfJars = {"qtjambi-linux64-gcc-4.6.3.jar", "qtjambi-linux32-gcc-4.6.3.jar", "qtjambi-win32-msvc2005-4.6.3.jar"};

	public void close() throws IOException {
		for (File file : tempdir.toFile().listFiles())
			file.delete();
		tempdir.toFile().delete();
	}
}
