package mta.util;

import com.trolltech.qt.QThread;
import com.trolltech.qt.core.*;

public abstract class Future<T> extends QObject {
	public Future(Object recipient, String slot) {
		done.connect(recipient, slot, Qt.ConnectionType.QueuedConnection);
	}
	
	public void start () {
		QThread th = new QThread(new Runnable () {
			@Override
			public void run() {
				val = evaluate();
				done.emit();
			}
		});
		moveToThread(th);
		th.start();
	}
	public T get() {return val;}
	
	protected abstract T evaluate();
	private Signal0 done = new Signal0();
	private T val;
}
