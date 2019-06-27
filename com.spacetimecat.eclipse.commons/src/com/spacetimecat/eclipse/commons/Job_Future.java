package com.spacetimecat.eclipse.commons;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.jobs.Job;

final class Job_Future<T> implements Future<T> {

    Job job;

    private boolean canceled;
    private boolean done;
    private T value;
    private Throwable throwable;

    @Override
    public boolean cancel (boolean mayInterruptIfRunning) {
        synchronized (this) {
            job.cancel();
            done = true;
            canceled = true;
            this.notifyAll();
            return true;
        }
    }

    @Override
    public boolean isCancelled () {
        synchronized (this) {
            return canceled;
        }
    }

    @Override
    public boolean isDone () {
        synchronized (this) {
            return done;
        }
    }

    public void complete_normally (T value) {
        synchronized (this) {
            this.value = value;
            this.done = true;
            this.notifyAll();
        }
    }

    /**
     * @param throwable should not be null
     */
    public void complete_abnormally (Throwable throwable) {
        synchronized (this) {
            this.throwable = throwable;
            this.done = true;
            this.notifyAll();
        }
    }

    public void complete_with_cancellation () {
        synchronized (this) {
            this.done = true;
            this.canceled = true;
            this.notifyAll();
        }
    }

    @Override
    public T get () throws InterruptedException, ExecutionException {
        synchronized (this) {
            while (!done) {
                this.wait();
            }
            return return_();
        }
    }

    @Override
    public T get (long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final long begin = System.currentTimeMillis();
        final long deadline = begin + unit.toMillis(timeout);
        synchronized (this) {
            while (!done) {
                final long patience = deadline - System.currentTimeMillis();
                if (patience < 0) {
                    throw new TimeoutException();
                }
                this.wait(patience);
            }
            return return_();
        }
    }

    private T return_ () throws ExecutionException {
        if (canceled) {
            throw new CancellationException();
        }
        if (throwable != null) {
            throw new ExecutionException(throwable);
        }
        return value;
    }

}
