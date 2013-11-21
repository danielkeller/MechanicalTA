package mta.util;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;

public class PlatformExtractor implements AutoCloseable {
	private Path tempdir;
	
	public PlatformExtractor() throws IOException {
		tempdir = Files.createTempDirectory("MTA_Jars");
		
		byte[] buffer = new byte[4096];
		int read;

		ArrayList<URL> jarUrls = new ArrayList<URL>();
		for (String jar : pfJars) {
			InputStream res = PlatformExtractor.class.getClassLoader().getResourceAsStream(jar);
			
			try (OutputStream temp = new FileOutputStream(tempdir.resolve(jar).toString());)
			{
				while ((read = res.read(buffer)) != -1)
			        temp.write(buffer, 0, read);
			}
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
