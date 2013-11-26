package mta.util;

import com.trolltech.qt.QThread;
import com.trolltech.qt.core.*;

public abstract class Future<T> extends QObject {
	public Future(Object recipient, String slot) {
		done.connect(recipient, slot);
	}

	public synchronized T get() {return val;}
	protected abstract T evaluate();
	
	public synchronized void start() {		
		final QThread th = new QThread(new Runnable () {
			@Override
			public void run() {
				synchronized (this_) {
					val = evaluate();
				}
			}
		});
		th.finished.connect(done, Qt.ConnectionType.QueuedConnection);
		th.start();
	}
	
	private Signal0 done = new Signal0();
	private T val = null;
	private Future<T> this_ = this;
}
