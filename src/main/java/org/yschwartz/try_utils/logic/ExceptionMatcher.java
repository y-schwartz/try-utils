package org.yschwartz.try_utils.logic;

import java.util.Optional;
import java.util.function.Predicate;

public class ExceptionMatcher<E extends Exception> {
    private final Class<E> exceptionType;
    private Predicate<E> filter;

    public ExceptionMatcher(Class<E> exceptionType) {
        this.exceptionType = exceptionType;
    }

    public void addFilter(Predicate<E> filter) {
        this.filter = Optional.ofNullable(this.filter).map(p -> p.and(filter)).orElse(filter);
    }

    public boolean matches(Exception e) {
        return exceptionType.isInstance(e) && (filter == null || filter.test(exceptionType.cast(e)));
    }

    public E match(Exception e) {
        return exceptionType.cast(e);
    }
}
