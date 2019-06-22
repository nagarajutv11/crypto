package com.crypto.exchange.core;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Gain implements Comparable<Gain> {
	@Id
	@GeneratedValue
	private long id;
	@ManyToOne
	private Currency left;
	private double leftNom;
	@ManyToOne
	private Currency right;
	private double rightNom;
	private double gain;
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Currency getLeft() {
		return left;
	}

	public double getLeftNom() {
		return leftNom;
	}

	public void setLeftNom(double leftNom) {
		this.leftNom = leftNom;
	}

	public double getRightNom() {
		return rightNom;
	}

	public void setRightNom(double rightNom) {
		this.rightNom = rightNom;
	}

	public void setLeft(Currency left) {
		this.left = left;
	}

	public Currency getRight() {
		return right;
	}

	public void setRight(Currency right) {
		this.right = right;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	@Override
	public int compareTo(Gain o) {
		return Double.compare(o.gain, gain);
	}

	@Override
	public String toString() {
		return left.getCode() + ":" + gain + "%";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
