package org.yschwartz.try_utils.model;

import org.yschwartz.try_utils.functional.ThrowingConsumer;
import org.yschwartz.try_utils.functional.ThrowingFunction;
import org.yschwartz.try_utils.functional.ThrowingRunnable;
import org.yschwartz.try_utils.logic.BaseTryLogic;
import org.yschwartz.try_utils.logic.SimpleTryLogic;
import org.yschwartz.try_utils.logic.TryWithResourceLogic;
import org.yschwartz.try_utils.util.FunctionalUtils;

import java.util.concurrent.Callable;

public class Try<R> {

    private final BaseTryLogic<R> tryLogic;

    protected Try(BaseTryLogic<R> tryLogic) {
        this.tryLogic = tryLogic;
    }

    public static <R> Try<R> of(Callable<R> callable) {
        return new Try<>(new SimpleTryLogic<>(callable));
    }

    public static Try<Void> of(ThrowingRunnable runnable) {
        return new Try<>(new SimpleTryLogic<>(FunctionalUtils.runnableToCallable(runnable)));
    }

    public static <S extends AutoCloseable> Try<Void> of(S resource,
                                                         ThrowingConsumer<S> resourceConsumer) {
        return new Try<>(new TryWithResourceLogic<>(resource, FunctionalUtils.consumerToFunction(resourceConsumer)));
    }

    public static <S extends AutoCloseable, R> Try<R> of(S resource,
                                                         ThrowingFunction<S, R> resourceToValueFunction) {
        return new Try<>(new TryWithResourceLogic<>(resource, resourceToValueFunction));
    }

    public Retry<R> withRetries() {
        return new Retry<>(tryLogic);
    }

    public <S> Try<S> map(ThrowingFunction<R, S> function) {
        return of(() -> function.apply(execute()));
    }

    public Catch<Exception, R> catchAny() {
        return catchException(Exception.class);
    }

    public <E extends Exception> Catch<E, R> catchException(Class<E> exceptionType) {
        return new Catch<>(this, exceptionType);
    }

    public Try<R> finallyDo(Runnable runnable) {
        tryLogic.setFinallyRunnable(runnable);
        return this;
    }

    public R execute() {
        return tryLogic.execute();
    }

    BaseTryLogic<R> getTryLogic() {
        return tryLogic;
    }
}
