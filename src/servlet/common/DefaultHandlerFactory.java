/*
 * Copyright 2011, Evan Summers
 * Apache Software License 2.0
 */
package servlet.common;

/**
 *
 * @author evan
 */
public class DefaultHandlerFactory implements HandlerFactory {

    Class<? extends HttpHandler> handlerType;

    public DefaultHandlerFactory(Class<? extends HttpHandler> handlerType) {
        this.handlerType = handlerType;
    }

    public HttpHandler newHandler() throws Exception {
        return handlerType.newInstance();
    }
}
