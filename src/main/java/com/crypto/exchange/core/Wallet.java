package com.crypto.exchange.core;

public class Wallet {

	public String name;
	public Currency converter;

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

	@Override
	public String toString() {
		return name;
	}

	public double to(double price) {
		return converter.price * price;
	}
}
