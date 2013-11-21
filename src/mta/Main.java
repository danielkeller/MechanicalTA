package mta;

import java.io.IOException;

import mta.ui.MainWindow;
import mta.util.PlatformExtractor;

import com.trolltech.qt.gui.QApplication;


public class Main {

	public static void main(String[] args) {
		try (PlatformExtractor pe = new PlatformExtractor();) {
			QApplication.initialize(args);
			new MainWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
