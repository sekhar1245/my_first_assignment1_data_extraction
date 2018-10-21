package com.sekhar.pdfextractor.exporttoexcel;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sekhar.pdfextractor.PDF2ImageConverter;
import com.sekhar.pdfextractor.beans.Voter;
import com.sekhar.pdfextractor.extractText.ProcessRawData;

public class ExportExcel {
	
	private static final String OUTPUT_DIR =  "\\OCR\\";
	private static final String rawFile = "RawText.txt";
	private static final PDF2ImageConverter pdf2imageconverter = new PDF2ImageConverter();
	private static final File executionFilePath = new File(pdf2imageconverter.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());		
	private static final String saveToPath = executionFilePath.getPath()+OUTPUT_DIR;
	private static final String RAWDATAFile = "RawText.txt";
	private static final ProcessRawData prdata = new ProcessRawData();
	
	
	
	
	public  Set<Voter> retrieveBeanObjects() throws IOException{
		
		Set<Voter> beansSet = new HashSet<Voter>();
		
		List<String> imageNamesList = pdf2imageconverter.convertPDF2Images(saveToPath);	
		
		StringBuffer buffer =null;
		for(int i=2;i<imageNamesList.size()-1;i++) {
			buffer = pdf2imageconverter.extractDataFromImage(imageNamesList.get(i).trim());
			pdf2imageconverter.saveRawData(buffer, saveToPath + RAWDATAFile);
			
			Set<Voter> beanData = prdata.removeHeaderAndTrailerFromRawFile(saveToPath + RAWDATAFile);
			System.out.println("Retrived Data from "+imageNamesList.get(i).trim()+"Size is "+beanData.size());
			PrintWriter writer = new PrintWriter(saveToPath + RAWDATAFile);
			writer.close();
			beansSet.addAll(beanData);
			
		}
		
		
		return beansSet;
		
		
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	

}
