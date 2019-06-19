package com.crypto.exchange.core;

public class Currency {
	public String wallet;
	public String baseCurrency;
	public String code;
	public double price;

	public String getWallet() {
		return wallet;
	}

	public void setWallet(String wallet) {
		this.wallet = wallet;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return code + "/" + baseCurrency;
	}

	public Currency revert() {
		Currency c = new Currency();
		c.price = 1 / price;
		return c;
	}
}
