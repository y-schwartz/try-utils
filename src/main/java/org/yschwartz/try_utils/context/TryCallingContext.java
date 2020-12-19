package org.yschwartz.try_utils.context;

import org.yschwartz.try_utils.logic.BaseTryLogic;

/**
 * Provides methods for defining the catch blocks.
 *
 * @param <R>
 *            the type of the result of the try/catch block
 */
public class TryCallingContext<R> extends TryFinallyContext<R> {

	public TryCallingContext(BaseTryLogic<R> tryLogic) {
		super(tryLogic);
	}

	/**
	 * Sets the Exception type to be caught.
	 *
	 * @param exceptionType
	 *            the Exception type to be caught
	 * @param <E>
	 *            the Exception type
	 * @return a CatchCallingContext for defining the catch block
	 */
	public <E extends Exception> CatchCallingContext<E, R> catchException(Class<E> exceptionType) {
		return new CatchCallingContext<>(this, exceptionType);
	}

	/**
	 * Sets the catch block to catch any exception.
	 *
	 * @return a CatchCallingContext for defining the catch block
	 */
	public CatchCallingContext<Exception, R> catchAny() {
		return catchException(Exception.class);
	}
}
