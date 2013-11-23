package mta.qt;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.gui.QFocusEvent;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QWidget;

public class QFocusLineEdit extends QLineEdit {
	boolean isCleared = false;
	
	public QFocusLineEdit(String labelText, QWidget parent) {
		super(labelText, parent);
		setStyleSheet("color:grey;");
	}
	
	public QSignalEmitter.Signal1<Boolean> cleared = new Signal1<Boolean>();
	
	//TODO: reset help text when box is cleared by user
	
	@Override
	@QtBlockedSlot
	protected void focusInEvent(QFocusEvent evt) {
		super.focusInEvent(evt);
		if (!isCleared) {
			setText("");
			setStyleSheet("");
			isCleared = true;
			cleared.emit(true);
		}
	}
}
