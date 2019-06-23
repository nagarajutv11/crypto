package com.crypto.exchange.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crypto.exchange.core.Currency;
import com.crypto.exchange.core.PathData;
import com.crypto.exchange.service.GainCalculator;

@Component
public class ScheduledTasks {

	private static final Logger log = Logger.getLogger(ScheduledTasks.class.getName());

	@Autowired
	private PathDataRepository pathDataRepository;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Scheduled(cron = "0 0/5 * * * ?") // 5Min
	public void saveGains() {
		log.info("Schedule started: " + new Date().toString());
		String[] wallets = { "Koinex/INR", "Binance/USDT", "Bitfinex/USD" };
		List<PathData> gains = GainCalculator.get().calculateGains(wallets);
		Set<Currency> currencies = new HashSet<>();
		gains.stream().flatMap(g -> g.getGains().stream()).forEach(g -> {
			currencies.add(g.getLeft());
			currencies.add(g.getRight());
		});
		currencyRepository.saveAll(currencies);
		pathDataRepository.saveAll(gains);
		log.info("Schedule done   : " + new Date().toString());
	}
}
