package ru.exlmoto.digest.flat.parser.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlatCianParser extends FlatParser {
	private final Logger log = LoggerFactory.getLogger(FlatCianParser.class);

	// Cell indexes format constants.
	private final int ROOM    =  1;
	private final int ADDRESS =  4;
	private final int SQUARE  =  5;
	private final int FLOOR   =  6;
	private final int PRICE   =  8;
	private final int PHONE   =  9;
	private final int LINK    = 18;

	@Override
	public List<Flat> getAvailableFlats(String path) {
		List<Flat> flatList = new ArrayList<>();
		try {
			File xlsx = new File(path);
			Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsx));

			// Get first sheet.
			Sheet sheet = workbook.getSheetAt(0);

			int rowSize = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rowSize; ++i) {
				Row row = sheet.getRow(i);
				flatList.add(new Flat(
					row.getCell(ROOM).toString(),
					row.getCell(SQUARE).toString(),
					row.getCell(FLOOR).toString(),
					row.getCell(ADDRESS).toString(),
					row.getCell(PRICE).toString(),
					row.getCell(PHONE).toString(),
					row.getCell(LINK).toString()
				));
			}

			flatList.forEach(System.out::println);

//			if (!xlsx.delete()) {
//				log.error(String.format("Cannot delete xlsx file on '%s' path.", path));
//			}
		} catch (IOException ioe) {
			log.error(String.format("Cannot read xlsx file on '%s' path.", path), ioe);
		}
		return flatList;
	}
}
