package mta.ui;

import java.util.prefs.Preferences;

import mta.pearson.API;
import mta.qt.QFocusLineEdit;
import mta.util.Errors;

import com.trolltech.qt.gui.*;

public class LoginWindow {
	QLineEdit client, domain;
	QFocusLineEdit username, password;
	QDialog window;
	QWidget mainWnd;
	boolean userCleared = false, pwCleared = false;
	
	Preferences prefs = Preferences.userNodeForPackage(getClass());
	
	public LoginWindow(QWidget mainWnd) {
		this.mainWnd = mainWnd;
		mainWnd.setEnabled(false);
		//window stuff
		window = new QDialog(mainWnd);
		//window.setFixedSize(300, 200);
		window.setModal(true); //	does weird things to xmonad
		window.setWindowTitle("Log In");
		window.rejected.connect(mainWnd, "close()");

		//fields
		domain = new QFocusLineEdit("Enter domain..", "domain", prefs, window);
		domain.setMinimumWidth(200);
		client = new QFocusLineEdit("Enter client string...", "client", prefs, window);
		username = new QFocusLineEdit("Enter Username...", "user", prefs, window);
		password = new QFocusLineEdit("Enter Password...", null, null, window);
		password.set.connect(this, "clearPwd(boolean)");
		
		//buttons
		QPushButton login = new QPushButton("Login", window);
		//login.clicked.connect(window, "setEnabled(boolean)");
		login.clicked.connect(this, "login()");
		QPushButton quit = new QPushButton("Quit", window);
		quit.clicked.connect(this, "quit()");
		
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
		try {
			window.setEnabled(false);
			window.repaint();
			QApplication.processEvents();
			if (API.Authenticate(domain.text(), client.text(),
					username.text(), password.text())) {
				//QMessageBox.information(window, "Login successful",
				//		"You were logged in");
				mainWnd.setEnabled(true);
				window.accept();
				return;
			}
			QMessageBox.critical(window, "Login failed",
					"The credentials you provided could not be used",
					new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok));

			window.setEnabled(true);
			password.setFocus();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}

	@SuppressWarnings("unused")
	private void clearPwd(boolean f) {
		if (f) {
			password.setEchoMode(QLineEdit.EchoMode.Password);
		}
	}
	
	@SuppressWarnings("unused")
	private void quit() {
		QApplication.quit();
	}
}
