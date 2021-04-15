package org.yschwartz.try_utils.model;

import org.yschwartz.try_utils.logic.BaseTryLogic;
import org.yschwartz.try_utils.logic.RetryLogic;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Retry<R> extends Try<R> {

    private final RetryLogic<R> retryLogic;

    protected Retry(BaseTryLogic<R> tryLogic) {
        super(tryLogic);
        tryLogic.setRetries();
        retryLogic = tryLogic.getRetryLogic();
    }

    public Retry<R> filter(Predicate<Exception> filter) {
        retryLogic.addFilter(filter);
        return this;
    }

    public Retry<R> maxAttempts(long maxAttempts) {
        retryLogic.setMaxAttempts(maxAttempts);
        return this;
    }

    public Retry<R> noDelay() {
        return fixedDelay(0);
    }

    public Retry<R> fixedDelay(long fixedDelay) {
        retryLogic.setFixedDelay(fixedDelay);
        return this;
    }

    public Retry<R> delayFunction(Function<Long, Long> iterationToDelayFunction) {
        retryLogic.setDelayFunction(iterationToDelayFunction);
        return this;
    }

    public Retry<R> doOnError(Consumer<Exception> exceptionConsumer) {
        return doOnError((e, x) -> exceptionConsumer.accept(e));
    }

    public Retry<R> doOnError(BiConsumer<Exception, Long> exceptionAndIterationConsumer) {
        retryLogic.setFailureConsumer(exceptionAndIterationConsumer);
        return this;
    }
}
