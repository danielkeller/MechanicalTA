package mta.test;

import java.io.InputStream;
import java.util.*;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

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
	
	public static Map<Message, Score> runTests(InMemoryFileManager testSuiteMgr, Messages submissions) {
		Map<Message, Score> ret = new TreeMap<Message, Score>(); 
		for (Message subm : submissions.messages) {
			if (subm.attachments.length < 1)
				continue;
			
			Attachment att = subm.attachments[0];
			InputStream cont = API.getMessageContent(att.contentUrl);
			
			InMemoryFileManager classesMgr = new SourceLoader().load(cont);
			
			//create class loaders
			InMemoryClassLoader classes = classesMgr.getLoader();
			InMemoryClassLoader testSuite = testSuiteMgr.getLoader(classes);
			
			Score result = runTest(testSuite, classes);
			result.diagnostics = classesMgr.diagnostics;
			ret.put(subm, result);
		}
		return ret;
	}
	
	private static Score runTest(InMemoryClassLoader testSuite, InMemoryClassLoader DUT) {
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
		return points.getScore();
	}
	
	public static class Score {
		public int totalPoints = 0;
		public int earnedPoints = 0;
		public Result result;
		public DiagnosticCollector<JavaFileObject> diagnostics;
	};
	
	private static class PointListener extends RunListener {
		Score score = new Score();
		
		public Score getScore() {
			return score;
		}
		
		//This looks silly, but it has to work this way because there is
		//no success callback
		
		@Override
		public void testFinished(Description description) throws Exception {
			//all of these should have this annotation
			PointValue points = description.getAnnotation(PointValue.class);
			score.earnedPoints += points.value();
			if (!points.extraCredit())
				score.totalPoints += points.value();
		}
		
		@Override
		public void testFailure(Failure failure) throws Exception {
			PointValue points = failure.getDescription().getAnnotation(PointValue.class);
			score.earnedPoints -= points.value();
			System.out.println(failure.toString());
		}
		
		@Override
		public void testRunFinished(Result result) throws Exception {
			score.result = result;
		}
	}
}
