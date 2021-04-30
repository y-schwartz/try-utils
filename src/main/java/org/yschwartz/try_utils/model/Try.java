package org.yschwartz.try_utils.model;

import org.yschwartz.try_utils.functional.ExtendedRunnable;
import org.yschwartz.try_utils.functional.ThrowingConsumer;
import org.yschwartz.try_utils.functional.ThrowingFunction;
import org.yschwartz.try_utils.functional.ThrowingRunnable;
import org.yschwartz.try_utils.logic.BaseTryLogic;
import org.yschwartz.try_utils.logic.SimpleTryLogic;
import org.yschwartz.try_utils.logic.TryWithResourceLogic;

import java.util.Objects;
import java.util.concurrent.Callable;

import static org.yschwartz.try_utils.util.FunctionalUtils.consumerToFunction;
import static org.yschwartz.try_utils.util.FunctionalUtils.runnableToCallable;

public class Try<R> {

    private final BaseTryLogic<R> tryLogic;

    protected Try(BaseTryLogic<R> tryLogic) {
        this.tryLogic = tryLogic;
    }

    public static Try<Void> of(ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable);
        return of(runnableToCallable(runnable));
    }

    public static <R> Try<R> of(Callable<R> callable) {
        Objects.requireNonNull(callable);
        return new Try<>(new SimpleTryLogic<>(callable));
    }

    public static <S extends AutoCloseable> Try<Void> of(S resource,
                                                         ThrowingConsumer<S> resourceConsumer) {
        Objects.requireNonNull(resource);
        Objects.requireNonNull(resourceConsumer);
        return of(resource, consumerToFunction(resourceConsumer));
    }

    public static <S extends AutoCloseable, R> Try<R> of(S resource,
                                                         ThrowingFunction<S, R> resourceToValueFunction) {
        Objects.requireNonNull(resource);
        Objects.requireNonNull(resourceToValueFunction);
        return new Try<>(new TryWithResourceLogic<>(resource, resourceToValueFunction));
    }

    public Retry<R> retry() {
        return new Retry<>(tryLogic);
    }

    public <S> Try<S> map(ThrowingFunction<R, S> function) {
        Objects.requireNonNull(function);
        return of(() -> function.apply(execute()));
    }

    public Try<Void> consume(ThrowingConsumer<R> consumer) {
        Objects.requireNonNull(consumer);
        return map(consumerToFunction(consumer));
    }

    public Catch<Exception, R> catchAny() {
        return catchException(Exception.class);
    }

    public <E extends Exception> Catch<E, R> catchException(Class<E> exceptionType) {
        Objects.requireNonNull(exceptionType);
        return new Catch<>(this, exceptionType);
    }

    public Try<R> finallyDo(ExtendedRunnable runnable) {
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
