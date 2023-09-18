package com.pdf.processor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pdf.common.PDFProcessorConstant.PDFProcessorMessageEnum;
import com.pdf.common.PDFProcessorUtils;
import com.pdf.exception.InvalidFileException;
import com.pdf.exception.InvalidImageFormatException;
import com.pdf.exception.InvalidImageGenerationMode;
import com.pdf.response.PDFProcessorResponse;

@ControllerAdvice
public class PDFProcessorExceptionHandler {

	
	@ExceptionHandler(value = InvalidImageFormatException.class)
	public ResponseEntity<PDFProcessorResponse> handleInvalidImageFormatException(InvalidImageFormatException exception) {
		return new ResponseEntity<>(PDFProcessorUtils.processorResponse(PDFProcessorMessageEnum.INVALIDIMAGEFORMAT, null), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = InvalidFileException.class)
	public ResponseEntity<PDFProcessorResponse> handleIinvalidFileException(InvalidFileException exception) {
		return new ResponseEntity<>(PDFProcessorUtils.processorResponse(PDFProcessorMessageEnum.FILENULL, null), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = IOException.class)
	public ResponseEntity<PDFProcessorResponse> handleIinternalServerError(IOException exception) {
		return new ResponseEntity<>(PDFProcessorUtils.processorResponse(PDFProcessorMessageEnum.INTERNALERROR, null), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = NumberFormatException.class)
	public ResponseEntity<PDFProcessorResponse> handleNumberFormatException(NumberFormatException exception) {
		return new ResponseEntity<>(PDFProcessorUtils.processorResponse(PDFProcessorMessageEnum.INCORRECTPARAM, null), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = InvalidImageGenerationMode.class)
	public ResponseEntity<PDFProcessorResponse> handleInvalidImageGenerationMode(InvalidImageGenerationMode exception) {
		return new ResponseEntity<>(PDFProcessorUtils.processorResponse(PDFProcessorMessageEnum.INVALIDMODE, null), HttpStatus.BAD_REQUEST);
	}
	
}
