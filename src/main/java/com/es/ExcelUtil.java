package com.es;

import org.apache.poi.hssf.usermodel.*;

import java.io.OutputStream;
import java.util.List;

public class ExcelUtil {
	
	//每个sheet最多显示60000条数据
	private static final int SHEET_LENGTH=6000;
	
    public static void exportExcel(String title, String[] headers, List<String[]> dataList, OutputStream out) throws Exception {
    	HSSFWorkbook workbook = new HSSFWorkbook();
    	HSSFSheet sheet = workbook.createSheet(title);
    	sheet.setDefaultColumnWidth(15);
    	
    	HSSFCellStyle style = workbook.createCellStyle();
    	HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
    	
    	//表头
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
			
		}
    	
		//表体数据  
    	for (int j=0; j < dataList.size(); j++) {
    		row = sheet.createRow(j+1);
    		
    		String[] data = dataList.get(j);
    		
    		for(int k=0;k<headers.length;k++) {
    			HSSFCell cell = row.createCell(k);

                HSSFRichTextString text = new HSSFRichTextString(data[k]);
    			cell.setCellValue(text);
    		}
    	}
    	
    	//自动调整宽度
    	for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i,true);
		}
    	
		workbook.write(out);
		
    }
    
    //导出多个sheet 的excel 文件
    public static void exportExcelWithMoreSheet(String sheetName, String[] headers, List<String[]> dataList,
                                                int maxSheetNum, OutputStream out) throws Exception {
    	HSSFWorkbook workbook = new HSSFWorkbook();
    	
    	int dataLength = dataList.size();
    	int sheetNum = dataLength/SHEET_LENGTH;
    	for(int kk=0;kk<=sheetNum&&kk<maxSheetNum;kk++) {
    	
	    	HSSFSheet sheet = workbook.createSheet(sheetName+(kk+1));

	    	HSSFCellStyle style = workbook.createCellStyle();
	    	HSSFFont font = workbook.createFont();
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        style.setFont(font);

            HSSFCellStyle cellStyle=workbook.createCellStyle();
            cellStyle.setWrapText(true);

	    	//表头
			HSSFRow row = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style);
				HSSFRichTextString text = new HSSFRichTextString(headers[i]);
				cell.setCellValue(text);
			}
	    	
			//表体数据  
	    	for (int j=kk*SHEET_LENGTH; j < dataLength && j < (kk+1)*SHEET_LENGTH; j++) {
	    		row = sheet.createRow(j+1-(kk*SHEET_LENGTH));
	    		
	    		String[] data = dataList.get(j);
	    		
	    		for(int k=0;k<headers.length;k++) {
	    			HSSFCell cell = row.createCell(k);
                    if(k==5||k==7) {
                        cell.setCellStyle(cellStyle);
                    }
	    			
	    			HSSFRichTextString text = new HSSFRichTextString(data[k]);
	    			cell.setCellValue(text);
	    		}
	    	}
	    	
	    	//自动调整宽度
	    	for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i,true);
			}
    	}
		workbook.write(out);
		
    }
    public static void exportEventExcel(String sheetName, String[] headers, List<String[]> dataList,
                                                OutputStream out) throws Exception {
    	HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);

        HSSFCellStyle cellStyle=workbook.createCellStyle();
        cellStyle.setWrapText(true);

        //表头
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //表体数据
        for (int j=0; j < dataList.size(); j++) {
            row = sheet.createRow(j+1);

            String[] data = dataList.get(j);

            for(int k=0;k<headers.length;k++) {
                HSSFCell cell = row.createCell(k);
                if(k==5||k==7) {
                    cell.setCellStyle(cellStyle);
                }

                HSSFRichTextString text = new HSSFRichTextString(data[k]);
                cell.setCellValue(text);
            }
        }

        //自动调整宽度
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i,true);
        }
		workbook.write(out);

    }


}
