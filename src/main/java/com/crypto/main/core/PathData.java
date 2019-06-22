package com.crypto.main.core;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
//@Entity
public class PathData {
	//@Column
	public String name;
	//@OneToMany
	public Wallet from;
	//@OneToMany
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
