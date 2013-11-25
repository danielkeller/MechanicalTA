package mta.test;

import mta.loader.SourceLoader;


public class TestRunner {
	public static void runTest(String testfolder) {
		new SourceLoader().loadFolder(testfolder);
	}
		
}
