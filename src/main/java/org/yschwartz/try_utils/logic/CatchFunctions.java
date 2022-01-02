package org.yschwartz.try_utils.logic;

import java.util.LinkedList;
import java.util.function.Function;

import static org.yschwartz.try_utils.util.ExceptionUtils.toRuntimeException;

public class CatchFunctions<R> extends LinkedList<CatchFunctions.CatchFunction<? extends Exception, R>> {

    <E extends Exception> void add(ExceptionMatcher<E> exceptionMatcher, Function<E, R> exceptionToValueFunction) {
        add(new CatchFunction<>(exceptionMatcher, exceptionToValueFunction));
    }

    R applyFunction(Exception e) {
        return stream().filter(catchFunction -> catchFunction.exceptionMatcher.matches(e)).findFirst()
                .orElseThrow(() -> toRuntimeException(e)).apply(e);
    }

    static class CatchFunction<E extends Exception, R> implements Function<E, R> {
        private final ExceptionMatcher<E> exceptionMatcher;
        private final Function<E, R> exceptionToValueFunction;

        CatchFunction(ExceptionMatcher<E> exceptionMatcher, Function<E, R> exceptionToValueFunction) {
            this.exceptionMatcher = exceptionMatcher;
            this.exceptionToValueFunction = exceptionToValueFunction;
        }

        @Override
        public R apply(Exception e) {
            return exceptionToValueFunction.apply(exceptionMatcher.match(e));
        }
    }
}
