package com.pdf.common;

import java.util.List;

import org.apache.pdfbox.rendering.ImageType;

import com.pdf.common.PDFProcessorConstant.ImageTypeEnum;
import com.pdf.common.PDFProcessorConstant.PDFProcessorMessageEnum;
import com.pdf.response.PDFProcessorResponse;
import com.pdf.response.Page;

/**
 * Utility class for the image generation from the PDF file.
 * 
 * @author Vinod Patel
 *
 */
public class PDFProcessorUtils {

	public static int convertStringToInt(String isOnlyImage) {
		try {
			return Integer.parseInt(isOnlyImage);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	public static ImageTypeEnum getImageType(String type) {

		ImageTypeEnum imageType = ImageTypeEnum.GRAY;

		switch (type) {
		case PDFProcessorConstant.BINARY_IMAGE:
			imageType = ImageTypeEnum.BINARY;
			break;
		case PDFProcessorConstant.RGB_IMAGE:
			imageType = ImageTypeEnum.RGB;
			break;
		case PDFProcessorConstant.ARGB_IMAGE:
			imageType = ImageTypeEnum.ARGB;
			break;
		default:
			imageType = ImageTypeEnum.GRAY;
			break;
		}
		return imageType;
	}

	public static PDFProcessorResponse processorResponse(PDFProcessorMessageEnum responseEnum, List<Page> pages) {
		PDFProcessorResponse pdfProcessorResponse = new PDFProcessorResponse();
		pdfProcessorResponse.setCode(responseEnum.getCode());
		pdfProcessorResponse.setMessage(responseEnum.getMessage());
		pdfProcessorResponse.setPages(pages);
		return pdfProcessorResponse;
	}

}
