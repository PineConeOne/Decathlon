package com.example.decathlon.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	
	public String getCellInfo(String excelName, int sheetNumber, int rowNumber, int colNumber) throws IOException {
		File excelfile = new File("C:/Eclipse/resultat_" + excelName + ".xlsx");
		FileInputStream fis = new FileInputStream(excelfile);
		
		@SuppressWarnings("resource")
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		Sheet sheet = wb.getSheetAt(sheetNumber);
		Row row = sheet.getRow(rowNumber);
        Cell cell = row.getCell(colNumber);
				
        DataFormatter dataFormatter = new DataFormatter();
		return dataFormatter.formatCellValue(cell);
		
	}

}
