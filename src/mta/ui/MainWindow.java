package mta.ui;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QSizePolicy.Policy;

public class MainWindow {
	public MainWindow() {
		QFrame window = new QFrame();
		window.setMinimumSize(400, 300);
		window.setSizePolicy(new QSizePolicy(Policy.Minimum, Policy.Minimum));
		
		QLabel label = new QLabel(window);
		label.setText("Hello world!");
		label.setToolTip("Motherf*ckaaaaaaasss");
		window.show();
		QApplication.exec();
	}
}
