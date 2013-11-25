package mta.test;

import java.util.List;

import mta.api.AssignmentRunner;
import mta.api.PointValue;
import mta.util.Errors;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.InitializationError;

public class TestRunner {
	public static void runTest(List<Class<?>> classes) {
		JUnitCore core = new JUnitCore();
		PointListener points = new PointListener();
		core.addListener(points);

		for (Class<?> clazz : classes)
		{
			if (isTest(clazz))
			{
				try {
					Runner r = new AssignmentRunner(clazz, classes);
					core.run(Request.runner(r));
				} catch (InitializationError e) {
					Errors.dieGracefully(new Exception(
							"For " + clazz.getName() + "\n"
							+ e.getCauses().get(0).getMessage()));
					return;
				}
			}
		}
		
		System.out.println("Earned " + points.earnedPoints + " of " + points.totalPoints);
	}
	
	public static boolean isTest(Class<?> clazz) {
		return clazz.isAnnotationPresent(RunWith.class)
				&& clazz.getAnnotation(RunWith.class)
					.value().equals(AssignmentRunner.class);
	}
	
	private static class PointListener extends RunListener {
		int totalPoints = 0;
		int earnedPoints = 0;
		
		//This looks silly, but it has to work this way because there is
		//no success callback
		
		@Override
		public void testFinished(Description description) throws Exception {
			//all of these should have this annotation
			PointValue points = description.getAnnotation(PointValue.class);
			earnedPoints += points.value();
			if (!points.extraCredit())
				totalPoints += points.value();
		}
		
		@Override
		public void testFailure(Failure failure) throws Exception {
			PointValue points = failure.getDescription().getAnnotation(PointValue.class);
			earnedPoints -= points.value();
			System.out.println(failure.toString());
		}
	}
}
