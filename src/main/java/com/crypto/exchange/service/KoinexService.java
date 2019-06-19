package com.crypto.exchange.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.crypto.exchange.core.Currency;

public class KoinexService {

	private static KoinexService INSTANCE;

	private KoinexService() {

	}

	public static KoinexService get() {
		if (INSTANCE == null) {
			INSTANCE = new KoinexService();
		}
		return INSTANCE;
	}

	public static void main(String[] args) {
		List<Currency> all = KoinexService.get().read();
		System.out.println(all.size());
	}

	public List<Currency> read() {
		try {
			URLConnection connection = new URL("https://koinex.in/api/ticker").openConnection();
			InputStream in = connection.getInputStream();
			String data = IOUtils.toString(in);
			return readCurrencies(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private List<Currency> readCurrencies(String data) {
		JSONObject obj = new JSONObject(data);
		List<Currency> all = new ArrayList<>();
		all.addAll(readCurrencies(obj, "inr"));
		all.addAll(readCurrencies(obj, "true_usd"));
		return all;
	}

	private List<Currency> readCurrencies(JSONObject obj, String base) {
		JSONObject prices = obj.getJSONObject("prices").getJSONObject(base);
		JSONObject stats = obj.getJSONObject("stats").getJSONObject(base);
		Iterator<String> keys = prices.keys();
		double usdFactor = readUSDFactor();
		List<Currency> all = new ArrayList<>();
		keys.forEachRemaining(k -> all.add(readCurrency(usdFactor, prices.getString(k), stats.getJSONObject(k))));
		return all;
	}

	private double readUSDFactor() {
		return 1;
	}

	private Currency readCurrency(double factor, String price, JSONObject obj) {
		Currency cur = new Currency();
		cur.baseCurrency = obj.getString("baseCurrency");
		if (cur.baseCurrency.equals("inr")) {
			cur.baseCurrency = "INR";
		} else {
			cur.baseCurrency = "TUSD";
		}
		cur.code = obj.getString("currency_short_form");
		cur.price = obj.getDouble("last_traded_price") * factor;
		cur.wallet = "Koinex/" + cur.baseCurrency;
		return cur;
	}
}
