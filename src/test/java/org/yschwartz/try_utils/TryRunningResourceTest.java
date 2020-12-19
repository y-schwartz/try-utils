package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;
import org.yschwartz.try_utils.resource.TestResource;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;
import static org.yschwartz.try_utils.TryUtils.tryRunningResource;

public class TryRunningResourceTest {
    private final AtomicBoolean boolean1 = new AtomicBoolean();
    private final AtomicBoolean boolean2 = new AtomicBoolean();
    private final TestResource testResource = new TestResource();

    @Test
    public void testTry() {
        tryRunningResource(testResource, this::setBoolean1).execute();
        assert boolean1.get();
        assert testResource.isClosed();
    }

    @Test
    public void testTryThrows() {
        try {
            tryRunningResource(testResource, this::throwA).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert testResource.isClosed();
    }

    @Test
    public void testTryThrowsFinally() {
        try {
            tryRunningResource(testResource, this::throwA).finallyRun(this::setBoolean2).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert boolean2.get();
        assert testResource.isClosed();
    }

    @Test
    public void testFinallyThrows() {
        try {
            tryRunningResource(testResource, this::throwA).finallyRun(this::throwB).execute();
            fail();
        } catch (ExceptionA | ExceptionB ignored) {
        }
        assert testResource.isClosed();
    }

    @Test
    public void testCloseThrows() {
        try {
            tryRunningResource(testResource, TestResource::close).finallyRun(this::setBoolean2).execute();
            fail();
        } catch (RuntimeException ignored) {
        }
        assert testResource.isClosed();
        assert boolean2.get();
    }

    private void setBoolean1(TestResource testResource) {
        boolean1.set(true);
    }

    private void setBoolean2() {
        boolean2.set(true);
    }

    private void throwA(TestResource testResource) {
        throw new ExceptionA();
    }

    private void throwB() {
        throw new ExceptionB();
    }
}
