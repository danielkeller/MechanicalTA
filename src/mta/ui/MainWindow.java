package mta.ui;

import mta.test.TestRunner;
import mta.util.Errors;

import com.trolltech.qt.gui.*;

public class MainWindow {
	QFrame window;
	QPushButton testRun;
	CourseView cview;
	TestView tview;
	
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(400, 400);
		window.resize(700, 500);
		
		QGridLayout winGrid = new QGridLayout();
		window.setLayout(winGrid);
		
		QGroupBox testGroup = new QGroupBox("Tests", window);
		winGrid.addWidget(testGroup, 0, 0);
		tview = new TestView(window, testGroup);
		tview.readyStateChange.connect(this, "testReady()");

		QGroupBox assignmentGroup = new QGroupBox("Assignment", window);
		winGrid.addWidget(assignmentGroup, 0, 1);
		cview = new CourseView(assignmentGroup);
		cview.readyStateChange.connect(this, "testReady()");
		
		testRun = new QPushButton("Grade everything!", window);
		testRun.clicked.connect(this, "runTest()");
		testRun.setEnabled(false);
		winGrid.addWidget(testRun, 2, 0, 1, 2);
		
		window.show();
		LoginWindow login = new LoginWindow(window);
		login.window.accepted.connect(this, "loggedin()");
		QApplication.exec();
	}

	@SuppressWarnings("unused")
	private void loggedin() {
		cview.update();
	}
	
	@SuppressWarnings("unused")
	private void testReady() {
		testRun.setEnabled(tview.isReady() && cview.isReady());
	}
	
	@SuppressWarnings("unused")
	private void runTest() {
		try {
			TestRunner.runTests(tview.getClasses(), cview.getSubmissions());
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
}
