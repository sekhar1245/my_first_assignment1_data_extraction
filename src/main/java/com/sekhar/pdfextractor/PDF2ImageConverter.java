package com.sekhar.pdfextractor;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.sekhar.pdfextractor.beans.Voter;
import com.sekhar.pdfextractor.exporttoexcel.ExportExcel;
import com.sekhar.pdfextractor.extractText.ProcessRawData;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class PDF2ImageConverter {

	

	public List<String> convertPDF2Images(String OUTPUT_DIR) throws IOException {
		
		 List<String> imageNamesList = new ArrayList<String>();
		 PDDocument document = null;
		 PDFRenderer pdfRenderer;
		
		try {
			
			 document = PDDocument.load(new File("PDF_Demo.pdf"));
			 pdfRenderer = new PDFRenderer(document);
			
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				String fileName = OUTPUT_DIR + "image-" + page + ".png";
				imageNamesList.add(fileName);
				ImageIOUtil.writeImage(bim, fileName, 300);

			}
			
			System.out.println("Image converation completed...!");
			
			document.close();

		} catch (IOException e) {
			System.err.println("Exception while trying to create pdf document - " + e);
		} finally {
			document.close();
			
			
			System.out.println(" finally block : Image converation completed...!");
		}
		return imageNamesList;
		
	

	}
	
	
public List<String> convertPDF2Images(String OUTPUT_DIR,String PDF_Fila_Name) {
		
	List<String> imageNamesList = new ArrayList<String>();

		try {
			
			PDDocument document = PDDocument.load(new File(PDF_Fila_Name));
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				String fileName = OUTPUT_DIR + "image-" + page + ".png";
				ImageIOUtil.writeImage(bim, fileName, 300);

			}
			
			System.out.println("Image converation completed...!");

		} catch (IOException e) {
			System.err.println("Exception while trying to create pdf document - " + e);
		} finally {
			System.out.println(" finally block : Image converation completed...!");
		}
		
		return imageNamesList;

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

	public static void main(String arg[]) throws Exception {

	/*	PDF2ImageConverter obj = new PDF2ImageConverter();
		File f = new File(obj.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		String OUTPUT_DIR = f.getPath() + "\\OCR\\";

		//List<String> imageNames = obj.convertPDF2Images(OUTPUT_DIR);		

		String imagePath = OUTPUT_DIR + "image-7.png";
		StringBuffer sb = obj.extractDataFromImage(imagePath);
		String rawFile = "RawText.txt";
		//obj.saveRawData(sb, OUTPUT_DIR + rawFile);
		
		ProcessRawData prdata = new ProcessRawData();
		Set<Voter> beansSet = prdata.removeHeaderAndTrailerFromRawFile(OUTPUT_DIR + rawFile);
		 beansSet.addAll(prdata.removeHeaderAndTrailerFromRawFile(OUTPUT_DIR +
		 rawFile));
		System.out.println("Size of the beans" + beansSet.size());
		
		*/
		
		
		//need to uncheck the below 3 lines
				ExportExcel exportObject = new ExportExcel();
				Set<Voter> allVoterBeans = exportObject.retrieveBeanObjects();
				
			System.out.println(allVoterBeans.size());

	}

}
