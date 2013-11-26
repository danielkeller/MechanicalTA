package mta.ui;

import com.trolltech.qt.gui.*;

public class MainWindow {
	QFrame window;
	QPushButton testRun;
	CourseView cview;
	
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(400, 400);
		window.resize(700, 500);
		
		QGridLayout winGrid = new QGridLayout();
		window.setLayout(winGrid);
		
		QGroupBox testGroup = new QGroupBox("Tests", window);
		winGrid.addWidget(testGroup, 0, 0);
		TestView testview = new TestView(window, testGroup);
		testview.testReady.connect(this, "testReady(boolean)");

		QGroupBox assignmentGroup = new QGroupBox("Assignment", window);
		winGrid.addWidget(assignmentGroup, 0, 1);
		cview = new CourseView(assignmentGroup);
		
		testRun = new QPushButton("Grade everything!", window);
		testRun.clicked.connect(testview, "runTest()");
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
	private void testReady(boolean ready) {
		testRun.setEnabled(ready);
	}
}
