package mta.ui;

import java.util.List;

import mta.loader.SourceLoader;
import mta.qt.ClassListModel;
import mta.test.TestRunner;
import mta.util.Errors;
import mta.util.ResourceExtractor;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class TestView extends QObject {
	QListView testClassesView;
	List<Class<?>> classes = null;
	String testSrc;
	QWidget window;

	public TestView(QWidget window, QWidget container) {
		this.window = window;
		
		QGridLayout testGrid = new QGridLayout();
		container.setLayout(testGrid);
		
		QPushButton apiExtract = new QPushButton("Extract test API", container);
		apiExtract.clicked.connect(this, "extractAPI()");
		testGrid.addWidget(apiExtract, 0, 0);
		
		QPushButton testLoad = new QPushButton("Load test folder", container);
		testLoad.clicked.connect(this, "loadTest()");
		testGrid.addWidget(testLoad, 1, 0);
		
		testClassesView = new QListView(container);
		testClassesView.setSelectionMode(QListView.SelectionMode.NoSelection);
		testGrid.addWidget(testClassesView, 2, 0);	
	}
	
	public Signal1<Boolean> testReady = new Signal1<Boolean>();

	@SuppressWarnings("unused")
	private void loadTest() {
		try {
			testReady.emit(false);
			
			String testSrc = QFileDialog.getExistingDirectory(
				window, "Choose a test source directory", "");
			if (testSrc.equals(""))
				return;
			
			classes = new SourceLoader().loadFolder(testSrc);
			
			testClassesView.setModel(new ClassListModel(classes));
	
			for (Class<?> clazz : classes)
				if (TestRunner.isTest(clazz)) {
					testReady.emit(true);
					break;
				}
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}

	@SuppressWarnings("unused")
	private void extractAPI() {
		try {
			ResourceExtractor.extractAPI(QFileDialog.getSaveFileName(window, "Saving API files",
					QDir.homePath() + QDir.separator() + "mta_api.jar",
					new QFileDialog.Filter("API jar (mta_api.jar)")));
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	public void runTest() {
		TestRunner.runTest(classes);
	}
}