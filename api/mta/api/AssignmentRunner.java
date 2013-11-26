package mta.api;

import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class AssignmentRunner extends BlockJUnit4ClassRunner {
	List<Class<?>> testClasses;
	
	public AssignmentRunner(Class<?> klass, List<Class<?>> testSet)
			throws InitializationError {
		super(klass);
		testClasses = testSet;
	}
	
	//called when we're runnning in normal junit mode
	public AssignmentRunner(Class<?> klass)
			throws InitializationError {
		super(klass);
		testClasses = null;
	}
	
	@Override
	protected Object createTest() throws Exception {
		Constructor<?> ifaceCtor = getTestClass().getOnlyConstructor();
		Class<?> iface = ifaceCtor.getParameterTypes()[0];
		Class<?> impl = null;
		
		//running in junit mode
		if (testClasses == null) {
			if (!ifaceCtor.isAnnotationPresent(ModelImpl.class))
				throw new Exception("Could not find @ModelImpl annotation on constructor");
			impl = ifaceCtor.getAnnotation(ModelImpl.class).value();
		}
		else {
			for (Class<?> cls : testClasses)
			{
				if (iface.isAssignableFrom(cls) && !cls.isInterface()) {
					impl = cls;
					break;
				}
			}
		}
		
		if (impl == null)
			throw new Exception("No class found implementing " + iface.getName());
		
		if (impl.getConstructors().length != 1)
			throw new Exception("Implementation class " + impl.getName()
					+ " does not have 1 constructor");
		
		if (impl.getConstructors()[0].getParameterTypes().length != 0)
			throw new Exception("Implementation class " + impl.getName()
					+ " does not have 0-argument constructor");
		
		return ifaceCtor.newInstance(impl.getConstructors()[0].newInstance());
	}
	
	@Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);
        validateNoTestAnnotations(errors);
	}
	
	//has to be in separate methods so we can initialize before calling it
	@Override
	protected void validateConstructor(List<Throwable> errors) {
		validateOnlyOneConstructor(errors);
        validateOneArgConstructor(errors);
	}
	protected void validateOneArgConstructor(List<Throwable> errors) {
        Constructor<?>[] ctors = getTestClass().getJavaClass().getConstructors();
        
        Constructor<?> onearg = null;
        for (Constructor<?> ctor : ctors) {
        	if (ctor.getParameterTypes().length == 1) {
        		if (onearg == null) {
        			onearg = ctor;
        		}
        		else {
        	        String gripe = "Test class should have exactly one " +
        	        		"public one-argument constructor";
        	        errors.add(new Exception(gripe));
        	        return;
                }
        	}
        }
        
        if (onearg == null) {
	        String gripe = "Test class should have exactly one " +
	        		"public one-argument constructor";
	        errors.add(new Exception(gripe));
	        return;
        }

        if (!onearg.getParameterTypes()[0].isInterface()) {
	        String gripe = "Test class should have exactly one public constructor" +
	        		" taking one interface type";
	        System.out.println(onearg.getParameterTypes()[0].getName());
	        errors.add(new Exception(gripe));
        }
        
        //check model impl if it's there
		if (onearg.isAnnotationPresent(ModelImpl.class)) {
			if (!onearg.getParameterTypes()[0]
					.isAssignableFrom(
							onearg.getAnnotation(ModelImpl.class).value())) {
		        String gripe = "If @ModelImpl is present, its value must be a class implementing" +
		        		" the interface that the constructor takes";
		        System.out.println(onearg.getParameterTypes()[0].getName());
		        errors.add(new Exception(gripe));
				
			}
		}
			
	}
	
	private void validateNoTestAnnotations(List<Throwable> errors) {
		if (super.computeTestMethods().size() != 0)
			errors.add(new Exception(
					"Do not use the @Test annotation with AssignmentRunner, use @PointValue"));
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(PointValue.class);
    }
}
