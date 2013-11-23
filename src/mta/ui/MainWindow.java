package mta.ui;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QSizePolicy.Policy;

public class MainWindow {
	QFrame window;
	public MainWindow() {
		window = new QFrame();
		window.setWindowTitle("Mechanical TA");
		window.setMinimumSize(400, 300);
		window.resize(400, 300);
		window.setSizePolicy(new QSizePolicy(Policy.Preferred, Policy.Preferred));
		
		window.show();
		
		new LoginWindow(window);
		QApplication.exec();
	}
}
