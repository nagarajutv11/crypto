package com.crypto.exchange.core;

import java.util.function.Supplier;

public class Wallet {

	private String name;
	private double factor;
	public Supplier<Double> converterProvider;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getFactor() {
		return factor;
	}

	public void prepare() {
		factor = converterProvider.get();
	}

	@Override
	public String toString() {
		return name;
	}

	public double to(double price) {
		return factor * price;
	}
}
