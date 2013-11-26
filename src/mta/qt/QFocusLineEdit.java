package mta.qt;

import java.util.prefs.Preferences;

import com.trolltech.qt.*;
import com.trolltech.qt.gui.*;

public class QFocusLineEdit extends QLineEdit {
	boolean isCleared = false;
	String name;
	Preferences prefs;
	
	public QFocusLineEdit(String labelText, String name, Preferences prefs, QWidget parent) {
		super(labelText, parent);
		setStyleSheet("color:grey;");
		
		this.name = name;
		this.prefs = prefs;
		
		if (prefs != null) {
			String stored = prefs.get(name, null);
			if (stored != null) {
				setText(stored);
				doSet();
			}
		}
	}
	
	public QSignalEmitter.Signal1<Boolean> set = new QSignalEmitter.Signal1<Boolean>();
	
	//TODO: reset help text when box is cleared by user
	
	@Override
	@QtBlockedSlot
	protected void focusInEvent(QFocusEvent evt) {
		super.focusInEvent(evt);
		if (!isCleared)
			 setText("");
		doSet();
	}

	private void doSet() {
		if (!isCleared) {
			setStyleSheet("");
			isCleared = true;
			set.emit(true);
		}
	}
	
	@Override
	@QtBlockedSlot
	protected void focusOutEvent(QFocusEvent evt) {
		super.focusOutEvent(evt);
		if (prefs != null)
			prefs.put(name, text());
	}
}
