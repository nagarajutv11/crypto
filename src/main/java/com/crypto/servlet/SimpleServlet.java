package com.crypto.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.crypto.exchange.service.GainCalculator;
import com.crypto.main.core.PathData;

public class SimpleServlet extends HttpServlet {
	private static final long serialVersionUID = -4751096228274971485L;

	@Override
	protected void doGet(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {
		String[] wallets = reqest.getParameterValues("wallets[]");
		System.out.println("Started: " + Arrays.toString(wallets));
		List<PathData> gains = GainCalculator.get().calculateGains(wallets);
		System.out.println("End");
		JSONArray arr = new JSONArray(gains);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().println(arr.toString(3));
		System.out.println("Done");
	}
}