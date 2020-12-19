package org.yschwartz.try_utils.functional;

/**
 * Represents a function that accepts one argument and produces a result. Can
 * throw a checked Exception.
 *
 * @param <S> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<S, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param s the function argument
     * @return the function result
     * @throws Exception if the function Threw an Exception
     */
    R apply(S s) throws Exception;
}
