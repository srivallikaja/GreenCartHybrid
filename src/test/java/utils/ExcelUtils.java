package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtils {
	public static Map<String, String> getItemsFromExcel(String filePath, String sheetName)

	{
		Map<String, String> itemsList = new HashMap();

		try {
			FileInputStream file = new FileInputStream(
					"E:\\selenium projects 2026\\greenCartHybrid\\testData\\itemsList.xlsx");
			Workbook workbook = WorkbookFactory.create(file);
			Sheet sheet = workbook.getSheet("items");
			DataFormatter formatter = new DataFormatter();
			int rowCount = sheet.getPhysicalNumberOfRows();

			for (int i = 1; i < rowCount; i++) { // skip header row

				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				String veggie = formatter.formatCellValue(row.getCell(0)).trim();
				String quantity = formatter.formatCellValue(row.getCell(1)).trim();
				itemsList.put(veggie, quantity);
				if (!veggie.isEmpty() && !quantity.isEmpty()) {
					itemsList.put(veggie, quantity);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return itemsList;
	}

	public static void writePriceToExcel(String filePath, String sheetName, String veggieName, String price)
			throws Exception {

		FileInputStream file = new FileInputStream(
				"E:\\selenium projects 2026\\greenCartHybrid\\testData\\itemsList.xlsx");
		Workbook workbook = WorkbookFactory.create(file);
		Sheet sheet = workbook.getSheet("items");

		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {

			Row row = sheet.getRow(i);
			String excelVeggie = row.getCell(0).getStringCellValue();

			if (excelVeggie.equalsIgnoreCase(veggieName)) {

				Cell priceCell = row.getCell(2);
				if (priceCell == null) {
					priceCell = row.createCell(2);
				}

				priceCell.setCellValue(price);
				break;
			}
		}

		file.close();

		FileOutputStream fos = new FileOutputStream(filePath);
		workbook.write(fos);

		fos.close();
		workbook.close();
	}
}
