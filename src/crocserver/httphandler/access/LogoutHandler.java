/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers, 2010 iPay (Pty) Ltd
 * 
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.app.CrocCookie;
import crocserver.app.CrocCookieMeta;
import crocserver.app.JsonStrings;
import crocserver.httpserver.HttpExchangeInfo;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.util.Date;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Lists;

/**
 *
 * @author evans
 */
public class LogoutHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;

    public LogoutHandler(CrocApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath(), httpExchangeInfo.getParameterMap());
        try {
            handle();
        } catch (Exception e) {
            httpExchangeInfo.handleException(e);
        }
        httpExchange.close();
    }
        
    private void handle() throws Exception {
        CrocCookie cookie = new CrocCookie(httpExchangeInfo.getCookieMap());
        logger.info("cookie", cookie);
        AdminUser user = app.getStorage().getUserStorage().get(cookie.getEmail());
        user.setLogoutTime(new Date());
        app.getStorage().getUserStorage().updateLogout(user);
        httpExchangeInfo.clearCookie(Lists.toStringList(CrocCookieMeta.values()));
        httpExchangeInfo.sendResponse("text/json", true);
        String json = JsonStrings.buildJson(cookie.toMap());
        logger.info("json", json);
        httpExchangeInfo.getPrintStream().print(json);
    }    
}
