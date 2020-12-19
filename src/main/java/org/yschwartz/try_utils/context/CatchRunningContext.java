package org.yschwartz.try_utils.context;

import org.yschwartz.try_utils.util.FunctionalUtils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides methods for defining a catch block.
 *
 * @param <E> the type of exception that should be caught
 */
public class CatchRunningContext<E extends Exception> extends BaseCatchContext<E, Void> {
    CatchRunningContext(TryRunningContext tryRunningContext, Class<E> exceptionType) {
        super(tryRunningContext, exceptionType);
    }

    /**
     * Sets the exception consumer for the catch block.
     *
     * @param exceptionConsumer the exception consumer
     * @return the TryRunningContext for defining the remaining blocks
     */
    public TryRunningContext thenDo(Consumer<E> exceptionConsumer) {
        setReturnValueFunction(FunctionalUtils.consumerToFunction(exceptionConsumer));
        return (TryRunningContext) tryContext;
    }

    /**
     * Sets the catch block to throw an exception.
     *
     * @param exceptionMapper the exception mapper from the caught exception to a new exception
     * @return the TryRunningContext for defining the remaining blocks
     */
    public TryRunningContext thenThrow(Function<E, ? extends RuntimeException> exceptionMapper) {
        setThrowsException(exceptionMapper);
        return (TryRunningContext) tryContext;
    }
}
