package com.crypto.exchange.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crypto.exchange.core.PathData;
import com.crypto.exchange.core.TopCoin;
import com.crypto.exchange.service.GainCalculator;

@RestController
public class DataController {

	private static final Logger log = Logger.getLogger(DataController.class.getName());
	@Autowired
	private PathDataRepository dataRepository;

	@RequestMapping("/data")
	public List<PathData> greeting(
			@RequestParam(value = "wallets[]", defaultValue = "", required = false) String[] wallets) {
		log.info("Started: " + Arrays.toString(wallets));
		List<PathData> gains = GainCalculator.get().calculateGains(wallets);
		log.info("End");
		return gains;
	}

	@RequestMapping("/top")
	public Map<String, List<TopCoin>> greeting(
			@RequestParam(value = "wallets[]", defaultValue = "", required = false) String[] wallets,
			@RequestParam(value = "rank", defaultValue = "5", required = false) int rank) {
		log.info("Rank Started: " + Arrays.toString(wallets) + ", rank: " + rank);
		if (wallets == null || wallets.length == 0) {
			wallets = GainCalculator.get().getWalletNames();
		}
		List<String> pairs = getWalletPairs(wallets);
		Map<String, List<TopCoin>> coins = dataRepository.findTopCoins(pairs, rank).stream()
				.collect(Collectors.groupingBy(c -> c.getName()));
		log.info("Rank End");
		return coins;
	}

	private List<String> getWalletPairs(String[] wallets) {
		List<String> walletPairs = new ArrayList<>();
		for (String l : wallets) {
			for (String r : wallets) {
				if (!l.equals(r)) {
					walletPairs.add(l + "->" + r);
				}
			}
		}
		return walletPairs;
	}
}
