package org.yschwartz.try_utils.logic;

import org.yschwartz.try_utils.exception.RetriesInterruptedException;
import org.yschwartz.try_utils.model.Try;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;

public class RetryLogic<R> implements Callable<R> {

    private static final int DEFAULT_ATTEMPTS = 3;
    private static final int DEFAULT_DELAY = 1000;

    private final ExceptionMatcher<Exception> exceptionMatcher = new ExceptionMatcher<>(Exception.class);
    private final Callable<R> callable;
    private long maxAttempts = DEFAULT_ATTEMPTS;
    private Function<Long, Long> delayFunction = l -> (long) DEFAULT_DELAY;
    private BiConsumer<Exception, Long> failureConsumer = (e, x) -> {
    };

    public RetryLogic(Callable<R> callable) {
        this.callable = callable;
    }

    @Override
    public R call() throws Exception {
        for (long i : LongStream.range(1, maxAttempts).toArray()) {
            try {
                return callable.call();
            } catch (Exception e) {
                handleException(e, i);
            }
        }
        return callable.call();
    }

    public void addFilter(Predicate<Exception> filter) {
        exceptionMatcher.addFilter(filter);
    }

    public void setMaxAttempts(long maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setFixedDelay(long fixedDelay) {
        setDelayFunction(l -> fixedDelay);
    }

    public void setDelayFunction(Function<Long, Long> delayFunction) {
        this.delayFunction = delayFunction;
    }

    public void setFailureConsumer(BiConsumer<Exception, Long> consumer) {
        failureConsumer = failureConsumer.andThen(consumer);
    }

    private void handleException(Exception e, long iteration) throws Exception {
        if (!exceptionMatcher.matches(e)) {
            throw e;
        }
        consumeFailure(e, iteration);
        sleep(iteration);
    }

    private void consumeFailure(Exception e, long iteration) {
        failureConsumer.accept(e, iteration);
    }

    private void sleep(long iteration) {
        Try.of(() -> delayFunction.apply(iteration)).consume(Thread::sleep).catchException(InterruptedException.class)
                .thenThrow(e -> new RetriesInterruptedException(e, iteration)).execute();
    }
}
