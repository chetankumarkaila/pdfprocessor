package com.pdf.common;


public class Validator {

	/**
	 * Validate the file for the proper PDF format.
	 * 
	 * @param ext
	 * @return
	 */
	public static boolean validateFile(String ext) {
		if (ext == null || !ext.toUpperCase().endsWith(PDFProcessorConstant.PDF_FORMAT)) {
			return false;
		}
		return true;
	}

}
