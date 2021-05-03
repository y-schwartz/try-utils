package org.yschwartz.try_utils.functional;

import java.util.Objects;

@FunctionalInterface
public interface ExtendedRunnable extends Runnable {

    default ExtendedRunnable andThen(Runnable other) {
        Objects.requireNonNull(other);
        return () -> {
            this.run();
            other.run();
        };
    }

    default <T> ExtendedConsumer<T> toConsumer() {
        return x -> run();
    }
}
