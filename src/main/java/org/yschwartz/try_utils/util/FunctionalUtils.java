package org.yschwartz.try_utils.util;

import org.yschwartz.try_utils.functional.ThrowingConsumer;
import org.yschwartz.try_utils.functional.ThrowingFunction;
import org.yschwartz.try_utils.functional.ThrowingRunnable;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class FunctionalUtils {

    public static <S> ThrowingFunction<S, Void> consumerToFunction(ThrowingConsumer<S> consumer) {
        Objects.requireNonNull(consumer);
        return s -> {
            consumer.accept(s);
            return null;
        };
    }

    public static <S, T> Function<S, T> consumerToFunction(Consumer<S> consumer) {
        Objects.requireNonNull(consumer);
        return s -> {
            consumer.accept(s);
            return null;
        };
    }

    public static Callable<Void> runnableToCallable(ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable);
        return () -> {
            runnable.run();
            return null;
        };
    }

    public static <S, T> Function<S, T> consumerThenFunction(Consumer<S> consumer, Function<S, T> function) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(function);
        return s -> {
            consumer.accept(s);
            return function.apply(s);
        };
    }

    public static <T> Consumer<T> runnableToConsumer(Runnable runnable) {
        Objects.requireNonNull(runnable);
        return x -> runnable.run();
    }
}
