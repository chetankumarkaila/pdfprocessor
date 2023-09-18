package com.pdf.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PDFProcessorResponse{

	private int code;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Page> pages;
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Page> getPages() {
		return pages;
	}
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	@Override
	public String toString() {
		return "PDFProcessorResponse [code=" + code + ", message=" + message + ", pages=" + pages + "]";
	}
		
}