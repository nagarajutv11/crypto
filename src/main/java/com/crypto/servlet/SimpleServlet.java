package com.crypto.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.crypto.exchange.core.PathData;
import com.crypto.exchange.service.GainCalculator;

public class SimpleServlet extends HttpServlet {
	private static final long serialVersionUID = -4751096228274971485L;

	@Override
	protected void doGet(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Started");
		List<PathData> gains = GainCalculator.get().calculateGains();
		System.out.println("End");
		JSONArray arr = new JSONArray(gains);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().println(arr.toString(3));
		System.out.println("Done");
	}
}