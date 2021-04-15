package org.yschwartz.try_utils.logic;

import org.yschwartz.try_utils.model.Try;

import java.util.Optional;
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
    private long fixedDelay = DEFAULT_DELAY;
    private Function<Long, Long> delayFunction;
    private BiConsumer<Exception, Long> failureConsumer;

    public RetryLogic(Callable<R> callable) {
        this.callable = callable;
    }

    @Override
    public R call() throws Exception {
        for (long i : LongStream.range(1, maxAttempts).toArray()) {
            try {
                return callable.call();
            } catch (Exception e) {
                if (!exceptionMatcher.matches(e)) {
                    throw e;
                }
                consumeFailure(e, i);
                sleep(i);
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
        this.fixedDelay = fixedDelay;
    }

    public void setDelayFunction(Function<Long, Long> delayFunction) {
        this.delayFunction = delayFunction;
    }

    public void setFailureConsumer(BiConsumer<Exception, Long> failureConsumer) {
        this.failureConsumer = failureConsumer;
    }

    private void sleep(long iteration) {
        Optional.ofNullable(delayFunction)
                .map(f -> f.apply(iteration))
                .or(() -> Optional.of(fixedDelay))
                .filter(l -> l > 0)
                .map(l -> Try.of(() -> Thread.sleep(l)))
                .map(t -> t.catchException(InterruptedException.class))
                .map(c -> c.thenThrow(e -> new RuntimeException("Interrupted during retries", e)))
                .ifPresent(Try::execute);
    }

    private void consumeFailure(Exception e, long iteration) {
        Optional.ofNullable(failureConsumer).ifPresent(consumer -> consumer.accept(e, iteration));
    }
}
