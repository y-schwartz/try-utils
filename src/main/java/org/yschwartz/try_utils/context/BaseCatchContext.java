package org.yschwartz.try_utils.context;

import java.util.function.Function;

abstract class BaseCatchContext<E extends Exception, R> {
    protected final BaseTryContext<R> tryContext;
    protected final Class<E> exceptionType;

    protected BaseCatchContext(BaseTryContext<R> tryContext, Class<E> exceptionType) {
        this.tryContext = tryContext;
        this.exceptionType = exceptionType;
    }

    protected void setReturnValueFunction(Function<E, R> returnValueFunction) {
        tryContext.getTryLogic().addCatchFunction(exceptionType, returnValueFunction);
    }

    protected void setThrowsException(Function<E, ? extends RuntimeException> exceptionMapper) {
        setReturnValueFunction(e -> {
            throw exceptionMapper.apply(e);
        });
    }
}
