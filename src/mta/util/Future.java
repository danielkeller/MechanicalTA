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
		//ensure that the last thread to start gets to set the value
		//as opposed to the last thread to finish
		if (running != null) {
			running.canFinish = false;
			//don't send extra signals
			th.finished.disconnect();
		}
		
		running = new FRunnable();
		th = new QThread(running);
		
		th.finished.connect(done, Qt.ConnectionType.QueuedConnection);
		th.start();
	}
	
	class FRunnable implements Runnable {
		boolean canFinish = true;
		@Override
		public void run() {
			T temp = evaluate();
			synchronized (this_) {
				if (canFinish)
					val = temp;
			}
		}
	}
	
	private Signal0 done = new Signal0();
	private T val = null;
	private Future<T> this_ = this;
	FRunnable running = null;
	QThread th = null;
}
