package ru.aplana.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import tools.Validation;
import db.dbConn;
import db.dbOperation;

/**
 * 
 * Generating results AS Request project
 * 
 * @author Maksim Stepanov
 * 
 */
public class Main {

	public static HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<String>> systemsError = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<String>> systemsResponses = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<String>> systemsErrorPercent = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<String>> operations = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<String>> errors = new HashMap<String, ArrayList<String>>();

	public static ArrayList<Timestamp> time = new ArrayList<Timestamp>();

	public static ArrayList<Timestamp> timeError = new ArrayList<Timestamp>();

	public static HashMap<String, String> countPFR = new HashMap<String, String>();

	public static ArrayList<String> sqls = new ArrayList<String>();

	public static int shettCount = 0;

	public static ArrayList<Integer> countPfr = new ArrayList<Integer>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args[0].equalsIgnoreCase("convert")) {

			File folder = new File(args[1]);

			for (File fileEntry : folder.listFiles()) {

				String fileName = fileEntry.getName();

				if (fileName.matches("(.*).CSV") || fileName.matches("(.*).csv")) {

					try {

						BufferedReader in = new BufferedReader(new FileReader(
								fileEntry));

						String line = null;

						String file = args[2] + "\\"
								+ fileName.substring(0, fileName.length() - 4)
								+ ".xls";

						FileOutputStream out = null;

						try {

							out = new FileOutputStream(file);

						} catch (FileNotFoundException e) {

							e.printStackTrace();
						}

						Workbook wb = new HSSFWorkbook();

						Sheet s = wb.createSheet();

						wb.setSheetName(0, fileName);

						int count = 0;

						int index = 0;

						Row header = s.createRow(count);

						Cell headerCell = null;

						CellStyle cs = wb.createCellStyle();

						Font f = wb.createFont();

						f.setBoldweight(Font.BOLDWEIGHT_BOLD);

						cs.setFont(f);

						String[] data = null;

						int len = 0;

						try {
							while ((line = in.readLine()) != null) {

								data = line.split("\",");

								len = data.length;

								Row row = s.createRow(index);

								for (int i = 0; i < len; i++) {

									if (count == 0) {

										headerCell = header.createCell(i);

										headerCell.setCellValue(data[i]
												.replace("\"", ""));

										headerCell.setCellStyle(cs);

									} else {

										Cell cell = row.createCell(i);

										String value = data[i]
												.replace("\"", "");

										try {

											Double.parseDouble(value);

											cell.setCellValue(Double
													.valueOf(value));

										} catch (NumberFormatException e) {

											
											cell.setCellValue(value);

										}

									}

								}

								index++;

								count++;

							}

						} catch (IOException e1) {

							e1.printStackTrace();

						}

						try {

							if (null != wb) {

								wb.write(out);

							}

							if (null != out) {

								out.close();

							}

						} catch (IOException e) {

							e.printStackTrace();
						}

						wb = null;

						try {

							in.close();

						} catch (IOException e) {

							e.printStackTrace();

						}

					} catch (FileNotFoundException e) {

						e.printStackTrace();
					}

				}

			}

		} else {

			if (!Validation.validating()) {

				System.err
						.println("Config is not valid! See error log! Application stopped!");

				System.exit(0);

			} else {

				Document settings = getSettings();

				Element root = settings.getRootElement();

				Element db = root.getChild("db");

				dbConn.ORA_HOST = db.getChildText("host");

				dbConn.ORA_PORT = db.getChildText("port");

				dbConn.ORA_SID = db.getChildText("sid");

				dbConn.ORA_USER = db.getChildText("user");

				dbConn.ORA_PASS = db.getChildText("password");

				settings = null;

				root = null;

				db = null;

			}

			System.out.println("Generating results....");

			dbOperation db = new dbOperation();

			db.mqStat();

			db.pfrStat();

			db.closeConnection();

			db = null;

			SimpleDateFormat sdfToday = new SimpleDateFormat("dd-MM-yyyy_HHmm");

			String fileName = "Report_" + sdfToday.format(new java.util.Date())
					+ ".xls";

			FileOutputStream out = null;

			try {

				out = new FileOutputStream(fileName);

			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

			Workbook wb = new HSSFWorkbook();

			if (systems.size() > 0) {

				System.out.println("Try generating mq sent messages stat...");

				getMqPutStat(wb, "Sending messages to mq by systems", 0);

			}

			if (systemsError.size() > 0) {

				System.out.println("Try generating mq errors systems stat...");

				getMqPutStat(wb, "Error mq systems by minutes", 1);

			}

			if (systemsResponses.size() > 0) {

				System.out.println("Try generating mq errors systems stat...");

				getMqPutStat(wb,
						"Response messages from as_request by systems", 2);

			}

			getMqListenerStat(wb);

			getPFRCountOperation(wb);

			pfrStatResponseTime(wb);

			pfrGetError(wb);

			sqlRequest(wb);

			getLoadingPFR(wb);

			try {

				if (null != wb) {

					wb.write(out);

				}

				if (null != out) {

					out.close();

				}

			} catch (IOException e) {

				e.printStackTrace();
			}

			systems = null;

			time = null;

			operations = null;

			timeError = null;

			errors = null;

			systemsError = null;

			systemsErrorPercent = null;

			System.out.println("Results was generated successfully!");

			System.out.println("Created file " + fileName + ". Check file!");

		}

	}

	/**
	 * 
	 * Loading PFR
	 * 
	 * @param wb
	 */
	public static void getLoadingPFR(Workbook wb) {

		System.out.println("Try generating pfr loading stat...");

		Sheet s = wb.createSheet();

		Cell c = null;

		wb.setSheetName(shettCount, "PFR loading");

		shettCount++;

		int index = 1;

		Row header = s.createRow(0);

		Cell headerCell = header.createCell(index);

		headerCell.setCellValue("PFR");

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		headerCell.setCellStyle(cs);

		Row row = null;

		for (Integer pfr : countPfr) {

			row = s.createRow(index);

			Cell cell = row.createCell(1);

			cell.setCellValue(pfr);

			index++;

		}

		s.autoSizeColumn(1);

		int newR = 0;

		if (null != row) {

			row.setRowNum(0);

			c = row.createCell(newR);

			c.setCellValue("Time");

			c.setCellStyle(cs);

			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

			for (Timestamp times : time) {

				++newR;

				row.setRowNum(newR);

				c = row.createCell(0);

				c.setCellValue(sdf.format(new Date(times.getTime())));

			}

			s.autoSizeColumn(0);

		}

	}

	/**
	 * 
	 * sent mq messages statistics
	 * 
	 * @param wb
	 */
	public static void getMqPutStat(Workbook wb, String nameShett, int flag) {

		Sheet s = wb.createSheet();

		Row r = null;

		Cell c = null;

		wb.setSheetName(shettCount, nameShett);

		shettCount++;

		int index = 1;

		Row header = null;

		Cell headerCell = null;

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		int newR = 0;

		Set<Entry<String, ArrayList<String>>> set = null;

		switch (flag) {

		case 0:
			set = systems.entrySet();
			break;
		case 1:
			set = systemsError.entrySet();
			break;
		case 2:
			set = systemsResponses.entrySet();
			break;

		}

		for (Map.Entry<String, ArrayList<String>> entry : set) {

			String system = entry.getKey();

			ArrayList<String> values = null;

			switch (flag) {

			case 0:
				values = systems.get(system);
				break;
			case 1:
				values = systemsError.get(system);
				break;
			case 2:
				values = systemsResponses.get(system);
				break;
			}

			if (index == 1) {

				header = s.createRow(0);

			}

			headerCell = header.createCell(index);

			headerCell.setCellValue(system);

			headerCell.setCellStyle(cs);

			int k = 0;

			for (int cellnum = 0; cellnum < values.size(); cellnum++) {

				if (index == 1) {

					r = s.createRow(cellnum + 1);

				} else {

					++newR;

					r.setRowNum(newR);

				}

				c = r.createCell(index);

				c.setCellValue(Integer.valueOf(values.get(k)));

				k++;

			}

			s.autoSizeColumn(index);

			index++;

			newR = 0;

			r.setRowNum(newR);

		}

		c = r.createCell(newR);

		c.setCellValue("Time");

		c.setCellStyle(cs);

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		for (Timestamp times : time) {

			++newR;

			r.setRowNum(newR);

			c = r.createCell(0);

			c.setCellValue(sdf.format(new Date(times.getTime())));

		}

		s.autoSizeColumn(0);

	}

	/**
	 * 
	 * MQ get stat for mq system
	 * 
	 * @param wb
	 */
	public static void getMqListenerStat(Workbook wb) {

		System.out.println("Try generating mq listeners messages stat...");

		Sheet s = wb.createSheet();

		Row r = null;

		Cell c = null;

		wb.setSheetName(shettCount, "Error by mq systems");

		shettCount++;

		int index = 0;

		Row header = null;

		Cell headerCell = null;

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		for (Map.Entry<String, ArrayList<String>> entry : systemsErrorPercent
				.entrySet()) {

			String key = entry.getKey();

			ArrayList<String> values = systemsErrorPercent.get(key);

			if (index == 0) {

				header = s.createRow(0);

				headerCell = header.createCell(index);

				headerCell.setCellValue("SystemName");

				headerCell.setCellStyle(cs);

				headerCell = header.createCell(index + 1);

				headerCell.setCellValue("Fail");

				headerCell.setCellStyle(cs);

				headerCell = header.createCell(index + 2);

				headerCell.setCellValue("Pass");

				headerCell.setCellStyle(cs);

				headerCell = header.createCell(index + 3);

				headerCell.setCellValue("Persent_Errors");

				headerCell.setCellStyle(cs);

			}

			r = s.createRow(index + 1);

			c = r.createCell(0);

			c.setCellValue(key);

			int valuesSize = values.size();

			for (int i = 0; i < valuesSize; i++) {

				c = r.createCell(i + 1);

				c.setCellValue(Double.valueOf(values.get(i)));

			}

			s.autoSizeColumn(index);

			index++;

		}

	}

	/**
	 * 
	 * All count operation for pfr
	 * 
	 * @param wb
	 */
	public static void getPFRCountOperation(Workbook wb) {

		System.out.println("Try generating count pfr operations stat...");

		Sheet s = wb.createSheet();

		Row r = null;

		Cell c = null;

		wb.setSheetName(shettCount, "PFR count operations by name");

		shettCount++;

		int index = 0;

		Row header = null;

		Cell headerCell = null;

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		for (Map.Entry<String, String> entry : countPFR.entrySet()) {

			String key = entry.getKey();

			String value = countPFR.get(key);

			if (index == 0) {

				header = s.createRow(0);

				headerCell = header.createCell(index);

				headerCell.setCellValue("Operation_name");

				headerCell.setCellStyle(cs);

				headerCell = header.createCell(index + 1);

				headerCell.setCellValue("Count");

				headerCell.setCellStyle(cs);

			}

			r = s.createRow(index + 1);

			c = r.createCell(0);

			c.setCellValue(key);

			c = r.createCell(1);

			c.setCellValue(value);

			s.autoSizeColumn(index);

			index++;

		}

	}

	/**
	 * Sql requests
	 * 
	 * @param wb
	 */
	public static void sqlRequest(Workbook wb) {

		System.out.println("Save used sql requests");

		Sheet s = wb.createSheet();

		Row r = null;

		Cell c = null;

		wb.setSheetName(shettCount, "SQL");

		shettCount++;

		int index = 1;

		Row header = null;

		Cell headerCell = null;

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		header = s.createRow(0);

		headerCell = header.createCell(0);

		headerCell.setCellStyle(cs);

		headerCell.setCellValue("SQL_Request");

		headerCell = header.createCell(1);

		headerCell.setCellStyle(cs);

		s.autoSizeColumn(0);

		for (String sql : sqls) {

			r = s.createRow(index);

			c = r.createCell(0);

			c.setCellValue(sql);

			index++;

		}

	}

	/**
	 * AVG , MAX, MIN response time PFR by operation
	 * 
	 * @param wb
	 */
	public static void pfrStatResponseTime(Workbook wb) {

		System.out
				.println("Try generating avg, max, min response time for  pfr operations stat...");

		Sheet s = wb.createSheet();

		Row r = null;

		Cell c = null;

		wb.setSheetName(shettCount, "PFR response time");

		shettCount++;

		int index = 1;

		Row header = null;

		Cell headerCell = null;

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		header = s.createRow(0);

		headerCell = header.createCell(0);

		headerCell.setCellStyle(cs);

		headerCell.setCellValue("OPERATION_NAME");

		headerCell = header.createCell(1);

		headerCell.setCellStyle(cs);

		headerCell.setCellValue("AVG (sec)");

		headerCell = header.createCell(2);

		headerCell.setCellStyle(cs);

		headerCell.setCellValue("MAX (sec)");

		headerCell = header.createCell(3);

		headerCell.setCellStyle(cs);

		headerCell.setCellValue("MIN (sec)");

		s.autoSizeColumn(0);

		s.autoSizeColumn(1);

		s.autoSizeColumn(2);

		s.autoSizeColumn(3);

		for (Map.Entry<String, ArrayList<String>> entry : operations.entrySet()) {

			String operation = entry.getKey();

			ArrayList<String> values = operations.get(operation);

			int k = 0;

			r = s.createRow(index);

			c = r.createCell(0);

			c.setCellValue(operation);

			for (int cellnum = 0; cellnum < values.size(); cellnum++) {

				c = r.createCell(cellnum + 1);

				c.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				c.setCellValue(Double.valueOf(values.get(k)) / 1000);

				k++;

			}

			index++;

		}

	}

	/**
	 * Error pfr by operation
	 * 
	 * @param wb
	 */
	public static void pfrGetError(Workbook wb) {

		System.out.println("Try generating  pfr errors...");

		Sheet s = wb.createSheet();

		Row r = null;

		Cell c = null;

		wb.setSheetName(shettCount, "PFR errors by operations");

		shettCount++;

		int index = 1;

		Row header = null;

		Cell headerCell = null;

		CellStyle cs = wb.createCellStyle();

		Font f = wb.createFont();

		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cs.setFont(f);

		int newR = 0;

		for (Map.Entry<String, ArrayList<String>> entry : errors.entrySet()) {

			String operation = entry.getKey();

			ArrayList<String> values = errors.get(operation);

			if (index == 1) {

				header = s.createRow(0);

			}

			headerCell = header.createCell(index);

			headerCell.setCellValue(operation);

			headerCell.setCellStyle(cs);

			int k = 0;

			for (int cellnum = 0; cellnum < values.size(); cellnum++) {

				if (index == 1) {

					r = s.createRow(cellnum + 1);

				} else {

					++newR;

					r.setRowNum(newR);

				}

				c = r.createCell(index);

				c.setCellValue(values.get(k));

				k++;

			}

			s.autoSizeColumn(index);

			if (null != r) {

				index++;

				newR = 0;

				r.setRowNum(newR);

			}

		}

		if (null != r) {

			c = r.createCell(newR);

			c.setCellValue("Time");

			c.setCellStyle(cs);

		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		for (Timestamp times : timeError) {

			++newR;

			r.setRowNum(newR);

			c = r.createCell(0);

			c.setCellValue(sdf.format(new Date(times.getTime())));

		}

		s.autoSizeColumn(0);

	}

	/**
	 * Init settings
	 * 
	 * @return
	 */
	private static Document getSettings() {

		SAXBuilder builder = new SAXBuilder();

		File xmlSettings = new File("Settings.xml");

		Document settings = null;

		try {

			settings = (Document) builder.build(xmlSettings);

			System.out.println("Settings file " + xmlSettings
					+ " read successfully!");

		} catch (JDOMException e) {

			System.err.println("[JDOM Error] Can't parse " + xmlSettings
					+ "; Error:" + e.getMessage());

			e.printStackTrace();

		} catch (IOException e) {

			System.err.println("[IO Error] Can't read " + xmlSettings
					+ "; Error:" + e.getMessage());

			e.printStackTrace();
		}

		return settings;

	}

}
