package com.sekhar.pdfextractor.exporttoexcel;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sekhar.pdfextractor.PDF2ImageConverter;
import com.sekhar.pdfextractor.beans.Voter;
import com.sekhar.pdfextractor.extractText.ProcessRawData;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportExcel {

	private static final String OUTPUT_DIR = "\\OCR\\";
	private static final String rawFile = "RawText.txt";
	private static final PDF2ImageConverter pdf2imageconverter = new PDF2ImageConverter();
	private static final File executionFilePath = new File(
			pdf2imageconverter.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	private static final String saveToPath = executionFilePath.getPath() + OUTPUT_DIR;
	private static final String RAWDATAFile = "RawText.txt";
	private static final ProcessRawData prdata = new ProcessRawData();
	private static final XSSFWorkbook workbook = new XSSFWorkbook();
	private static final String Sheet_Name = "sheet1";

	public Set<Voter> retrieveBeanObjects() throws IOException {

		FileOutputStream out = new FileOutputStream(new File("NewFile.xlsx"));

		Set<Voter> beansSet = new HashSet<Voter>();

		List<String> imageNamesList = pdf2imageconverter.convertPDF2Images(saveToPath);

		StringBuffer buffer = null;
		for (int i = 2; i < imageNamesList.size() - 1; i++) {
			buffer = pdf2imageconverter.extractDataFromImage(imageNamesList.get(i).trim());
			pdf2imageconverter.saveRawData(buffer, saveToPath + RAWDATAFile);

			Set<Voter> beanData = prdata.removeHeaderAndTrailerFromRawFile(saveToPath + RAWDATAFile);
			System.out.println("Retrived Data from " + imageNamesList.get(i).trim() + "Size is " + beanData.size());
			generateExcel(workbook, beanData, beansSet.size(), out);
			PrintWriter writer = new PrintWriter(saveToPath + RAWDATAFile);
			writer.close();
			beansSet.addAll(beanData);

		}

		return beansSet;

	}

	public static void generateExcel(XSSFWorkbook workbook, Set<Voter> beanData, int voterCount, FileOutputStream out) {
		try {

			XSSFSheet sheet = null;
			if (workbook.getSheet(Sheet_Name) == null) {

				sheet = workbook.createSheet(Sheet_Name);

			}

			for (Voter voter : beanData) {
				voterCount = voterCount + 1;
				Row row = sheet.createRow(voterCount);
				createList(voter, row);

			}
			// file name with path
			workbook.write(out);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void createList(Voter voter, Row row) // creating cells for each row
	{
		Cell cell = row.createCell(0);
		cell.setCellValue(voter.getVoterID());

		cell = row.createCell(1);
		cell.setCellValue(voter.getUserName());

		cell = row.createCell(2);
		cell.setCellValue(voter.getDependentType());

		cell = row.createCell(3);
		cell.setCellValue(voter.getDependentName());

		cell = row.createCell(4);
		cell.setCellValue(voter.getAge());

		cell = row.createCell(5);
		cell.setCellValue(voter.getGender());

		cell = row.createCell(6);
		cell.setCellValue(voter.getHouseNumber());

		cell = row.createCell(7);
		cell.setCellValue(voter.getHouseNumber());

	}

}
