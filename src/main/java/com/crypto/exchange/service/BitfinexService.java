package com.crypto.exchange.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import com.crypto.exchange.core.Currency;

public class BitfinexService {

	private static BitfinexService INSTANCE;

	private BitfinexService() {

	}

	public static BitfinexService get() {
		if (INSTANCE == null) {
			INSTANCE = new BitfinexService();
		}
		return INSTANCE;
	}

	public static void main(String[] args) {
		List<Currency> all = BitfinexService.get().read();
		Map<String, List<Currency>> collect = all.stream().collect(Collectors.groupingBy(c -> c.baseCurrency));
		collect.forEach((k, e) -> {
			System.out.println(k + "->" + e.size());
		});
		System.out.println(all.size());
	}

	public List<Currency> read() {
		try {
			URLConnection connection = new URL("https://api-pub.bitfinex.com/v2/tickers?symbols=ALL").openConnection();
			connection.addRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
			InputStream in = connection.getInputStream();
			String data = IOUtils.toString(in);
			return readCurrencies(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private List<Currency> readCurrencies(String data) {
		JSONArray arr = new JSONArray(data);
		List<Currency> all = new ArrayList<>();
		arr.forEach(d -> all.add(readCurrency((JSONArray) d)));
		return all;
	}

	private Currency readCurrency(JSONArray obj) {
		Currency cur = new Currency();
		String substring = obj.getString(0);
		if (substring.startsWith("t")) {
			cur.baseCurrency = substring.substring(substring.length() - 3, substring.length());
			cur.code = substring.substring(1, substring.length() - 3);
		} else {
			cur.baseCurrency = "f";
			cur.code = substring.substring(1, substring.length());
		}
		cur.price = obj.getDouble(7);
		cur.wallet = "Bitfinex/" + cur.baseCurrency;
		return cur;
	}
}
