package com.pdf.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
//import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.common.PDFProcessorUtils;
import com.pdf.response.Coordinate;
import com.pdf.response.Page;

public class PDFBoxService2 extends PDFTextStripper {

	final static Logger logger = Logger.getLogger(PDFBoxService2.class);

	private List<Coordinate> coordinates;
	private MultipartFile file;

	private double width_image;
	private double height_image;

	private double width_page;
	private double height_page;

	public PDFBoxService2(MultipartFile file, List<Coordinate> coordinates, double width_image, double height_image, double width_page, double height_page) throws IOException {
		this.coordinates = coordinates;
		this.file = file;

		this.width_image = width_image;
		this.height_image = height_image;

		this.width_page = width_page;
		this.height_page = height_page;
	}

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
		int n = 0;

		try {
			n = Integer.parseInt(text);

			if (n >= 0 && text != null && !text.trim().equals("") && !textPositions.isEmpty()) {
				TextPosition p = textPositions.get(text.trim().length() / 2);

				logger.debug("[FileName ] " + file.getName() + " = " + text + " [(X=" + p.getXDirAdj() + ",Y=" + p.getYDirAdj() + ") height=" + p.getHeightDir() + " width=" + p.getWidthDirAdj() + "]");

				double ix = (p.getXDirAdj() * width_image / width_page) + (p.getWidthDirAdj() / 2);
				double iy = (height_image - ((height_page - p.getYDirAdj()) * height_image / height_page)) - (p.getHeightDir() / 2);

				Coordinate coordinate = new Coordinate();
				coordinate.setText(text);
				coordinate.setIx(ix);
				coordinate.setIy(iy);

				coordinates.add(coordinate);
			}
		} catch (Exception e) {

		}
	}

	public static List<Page> generateImage(MultipartFile file, int isOnlyImage, int dpi, String imageType) throws IOException {

		PDDocument document = null;
		ByteArrayOutputStream baos = null;
		try {
			document = PDDocument.load(file.getInputStream());
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			int pageCounter = 0;
			List<Page> pagesList = new ArrayList<Page>();

			for (PDPage page : document.getPages()) {
				logger.debug("[Filename] " + file.getName() + " Processing page " + pageCounter);

				List<Coordinate> coordinates = new ArrayList<Coordinate>();
				ImageType imgType = ImageType.GRAY;
				//ImageType imgType = PDFProcessorUtils.getImageType(imageType);
				
				logger.debug("Image Type for the actual rendering : " + imageType);
				
				BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, dpi, imgType);

				if (isOnlyImage == 0) {
					double width_image = bim.getWidth();
					double height_image = bim.getHeight();

					double width_page = page.getArtBox().getWidth();
					double height_page = page.getArtBox().getHeight();

					logger.debug("[Filename] " + file.getName() + " [width_image = " + width_image + ",	width_page =" + width_page + "] [height_image =" + height_image + ", height_page =" + height_page + "]");

					PDFTextStripper stripper = new PDFBoxService2(file, coordinates, width_image, height_image, width_page, height_page);
					stripper.setSortByPosition(false);
					stripper.setStartPage(pageCounter);
					stripper.setEndPage(pageCounter + 1);

					Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
					stripper.writeText(document, dummy);
				}

				baos = new ByteArrayOutputStream();
				ImageIO.write(bim, "png", baos);				

				Page responsePage = new Page();
				responsePage.setBytes(Base64.encodeBase64String(baos.toByteArray()));
				responsePage.setPageNumber(pageCounter);
				responsePage.setCoordinates(isOnlyImage == 0 ? coordinates : null);

				pagesList.add(responsePage);

				pageCounter++;
				baos.flush();
			}

			return pagesList;

		} catch (IOException e) {
			throw new IOException();
		} finally {
			if (document != null) {
				document.close();
			}
			if (baos != null) {
				baos.close();
			}
		}

	}

}