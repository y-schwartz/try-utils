package org.yschwartz.try_utils.context;

import org.yschwartz.try_utils.logic.BaseTryLogic;

/**
 * Provides a method for defining the finally block.
 *
 * @param <R>
 *            the type of the result of the try/catch block
 */
public class TryFinallyContext<R> extends BaseTryContext<R> {
	protected TryFinallyContext(BaseTryLogic<R> tryLogic) {
		super(tryLogic);
	}

	/**
	 * Sets the finally block to run a task.
	 *
	 * @param runnable
	 *            the task to run
	 * @return a TryExecuteContext for executing the try/catch block
	 */
	public BaseTryContext<R> finallyRun(Runnable runnable) {
		tryLogic.setFinallyRunnable(runnable);
		return this;
	}
}
