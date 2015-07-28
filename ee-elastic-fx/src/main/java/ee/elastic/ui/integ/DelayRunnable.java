package ee.elastic.ui.integ;

import java.util.concurrent.TimeUnit;

public class DelayRunnable implements Runnable {
	private long delay;
	private TimeUnit timeUnit;
	private Runnable delegate;
	private boolean closed = false;
	private boolean trigger = false;

	public DelayRunnable(Runnable delegate, long delay, TimeUnit timeUnit) {
		super();
		this.delegate = delegate;
		this.delay = delay;
		this.timeUnit = timeUnit;
	}

	public void trigger() {
		trigger = true;
		synchronized (delegate) {
			delegate.notify();
		}
	}

	public void close() {
		closed = true;
		synchronized (delegate) {
			delegate.notify();
		}
	}

	@Override
	public void run() {
		while (!closed) {
			checkAndWaitForTrigger();
			if (!closed) {
				delayAndExecute();
			}
		}
	}

	private void delayAndExecute() {
		try {
			while (trigger) {
				trigger = false;
				Thread.sleep(timeUnit.toMillis(delay));
			}
			delegate.run();
		} catch (InterruptedException e) {
			closed = true;
		}
	}

	private void checkAndWaitForTrigger() {
		if (!trigger) {
			try {
				synchronized (delegate) {
					delegate.wait();
				}
			} catch (InterruptedException e) {
				closed = true;
			}
		}
	}
}
