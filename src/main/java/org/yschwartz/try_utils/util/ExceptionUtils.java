package org.yschwartz.try_utils.util;

import java.util.Objects;

public class ExceptionUtils {

    public static RuntimeException toRuntimeException(Exception e) {
        Objects.requireNonNull(e);
        return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
}
