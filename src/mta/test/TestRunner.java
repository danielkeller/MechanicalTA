package mta.test;

import java.io.InputStream;

import mta.api.*;
import mta.loader.*;
import mta.pearson.*;
import mta.pearson.Messages.*;
import mta.util.Errors;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.InitializationError;

public class TestRunner {
	public static boolean isTest(Class<?> clazz) {
		return clazz.isAnnotationPresent(RunWith.class)
				&& clazz.getAnnotation(RunWith.class)
					.value().equals(AssignmentRunner.class);
	}
	
	public static void runTests(InMemoryClassLoader testSuite, Messages submissions) {
		for (Message subm : submissions.messages) {
			for (Attachment att : subm.attachments) {
				InputStream cont = API.getMessageContent(att.contentUrl);
				
				InMemoryClassLoader classes = new SourceLoader().load(testSuite, cont);
				
				PointListener points = runTest(testSuite, classes);
				System.out.println(att.name + " earned " + points.earnedPoints + " of " + points.totalPoints);
			}
		}
	}
	
	private static PointListener runTest(InMemoryClassLoader testSuite, InMemoryClassLoader DUT) {
		JUnitCore core = new JUnitCore();
		PointListener points = new PointListener();
		core.addListener(points);

		for (Class<?> clazz : testSuite.getClasses())
		{
			if (isTest(clazz))
			{
				try {
					Runner r = new AssignmentRunner(clazz, DUT.getClasses());
					core.run(Request.runner(r));
				} catch (InitializationError e) {
					Errors.dieGracefully(new Exception(
							"For " + clazz.getName() + "\n"
							+ e.getCauses().get(0).getMessage()));
				}
			}
		}
		return points;
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
