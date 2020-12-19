package org.yschwartz.try_utils.context;

import java.util.function.Function;

/**
 * Provides methods for defining a catch block.
 *
 * @param <E> the type of exception that should be caught
 * @param <R> the type of the result of the try/catch block
 */
public class CatchCallingContext<E extends Exception, R> extends BaseCatchContext<E, R> {
    CatchCallingContext(BaseTryContext<R> tryContext, Class<E> exceptionType) {
        super(tryContext, exceptionType);
    }

    /**
     * Sets the catch block to return a value.
     *
     * @param value the result value
     * @return the TryCallingContext for defining the remaining blocks
     */
    public TryCallingContext<R> thenReturn(R value) {
        return thenReturn(e -> value);
    }

    /**
     * Sets the exception to value function for the catch block.
     *
     * @param exceptionToValueFunction the function that receives the caught exception and returns a
     *                                 result value
     * @return the TryCallingContext for defining the remaining blocks
     */
    public TryCallingContext<R> thenReturn(Function<E, R> exceptionToValueFunction) {
        setReturnValueFunction(exceptionToValueFunction);
        return (TryCallingContext<R>) tryContext;
    }

    /**
     * Sets the catch block to throw an exception.
     *
     * @param exceptionMapper the exception mapper from the caught exception to a new exception
     * @return the TryCallingContext for defining the remaining blocks
     */
    public TryCallingContext<R> thenThrow(Function<E, ? extends RuntimeException> exceptionMapper) {
        setThrowsException(exceptionMapper);
        return (TryCallingContext<R>) tryContext;
    }
}
