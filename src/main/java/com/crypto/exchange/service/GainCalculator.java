package com.crypto.exchange.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
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
		koinex.setName("Koinex/INR");
		koinex.converterProvider = () -> getINRinUSD();

		wallets.put(koinex.getName(), koinex);

		// Bitfinex/USD(NO),
		Wallet bitfinex = new Wallet();
		bitfinex.setName("Bitfinex/USD");
		bitfinex.converterProvider = () -> 1d;
		wallets.put(bitfinex.getName(), bitfinex);

		// Bitfinex/BTC(Bitfinex/USD),
		Wallet bitfinexBtc = new Wallet();
		bitfinexBtc.setName("Bitfinex/BTC");
		bitfinexBtc.converterProvider = () -> find(collect.get("Bitfinex/USD"), "BTC").getPrice();
		wallets.put(bitfinexBtc.getName(), bitfinexBtc);

		// Binance/BNB(Binance/USDT),
		Wallet binanceBnb = new Wallet();
		binanceBnb.setName("Binance/BNB");
		binanceBnb.converterProvider = () -> find(collect.get("Binance/USDT"), "BNB").getPrice();
		wallets.put(binanceBnb.getName(), binanceBnb);

		// Binance/BTC(Binance/USDT)
		Wallet binanceBtc = new Wallet();
		binanceBtc.setName("Binance/BTC");
		binanceBtc.converterProvider = () -> find(collect.get("Binance/USDT"), "BTC").getPrice();
		wallets.put(binanceBtc.getName(), binanceBtc);

		// Binance/USDT(NO)
		Wallet binanceUsd = new Wallet();
		binanceUsd.setName("Binance/USDT");
		binanceUsd.converterProvider = () -> 1d;
		wallets.put(binanceUsd.getName(), binanceUsd);
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

	public String[] getWalletNames() {
		return wallets.keySet().toArray(new String[0]);
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
	public synchronized List<PathData> calculateGains(String... walletNames) {
		if (walletNames == null || walletNames.length == 0) {
			walletNames = getWalletNames();
		}
		List<Currency> all = getAllCurrencies();
		this.collect = all.stream().collect(Collectors.groupingBy(c -> c.getWallet()));
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
					data.setFromWallet(l.getName());
					data.setFromFactor(l.getFactor());
					data.setToWallet(r.getName());
					data.setToFactor(r.getFactor());
					data.setName(l + "->" + r);
					data.setGains(calculate(l, r));
					data.setDate(new Date());
					gains.add(data);
				}
			}
		}
		return gains;
	}

	private List<Gain> calculate(Wallet lw, Wallet rw) {
		List<Gain> gains = new ArrayList<>();
		List<Currency> left = collect.get(lw.getName());
		List<Currency> right = collect.get(rw.getName());
		left.forEach(l -> {
			Currency r = find(right, l.getCode());
			if (r != null) {
				Gain gain = new Gain();
				gain.setLeft(l);
				gain.setLeftNom(lw.to(l.getPrice()));
				gain.setRight(r);
				gain.setRightNom(rw.to(r.getPrice()));
				gain.setGain(
						(long) (((gain.getRightNom() - gain.getLeftNom()) / gain.getLeftNom()) * 100000000) / 1000000.0);
				gain.setDate(new Date());
				gains.add(gain);
			}
		});
		Collections.sort(gains);
		return gains;
	}

	private List<Currency> getAllCurrencies() {
		List<Currency> all = new ArrayList<>();
		all.addAll(BinaceService.get().read());
		all.addAll(BitfinexService.get().read());
		all.addAll(KoinexService.get().read());
		return all;
	}

	private Currency find(List<Currency> right, String code) {
		for (Currency c : right) {
			if (c.getCode().equalsIgnoreCase(code)) {
				return c;
			}
		}
		return null;
	}
}
