package org.yschwartz.try_utils.logic;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

public class CatchFunctions<R> extends LinkedList<CatchFunctions.CatchFunction<? extends Exception, R>> {

    CatchFunctions() {
        super();
    }

    <T extends Exception> void addCatchFunction(Class<T> exceptionType, Function<T, R> exceptionToValueFunction) {
        add(new CatchFunction<>(exceptionType, exceptionToValueFunction));
    }

    R applyFunction(Exception e) {
        return stream().filter(catchFunction -> catchFunction.isExceptionType(e)).findFirst()
                .orElseThrow(getRuntimeException(e)).applyFunction(e);
    }

    private static Supplier<RuntimeException> getRuntimeException(Exception e) {
        return () -> e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }

    static class CatchFunction<E extends Exception, R> {
        private final Class<E> exceptionType;
        private final Function<E, R> exceptionToValueFunction;

        private CatchFunction(Class<E> exceptionType, Function<E, R> exceptionToValueFunction) {
            this.exceptionType = exceptionType;
            this.exceptionToValueFunction = exceptionToValueFunction;
        }

        private boolean isExceptionType(Exception e) {
            return exceptionType.isInstance(e);
        }

        private R applyFunction(Exception e) {
            return exceptionToValueFunction.apply(exceptionType.cast(e));
        }
    }
}
