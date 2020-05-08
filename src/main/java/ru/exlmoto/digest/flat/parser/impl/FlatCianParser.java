package ru.exlmoto.digest.flat.parser.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class FlatCianParser extends FlatParser {
	private final Logger log = LoggerFactory.getLogger(FlatCianParser.class);

	private final String patchAddress1 = "улица";
	private final String patchAddress2 = "Владимира";
	private final String patchAddress3 = "В.";
	private final String patchPrice1 = "руб";
	private final String patchPrice2 = "р";

	private final FilterHelper filter;

	// Cell indexes format constants.
	// See XLSX file for column indexes.
	private final int ROOM    =  1;
	private final int ADDRESS =  4;
	private final int SQUARE  =  5;
	private final int FLOOR   =  6;
	private final int PRICE   =  8;
	private final int PHONE   =  9;
	private final int LINK    = 19;

	public FlatCianParser(FilterHelper filter) {
		this.filter = filter;
	}

	@Override
	public Answer<List<Flat>> getAvailableFlats(String path) {
		String error = null;
		List<Flat> flats = new ArrayList<>();
		try {
			File xlsx = new File(path);
			Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsx));

			// Get first sheet from XLSX book.
			Sheet sheet = workbook.getSheetAt(0);

			// Drop first line of table with column descriptions.
			int start = 1;
			int rowSize = sheet.getPhysicalNumberOfRows();
			for (int i = start; i < rowSize; ++i) {
				Row row = sheet.getRow(i);
				flats.add(new Flat(
					parseCell(row, ROOM),
					parseSquare(parseCell(row, SQUARE)),
					parseFloor(parseCell(row, FLOOR)),
					applyAddressPatch(parseAddress(parseCell(row, ADDRESS))),
					applyPricePatch(parsePrice(parseCell(row, PRICE))),
					applyPhonePatch(parsePhone(parseCell(row, PHONE))),
					parseCell(row, LINK)
				));
			}

			if (!xlsx.delete()) {
				log.error(String.format("Cannot delete xlsx file on '%s' path.", path));
			}
		} catch (IOException ioe) {
			error = String.format("Cannot read xlsx file on '%s' path.", path);
			log.error(error, ioe);
		} catch (NotOfficeXmlFileException noxfe) {
			error = String.format("Cannot parse xlsx file on '%s' path.", path);
			log.error(error, noxfe);
		}
		if (flats.isEmpty()) {
			if (error == null) {
				error = "Flat list is empty, please check CIAN parser.";
			}
			return Error(error);
		}
		return Ok(flats);
	}

	private String parseCell(Row row, int index) {
		return row.getCell(index).getStringCellValue();
	}

	protected String parseSquare(String squares) {
		if (squares.contains("/")) {
			return squares.substring(0, squares.indexOf("/"));
		}
		return squares;
	}

	protected String parseFloor(String floor) {
		if (floor.contains(",")) {
			return floor.substring(0, floor.indexOf(","));
		}
		return floor;
	}

	protected String parseAddress(String address) {
		if (address.contains(", ")) {
			String reversed = new StringBuilder(address).reverse().toString();
			String[] parsed = reversed.split(" ,");
			// 0 - house number, 1 - street.
			String res = parsed[0] + " ," + parsed[1];
			return new StringBuilder(res).reverse().toString();
		}
		return address;
	}

	protected String parsePrice(String price) {
		if (price.contains(" ")) {
			String[] parsed = filter.strip(price).split("\\s+");
			// 0 - price, 1 - currency.
			String res = adjustPrice(parsed[0]) + " " + parsed[1];
			// Delete last comma if exist.
			return (res.endsWith(",")) ? res.replaceFirst(".$", "") : res;
		}
		return price;
	}

	protected String parsePhone(String phone) {
		if (onePhone && phone.contains(", ")) {
			return phone.substring(0, phone.indexOf(", "));
		}
		return phone;
	}

	private String applyAddressPatch(String address) {
		return filter.strip(address.replace(patchAddress1, "").replace(patchAddress2, patchAddress3))
			.replace(" ,", ",");
	}

	private String applyPricePatch(String price) {
		return price.replace(patchPrice1, patchPrice2);
	}
}
