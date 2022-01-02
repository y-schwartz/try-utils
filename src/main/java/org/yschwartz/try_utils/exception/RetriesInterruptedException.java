package org.yschwartz.try_utils.exception;

import java.text.MessageFormat;

public class RetriesInterruptedException extends RuntimeException {
    private final long attemptNumber;

    public RetriesInterruptedException(Throwable cause, long attemptNumber) {
        super(MessageFormat.format("Interrupted during retries. attempt number: {0}", attemptNumber), cause);
        this.attemptNumber = attemptNumber;
    }

    public long getAttemptNumber() {
        return attemptNumber;
    }
}
