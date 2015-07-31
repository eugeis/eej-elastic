package ee.elastic.ui.integ;

import java.util.concurrent.TimeUnit;

public class DelayExecutor {
  private Thread executor;
  private DelayRunnable runnableDelay;

  public DelayExecutor(Runnable runnable, long delay, TimeUnit timeUnit) {
    runnableDelay = new DelayRunnable(runnable, delay, TimeUnit.MILLISECONDS);
    executor = new Thread(runnableDelay);
    executor.start();
  }

  public void trigger() {
    runnableDelay.trigger();
  }

  public void close() {
    executor.interrupt();
  }
}
