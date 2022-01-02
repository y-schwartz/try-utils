package org.yschwartz.try_utils.model;

import org.yschwartz.try_utils.functional.ExtendedConsumer;
import org.yschwartz.try_utils.functional.ExtendedRunnable;
import org.yschwartz.try_utils.logic.ExceptionMatcher;
import org.yschwartz.try_utils.util.ExceptionUtils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.yschwartz.try_utils.util.FunctionalUtils.consumerThenFunction;

public class Catch<E extends Exception, R> {

    private final Try<R> tryy;
    private final ExceptionMatcher<E> exceptionMatcher;
    private Consumer<E> exceptionConsumer = e -> {
    };

    Catch(Try<R> tryy, Class<E> exceptionType) {
        this.tryy = tryy;
        this.exceptionMatcher = new ExceptionMatcher<>(exceptionType);
    }

    public Catch<E, R> filter(Predicate<E> filter) {
        Objects.requireNonNull(filter);
        exceptionMatcher.addFilter(filter);
        return this;
    }

    public Catch<E, R> then(ExtendedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return then(runnable.toConsumer());
    }

    public Catch<E, R> then(Consumer<E> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer);
        this.exceptionConsumer = this.exceptionConsumer.andThen(exceptionConsumer);
        return this;
    }

    public Try<R> thenThrow() {
        return thenThrow(ExceptionUtils::toRuntimeException);
    }

    public Try<R> thenThrow(RuntimeException exception) {
        return thenThrow(e -> exception);
    }

    public Try<R> thenThrow(Function<E, ? extends RuntimeException> exceptionMapper) {
        Objects.requireNonNull(exceptionMapper);
        return thenDo(e -> {
            throw exceptionMapper.apply(e);
        });
    }

    public Try<R> thenDo(ExtendedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return thenDo(runnable.toConsumer());
    }

    public Try<R> thenDo(ExtendedConsumer<E> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer);
        return thenReturn(exceptionConsumer.toFunction());
    }

    public Try<R> thenReturn() {
        return thenReturn((R) null);
    }

    public Try<R> thenReturn(R value) {
        return thenReturn(() -> value);
    }

    public Try<R> thenReturn(Supplier<R> valueSupplier) {
        Objects.requireNonNull(valueSupplier);
        return thenReturn(e -> valueSupplier.get());
    }

    public Try<R> thenReturn(Function<E, R> exceptionToValueFunction) {
        Objects.requireNonNull(exceptionToValueFunction);
        setCatchFunction(exceptionToValueFunction);
        return tryy;
    }

    private void setCatchFunction(Function<E, R> exceptionToValueFunction) {
        tryy.getTryLogic().addCatchFunction(exceptionMatcher,
                consumerThenFunction(exceptionConsumer, exceptionToValueFunction));
    }
}
