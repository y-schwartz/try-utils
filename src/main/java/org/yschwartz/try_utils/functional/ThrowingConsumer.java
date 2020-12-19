package org.yschwartz.try_utils.functional;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Can throw a checked Exception.
 *
 * @param <S> the type of the input to the operation
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ThrowingConsumer<S> {

    /**
     * Performs this operation on the given argument.
     *
     * @param s the input argument
     * @throws Exception if the operation Threw an Exception
     */
    void accept(S s) throws Exception;
}
