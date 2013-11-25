package mta.loader;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

public class InMemoryFileObject implements JavaFileObject {
	
	private String binaryName;
	private long lastMod = new Date().getTime();
	private ByteArrayOutputStream contents = new ByteArrayOutputStream();

	public InMemoryFileObject(String className) {
		binaryName = className;
	}
	
	public byte[] getBytes() {
		return contents.toByteArray();
	}
	
	@Override
	public String toString() {
		return "InMemoryFileObject[" + binaryName +"]";
	}
	
	@Override
	public boolean delete() {
		return true;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		throw new UnsupportedOperationException("This is a binary file");
	}

	@Override
	public long getLastModified() {
		return lastMod;
	}

	@Override
	public String getName() {
		return binaryName;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(contents.toByteArray());
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return contents;
	}

	@Override
	public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
		throw new UnsupportedOperationException("This is a binary file");
	}

	@Override
	public Writer openWriter() throws IOException {
		throw new UnsupportedOperationException("This is a binary file");
	}

	@Override
	public URI toUri() {
		try {
			return new URI("inmem", binaryName, null);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	@Override
	public Modifier getAccessLevel() {
		return Modifier.PUBLIC;
	}

	@Override
	public Kind getKind() {
		return Kind.CLASS;
	}

	@Override
	public NestingKind getNestingKind() {
		return null; //maybe... NestingKind.TOP_LEVEL; //no
	}

	@Override
	public boolean isNameCompatible(String arg0, Kind arg1) {
		return true;
	}

}
