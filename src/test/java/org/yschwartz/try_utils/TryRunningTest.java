package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;
import static org.yschwartz.try_utils.TryUtils.tryRunning;

public class TryRunningTest {
    private final AtomicBoolean boolean1 = new AtomicBoolean();
    private final AtomicBoolean boolean2 = new AtomicBoolean();

    @Test
    public void testTry() {
        tryRunning(this::setBoolean1).execute();
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testTryThrows() {
        tryRunning(this::throwA).execute();
    }

    @Test(expected = RuntimeException.class)
    public void testTryThrowsChecked() {
        tryRunning(this::throwChecked).execute();
    }

    @Test
    public void testFinally() {
        tryRunning(this::doNothing).finallyRun(this::setBoolean1).execute();
        assert boolean1.get();
    }

    @Test
    public void testTryThrowsFinally() {
        try {
            tryRunning(this::throwA).finallyRun(this::setBoolean1).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testFinallyThrows() {
        tryRunning(this::doNothing).finallyRun(this::throwA).execute();
    }

    @Test
    public void testCatch() {
        tryRunning(this::doNothing).catchAny().thenDo(t -> setBoolean1()).execute();
        assert !boolean1.get();
    }

    @Test
    public void testTryThrowsCatch() {
        tryRunning(this::throwA).catchAny().thenDo(t -> setBoolean1()).execute();
        assert boolean1.get();
    }

    @Test
    public void testTryThrowsExplicitCatch() {
        tryRunning(this::throwA).catchException(RuntimeException.class).thenDo(t -> setBoolean1()).execute();
        assert boolean1.get();
    }

    @Test
    public void testTryThrowsNotCaught() {
        try {
            tryRunning(this::throwA).catchException(ExceptionB.class).thenDo(t -> setBoolean1()).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert !boolean1.get();
    }

    @Test
    public void testDoubleCatchFirst() {
        tryRunning(this::throwA).catchException(ExceptionA.class).thenDo(t -> setBoolean1())
                .catchException(RuntimeException.class).thenDo(t -> setBoolean2()).execute();
        assert boolean1.get();
        assert !boolean2.get();
    }

    @Test
    public void testDoubleCatchSecond() {
        tryRunning(this::throwA).catchException(ExceptionB.class).thenDo(t -> setBoolean1())
                .catchException(RuntimeException.class).thenDo(t -> setBoolean2()).execute();
        assert !boolean1.get();
        assert boolean2.get();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrow() {
        tryRunning(this::throwA).catchAny().thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrowExplicit() {
        tryRunning(this::throwA).catchException(RuntimeException.class).thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchAndThrowNotCaught() {
        tryRunning(this::throwA).catchException(ExceptionB.class).thenThrow(t -> t).execute();
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
