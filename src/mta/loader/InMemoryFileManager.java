package mta.loader;

import java.io.*;
import java.util.*;

import javax.tools.*;

import mta.util.ResourceExtractor;

public class InMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	private Map<String, InMemoryFileObject> sources
		= new TreeMap<String, InMemoryFileObject>();
	private Map<String, InMemoryFileObject> classes
		= new TreeMap<String, InMemoryFileObject>();
	
	public DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

	protected InMemoryFileManager(JavaCompiler compiler) {
		super(compiler.getStandardFileManager(null, null, null));
	}

	protected InMemoryFileManager(JavaCompiler compiler, DiagnosticCollector<JavaFileObject> diagnostics) {
		super(compiler.getStandardFileManager(null, null, null));
		this.diagnostics = diagnostics;
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
	
	public Collection<InMemoryFileObject> getSources() {
		return sources.values();
	}
	
	public Map<String, InMemoryFileObject> getClassMap() {
		return classes;
	}
	
	public InMemoryClassLoader getLoader(ClassLoader parent) {
		return new InMemoryClassLoader(this, parent);
	}
	public InMemoryClassLoader getLoader() {
		return getLoader(Thread.currentThread().getContextClassLoader());
	}
	
	public JavaFileObject newFileObject(String name, JavaFileObject.Kind kind) {
		InMemoryFileObject obj = new InMemoryFileObject(name, kind);
		if (kind == JavaFileObject.Kind.CLASS)
			classes.put(name, obj);
		else
			sources.put(name, obj);
		return obj;
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, JavaFileObject.Kind kind, FileObject sibling)
			throws IOException {
		if (location == StandardLocation.CLASS_OUTPUT
				&& kind == JavaFileObject.Kind.CLASS)
			return newFileObject(className, kind);
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