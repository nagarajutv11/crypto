package com.crypto.exchange.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.crypto.exchange.core.Currency;

public class BinaceService {

	private static BinaceService INSTANCE;

	private BinaceService() {

	}

	public static BinaceService get() {
		if (INSTANCE == null) {
			INSTANCE = new BinaceService();
		}
		return INSTANCE;
	}

	public static void main(String[] args) {
		List<Currency> all = BinaceService.get().read();
		Map<String, List<Currency>> collect = all.stream().collect(Collectors.groupingBy(c -> c.baseCurrency));
		collect.forEach((k, e) -> {
			System.out.println(k + "->" + e.size());
		});
		System.out.println(all.size());
	}

	public List<Currency> read() {
		try {
			URLConnection connection = new URL("https://www.binance.com/en").openConnection();
			InputStream in = connection.getInputStream();
			String data = IOUtils.toString(in);
			return readCurrencies(data);
		} catch (Exception e) {
		}
		return new ArrayList<>();
	}

	private List<Currency> readCurrencies(String data) {
		data = data.split("\"tickers\":")[1];
		data = data.split(",\"tickSizes\"")[0];
		JSONObject obj = new JSONObject(data);
		Iterator<String> keys = obj.keys();
		List<Currency> all = new ArrayList<>();
		keys.forEachRemaining(k -> all.add(readCurrency(obj.getJSONObject(k))));
		return all;
	}

	private Currency readCurrency(JSONObject obj) {
		Currency cur = new Currency();
		cur.baseCurrency = obj.getString("qa");
		cur.code = obj.getString("ba");
		cur.price = obj.getDouble("c");
		cur.wallet = "Binance/" + cur.baseCurrency;
		return cur;
	}
}
