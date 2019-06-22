package com.crypto.exchange.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crypto.exchange.core.PathData;
import com.crypto.exchange.service.GainCalculator;

@RestController
public class DataController {

	@RequestMapping("/data")
	public List<PathData> greeting(@RequestParam(value = "wallets[]", defaultValue = "") String[] wallets) {
		System.out.println("Started: " + Arrays.toString(wallets));
		List<PathData> gains = GainCalculator.get().calculateGains(wallets);
		System.out.println("End");
		return gains;
	}
}
