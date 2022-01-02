package org.yschwartz.try_utils.functional;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface ThrowingRunnable {

    void run() throws Exception;

    default <T> Callable<T> toCallable() {
        return () -> {
            run();
            return null;
        };
    }

    default <T> ThrowingConsumer<T> toConsumer() {
        return x -> run();
    }
}
