package org.yschwartz.try_utils.util;

import java.util.Arrays;

public class StackTraceUtils {
    private static final String PACKAGE_NAME = StackTraceUtils.class.getPackage().getName();
    private static final String PACKAGE_PREFIX = PACKAGE_NAME.substring(0, PACKAGE_NAME.lastIndexOf(".") + 1);

    public static void setExceptionStackTrace(Exception e) {
        e.setStackTrace(Arrays.stream(e.getStackTrace())
                .filter(stackTraceElement -> !stackTraceElement.getClassName().startsWith(PACKAGE_PREFIX))
                .toArray(StackTraceElement[]::new));
    }
}
