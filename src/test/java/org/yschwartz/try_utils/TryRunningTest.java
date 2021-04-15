package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;
import org.yschwartz.try_utils.model.Try;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;

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
        Try.of(this::throwA).catchAny().thenDo(t -> setBoolean1()).execute();
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
        Try.of(this::throwA).catchException(ExceptionA.class).thenDo(t -> setBoolean1())
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
