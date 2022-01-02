package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.exception.ExceptionB;
import org.yschwartz.try_utils.model.Try;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TryCallingTest {
    private final AtomicBoolean boolean1 = new AtomicBoolean();

    @Test
    public void testTry() {
        String value1 = Try.of(this::getValue1).execute();
        assertEquals("value1", value1);
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
        String value1 = Try.of(this::getValue1).finallyDo(this::setBoolean1).execute();
        assert boolean1.get();
        assertEquals("value1", value1);
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

    @Test
    public void testCatch() {
        String value1 = Try.of(this::getValue1).catchAny().thenReturn("value2").execute();
        assertEquals("value1", value1);
    }

    @Test
    public void testTryThrowsCatch() {
        String value1 = Try.of(this::throwA).catchAny().thenReturn(t -> getValue1()).execute();
        assertEquals("value1", value1);
    }

    @Test
    public void testTryThrowsExplicitCatch() {
        String value1 = Try.of(this::throwA).catchException(RuntimeException.class).thenReturn(t -> getValue1())
                .execute();
        assertEquals("value1", value1);
    }

    @Test(expected = ExceptionA.class)
    public void testTryThrowsNotCaught() {
        Try.of(this::throwA).catchException(ExceptionB.class).thenReturn(t -> getValue1()).execute();
    }

    @Test
    public void testDoubleCatchFirst() {
        String value1 = Try.of(this::throwA).catchException(ExceptionA.class).thenReturn(t -> getValue1())
                .catchException(RuntimeException.class).thenReturn("value2").execute();
        assertEquals("value1", value1);
    }

    @Test
    public void testDoubleCatchSecond() {
        String value2 = Try.of(this::throwA).catchException(ExceptionB.class).thenReturn(t -> getValue1())
                .catchException(RuntimeException.class).thenReturn("value2").execute();
        assertEquals("value2", value2);
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrow() {
        Try.of(this::throwA).catchAny().thenThrow(new ExceptionB()).execute();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrowMapper() {
        Try.of(this::throwA).catchAny().thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionB.class)
    public void testCatchAndThrowExplicit() {
        Try.of(this::throwA).catchException(RuntimeException.class).thenThrow(t -> new ExceptionB()).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testCatchAndThrowNotCaught() {
        Try.of(this::throwA).catchException(ExceptionB.class).thenThrow().execute();
    }

    @Test
    public void testCatchThen() {
        AtomicReference<Exception> exception = new AtomicReference<>(null);
        String actual = Try.of(this::throwA)
                .catchAny()
                .then(exception::set)
                .then(this::setBoolean1)
                .thenReturn("test")
                .execute();
        assert actual.equals("test");
        assert exception.get() != null;
        assert boolean1.get();
    }

    @Test
    public void testTryMap() {
        boolean actual = Try.of(() -> "test")
                .map(b -> b.equals("test"))
                .execute();
        assert actual;
    }

    @Test(expected = ExceptionA.class)
    public void testTryMapThrows() {
        Try.of(this::throwA).map(b -> null).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testTryMapThrowsSecond() {
        Try.of(() -> null).map(x -> throwA()).execute();
    }

    @Test
    public void testTryMapCatch() {
        boolean actual = Try.of(this::throwA)
                .catchAny().thenReturn("test")
                .map(b -> b.equals("test"))
                .execute();
        assert actual;
    }

    @Test
    public void testTryMapCatchSecond() {
        boolean actual = Try.of(this::throwA)
                .map(b -> false)
                .catchAny().thenReturn(true)
                .execute();
        assert actual;
    }

    @Test
    public void testTryFlatap() {
        boolean actual = Try.of(() -> "test").flatMap(b -> Try.of(() -> b.equals("test"))).execute();
        assert actual;
    }

    @Test
    public void testTryMConsume() {
        Try.of(() -> "test")
                .consume(b -> setBoolean1())
                .execute();
        assert boolean1.get();
    }

    @Test(expected = ExceptionA.class)
    public void testTryConsumeThrows() {
        Try.of(this::throwA).consume(b -> setBoolean1()).execute();
    }

    @Test(expected = ExceptionA.class)
    public void testTryConsumeThrowsSecond() {
        Try.of(() -> null).consume(x -> throwA()).execute();
    }

    @Test
    public void testTryConsumeCatch() {
        Try.of(this::throwA)
                .catchAny().thenReturn("test")
                .consume(b -> setBoolean1())
                .execute();
        assert boolean1.get();
    }

    @Test
    public void testTryConsumeCatchSecond() {
        Try.of(this::throwA)
                .consume(b -> setBoolean1())
                .catchAny().thenDo(x -> setBoolean1())
                .execute();
        assert boolean1.get();
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
