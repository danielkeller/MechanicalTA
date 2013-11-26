package mta.loader;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

import javax.tools.*;

import mta.util.ResourceExtractor;

class InMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	private InMemoryClassLoader loader;
	private Map<String, InMemoryFileObject> sources
		= new TreeMap<String, InMemoryFileObject>();
	
	protected InMemoryFileManager(StandardJavaFileManager fileManager,
			InMemoryClassLoader l) {
		super(fileManager);
		loader = l;
	}
	
	Iterable<? extends JavaFileObject>
		getJavaFileObjectsFromPaths(Iterable<String> names) {
		return fileManager.getJavaFileObjectsFromStrings(names);
	}
	
	public void newSourceFrom(String name, InputStream str) throws IOException {
		InMemoryFileObject obj = new InMemoryFileObject(name, JavaFileObject.Kind.SOURCE);
		
		try (OutputStream out = obj.openOutputStream();) {
			ResourceExtractor.dumpStream(str, out);
		}
		sources.put(name, obj);
	}
	
	public Iterable<InMemoryFileObject> getSources () {
		return sources.values();
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
	
	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		//they may be the same in reality, but if they were loaded from different FileManagers
		//we consider them different
		if (a instanceof InMemoryFileObject && b instanceof InMemoryFileObject)
			return a == b;
		if (!(a instanceof InMemoryFileObject) && !(b instanceof InMemoryFileObject))
			return super.isSameFile(a, b);
		else
			return false;
	}
}