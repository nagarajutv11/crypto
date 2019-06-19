package com.crypto.exchange.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private Map<String, Wallet> wallets;

	private GainCalculator() {
		wallets = new HashMap<>();

		// Koinex/INR,
		Wallet koinex = new Wallet();
		koinex.name = "Koinex/INR";
		koinex.converterProvider = () -> {
			Currency c = new Currency();
			c.price = getINRinUSD();
			return c;
		};

		wallets.put(koinex.name, koinex);

		// Bitfinex/USD(NO),
		Wallet bitfinex = new Wallet();
		bitfinex.name = "Bitfinex/USD";
		bitfinex.converter = new Currency();
		bitfinex.converterProvider = () -> {
			Currency c = new Currency();
			c.price = 1;
			return c;
		};
		wallets.put(bitfinex.name, bitfinex);

		// Bitfinex/BTC(Bitfinex/USD),
		Wallet bitfinexBtc = new Wallet();
		bitfinexBtc.name = "Bitfinex/BTC";
		bitfinexBtc.converterProvider = () -> find(collect.get("Bitfinex/USD"), "BTC");
		wallets.put(bitfinexBtc.name, bitfinexBtc);

		// Binance/BNB(Binance/USDT),
		Wallet binanceBnb = new Wallet();
		binanceBnb.name = "Binance/BNB";
		binanceBnb.converterProvider = () -> find(collect.get("Binance/USDT"), "BNB");
		wallets.put(binanceBnb.name, binanceBnb);

		// Binance/BTC(Binance/USDT)
		Wallet binanceBtc = new Wallet();
		binanceBtc.name = "Binance/BTC";
		binanceBtc.converterProvider = () -> find(collect.get("Binance/USDT"), "BTC");
		wallets.put(binanceBtc.name, binanceBtc);

		// Binance/USDT(NO)
		Wallet binanceUsd = new Wallet();
		binanceUsd.name = "Binance/USDT";
		binanceUsd.converterProvider = () -> {
			Currency c = new Currency();
			c.price = 1;
			return c;
		};
		wallets.put(binanceUsd.name, binanceUsd);
	}

	private double getINRinUSD() {
		try {
			URLConnection connection = new URL(
					"https://free.currconv.com/api/v7/convert?q=INR_USD&compact=ultra&apiKey=670ae96d027a3b5f6b52")
							.openConnection();
			InputStream in = connection.getInputStream();
			String data = IOUtils.toString(in);
			return new JSONObject(data).getDouble("INR_USD");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
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
	public List<PathData> calculateGains(String... walletNames) {
		if (walletNames == null || walletNames.length == 0) {
			walletNames = wallets.keySet().toArray(new String[0]);
		}
		List<Currency> all = getAllCurrencies();
		this.collect = all.stream().collect(Collectors.groupingBy(c -> c.wallet));
		Wallet[] selectedWallets = new Wallet[walletNames.length];
		int i = 0;
		for (String s : walletNames) {
			Wallet wallet = wallets.get(s);
			if (wallet != null) {
				wallet.prepare();
				selectedWallets[i++] = wallet;
			}
		}
		List<PathData> gains = calculate(selectedWallets);
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
				gain.leftNom = lw.to(l.price);
				gain.right = r;
				gain.rightNom = rw.to(r.price);
				gain.gain = (int) (((gain.rightNom - gain.leftNom) / gain.leftNom) * 10000) / 100.0;
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
