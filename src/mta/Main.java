package mta;

import mta.ui.MainWindow;
import mta.util.PlatformExtractor;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QMessageBox;


public class Main {

	private static Throwable getBaseException(Throwable e) {
		while (e.getCause() != null)
			e = e.getCause();
		return e;
	}
	
	public static void main(String[] args) {
		try (PlatformExtractor pe = new PlatformExtractor();) {
			QApplication.initialize(args);
			new MainWindow();
		} catch (Exception e) {
			QMessageBox.critical(null, "A critical error occured",
					getBaseException(e).getMessage(),
					new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok));
			e.printStackTrace();
		}
	}

}
