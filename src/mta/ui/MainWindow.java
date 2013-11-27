package mta.ui;

import java.awt.SplashScreen;
import java.util.Map;

import mta.pearson.API;
import mta.pearson.Grade;
import mta.pearson.Messages.Message;
import mta.test.TestRunner;
import mta.test.TestRunner.Score;
import mta.util.Errors;

import com.trolltech.qt.gui.*;

public class MainWindow {
	QFrame window;
	QPushButton testRun, sendGrades;
	QGroupBox testGroup, assignmentGroup, resultGroup;
	CourseView cview;
	TestView tview;
	ResultsView rview;
	
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(400, 400);
		window.resize(1024, 500);
		
		QGridLayout winGrid = new QGridLayout();
		window.setLayout(winGrid);
		
		testGroup = new QGroupBox("Tests", window);
		winGrid.addWidget(testGroup, 0, 0);
		testGroup.setMaximumWidth(250);
		tview = new TestView(window, testGroup);
		tview.readyStateChange.connect(this, "testReady()");

		assignmentGroup = new QGroupBox("Assignment", window);
		winGrid.addWidget(assignmentGroup, 0, 1);
		assignmentGroup.setMaximumWidth(300);
		cview = new CourseView(assignmentGroup);
		cview.assignmentSelected.connect(this, "setSubmissions()");

		resultGroup = new QGroupBox("Results", window);
		winGrid.addWidget(resultGroup, 0, 2);
		rview = new ResultsView(resultGroup);
		rview.readyStateChange.connect(this, "testReady()");
		resultGroup.setEnabled(false);
		
		testRun = new QPushButton("Grade everything!", window);
		testRun.clicked.connect(this, "runTest()");
		testRun.setMinimumHeight(testRun.height() * 2); //make it big
		testRun.setFont(new QFont(null, 14));
		testRun.setEnabled(false);
		winGrid.addWidget(testRun, 1, 0, 1, 2);

		sendGrades = new QPushButton("Looks good!", window);
		sendGrades.clicked.connect(this, "uploadGrades()");
		sendGrades.setMinimumHeight(testRun.height()); //make it big
		sendGrades.setFont(new QFont(null, 14));
		sendGrades.setEnabled(false);
		winGrid.addWidget(sendGrades, 1, 2);
		
		window.show();
		window.move(QApplication.desktop().screen().rect().center().subtract(window.rect().bottomRight().divide(2)));
		
		LoginWindow login = new LoginWindow(window);
		login.window.accepted.connect(this, "loggedin()");
		
		if (SplashScreen.getSplashScreen() != null)
			SplashScreen.getSplashScreen().close();
		
		QApplication.exec();
	}

	@SuppressWarnings("unused")
	private void loggedin() {
		try {
			cview.update();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setSubmissions() {
		try {
			rview.setAssignment(cview.getSelectedAssignment());
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void testReady() {
		testRun.setEnabled(tview.isReady() && rview.isReady());
	}
	
	Map<Message, Score> result;
	
	@SuppressWarnings("unused")
	private void runTest() {
		try {
			testRun.setEnabled(false);
			testGroup.setEnabled(false);
			assignmentGroup.setEnabled(false);
			resultGroup.setEnabled(false);
			window.repaint();
			QApplication.processEvents();
			
			result = TestRunner.runTests(tview.getClasses(), rview.getSubmissions(),
					new QProgressDialog("Running tests", "Cancel", 0, 0, window));
			rview.setResult(result);
			
			resultGroup.setEnabled(true);
			sendGrades.setEnabled(true);
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void uploadGrades() {
		try {
			int answer = QMessageBox.question(window, "Confirm", "Are you sure you want to upload grades?",
					QMessageBox.StandardButton.Yes, QMessageBox.StandardButton.No);
			if (answer == QMessageBox.StandardButton.No.value())
				return;
			
			for (Message res : result.keySet()) {
				Grade.GradeWr grade = new Grade.GradeWr();
				Score score = result.get(res);
				grade.grade.points = "" + score.earnedPoints;
				grade.grade.comments = score.toString().replace("\n", "<br/>");
				
				String gradeLoc = "users/" + res.submissionStudent.id +
						"/courses/" + cview.getSelectedCourse() +
						"/gradebookItems/" + cview.getSelectedAssignment().gradebookID +
						"/grade";
				API.deleteRequest(gradeLoc); //delete grade if it's there
				String gradeURL = API.postRequest(gradeLoc, grade); //post the new grade
			}
			
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
}
