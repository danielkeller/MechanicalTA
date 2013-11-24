package mta.ui;

import java.io.IOException;
import java.nio.file.*;

import mta.test.TestRunner;

import com.trolltech.qt.gui.*;

public class MainWindow {
	QFrame window;
	QTextEdit testPreview;
	String testFile = null;
	
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(300, 400);
		window.resize(600, 800);
		//window.setSizePolicy(new QSizePolicy(QSizePolicy.Policy.Preferred, QSizePolicy.Policy.Preferred));
		
		QGridLayout grid = new QGridLayout();
		window.setLayout(grid);
		
		QPushButton testLoad = new QPushButton("Load the test!", window);
		testLoad.clicked.connect(this, "loadTest()");
		grid.addWidget(testLoad, 0, 0);
		
		testPreview = new QTextEdit();
		testPreview.setFontFamily("Courier");
		testPreview.setReadOnly(true);
		grid.addWidget(testPreview, 1, 0);
		
		window.show();
		
		//new LoginWindow(window);
		QApplication.exec();
	}
	
	@SuppressWarnings("unused")
	private void loadTest() {
		QFileDialog dlg = new QFileDialog(window, "Select a test file");
		dlg.setNameFilter("Java Source (*.java)");
		dlg.setFileMode(QFileDialog.FileMode.ExistingFile);
		dlg.fileSelected.connect(this, "loadTestFile(String)");
		dlg.setModal(true);
		dlg.show();
	}
	
	@SuppressWarnings("unused")
	private void loadTestFile(String file) {
		testFile = file;
		try {
			testPreview.setText(new String(Files.readAllBytes(Paths.get(file)), "ASCII"));
		} catch (IOException e) {
			QMessageBox.critical(window, "Could not open file",
					"Could not open file because " + e.getMessage(),
					new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok));
			testFile = null;
		}
		TestRunner.runTest(testFile);
	}
}
