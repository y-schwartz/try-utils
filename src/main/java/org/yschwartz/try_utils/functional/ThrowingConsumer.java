package org.yschwartz.try_utils.functional;

@FunctionalInterface
public interface ThrowingConsumer<S> {

    void accept(S s) throws Exception;

    default <T> ThrowingFunction<S, T> toFunction() {
        return s -> {
            accept(s);
            return null;
        };
    }
}
