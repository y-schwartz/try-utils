package org.yschwartz.try_utils.functional;

/**
 * Represents an operation that runs a task. Can throw a checked Exception.
 *
 * @see java.lang.Runnable
 */
@FunctionalInterface
public interface ThrowingRunnable {

    /**
     * Performs the task.
     *
     * @throws Exception if the task Threw an Exception
     */
    void run() throws Exception;
}
