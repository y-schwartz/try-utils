package org.yschwartz.try_utils.context;

import org.yschwartz.try_utils.logic.BaseTryLogic;

public abstract class BaseTryContext<R> {
	protected final BaseTryLogic<R> tryLogic;

	protected BaseTryContext(BaseTryLogic<R> tryLogic) {
		this.tryLogic = tryLogic;
	}

	protected BaseTryLogic<R> getTryLogic() {
		return tryLogic;
	}

	/**
	 * executes the try/catch block.
	 *
	 * @return the result of the try/catch block
	 */
	public R execute() {
		return tryLogic.execute();
	}
}
