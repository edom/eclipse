package com.spacetimecat.eclipse.commons;

import java.util.concurrent.Future;

public interface Scheduler {

    /**
     * <p>
     * Schedule the task to run in the background,
     * and add an item to the Progress View.
     * </p>
     * <p>
     * The task should be cancelable.
     * It should poll {@link Progress_Monitor#is_canceled()}.
     * </p>
     */
    Future<?> schedule (Consumer0<Progress_Monitor> task);

}
