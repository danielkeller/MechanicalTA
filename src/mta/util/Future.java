package mta.util;

import com.trolltech.qt.QThread;
import com.trolltech.qt.core.*;

public abstract class Future<T> extends QObject {
	public Future(Object recipient, String slot) {
		done.connect(recipient, slot, Qt.ConnectionType.QueuedConnection);
	}

	public synchronized T get() {return val;}
	protected abstract T evaluate();
	
	public void start() {
		if (running)
			return;
		running = true;
		
		final QThread th = new QThread(new Runnable () {
			@Override
			public void run() {
				try {
					T tmp = evaluate();
					synchronized (this_) {
						val = tmp;
					}
					done.emit();
				} finally {
					running = false;
					this_.moveToThread(orig);
				}
			}
		});
		moveToThread(th);
		th.start();
	}
	
	private Signal0 done = new Signal0();
	private T val = null;
	private Thread orig = thread();
	private Future<T> this_ = this;
	private boolean running = false;
}
