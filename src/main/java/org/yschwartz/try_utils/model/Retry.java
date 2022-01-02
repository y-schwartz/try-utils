package org.yschwartz.try_utils.model;

import org.yschwartz.try_utils.functional.ExtendedRunnable;
import org.yschwartz.try_utils.logic.BaseTryLogic;
import org.yschwartz.try_utils.logic.RetryLogic;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Retry<R> extends Try<R> {

    private final RetryLogic<R> retryLogic;

    Retry(BaseTryLogic<R> tryLogic) {
        super(tryLogic);
        tryLogic.setRetries();
        retryLogic = tryLogic.getRetryLogic();
    }

    public Retry<R> filter(Class<? extends Exception> exceptionType) {
        return filter(exceptionType::isInstance);
    }

    public Retry<R> filter(Predicate<Exception> filter) {
        Objects.requireNonNull(filter);
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

    public Retry<R> delayFunction(Function<Long, Long> attemptNumToDelay) {
        Objects.requireNonNull(attemptNumToDelay);
        retryLogic.setDelayFunction(attemptNumToDelay);
        return this;
    }

    public Retry<R> onFailedAttempt(ExtendedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return onFailedAttempt(runnable.toConsumer());
    }

    public Retry<R> onFailedAttempt(Consumer<Exception> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer);
        return onFailedAttempt((e, x) -> exceptionConsumer.accept(e));
    }

    public Retry<R> onFailedAttempt(BiConsumer<Exception, Long> exceptionAndAttemptNumConsumer) {
        Objects.requireNonNull(exceptionAndAttemptNumConsumer);
        retryLogic.setFailureConsumer(exceptionAndAttemptNumConsumer);
        return this;
    }
}
