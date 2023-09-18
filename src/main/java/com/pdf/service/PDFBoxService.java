package com.pdf.service;

import static com.pdf.common.PDFProcessorConstant.AUTO_IMAGE_GENERATION_MODE;

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
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.pdf.Document;
import com.aspose.pdf.facades.PdfFileInfo;
import com.pdf.common.PDFProcessorConstant;
import com.pdf.common.PDFProcessorConstant.ImageTypeEnum;
import com.pdf.common.PDFProcessorUtils;
import com.pdf.response.Coordinate;
import com.pdf.response.Page;;

public class PDFBoxService extends PDFTextStripper {

	final static Logger logger = Logger.getLogger(PDFBoxService.class);

	private List<Coordinate> coordinates;
	private MultipartFile file;

	private double width_image;
	private double height_image;

	private double width_page;
	private double height_page;

	private boolean retrace;

	public PDFBoxService(MultipartFile file, List<Coordinate> coordinates, double width_image, double height_image,
			double width_page, double height_page, boolean retrace) throws IOException {
		this.coordinates = coordinates;
		this.file = file;

		this.width_image = width_image;
		this.height_image = height_image;

		this.width_page = width_page;
		this.height_page = height_page;

		this.retrace = retrace;
	}

	public PDFBoxService() throws IOException {
		this(null, null, 0.0, 0.0, 0.0, 0.0, false);
	}

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
		int n = 0;
		try {
			if (this.retrace) {
				parseWords(text, textPositions);
			} else {
				writeStringInternal(text, textPositions);
			}
		} catch (Exception e) {

		}

	}

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	protected void writeStringInternal(String text, List<TextPosition> textPositions) throws IOException {
		int n = 0;
		try {
			logger.debug("textPositions size = " + textPositions.size());
			n = Integer.parseInt(this.retrace ? text.trim() : text);

			if (n >= 0 && text != null && !text.trim().equals("") && !textPositions.isEmpty()) {
				TextPosition p = textPositions.get(text.trim().length() / 2);

				logger.debug("[FileName ] " + file.getName() + " = " + text + " [(X=" + p.getXDirAdj() + ",Y="
						+ p.getYDirAdj() + ") height=" + p.getHeightDir() + " width=" + p.getWidthDirAdj() + "]");

				double ix = (p.getXDirAdj() * width_image / width_page) + (p.getWidthDirAdj() / 2);
				double iy = (height_image - ((height_page - p.getYDirAdj()) * height_image / height_page))
						- (p.getHeightDir() / 2);

				Coordinate coordinate = new Coordinate();
				coordinate.setText(text);
				coordinate.setIx(ix);
				coordinate.setIy(iy);

				coordinates.add(coordinate);
			}
		} catch (Exception e) {

		}

	}

	private void parseWords(String text, List<TextPosition> textPositions) throws IOException {

		boolean start = false;
		int fromIndex = -1;
		int toIndex = -1;
		int i = 0;
		StringBuilder builder = new StringBuilder();
		TextPosition pp = null;
		TextPosition cp = null;
		float p = 0, c = 0;
		boolean withRange = false;

		for (i = 0; i < text.length(); i++) {
			cp = textPositions.get(i);

			if (pp != null) {
				p = pp.getX() + pp.getIndividualWidths()[0];
				c = cp.getX();
				p = Math.round(pp.getX() + pp.getIndividualWidths()[0]);
				c = Math.round(cp.getX());
			}

			withRange = (p == c) || (((p - c) >= -3) && ((p - c) <= 3));

			if (text.charAt(i) != ' ' && withRange) {
				start = true;
				if (fromIndex < 0) {
					fromIndex = i;
				}
				builder.append(text.charAt(i));
			}

			if ((start == true && text.charAt(i) == ' ') || !withRange) {
				start = false;
				toIndex = i;

				writeStringInternal(builder.toString(), textPositions.subList(fromIndex, toIndex));

				if (text.charAt(i) != ' ' && !withRange) {
					i--;
				}

				fromIndex = -1;
				toIndex = -1;
				pp = cp = null;
				p = c = 0;

				builder = new StringBuilder();

			}

			pp = cp;
		}

		if (start == true) {
			start = false;
			toIndex = i;

			textPositions.subList(fromIndex, toIndex);

			writeStringInternal(builder.toString(), textPositions.subList(fromIndex, toIndex));
			builder = new StringBuilder();

			fromIndex = -1;
			toIndex = -1;

			builder = new StringBuilder();
		}
	}

	public List<Page> generateImage(MultipartFile file, int isOnlyImage, int dpi, String imageType, boolean retrace,
			String imageFormat, String mode) throws IOException {

		PDDocument document = null;
		ByteArrayOutputStream baos = null;
		BufferedImage bim = null;
		Document doc = null;

		try {
			document = PDDocument.load(file.getInputStream());
			doc = new Document(file.getInputStream());
			PdfFileInfo pdfFileinfo = new PdfFileInfo(doc);
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			int pageCounter = 0;
			List<Page> pagesList = new ArrayList<Page>();

			ImageTypeEnum imgType = PDFProcessorUtils.getImageType(imageType);

			if (dpi > imgType.getMaxManualDpi()) {
				dpi = imgType.getMaxManualDpi();
			}
			
			logger.debug("Number of Pages = " + document.getNumberOfPages() + " Total Pages = "
					+ document.getPages().getCount() + " Total doc pages = " + doc.getPages().size());
			for (PDPage page : document.getPages()) {
				logger.debug("[Filename] " + file.getName() + " Processing page " + pageCounter);

				if (mode.equals(AUTO_IMAGE_GENERATION_MODE)) {
					int pageColorType = doc.getPages().get_Item(pageCounter + 1).getColorType();
					logger.debug("pageColorType type : " + pageColorType);
					switch (pageColorType) {
					/*case 2:
						imgType = PDFProcessorUtils.getImageType(PDFProcessorConstant.BINARY_IMAGE);
						break;
					case 1:
						imgType = PDFProcessorUtils.getImageType(PDFProcessorConstant.GRAY_IMAGE);
						break;*/
					case 0:
						imgType = PDFProcessorUtils.getImageType(PDFProcessorConstant.RGB_IMAGE);
						break;
					case 3:
						imgType = PDFProcessorUtils.getImageType(PDFProcessorConstant.GRAY_IMAGE);
						break;
					default:
						imgType = PDFProcessorUtils.getImageType(PDFProcessorConstant.GRAY_IMAGE);
						break;
					}

					if (dpi > imgType.getMaxAutoDpi()) {
						dpi = imgType.getMaxAutoDpi();
					}
				}

				List<Coordinate> coordinates = new ArrayList<Coordinate>();
				logger.debug("Image Type = " + imgType.getImageType() + " dpi = " + dpi);

				bim = pdfRenderer.renderImageWithDPI(pageCounter, dpi, imgType.getImageType());

				if (isOnlyImage == 0) {
					double width_image = bim.getWidth();
					double height_image = bim.getHeight();

					// double width_page = page.getArtBox().getWidth();
					// double height_page = page.getArtBox().getHeight();

					double width_page = pdfFileinfo.getPageWidth(pageCounter + 1);
					double height_page = pdfFileinfo.getPageHeight(pageCounter + 1);

					logger.debug("[ArtBox] width =" + page.getArtBox().getWidth() + " height ="
							+ page.getArtBox().getHeight());
					logger.debug("[Filename] " + file.getName() + " [width_image = " + width_image + ",	width_page ="
							+ width_page + "] [height_image =" + height_image + ", height_page =" + height_page + "]");

					PDFTextStripper stripper = new PDFBoxService(file, coordinates, width_image, height_image,
							width_page, height_page, retrace);
					stripper.setSortByPosition(false);
					stripper.setStartPage(0);
					stripper.setEndPage(3);

					Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
					stripper.writeText(document, dummy);
				}

				baos = new ByteArrayOutputStream();
				ImageIO.write(bim, imageFormat, baos);

				Page responsePage = new Page();
				responsePage.setBytes(Base64.encodeBase64String(baos.toByteArray()));
				responsePage.setPageNumber(pageCounter);
				responsePage.setCoordinates(isOnlyImage == 0 ? coordinates : null);

				pagesList.add(responsePage);

				pageCounter++;
				baos.flush();
				bim.flush();
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
			if (bim != null) {
				bim.flush();
			}
			if (doc != null) {
				doc.close();
			}
		}

	}

	/*
	 * private boolean isGreyScale(BufferedImage image,int width, int height) {
	 * boolean isGrey = true; outer: for (int i = 0; i < width; i++) { for (int j =
	 * 0; j < height; j++) { Color color = new Color(image.getRGB(i, j)); int r =
	 * color.getRed(); int g = color.getGreen(); int b = color.getBlue();
	 * 
	 * int avg = (int)Math.round((double)(r + g +b) / 3);
	 * 
	 * int rGap = Math.abs(r-avg); int gGap = Math.abs(g-avg); int bGap =
	 * Math.abs(b-avg);
	 * 
	 * if(rGap != 0 && gGap != 0 && bGap != 0 && findMaxGap(rGap,gGap,bGap) > 1) {
	 * logger.debug(r+" "+g+" "+b+" "+rGap+" "+gGap+" "+bGap); isGrey = false; break
	 * outer; } }
	 * 
	 * } return isGrey; }
	 * 
	 * private int findMaxGap(int r,int g,int b) { Set<Integer> pixels = new
	 * TreeSet<Integer>(); pixels.add(r); pixels.add(g); pixels.add(b);
	 * 
	 * if(pixels.size() > 2) {
	 * 
	 * List<Integer> pixelList = new ArrayList<Integer>(pixels);
	 * 
	 * int firstDiff = pixelList.get(2) - pixelList.get(1); int secondDiff =
	 * pixelList.get(1) - pixelList.get(0);
	 * 
	 * return Math.abs(firstDiff-secondDiff); }
	 * 
	 * return 0; }
	 * 
	 * private ImageTypeEnum findImageType(BufferedImage bim) { boolean isGreyScale
	 * = isGreyScale(bim,bim.getWidth(),bim.getHeight()); ImageTypeEnum imgType =
	 * isGreyScale ? ImageTypeEnum.GRAY : ImageTypeEnum.RGB; bim.flush(); return
	 * imgType; }
	 */

}