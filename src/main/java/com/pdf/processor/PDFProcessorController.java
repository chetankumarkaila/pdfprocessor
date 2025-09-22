package com.pdf.processor;

import static com.pdf.common.PDFProcessorConstant.IMAGE_FORMATS;
import static com.pdf.common.PDFProcessorConstant.IMAGE_GENERATION_MODE;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.common.PDFProcessorConstant.PDFProcessorMessageEnum;
import com.pdf.common.PDFProcessorUtils;
import com.pdf.common.Validator;
import com.pdf.exception.InvalidFileException;
import com.pdf.exception.InvalidImageFormatException;
import com.pdf.exception.InvalidImageGenerationMode;
import com.pdf.response.PDFProcessorResponse;
import com.pdf.response.Page;
import com.pdf.service.PDFBoxService;;


@RestController
public class PDFProcessorController {

	final static Logger logger = Logger.getLogger(PDFProcessorController.class);

	@RequestMapping(value = { "/api/process" }, method = { RequestMethod.POST })
	public ResponseEntity<PDFProcessorResponse> process(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "isOnlyImage", required = false, defaultValue = "0") String isOnlyImage,
			@RequestParam(value = "dpi", required = false, defaultValue = "300") String dpi,
			@RequestParam(value = "imageType", required = false, defaultValue = "1") String imageType,
			@RequestParam(value = "retrace", required = false, defaultValue = "0") String retrace,
			@RequestParam(value = "imageFormat", required = false, defaultValue = "png") String imageFormat,
			@RequestParam(value = "mode", required = false, defaultValue = "auto") String mode)
			throws InvalidImageFormatException, InvalidFileException, IOException, InvalidImageGenerationMode {

		logger.debug("Request received for the file " + file.getName());
		logger.debug("DPI : " + dpi);
		logger.debug("imageType : " + imageType);
		logger.debug("Format : " + imageFormat);
		logger.debug("mode : " + mode);

		if (!imageFormat.matches(IMAGE_FORMATS)) {
			throw new InvalidImageFormatException();
		}

		if (!mode.matches(IMAGE_GENERATION_MODE)) {
			throw new InvalidImageGenerationMode();
		}

		if (file == null || !Validator.validateFile(FilenameUtils.getExtension(file.getOriginalFilename()))) {
			throw new InvalidFileException();
		}

		PDFProcessorResponse pdfProcessorResponse = null;
		try {

			List<Page> pages = new PDFBoxService().generateImage(file,
					PDFProcessorUtils.convertStringToInt(isOnlyImage), Integer.parseInt(dpi), imageType,
					retrace.trim().equals("1"), imageFormat, mode);
			pdfProcessorResponse = PDFProcessorUtils.processorResponse(PDFProcessorMessageEnum.SUCCESS, pages);
			return new ResponseEntity<>(pdfProcessorResponse, HttpStatus.OK);

		} catch (IOException io) {
			throw io;
		} catch (NumberFormatException nfe) {
			throw nfe;
		}

	}
}
