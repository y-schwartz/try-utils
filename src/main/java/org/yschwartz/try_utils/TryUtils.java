package org.yschwartz.try_utils;

import org.yschwartz.try_utils.context.TryCallingContext;
import org.yschwartz.try_utils.context.TryRunningContext;
import org.yschwartz.try_utils.functional.ThrowingConsumer;
import org.yschwartz.try_utils.functional.ThrowingFunction;
import org.yschwartz.try_utils.functional.ThrowingRunnable;
import org.yschwartz.try_utils.logic.SimpleTryLogic;
import org.yschwartz.try_utils.logic.TryWithResourceLogic;
import org.yschwartz.try_utils.util.FunctionalUtils;

import java.util.concurrent.Callable;

/**
 * Provides static methods for defining try/catch blocks in a functional style.
 */
public class TryUtils {

    /**
     * Starts a try/catch block for running a task that may throw an exception.
     *
     * @param runnable the runnable for the try block
     * @return a TryRunningContext instance for setting the remaining blocks
     */
    public static TryRunningContext tryRunning(ThrowingRunnable runnable) {
        return new TryRunningContext(new SimpleTryLogic<>(FunctionalUtils.runnableToCallable(runnable)));
    }

    /**
     * Starts a try/catch block for calling a callable that may throw an exception.
     *
     * @param callable the callable for the try block
     * @param <R>      the type of the result of the callable
     * @return a TryCallingContext instance for setting the remaining blocks
     */
    public static <R> TryCallingContext<R> tryCalling(Callable<R> callable) {
        return new TryCallingContext<>(new SimpleTryLogic<>(callable));
    }

    /**
     * Starts a try with resource block for consuming a resource that may throw an
     * exception.
     *
     * @param resource         the resource for the try block that will be closed automatically
     * @param resourceConsumer the resource consumer that may throw an exception
     * @param <S>              the type of the resource
     * @return a TryRunningContext instance for setting the remaining blocks
     */
    public static <S extends AutoCloseable> TryRunningContext tryRunningResource(S resource,
                                                                                 ThrowingConsumer<S> resourceConsumer) {
        return new TryRunningContext(new TryWithResourceLogic<>(resource, FunctionalUtils.consumerToFunction(resourceConsumer)));
    }

    /**
     * Starts a try with resource block for calling a resource to value function
     * that may throw an exception.
     *
     * @param resource                the resource for the try block that will be closed automatically
     * @param resourceToValueFunction resource to value the function for the try block
     * @param <R>                     the type of the result of the function
     * @return a TryCallingContext instance for setting the remaining blocks and
     * retrieving the value
     */
    public static <S extends AutoCloseable, R> TryCallingContext<R> tryCallingResource(S resource,
                                                                                       ThrowingFunction<S, R> resourceToValueFunction) {
        return new TryCallingContext<>(new TryWithResourceLogic<>(resource, resourceToValueFunction));
    }

    private TryUtils() {
    }
}
