package mta.util;

import java.io.*;
import java.nio.file.*;

public class ResourceExtractor {
	public static void extractAPI(String path) throws IOException {
		extractResource(Paths.get(path), "mta_api.jar");
	}
	
	public static void extractResource(Path file, String resource) throws IOException {
		InputStream res = PlatformExtractor.class.getClassLoader().getResourceAsStream(resource);
		
		if (res == null)
			throw new IOException("Resource " + resource + " not found");

		
		try (OutputStream temp = new FileOutputStream(file.toString());) {
			dumpStream(res, temp);
		}
	}
	
	public static void dumpStream(InputStream from, OutputStream to) throws IOException {
		byte[] buffer = new byte[4096];
		int read;
		
		while ((read = from.read(buffer)) != -1)
	        to.write(buffer, 0, read);
	}
}
