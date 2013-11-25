package mta.api;

import java.util.List;

import org.junit.rules.*;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class AssignmentRunner extends BlockJUnit4ClassRunner {

	int totalPoints = 0;
	int earnedPoints = 0;
	
	public AssignmentRunner(Class<?> klass) throws InitializationError {
		super(klass);
		
		List<FrameworkMethod> methods = computeTestMethods();
		for (FrameworkMethod method : methods) {
			PointValue points = method.getAnnotation(PointValue.class);
			if (!points.extraCredit())
				totalPoints += points.value();
		}
	}
	
	@Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);
        validateNoTestAnnotations(errors);
	}
	
	@Override
	protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
        //should have one constructor taking interface type
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
	
	@Override
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> ret = super.getTestRules(target);
		ret.add(pointWatcher);
		return ret;
	}

	TestWatcher pointWatcher = new TestWatcher() {
		@Override
		protected void succeeded(Description description) {
			//all of these should have this annotation
			PointValue points = description.getAnnotation(PointValue.class);
			earnedPoints += points.value();
		}

		@Override
		protected void failed(Throwable e, Description description) {
		}
	};
}
