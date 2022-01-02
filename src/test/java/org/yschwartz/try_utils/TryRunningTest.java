package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;
import org.yschwartz.try_utils.model.Try;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;
import static org.yschwartz.try_utils.RetryTest.NANOS_IN_MILLI;

public class TryRunningTest {
    private final AtomicBoolean boolean1 = new AtomicBoolean(false);
    private final AtomicBoolean boolean2 = new AtomicBoolean(false);

    @Test
    public void testTry() {
        Try.of(this::setBoolean1).execute();
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testTryThrows() {
        Try.of(this::throwA).execute();
    }

    @Test(expected = RuntimeException.class)
    public void testTryThrowsChecked() {
        Try.of(this::throwChecked).execute();
    }

    @Test
    public void testFinally() {
        Try.of(this::doNothing).finallyDo(this::setBoolean1).execute();
        assert boolean1.get();
    }

    @Test
    public void testFinallyMultiple() {
        AtomicReference<LocalDateTime> first = new AtomicReference<>();
        AtomicReference<LocalDateTime> second = new AtomicReference<>();
        Try.of(this::doNothing).finallyDo(() -> first.set(LocalDateTime.now()))
                .finallyDo(() -> Try.of(() -> Thread.sleep(100)).execute())
                .finallyDo(() -> second.set(LocalDateTime.now())).execute();
        assert first.get().plusNanos(90 * NANOS_IN_MILLI).isBefore(second.get());
        assert first.get().plusNanos(110 * NANOS_IN_MILLI).isAfter(second.get());
    }

    @Test
    public void testTryThrowsFinally() {
        try {
            Try.of(this::throwA).finallyDo(this::setBoolean1).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testFinallyThrows() {
        Try.of(this::doNothing).finallyDo(this::throwA).execute();
    }

    @Test
    public void testCatch() {
        Try.of(this::doNothing).catchAny().thenDo(t -> setBoolean1()).execute();
        assert !boolean1.get();
    }

    @Test
    public void testTryThrowsCatch() {
        Try.of(this::throwA).catchAny().thenDo(this::setBoolean1).execute();
        assert boolean1.get();
    }

    @Test
    public void testTryThrowsExplicitCatch() {
        Try.of(this::throwA).catchException(RuntimeException.class).thenDo(t -> setBoolean1()).execute();
        assert boolean1.get();
    }

    @Test
    public void testTryThrowsNotCaught() {
        try {
            Try.of(this::throwA).catchException(ExceptionB.class).thenDo(t -> setBoolean1()).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert !boolean1.get();
    }

    @Test
    public void testDoubleCatchFirst() {
        Try.of(this::throwA).catchException(ExceptionA.class).thenDo(this::setBoolean1)
                .catchException(RuntimeException.class).thenDo(t -> setBoolean2()).execute();
        assert boolean1.get();
        assert !boolean2.get();
    }

    @Test
    public void testDoubleCatchSecond() {
        Try.of(this::throwA).catchException(ExceptionB.class).thenDo(t -> setBoolean1())
                .catchException(RuntimeException.class).thenDo(t -> setBoolean2()).execute();
        assert !boolean1.get();
        assert boolean2.get();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrow() {
        Try.of(this::throwA).catchAny().thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchReThrow() {
        Try.of(this::throwA).catchAny().thenThrow().execute();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrowExplicit() {
        Try.of(this::throwA).catchException(RuntimeException.class).thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchAndThrowNotCaught() {
        Try.of(this::throwA).catchException(ExceptionB.class).thenThrow(t -> t).execute();
    }

    @Test
    public void testCatchWithFilterMatching() {
        Try.of(this::throwA).catchAny().filter(e -> e instanceof ExceptionA).thenDo(e -> setBoolean1()).execute();
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchWithFilterNotMatching() {
        Try.of(this::throwA).catchAny().filter(e -> e instanceof ExceptionB).thenReturn().execute();
    }

    @Test
    public void testCatchWithDoubleFilterMatching() {
        Try.of(this::throwA).catchAny()
                .filter(Objects::nonNull)
                .filter(e -> e instanceof ExceptionA)
                .thenDo(e -> setBoolean1()).execute();
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchWithDoubleFilterNotMatching() {
        Try.of(this::throwA).catchAny()
                .filter(e -> e instanceof ExceptionB)
                .filter(e -> {
                    throw new RuntimeException();
                })
                .thenReturn().execute();
    }

    private void setBoolean1() {
        boolean1.set(true);
    }

    private void setBoolean2() {
        boolean2.set(true);
    }

    private void throwA() {
        throw new ExceptionA();
    }

    private void doNothing() {
    }

    private void throwChecked() throws Exception {
        throw new Exception();
    }
}
