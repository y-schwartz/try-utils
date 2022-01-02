package org.yschwartz.try_utils.functional;

@FunctionalInterface
public interface ThrowingFunction<S, R> {

    R apply(S s) throws Exception;
}
