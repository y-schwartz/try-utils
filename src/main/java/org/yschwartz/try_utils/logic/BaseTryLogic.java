package org.yschwartz.try_utils.logic;

import org.yschwartz.try_utils.functional.ExtendedRunnable;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

public abstract class BaseTryLogic<R> implements Callable<R> {

    private final CatchFunctions<R> catchFunctions = new CatchFunctions<>();
    private RetryLogic<R> retryLogic;
    private ExtendedRunnable finallyRunnable;

    public abstract R execute();

    public void setRetries() {
        retryLogic = new RetryLogic<>(this);
    }

    public RetryLogic<R> getRetryLogic() {
        return retryLogic;
    }

    public <E extends Exception> void addCatchFunction(ExceptionMatcher<E> exceptionMatcher,
                                                       Function<E, R> exceptionToValueFunction) {
        catchFunctions.add(exceptionMatcher, exceptionToValueFunction);
    }

    public void setFinallyRunnable(ExtendedRunnable runnable) {
        finallyRunnable = Optional.ofNullable(finallyRunnable).orElse(() -> {
        }).andThen(runnable);
    }

    protected R doTry() throws Exception {
        return Optional.ofNullable(retryLogic).map(callable -> (Callable<R>) callable).orElse(this).call();
    }

    protected R doCatch(Exception e) {
        return catchFunctions.applyFunction(e);
    }

    protected void doFinally() {
        Optional.ofNullable(finallyRunnable).ifPresent(Runnable::run);
    }

    protected boolean hasFinally() {
        return finallyRunnable != null;
    }
}
