package mta;

import mta.ui.MainWindow;
import mta.util.Errors;
import mta.util.PlatformExtractor;

import com.trolltech.qt.gui.*;

public class Main {
	public static void main(String[] args) {
		try (PlatformExtractor pe = new PlatformExtractor();) {
			QApplication.initialize(args);
			QApplication.setStyle(QStyleFactory.create("Cleanlooks"));
			new MainWindow();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}

}
