package org.yschwartz.try_utils.util;

import java.util.Arrays;

public class ExceptionUtils {
    private static final String PACKAGE_NAME = ExceptionUtils.class.getPackage().getName();
    private static final String PACKAGE_PREFIX = PACKAGE_NAME.substring(0, PACKAGE_NAME.lastIndexOf(".") + 1);

    public static void filterStackTrace(Exception e) {
        e.setStackTrace(Arrays.stream(e.getStackTrace())
                .filter(stackTraceElement -> !stackTraceElement.getClassName().startsWith(PACKAGE_PREFIX))
                .toArray(StackTraceElement[]::new));
    }

    public static RuntimeException getRuntimeException(Exception e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
}
