package org.yschwartz.try_utils.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class FunctionalUtils {

    public static <S, T> Function<S, T> consumerThenFunction(Consumer<S> consumer, Function<S, T> function) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(function);
        return s -> {
            consumer.accept(s);
            return function.apply(s);
        };
    }
}
