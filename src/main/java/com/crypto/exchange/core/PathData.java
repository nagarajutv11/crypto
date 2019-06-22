package com.crypto.exchange.core;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class PathData {
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private String fromWallet;
	private double fromFactor;
	private double toFactor;
	private String toWallet;
	@OneToMany(cascade = CascadeType.ALL)
	private List<Gain> gains;
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromWallet() {
		return fromWallet;
	}

	public void setFromWallet(String fromWallet) {
		this.fromWallet = fromWallet;
	}

	public double getFromFactor() {
		return fromFactor;
	}

	public void setFromFactor(double fromFactor) {
		this.fromFactor = fromFactor;
	}

	public double getToFactor() {
		return toFactor;
	}

	public void setToFactor(double toFactor) {
		this.toFactor = toFactor;
	}

	public String getToWallet() {
		return toWallet;
	}

	public void setToWallet(String toWallet) {
		this.toWallet = toWallet;
	}

	public List<Gain> getGains() {
		return gains;
	}

	public void setGains(List<Gain> gains) {
		this.gains = gains;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
