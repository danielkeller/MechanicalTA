package mta.loader;

import java.util.*;

import javax.tools.JavaFileObject;

public class InMemoryClassLoader extends ClassLoader {
	private Map<String, InMemoryFileObject> objects
		= new TreeMap<String, InMemoryFileObject>();
	
	private List<Class<?>> classes = null;
	
	public InMemoryClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public JavaFileObject newFileObject(String className) {
		InMemoryFileObject obj = new InMemoryFileObject(className, JavaFileObject.Kind.CLASS);
		objects.put(className, obj);
		return obj;
	}
	
	public List<Class<?>> getClasses() {
		if (classes == null) {
			classes = new ArrayList<Class<?>>(objects.size());
			for (String clazz : objects.keySet())
				try {
					classes.add(loadClass(clazz));
				} catch (ClassNotFoundException e) {}
		}
		return classes;
	}
	
	public void loadAll() {
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (objects.containsKey(name))
		{
			byte[] bytes = objects.get(name).getBytes();
			return defineClass(name, bytes, 0, bytes.length);
		}
		throw new ClassNotFoundException();
	}
}
