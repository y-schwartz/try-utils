package org.yschwartz.try_utils.logic;

import java.util.concurrent.Callable;

public class SimpleTryLogic<R> extends BaseTryLogic<R> {
    private final Callable<R> tryCallable;

    public SimpleTryLogic(Callable<R> tryCallable) {
        super();
        this.tryCallable = tryCallable;
    }

    public R execute() {
        try {
            return tryCallable.call();
        } catch (Exception e) {
            return doCatch(e);
        } finally {
            doFinally();
        }
    }
}
