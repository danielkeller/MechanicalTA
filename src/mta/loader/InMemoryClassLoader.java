package mta.loader;

import java.util.*;

public class InMemoryClassLoader extends ClassLoader {
	private List<Class<?>> classes = null;
	InMemoryFileManager manager;
	
	public InMemoryClassLoader(InMemoryFileManager manager, ClassLoader parent) {
		super(parent);
		this.manager = manager;
	}
	
	public List<Class<?>> getClasses() {
		if (classes == null) {
			classes = new ArrayList<Class<?>>(manager.getClassMap().size());
			for (String clazz : manager.getClassMap().keySet())
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
		if (manager.getClassMap().containsKey(name))
		{
			byte[] bytes = manager.getClassMap().get(name).getBytes();
			return defineClass(name, bytes, 0, bytes.length);
		}
		throw new ClassNotFoundException();
	}
}
