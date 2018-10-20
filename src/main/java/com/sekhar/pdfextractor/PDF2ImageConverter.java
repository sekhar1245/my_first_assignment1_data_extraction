package com.sekhar.pdfextractor;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.sekhar.pdfextractor.extractText.ProcessRawData;

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

			}
			document.close();
			System.out.println("Image converation completed...!");
			
		} catch (IOException e) {
			System.err.println("Exception while trying to create pdf document - " + e);
		}
		finally {
			System.out.println(" finally block : Image converation completed...!");
		}

	}

	public StringBuffer extractDataFromImage(String imagePath) {

		File imageFile = new File(imagePath);
		ITesseract instance = new Tesseract();
		final File f = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		instance.setDatapath(f.getPath());

		// System.out.println(this.getClass().getPackage());
		StringBuffer result = new StringBuffer();
		try {

			result.append(instance.doOCR(imageFile).toString());

		} catch (TesseractException e) {
			System.err.println(e.getMessage());

		}
		return result;

	}

	public void saveRawData(StringBuffer rawText, String saveRawFile) {

		try {

			// String rawFileName="RawText.txt";
			@SuppressWarnings("resource")
			BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(saveRawFile)));
			bwr.write(rawText.toString());
			bwr.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processText() {

	}

	public static void main(String arg[]) throws Exception {

		PDF2ImageConverter obj = new PDF2ImageConverter();

		File f = new File(obj.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		// String OUTPUT_DIR = "E:\\sekhar\\image_dump\\";

		String OUTPUT_DIR = f.getPath() + "\\OCR\\";

		//obj.convertPDF2Images(OUTPUT_DIR);

		String imagePath = OUTPUT_DIR + "image-2.png";

		StringBuffer sb = obj.extractDataFromImage(imagePath);
		
		String rawFile = "RawText.txt";
		obj.saveRawData(sb, OUTPUT_DIR + rawFile);
		ProcessRawData.removeHeaderAndTrailerFromRawFile(OUTPUT_DIR + rawFile);
		System.out.println(OUTPUT_DIR + rawFile);

	
	}

}
