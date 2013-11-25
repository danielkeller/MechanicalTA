package mta.ui;

import java.util.List;

import mta.loader.SourceLoader;
import mta.qt.ClassListModel;
import mta.test.TestRunner;
import mta.util.ResourceExtractor;
import mta.util.Errors;

import com.trolltech.qt.core.QDir;
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
		
		QGridLayout winGrid = new QGridLayout();
		window.setLayout(winGrid);
		
		QGroupBox testGroup = new QGroupBox("Tests", window);
		winGrid.addWidget(testGroup, 0, 0);
		QGridLayout testGrid = new QGridLayout();
		testGroup.setLayout(testGrid);
		{
			QPushButton testLoad = new QPushButton("Load test folder", testGroup);
			testLoad.clicked.connect(this, "loadTest()");
			testGrid.addWidget(testLoad, 0, 0);
			
			QPushButton apiExtract = new QPushButton("Extract test API", testGroup);
			apiExtract.clicked.connect(this, "extractAPI()");
			testGrid.addWidget(apiExtract, 0, 1);
			
			testClassesView = new QListView(testGroup);
			testClassesView.setViewMode(ViewMode.ListMode);
			testClassesView.setFlow(Flow.TopToBottom);
			testClassesView.setSelectionMode(SelectionMode.NoSelection);
			testGrid.addWidget(testClassesView, 1, 0, 1, 2);
		}

		QGroupBox assignmentGroup = new QGroupBox("Assignment", window);
		winGrid.addWidget(assignmentGroup, 0, 1);
		QGridLayout assignmentGrid = new QGridLayout();
		assignmentGroup.setLayout(assignmentGrid);
		
		testRun = new QPushButton("Grade everything!", window);
		testRun.clicked.connect(this, "runTest()");
		testRun.setEnabled(false);
		winGrid.addWidget(testRun, 2, 0, 1, 2);
		
		window.show();
		
		new LoginWindow(window);
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
	private void extractAPI() {
		try {
			ResourceExtractor.extractAPI(QFileDialog.getSaveFileName(window, "Saving API files",
					QDir.homePath() + QDir.separator() + "mta_api.jar",
					new QFileDialog.Filter("API jar (mta_api.jar)")));
		} catch (Exception e) {
			Errors.dieGracefully(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void runTest() {
		TestRunner.runTest(classes);
	}
}
