package mta.util;

import com.trolltech.qt.gui.QMessageBox;

public class Errors {
	private static Throwable getBaseException(Throwable e) {
		while (e.getCause() != null)
			e = e.getCause();
		return e;
	}
	
	public static void dieGracefully(Throwable e) {
		QMessageBox.critical(null, "A critical error occured",
				getBaseException(e).toString(),
				new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok));
		e.printStackTrace();
		//can't exit here, finally blocks are not executed
	}
}
