package com.crypto.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.crypto.servlet.SimpleServlet;

public class Main {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(SimpleServlet.class, "/data");

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setWelcomeFiles(new String[] { "index.htm" });
		resource_handler.setResourceBase("src/main/webapp");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, handler });
		server.setHandler(handlers);
		server.start();
		server.join();
	}
}
