package com.pdf.common;

import org.apache.pdfbox.rendering.ImageType;

/**
 * 
 * @author Vinod Patel
 * 
 *         Class contains the constant declarations, used in the PDF Processor
 *
 */
public class PDFProcessorConstant {

	public final static String PDF_FORMAT = "PDF";
	public final static String GRAY_IMAGE = "1";
	public final static String BINARY_IMAGE = "2";
	public final static String RGB_IMAGE = "3";
	public final static String ARGB_IMAGE = "4";
	public final static String IMAGE_FORMATS= "png|jpeg|jpg";
	public final static String IMAGE_GENERATION_MODE="manual|auto";
	public final static String AUTO_IMAGE_GENERATION_MODE = "auto";
	
	public static enum PDFProcessorMessageEnum {

		SUCCESS(0, "PDF process successfully"), FILENULL(-1, "Provide PDF file to be process"), INTERNALERROR(-2, "Error encouter in PDF processing"), INCORRECTPARAM(-3, "Please provide correct parameter value"),INVALIDIMAGEFORMAT(-4, "Image format should be png, jpg or jpeg"),INVALIDMODE(-4, "mode should be manual or auto");

		private int code;
		private String message;

		PDFProcessorMessageEnum(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

	}
	
	public static enum ImageTypeEnum{
		
		GRAY(1000,550,ImageType.GRAY), 
		BINARY(1000,550,ImageType.BINARY),
		RGB(500,500,ImageType.RGB),
		ARGB(500,500,ImageType.ARGB);
		
		private int maxManualDpi;
		private int maxAutoDpi;
		private ImageType imageType;
		
		ImageTypeEnum(int maxManualDpi, int maxAutoDpi, ImageType imageType){
			this.maxManualDpi = maxManualDpi;
			this.maxAutoDpi = maxAutoDpi;
			this.imageType = imageType;
		}

		public ImageType getImageType() {
			return imageType;
		}

		public void setImageType(ImageType imageType) {
			this.imageType = imageType;
		}

		public int getMaxManualDpi() {
			return maxManualDpi;
		}

		public void setMaxManualDpi(int maxManualDpi) {
			this.maxManualDpi = maxManualDpi;
		}

		public int getMaxAutoDpi() {
			return maxAutoDpi;
		}

		public void setMaxAutoDpi(int maxAutoDpi) {
			this.maxAutoDpi = maxAutoDpi;
		}
		
	}

}
