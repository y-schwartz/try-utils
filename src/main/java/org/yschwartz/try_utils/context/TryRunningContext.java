package org.yschwartz.try_utils.context;

import org.yschwartz.try_utils.logic.BaseTryLogic;

/**
 * Provides methods for defining the catch blocks.
 */
public class TryRunningContext extends TryFinallyContext<Void> {

    public TryRunningContext(BaseTryLogic<Void> tryLogic) {
        super(tryLogic);
    }

    /**
     * Sets the Exception type to be caught.
     *
     * @param exceptionType the Exception type to be caught
     * @param <E>           the Exception type
     * @return a CatchRunningContext for defining the catch block
     */
    public <E extends Exception> CatchRunningContext<E> catchException(Class<E> exceptionType) {
        return new CatchRunningContext<>(this, exceptionType);
    }

    /**
     * Sets the catch block to catch any exception.
     *
     * @return a CatchRunningContext for defining the catch block
     */
    public CatchRunningContext<Exception> catchAny() {
        return catchException(Exception.class);
    }
}
