package org.vns.common;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Valery Shyshrin
 */
public class RequestExecutor {

    public static Task createTask(Runnable runnable) {
        return new Task(Executors.newSingleThreadScheduledExecutor(), runnable);
    }

    public static class Task {

        ScheduledExecutorService executor;
        Runnable runnable;
        //Future future; 
        
        public Task(ScheduledExecutorService executor, Runnable runnable) {
            this.executor = executor;
            this.runnable = runnable;
        }
        public Runnable getRunnable() {
            return runnable;
        }
        public ScheduledExecutorService getExecutor() {
            return executor;
        }        
        public Future submit() {
            if (executor.isShutdown()) {
                System.out.println("Is shutdown = TRUE" );
                executor = Executors.newSingleThreadScheduledExecutor();
            }
            Future future  = executor.submit(runnable);
            return future;
        }
        public void shutdown() {
            executor.shutdown();
        }
        public void waitFinished() {
            waitFinished(5000);
        }
        public void waitFinished(long mscTimeout) {
            try {
                System.out.println("attempt to shutdown executor");
                executor.shutdown();
                executor.awaitTermination(mscTimeout, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                System.err.println("tasks interrupted");
            } finally {
                if (!executor.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                executor.shutdownNow();
                System.out.println("shutdown finished");
            }
        }
        public void shutdownNow(long mscTimeout) {
            try {
                System.out.println("attempt to shutdown executor");
                executor.awaitTermination(mscTimeout, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                System.err.println("tasks interrupted");
            } finally {
                if (!executor.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                executor.shutdownNow();
                System.out.println("shutdown finished");
            }
        }
        
        
        public ScheduledFuture schedule(int delay) {
            return executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        }
        
        
    }
}
