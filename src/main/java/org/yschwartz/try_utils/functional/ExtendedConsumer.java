package org.yschwartz.try_utils.functional;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ExtendedConsumer<S> extends Consumer<S> {

    default <T> Function<S, T> toFunction() {
        return s -> {
            accept(s);
            return null;
        };
    }
}
