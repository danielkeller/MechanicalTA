package mta.ui;

import mta.test.TestRunner;
import mta.util.Errors;

import com.trolltech.qt.gui.*;

public class MainWindow {
	QFrame window;
	QPushButton testRun;
	CourseView cview;
	TestView tview;
	ResultsView rview;
	
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(400, 400);
		window.resize(700, 500);
		
		QGridLayout winGrid = new QGridLayout();
		window.setLayout(winGrid);
		
		QGroupBox testGroup = new QGroupBox("Tests", window);
		winGrid.addWidget(testGroup, 0, 0);
		testGroup.setMaximumWidth(250);
		tview = new TestView(window, testGroup);
		tview.readyStateChange.connect(this, "testReady()");

		QGroupBox assignmentGroup = new QGroupBox("Assignment", window);
		winGrid.addWidget(assignmentGroup, 0, 1);
		cview = new CourseView(assignmentGroup);
		cview.assignmentSelected.connect(this, "setSubmissions()");

		QGroupBox resultGroup = new QGroupBox("Results", window);
		winGrid.addWidget(resultGroup, 0, 2);
		rview = new ResultsView(resultGroup);
		rview.readyStateChange.connect(this, "testReady()");
		
		testRun = new QPushButton("Grade everything!", window);
		testRun.clicked.connect(this, "runTest()");
		testRun.setMinimumHeight(testRun.height() * 2); //make it big
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
	private void setSubmissions() {
		rview.setAssignment(cview.getSelectedAssignment());
	}
	
	@SuppressWarnings("unused")
	private void testReady() {
		testRun.setEnabled(tview.isReady() && rview.isReady());
	}
	
	@SuppressWarnings("unused")
	private void runTest() {
		try {
			window.setEnabled(false);
			TestRunner.runTests(tview.getClasses(), rview.getSubmissions());
			window.setEnabled(true);
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
}
