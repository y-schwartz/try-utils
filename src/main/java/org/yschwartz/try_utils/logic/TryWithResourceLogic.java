package org.yschwartz.try_utils.logic;

import org.yschwartz.try_utils.functional.ThrowingFunction;

public class TryWithResourceLogic<S extends AutoCloseable, R> extends BaseTryLogic<R> {
	private final S resource;
	private final ThrowingFunction<S, R> tryFunction;

	public TryWithResourceLogic(S resource, ThrowingFunction<S, R> tryFunction) {
		super();
		this.resource = resource;
		this.tryFunction = tryFunction;
	}

	public R execute() {
		try (S resource = this.resource) {
			return tryFunction.apply(resource);
		} catch (Exception e) {
			return doCatch(e);
		} finally {
			doFinally();
		}
	}
}
