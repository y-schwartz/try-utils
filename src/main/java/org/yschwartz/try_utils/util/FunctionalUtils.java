package org.yschwartz.try_utils.util;

import org.yschwartz.try_utils.functional.ThrowingConsumer;
import org.yschwartz.try_utils.functional.ThrowingFunction;
import org.yschwartz.try_utils.functional.ThrowingRunnable;

import java.util.concurrent.Callable;

public class FunctionalUtils {

    public static <S> ThrowingFunction<S, Void> consumerToFunction(ThrowingConsumer<S> consumer) {
        return s -> {
            consumer.accept(s);
            return null;
        };
    }

    public static Callable<Void> runnableToCallable(ThrowingRunnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}
