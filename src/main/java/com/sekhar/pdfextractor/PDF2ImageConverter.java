package com.sekhar.pdfextractor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class PDF2ImageConverter {

	public void convertPDF2Images(String OUTPUT_DIR) {

		try (final PDDocument document = PDDocument.load(new File("PDF_Demo.pdf"))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				String fileName = OUTPUT_DIR + "image-" + page + ".png";
				ImageIOUtil.writeImage(bim, fileName, 300);

				System.out.println("Image..convertion started " + page);
			}
			document.close();
		} catch (IOException e) {
			System.err.println("Exception while trying to create pdf document - " + e);
		}

	}

	public String extractDataFromImage(String imagePath) {

		File imageFile = new File(imagePath);
		ITesseract instance = new Tesseract();
		final File f = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		instance.setDatapath(f.getPath());

		System.out.println(this.getClass().getPackage());

		try {
			String result = instance.doOCR(imageFile);
			return result;
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
			return "Error while reading image";
		}

	}

	public static void main(String arg[]) {

		PDF2ImageConverter obj = new PDF2ImageConverter();

		String OUTPUT_DIR = "E:\\sekhar\\image_dump\\";

		String imagePath = OUTPUT_DIR + "image-2.png";

		StringBuffer sb = new StringBuffer(obj.extractDataFromImage(imagePath));

		System.out.println(sb.toString());

	}

}
