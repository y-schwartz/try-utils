package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;
import org.yschwartz.try_utils.exception.RetriesInterruptedException;
import org.yschwartz.try_utils.model.Try;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.fail;

public class RetryTest {

    public static final int NANOS_IN_MILLI = 1000000;

    @Test
    public void testRetry() {
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 2) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().execute();
        assert num.get() == 2;
    }

    @Test
    public void testRetryInterrupted() {
        AtomicLong num = new AtomicLong(0);
        Runnable runnable = () -> Try.of(this::throwA)
                .retry()
                .fixedDelay(100)
                .catchException(RetriesInterruptedException.class)
                .thenDo(e -> num.set(e.getAttemptNumber()))
                .execute();
        Thread thread = new Thread(runnable);
        Try.of(thread::start)
                .andThen(() -> Thread.sleep(150))
                .andThen(thread::interrupt)
                .andThen(thread::join)
                .execute();
        assert num.get() == 2;
    }

    @Test
    public void testRetryThrows() {
        final AtomicInteger num = new AtomicInteger();
        Try<Void> tryy = Try.of(() -> {
            if (num.get() != 5) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay();
        try {
            tryy.execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert num.get() == 3;
    }

    @Test
    public void testRetryFilterMatching() {
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 2) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().filter(e -> e instanceof ExceptionA).execute();
        assert num.get() == 2;
    }

    @Test
    public void testRetryFilterByTypeMatching() {
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 2) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().filter(ExceptionA.class).execute();
        assert num.get() == 2;
    }

    @Test
    public void testRetryFilterNotMatching() {
        final AtomicInteger num = new AtomicInteger();
        Try<Void> tryy = Try.of(() -> {
            if (num.get() != 5) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().filter(e -> e instanceof ExceptionB);
        try {
            tryy.execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert num.get() == 1;
    }

    @Test
    public void testRetryMaxAttempts() {
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 5) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().maxAttempts(10).execute();
        assert num.get() == 5;
    }

    @Test
    public void testRetryFixedDelay() {
        LocalDateTime begin = LocalDateTime.now();
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 2) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().fixedDelay(100).execute();
        LocalDateTime end = LocalDateTime.now();
        assert num.get() == 2;
        assert begin.plusNanos(100 * NANOS_IN_MILLI * 2).isBefore(end);
        assert begin.plusSeconds(100 * NANOS_IN_MILLI * 3).isAfter(end);
    }

    @Test
    public void testRetryDelayFunction() {
        LocalDateTime begin = LocalDateTime.now();
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 2) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().delayFunction(l -> l * 100).execute();
        LocalDateTime end = LocalDateTime.now();
        assert num.get() == 2;
        assert begin.plusNanos(300 * NANOS_IN_MILLI).isBefore(end);
        assert begin.plusSeconds(400 * NANOS_IN_MILLI).isAfter(end);
    }

    @Test
    public void testRetryDoOnError() {
        final AtomicInteger num1 = new AtomicInteger();
        final AtomicInteger num2 = new AtomicInteger();
        Try.of(() -> {
            if (num1.get() != 2) {
                num1.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().onFailedAttempt(num2::incrementAndGet).execute();
        assert num1.get() == 2;
        assert num2.get() == 2;
    }

    @Test
    public void testRetryDoOnErrorExceptionConsumer() {
        final AtomicInteger num1 = new AtomicInteger();
        final AtomicInteger num2 = new AtomicInteger();
        Try.of(() -> {
            if (num1.get() != 2) {
                num1.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().onFailedAttempt(e -> num2.incrementAndGet()).execute();
        assert num1.get() == 2;
        assert num2.get() == 2;
    }

    @Test
    public void testRetryDoOnErrorBiConsumer() {
        final AtomicInteger num1 = new AtomicInteger();
        final AtomicLong num2 = new AtomicLong();
        Try.of(() -> {
            if (num1.get() != 2) {
                num1.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().onFailedAttempt((e, l) -> num2.addAndGet(l)).execute();
        assert num1.get() == 2;
        assert num2.get() == 3;
    }

    @Test
    public void testRetryMap() {
        final AtomicInteger num = new AtomicInteger();
        Try.of(() -> {
            if (num.get() != 8) {
                num.incrementAndGet();
                throw new ExceptionA();
            }
        }).retry().noDelay().map(x -> {
            if (num.get() != 8) {
                throw new ExceptionA();
            }
            return null;
        }).retry().noDelay().execute();
        assert num.get() == 8;
    }

    private void throwA() {
        throw new ExceptionA();
    }
}
