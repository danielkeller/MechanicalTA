package mta.loader;

import java.io.IOException;

import javax.tools.*;

class InMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
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