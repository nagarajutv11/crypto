package com.crypto.exchange.core;

import java.util.function.Supplier;

public class Wallet {

	public String name;
	public Currency converter;
	public Supplier<Currency> converterProvider;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Currency getConverter() {
		return converter;
	}

	public void setConverter(Currency converter) {
		this.converter = converter;
	}

	public void prepare() {
		converter = converterProvider.get();
	}

	@Override
	public String toString() {
		return name;
	}

	public double to(double price) {
		return converter.price * price;
	}
}
