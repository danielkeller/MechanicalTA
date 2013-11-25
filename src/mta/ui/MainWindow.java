package mta.ui;

import java.util.List;

import mta.loader.SourceLoader;
import mta.qt.ClassListModel;
import mta.test.TestRunner;
import mta.util.Errors;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QAbstractItemView.SelectionMode;
import com.trolltech.qt.gui.QListView.Flow;
import com.trolltech.qt.gui.QListView.ViewMode;

public class MainWindow {
	QFrame window;
	QListView testClassesView;
	QPushButton testRun;
	List<Class<?>> classes = null;
	String testSrc;
	
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(300, 400);
		window.resize(300, 400);
		
		QGridLayout grid = new QGridLayout();
		window.setLayout(grid);
		
		QPushButton testLoad2 = new QPushButton("Load test folder", window);
		testLoad2.clicked.connect(this, "loadTest()");
		grid.addWidget(testLoad2, 0, 0);
		
		testClassesView = new QListView(window);
		testClassesView.setViewMode(ViewMode.ListMode);
		testClassesView.setFlow(Flow.TopToBottom);
		testClassesView.setSelectionMode(SelectionMode.NoSelection);
		grid.addWidget(testClassesView, 1, 0);
		
		testRun = new QPushButton("Run the test!", window);
		testRun.clicked.connect(this, "runTest()");
		testRun.setEnabled(false);
		grid.addWidget(testRun, 2, 0);
		
		window.show();
		
		//new LoginWindow(window);
		QApplication.exec();
	}

	@SuppressWarnings("unused")
	private void loadTest() {
		try {
			String testSrc = QFileDialog.getExistingDirectory(
				window, "Choose a test source directory", "");
			if (testSrc.equals(""))
				return;
			
			classes = new SourceLoader().loadFolder(testSrc);
			
			testClassesView.setModel(new ClassListModel(classes));
	
			testRun.setEnabled(false);
			for (Class<?> clazz : classes)
				if (TestRunner.isTest(clazz))
						testRun.setEnabled(true);
		} catch (Exception e) {
			Errors.dieGracefully(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void runTest() {
		TestRunner.runTest(classes);
	}
}
