package com.crypto.exchange.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crypto.exchange.core.Currency;
import com.crypto.exchange.core.Gain;
import com.crypto.exchange.core.PathData;
import com.crypto.exchange.core.Wallet;

public class GainCalculator {
	private static GainCalculator INSTANC;
	private Map<String, List<Currency>> collect;

	private GainCalculator() {
	}

	public static GainCalculator get() {
		if (INSTANC == null) {
			INSTANC = new GainCalculator();
		}
		return INSTANC;
	}

	/**
	 * Wallets: 22 Bitfinex/UST->11 Bitfinex/XLM->1 Bitfinex/ETH->108
	 * Koinex/TUSD->12 Bitfinex/f->25 Bitfinex/BTC->96 Binance/BTC->149
	 * Bitfinex/JPY->8 Bitfinex/GBP->8 Binance/USDC->23 Binance/XRP->2
	 * Binance/BNB->92 Bitfinex/USD->141 Bitfinex/EOS->3 Binance/ETH->130
	 * Binance/TUSD->25 Bitfinex/DAI->3 Binance/PAX->24 Bitfinex/EUR->10
	 * Koinex/INR->27 Binance/USDS->2 Binance/USDT->46 Currencies: 946
	 * 
	 * @param args
	 * @throws Exception
	 */
	public List<PathData> calculateGains() {
		List<Currency> all = getAllCurrencies();
		Map<String, List<Currency>> collect = all.stream().collect(Collectors.groupingBy(c -> c.wallet));
		GainCalculator cal = new GainCalculator();
		cal.collect = collect;
		// Koinex/INR,
		Wallet koinex = new Wallet();
		koinex.name = "Koinex/INR";
		koinex.converter = new Currency();
		koinex.converter.price = 0.01438;

		// Bitfinex/USD(NO),
		Wallet bitfinex = new Wallet();
		bitfinex.name = "Bitfinex/USD";
		bitfinex.converter = new Currency();
		bitfinex.converter.price = 1;

		// Bitfinex/BTC(Bitfinex/USD),
		Wallet bitfinexBtc = new Wallet();
		bitfinexBtc.name = "Bitfinex/BTC";
		bitfinexBtc.converter = find(collect.get("Bitfinex/USD"), "BTC");

		// Binance/BNB(Binance/USDT),
		Wallet binanceBnb = new Wallet();
		binanceBnb.name = "Binance/BNB";
		binanceBnb.converter = find(collect.get("Binance/USDT"), "BNB");

		// Binance/BTC(Binance/USDT)
		Wallet binanceBtc = new Wallet();
		binanceBtc.name = "Binance/BTC";
		binanceBtc.converter = find(collect.get("Binance/USDT"), "BTC");

		// Binance/USDT(NO)
		Wallet binanceUsd = new Wallet();
		binanceUsd.name = "Binance/USDT";
		binanceUsd.converter = new Currency();
		binanceUsd.converter.price = 1;

		List<PathData> gains = cal.calculate(koinex, bitfinex, bitfinexBtc, binanceBnb, binanceBtc, binanceUsd);
		return gains;
	}

	private List<PathData> calculate(Wallet... wallets) {
		List<PathData> gains = new ArrayList<>();
		for (Wallet l : wallets) {
			for (Wallet r : wallets) {
				if (l != r) {
					PathData data = new PathData();
					data.from = l;
					data.to = r;
					data.name = l + "->" + r;
					data.gains = calculate(l, r);
					gains.add(data);
				}
			}
		}
		return gains;
	}

	private List<Gain> calculate(Wallet lw, Wallet rw) {
		List<Gain> gains = new ArrayList<>();
		if (lw.converter == null || rw.converter == null) {
			return gains;
		}
		List<Currency> left = collect.get(lw.name);
		List<Currency> right = collect.get(rw.name);
		left.forEach(l -> {
			Currency r = find(right, l.code);
			if (r != null) {
				Gain gain = new Gain();
				gain.left = l;
				gain.right = r;
				gain.gain = (int) (((rw.to(r.price) - lw.to(l.price)) / lw.to(l.price)) * 10000) / 100.0;
				gains.add(gain);
			}
		});
		Collections.sort(gains);
		return gains;
	}

	private static List<Currency> getAllCurrencies2() throws Exception {
		try (InputStream input = new FileInputStream("C:\\Users\\nagar\\Desktop\\currencies.txt")) {
			String data = IOUtils.toString(input);
			JSONArray arr = new JSONArray(data);
			List<Currency> all = new ArrayList<>();
			arr.forEach(c -> {
				JSONObject o = (JSONObject) c;
				Currency cur = new Currency();
				cur.wallet = o.getString("wallet");
				cur.baseCurrency = o.getString("baseCurrency");
				cur.code = o.getString("code");
				cur.price = o.getDouble("price");
				all.add(cur);
			});
			return all;
		}
	}

	private static List<Currency> getAllCurrencies() {
		List<Currency> all = new ArrayList<>();
		all.addAll(BinaceService.get().read());
		all.addAll(BitfinexService.get().read());
		all.addAll(KoinexService.get().read());
		return all;
	}

	private static JSONObject createCurrencty(Currency c) {
		JSONObject obj = new JSONObject();
		obj.put("wallet", c.wallet);
		obj.put("baseCurrency", c.baseCurrency);
		obj.put("code", c.code);
		obj.put("price", c.price);
		return obj;
	}

	private static Currency find(List<Currency> right, String code) {
		for (Currency c : right) {
			if (c.code.equalsIgnoreCase(code)) {
				return c;
			}
		}
		return null;
	}
}
