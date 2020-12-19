package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.yschwartz.try_utils.TryUtils.tryCalling;

public class TryCallingTest {
    private final AtomicBoolean boolean1 = new AtomicBoolean();

    @Test
    public void testTry() {
        String value1 = tryCalling(this::getValue1).execute();
        assertEquals("value1", value1);
    }

    @Test(expected = ExceptionA.class)
    public void testTryThrows() {
        tryCalling(this::throwA).execute();
    }

    @Test(expected = RuntimeException.class)
    public void testTryThrowsChecked() {
        tryCalling(this::throwChecked).execute();
    }

    @Test
    public void testFinally() {
        String value1 = tryCalling(this::getValue1).finallyRun(this::setBoolean1).execute();
        assert boolean1.get();
        assertEquals("value1", value1);
    }

    @Test
    public void testTryThrowsFinally() {
        try {
            tryCalling(this::throwA).finallyRun(this::setBoolean1).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert boolean1.get();
    }

    @Test
    public void testCatch() {
        String value1 = tryCalling(this::getValue1).catchAny().thenReturn("value2").execute();
        assertEquals("value1", value1);
    }

    @Test
    public void testTryThrowsCatch() {
        String value1 = tryCalling(this::throwA).catchAny().thenReturn(t -> getValue1()).execute();
        assertEquals("value1", value1);
    }

    @Test
    public void testTryThrowsExplicitCatch() {
        String value1 = tryCalling(this::throwA).catchException(RuntimeException.class).thenReturn(t -> getValue1())
                .execute();
        assertEquals("value1", value1);
    }

    @Test(expected = ExceptionA.class)
    public void testTryThrowsNotCaught() {
        tryCalling(this::throwA).catchException(ExceptionB.class).thenReturn(t -> getValue1()).execute();
    }

    @Test
    public void testDoubleCatchFirst() {
        String value1 = tryCalling(this::throwA).catchException(ExceptionA.class).thenReturn(t -> getValue1())
                .catchException(RuntimeException.class).thenReturn("value2").execute();
        assertEquals("value1", value1);
    }

    @Test
    public void testDoubleCatchSecond() {
        String value2 = tryCalling(this::throwA).catchException(ExceptionB.class).thenReturn(t -> getValue1())
                .catchException(RuntimeException.class).thenReturn("value2").execute();
        assertEquals("value2", value2);
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrow() {
        tryCalling(this::throwA).catchAny().thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrowExplicit() {
        tryCalling(this::throwA).catchException(RuntimeException.class).thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchAndThrowNotCaught() {
        tryCalling(this::throwA).catchException(ExceptionB.class).thenThrow(t -> t).execute();
    }

    private String getValue1() {
        return "value1";
    }

    private void setBoolean1() {
        boolean1.set(true);
    }

    private String throwA() {
        throw new ExceptionA();
    }

    private String throwChecked() throws Exception {
        throw new Exception();
    }
}
