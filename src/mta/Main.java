package mta;

import javax.tools.ToolProvider;

import mta.ui.MainWindow;
import mta.util.Errors;
import mta.util.PlatformExtractor;

import com.trolltech.qt.gui.*;

public class Main {
	public static void main(String[] args) {
		try (PlatformExtractor pe = new PlatformExtractor();) {
			QApplication.initialize(args);
			QApplication.setStyle(QStyleFactory.create("Cleanlooks"));
			
			//test for jdk
			if (ToolProvider.getSystemJavaCompiler() == null)
				throw new Exception("This program must be run with JDK 7");
			
			new MainWindow();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}

}
