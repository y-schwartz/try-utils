package org.yschwartz.try_utils;

import org.junit.Test;
import org.yschwartz.try_utils.exception.ExceptionA;
import org.yschwartz.try_utils.model.Try;
import org.yschwartz.try_utils.resource.TestResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TryCallingResourceTest {
    private final TestResource testResource = new TestResource();

    @Test
    public void testTry() {
        String value1 = Try.of(testResource, this::getValue1).execute();
        assertEquals("value1", value1);
        assert testResource.isClosed();
    }

    @Test
    public void testTryThrows() {
        try {
            Try.of(testResource, this::throwA).execute();
            fail();
        } catch (ExceptionA ignored) {
        }
        assert testResource.isClosed();
    }

    private String getValue1(TestResource testResource) {
        return "value1";
    }

    private String throwA(TestResource testResource) {
        throw new ExceptionA();
    }
}
