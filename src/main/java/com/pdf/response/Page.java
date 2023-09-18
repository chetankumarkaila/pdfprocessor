package com.pdf.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Page {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Coordinate> coordinates;
	private String bytes;
	private int pageNumber;

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public String getBytes() {
		return bytes;
	}

	public void setBytes(String bytes) {
		this.bytes = bytes;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public String toString() {
		return "Page [coordinates=" + coordinates + ", pageNumber=" + pageNumber + "]";
	}

}