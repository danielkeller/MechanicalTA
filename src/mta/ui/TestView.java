package mta.ui;

import java.io.IOException;

import mta.loader.*;
import mta.qt.ClassListModel;
import mta.test.TestRunner;
import mta.util.Errors;
import mta.util.Future;
import mta.util.ResourceExtractor;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class TestView extends QObject {
	private QListView testClassesView;
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

	public InMemoryFileManager getClasses() {
		return classes.get();
	}
	
	private String testSrc;

	@SuppressWarnings("unused")
	private void loadTest() {
		try {
			setReadyState(false);
			
			testSrc = QFileDialog.getExistingDirectory(
				window, "Choose a test source directory", "");
			if (testSrc.equals(""))
				return;
			
			classes.start();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	private Future<InMemoryFileManager> classes
		= new Future<InMemoryFileManager> (this, "updateView()") {
			protected InMemoryFileManager evaluate() {
				try {
					return new SourceLoader().load(testSrc);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
	};

	@SuppressWarnings("unused")
	private void updateView() {
		//temporarily load the classes to check them
		InMemoryClassLoader testClasses = classes.get().getLoader();
		testClassesView.setModel(new ClassListModel(testClasses.getClasses()));
		for (Class<?> clazz : testClasses.getClasses())
			if (TestRunner.isTest(clazz)) {
				setReadyState(true);
				break;
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