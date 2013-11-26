package mta.ui;

import mta.loader.*;
import mta.qt.ClassListModel;
import mta.test.TestRunner;
import mta.util.Errors;
import mta.util.ResourceExtractor;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class TestView extends QObject {
	private QListView testClassesView;
	private InMemoryClassLoader classes = null;
	private QWidget window;

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
	
	public Signal0 readyStateChange = new Signal0();
	public boolean isReady() {return readyState;}

	public InMemoryClassLoader getClasses() {
		return classes;
	}

	@SuppressWarnings("unused")
	private void loadTest() {
		try {
			setReadyState(false);
			
			String testSrc = QFileDialog.getExistingDirectory(
				window, "Choose a test source directory", "");
			if (testSrc.equals(""))
				return;
			
			classes = new SourceLoader().load(testSrc);
			
			testClassesView.setModel(new ClassListModel(classes.getClasses()));
	
			for (Class<?> clazz : classes.getClasses())
				if (TestRunner.isTest(clazz)) {
					setReadyState(true);
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
	
	private boolean readyState = false;
	private void setReadyState(boolean state) {
		readyState = state;
		readyStateChange.emit();
	}
}