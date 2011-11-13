/*
 * Copyright 2011, Evan Summers
 * Apache Software License 2.0
 */
package server.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import servlet.common.DefaultHandlerFactory;
import servlet.common.EchoHandler;
import servlet.common.HandlerServlet;

/**
 *
 * @author evan
 */
public class TomcatServer {

    public static void main(String[] args) throws LifecycleException, InterruptedException, ServletException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
        HttpServlet helloServlet = new HttpServlet() {

            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                Writer w = resp.getWriter();
                w.write("Hello, World!");
                w.flush();
            }
        };
        Tomcat.addServlet(ctx, "echo", new HandlerServlet(new DefaultHandlerFactory(EchoHandler.class))); 
        ctx.addServletMapping("/*", "echo");
        tomcat.start();
        tomcat.getServer().await();
    }
}
