package org.yschwartz.try_utils.resource;

public class TestResource implements AutoCloseable {

    private boolean closed;

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            throw new Exception();
        }
        closed = true;
    }
}
