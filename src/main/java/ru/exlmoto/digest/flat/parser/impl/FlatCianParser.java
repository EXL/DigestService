/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.flat.parser.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

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

	private final String patchAddress0 = "ул.";
	private final String patchAddress1 = "улица";
	private final String patchAddress2 = "Владимира";
	private final String patchAddress3 = "В.";

	private final FilterHelper filter;
	private final LocaleHelper locale;

	private boolean deleteFile;

	// Cell indexes format constants.
	// See XLSX file for column indexes.
	private final int ROOM    =  1;
	private final int ADDRESS =  4;
	private final int SQUARE  =  5;
	private final int FLOOR   =  6;
	private final int PRICE   =  8;
	private final int PHONE   =  9;
	private final int LINK    = 19;

	public FlatCianParser(FilterHelper filter, LocaleHelper locale) {
		this.filter = filter;
		this.locale = locale;
		deleteFile = true;
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
					parsePrice(parseCell(row, PRICE)),
					applyPhonePatch(parsePhone(parseCell(row, PHONE))),
					parseCell(row, LINK)
				));
			}

			if (deleteFile && !xlsx.delete()) {
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
				error = locale.i18n("flat.error.empty");
			}
			return Error(error);
		}
		return Ok(flats);
	}

	public void setDeleteFile(boolean deleteFile) {
		this.deleteFile = deleteFile;
	}

	private String parseCell(Row row, int index) {
		if (row != null) {
			Cell cell = row.getCell(index);
			if (cell != null) {
				return cell.getStringCellValue();
			}
		}
		return "???";
	}

	private String parseSquare(String squares) {
		return getFirstChunk(squares, "/");
	}

	private String parseFloor(String floor) {
		return getFirstChunk(floor, ",");
	}

	private String parsePhone(String phone) {
		return (onePhone) ? getFirstChunk(phone, ",") : phone;
	}

	protected String parseAddress(String address) {
		if (address.contains(",")) {
			String reversed = new StringBuilder(address).reverse().toString();
			String[] parsed = reversed.split(",");
			// 0 - house number, 1 - street.
			String res = parsed[0] + " ," + parsed[1];
			return filter.strip(new StringBuilder(res).reverse().toString());
		}
		return address;
	}

	protected String parsePrice(String price) {
		if (StringUtils.hasText(price)) {
			return adjustPrice(filter.strip(price).split("\\s+")[0]) + " " + locale.i18n("flat.price.symbol");
		}
		return price;
	}

	protected String getFirstChunk(String full, String stop) {
		if (full.contains(stop)) {
			return full.substring(0, full.indexOf(stop));
		}
		return full;
	}

	protected String applyAddressPatch(String address) {
		return filter.strip(
			address
				.replace(patchAddress0, "")
				.replace(patchAddress1, "")
				.replace(patchAddress2, patchAddress3)
		).replace(" ,", ",");
	}
}
