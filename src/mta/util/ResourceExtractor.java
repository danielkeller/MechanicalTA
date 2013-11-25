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

		byte[] buffer = new byte[4096];
		int read;
		
		try (OutputStream temp = new FileOutputStream(file.toString());)
		{
			while ((read = res.read(buffer)) != -1)
		        temp.write(buffer, 0, read);
		}
	}
}
