package com.crypto.exchange.core;

public class Gain implements Comparable<Gain> {
	public Currency left;
	public Currency right;
	public double gain;

	public Currency getLeft() {
		return left;
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
		return left.code + ":" + gain + "%";
	}
}
