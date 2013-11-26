package mta.util;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.trolltech.qt.gui.*;

public class Errors {
	private static Throwable getBaseException(Throwable e) {
		while (e.getCause() != null)
			e = e.getCause();
		return e;
	}
	
	public static void dieGracefully(Throwable e) {
		QMessageBox.critical(null, "A critical error occured",
				getBaseException(e).getClass().getName() + "\n"
				+ getBaseException(e).getMessage(),
				new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok));
		e.printStackTrace();
		QApplication.exit(-1);
	}
	
	public static void DisplayErrorBox(
			List<Diagnostic<? extends JavaFileObject>> diags) {
		QDialog dlg = new QDialog();
		dlg.setWindowTitle("There are errors in your test");
		dlg.setMinimumSize(300, 200);
		dlg.resize(500, 600);
		
		QGridLayout layout = new QGridLayout(dlg);
		
		QLabel messages = new QLabel(dlg);
		messages.setFrameShadow(QFrame.Shadow.Sunken);
		messages.setFrameShape(QFrame.Shape.Panel);
		//messages.setFontFamily("Courier");
		layout.addWidget(messages, 0, 0, 1, 3);
		
		QPushButton ok = new QPushButton("Ok", dlg);
		ok.setMaximumWidth(80);
		ok.clicked.connect(dlg, "reject()");
		layout.addWidget(ok, 1, 1);
		
		dlg.setLayout(layout);
		
		StringBuilder message = new StringBuilder();
		for (Diagnostic<? extends JavaFileObject> diag : diags) {
			
			message.append("Line " + diag.getLineNumber() + "\n");
			message.append(diag.getMessage(null));
			message.append("\n");
		}
		
		messages.setText(message.toString());
		
		dlg.exec();
	}
}
