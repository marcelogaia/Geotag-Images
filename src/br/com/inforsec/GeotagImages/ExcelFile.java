package br.com.inforsec.GeotagImages;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ExcelFile {

	private String filename;
	private int rowCount = 0;
	private HSSFSheet sheet;
	private HSSFWorkbook workbook;

	public ExcelFile(String filename) {
		this.filename = filename;
		this.workbook = new HSSFWorkbook();
		this.sheet = this.workbook.createSheet("FirstSheet");
	}

	public void addRow(Object... cells) {
		HSSFRow row = sheet.createRow((short) rowCount);
		UrlValidator urlv = new UrlValidator();
		for (int i = 0; i < cells.length; i++) {
			HSSFCell cell = row.createCell(i);
			String value = cells[i].toString();
			cell.setCellValue(value);

			if (urlv.isValid(value)) {
				CellStyle hlink_style = this.workbook.createCellStyle();
				Font hlink_font = this.workbook.createFont();
				hlink_font.setUnderline(Font.U_SINGLE);
				hlink_font.setColor(IndexedColors.BLUE.getIndex());
				hlink_style.setFont(hlink_font);
				CreationHelper createHelper = this.workbook.getCreationHelper();
				Hyperlink link = createHelper
						.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);

				link.setAddress(value);
				cell.setHyperlink(link);
				cell.setCellStyle(hlink_style);
			}

			this.sheet.autoSizeColumn(i);
		}

		rowCount++;
	}

	public void close() {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();

			JOptionPane.showMessageDialog(null,
					"Seu arquivo Excel foi gerado com sucesso em: "
							+ this.filename);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace(GeotagImages.LOG);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace(GeotagImages.LOG);
		}

	}

	public void setHeader(String... cells) {
		HSSFRow rowhead = sheet.createRow((short) 0);
		HSSFFont font = this.workbook.createFont();
		HSSFCellStyle style = this.workbook.createCellStyle();

		font.setBold(true);
		style.setFont(font);

		for (int i = 0; i < cells.length; i++) {
			HSSFCell cell = rowhead.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(cells[i]);
		}

		rowCount++;
	}
}
