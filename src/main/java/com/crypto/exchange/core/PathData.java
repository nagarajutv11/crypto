package com.crypto.exchange.core;

import java.util.List;

public class PathData {
	public String name;
	public Wallet from;
	public Wallet to;
	public List<Gain> gains;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Wallet getFrom() {
		return from;
	}

	public void setFrom(Wallet from) {
		this.from = from;
	}

	public Wallet getTo() {
		return to;
	}

	public void setTo(Wallet to) {
		this.to = to;
	}

	public List<Gain> getGains() {
		return gains;
	}

	public void setGains(List<Gain> gains) {
		this.gains = gains;
	}

}
