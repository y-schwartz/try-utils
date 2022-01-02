package org.yschwartz.try_utils.logic;

import java.util.Objects;
import java.util.function.Predicate;

public class ExceptionMatcher<E extends Exception> {
    private final Class<E> exceptionType;
    private Predicate<E> filter = x -> true;

    public ExceptionMatcher(Class<E> exceptionType) {
        this.exceptionType = Objects.requireNonNull(exceptionType);
    }

    public void addFilter(Predicate<E> filter) {
        Objects.requireNonNull(filter);
        this.filter = this.filter.and(filter);
    }

    boolean matches(Exception e) {
        Objects.requireNonNull(e);
        return exceptionType.isInstance(e) && filter.test(match(e));
    }

    E match(Exception e) {
        Objects.requireNonNull(e);
        return exceptionType.cast(e);
    }
}
