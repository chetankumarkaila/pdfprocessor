package com.pdf.response;

public class Coordinate{
	
	private String text;
	private double ix;
	private double iy;
	
	public double getIx() {
		return ix;
	}
	public void setIx(double ix) {
		this.ix = ix;
	}
	public double getIy() {
		return iy;
	}
	public void setIy(double iy) {
		this.iy = iy;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "Corr [text=" + text + ", ix=" + ix + ", iy=" + iy + "] \n";
	}

}