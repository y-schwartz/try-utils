package org.yschwartz.try_utils.logic;

import java.util.concurrent.Callable;

public class SimpleTryLogic<R> extends BaseTryLogic<R> {
    private final Callable<R> tryCallable;

    public SimpleTryLogic(Callable<R> tryCallable) {
        super();
        this.tryCallable = tryCallable;
    }

    @Override
    public R call() throws Exception {
        return tryCallable.call();
    }

    @Override
    public R execute() {
        return hasFinally() ? executeWithFinally() : executeWithoutFinally();
    }

    private R executeWithFinally() {
        try {
            return doTry();
        } catch (Exception e) {
            return doCatch(e);
        } finally {
            doFinally();
        }
    }

    private R executeWithoutFinally() {
        try {
            return doTry();
        } catch (Exception e) {
            return doCatch(e);
        }
    }
}
