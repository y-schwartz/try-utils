package org.yschwartz.try_utils.logic;

import java.util.Optional;
import java.util.function.Function;

import org.yschwartz.try_utils.util.StackTraceUtils;

public abstract class BaseTryLogic<R> {

	protected final CatchFunctions<R> catchFunctions;
	protected Runnable finallyRunnable;

	protected BaseTryLogic() {
		this.catchFunctions = new CatchFunctions<>();
	}

	public abstract R execute();

	public <T extends Exception> void addCatchFunction(Class<T> exceptionType,
			Function<T, R> exceptionToValueFunction) {
		catchFunctions.addCatchFunction(exceptionType, exceptionToValueFunction);
	}

	public void setFinallyRunnable(Runnable runnable) {
		finallyRunnable = runnable;
	}

	protected R doCatch(Exception e) {
		StackTraceUtils.setExceptionStackTrace(e);
		return catchFunctions.applyFunction(e);
	}

	protected void doFinally() {
		Optional.ofNullable(finallyRunnable).ifPresent(Runnable::run);
	}
}
