package org.yschwartz.try_utils.logic;

import org.yschwartz.try_utils.functional.ThrowingFunction;

public class TryWithResourceLogic<S extends AutoCloseable, R> extends BaseTryLogic<R> {
    private final S resource;
    private final ThrowingFunction<S, R> tryFunction;

    public TryWithResourceLogic(S resource, ThrowingFunction<S, R> tryFunction) {
        super();
        this.resource = resource;
        this.tryFunction = tryFunction;
    }

    @Override
    public R call() throws Exception {
        return tryFunction.apply(resource);
    }

    @Override
    public R execute() {
        return hasFinally() ? executeWithFinally() : executeWithoutFinally();
    }

    private R executeWithFinally() {
        try (resource) {
            return doTry();
        } catch (Exception e) {
            return doCatch(e);
        } finally {
            doFinally();
        }
    }

    private R executeWithoutFinally() {
        try (resource) {
            return doTry();
        } catch (Exception e) {
            return doCatch(e);
        }
    }
}
