package mta.ui;

import mta.pearson.Auth;
import mta.qt.QFocusLineEdit;

import com.trolltech.qt.gui.*;

public class LoginWindow {
	QLineEdit client, domain;
	QFocusLineEdit username, password;
	QDialog window;
	boolean userCleared = false, pwCleared = false;
	
	public LoginWindow(QWidget mainWnd) {
		//window stuff
		window = new QDialog(mainWnd);
		//window.setFixedSize(300, 200);
		window.setModal(true); //	does weird things to xmonad
		window.setWindowTitle("Log In");
		window.rejected.connect(mainWnd, "close()");

		//fields
		domain = new QLineEdit("api.learningstudio.com", window);
		domain.setMinimumWidth(200);
		client = new QLineEdit("gbtestc", window);
		username = new QFocusLineEdit("Enter Username...", window);
		password = new QFocusLineEdit("Enter Password...", window);
		password.cleared.connect(this, "clearPwd(boolean)");
		
		//buttons
		QPushButton login = new QPushButton("Login", window);
		//login.clicked.connect(window, "setEnabled(boolean)");
		login.clicked.connect(this, "login()");
		QPushButton quit = new QPushButton("Quit", window);
		quit.clicked.connect(window.rejected);
		
		//layout
		QGridLayout grid = new QGridLayout();
		QLabel icon = new QLabel(window);
		icon.setPixmap(QIcon.fromTheme("dialog-password")
				.pixmap(40, 40));
		window.setLayout(grid);
		grid.addWidget(icon, 0, 0, 4, 1);
		grid.addWidget(domain, 0, 1);
		grid.addWidget(client, 1, 1);
		grid.addWidget(username, 2, 1);
		grid.addWidget(password, 3, 1);
		grid.addWidget(login, 4, 0, 1, 2);
		grid.addWidget(quit, 5, 0, 1, 2);
		
		window.setFocus(); //so the text stays
		window.show();
	}
	
	@SuppressWarnings("unused")
	private void login() {
		window.setEnabled(false);
		window.repaint();
		QApplication.processEvents();
		if (!Auth.Authenticate(domain.text(), client.text(),
				username.text(), password.text()))
			QMessageBox.critical(window, "Login failed",
					"The credentials you provided could not be used",
					new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok));
		else {
			window.accept();
			return;
		}
		window.setEnabled(true);
	}

	@SuppressWarnings("unused")
	private void clearPwd(boolean f) {
		if (f) {
			password.setEchoMode(QLineEdit.EchoMode.Password);
		}
	}
}
