package com.worktimetracker.spreadsheetmanager;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.DisplayFormat;
import jxl.format.Alignment;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * API for managing the jexcel library by creating/getting workbooks and worksheets,
 * adding header, and time records
 */
public class ExcelSpreadSheetManager
{
    private String directoryName, spreadSheetName, workSheetName;

    public ExcelSpreadSheetManager(String directoryName, String spreadSheetName, String
            workSheetName)
    {
        this.directoryName = directoryName;
        this.spreadSheetName = spreadSheetName;
        this.workSheetName = workSheetName;
    }

    public ExcelSpreadSheetManager(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public HashMap<String, ArrayList<String>> fetchSheetsFromDirectory(ArrayList<String> workBooksInDirectory) throws Exception
    {
        HashMap<String, ArrayList<String>> sheets = new HashMap<>();

        for (String file : workBooksInDirectory)
        {
                File convertToFile = new File(this.directoryName, file);

                if (file.endsWith(".xls"))
                {
                    Workbook wkbk = getWorkbook(convertToFile);
                    ArrayList<String> retrieveSheets = new ArrayList<>();

                    for (Sheet sheet : wkbk.getSheets())
                    {
                        retrieveSheets.add(sheet.getName());
                    }

                    sheets.put(convertToFile.getName(), retrieveSheets);
                }
        }

        return sheets;
    }

    public ArrayList<String> getFilesFromDirectory()
    {
        ArrayList<String> retrieveFiles = new ArrayList<>();

        File directory = new File(directoryName);

        if (!directory.exists())
        {
            directory.mkdir();
        }

        for (File file : directory.listFiles())
        {
            retrieveFiles.add(file.getName());
        }




        return retrieveFiles;
    }

    public WritableWorkbook createWorkbook(File spreadSheetFile) throws IOException, BiffException
    {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setUseTemporaryFileDuringWrite(true);

        return Workbook.createWorkbook(spreadSheetFile, settings);
    }

    public WritableWorkbook createWorkbook(File spreadSheetFile, Workbook workbook) throws
            IOException
    {
        return Workbook.createWorkbook(spreadSheetFile, workbook);
    }

    public Workbook getWorkbook(File spreadSheetFile) throws IOException, BiffException
    {
        return Workbook.getWorkbook(spreadSheetFile);
    }

    public WritableSheet createWorkSheet(WritableWorkbook writableWorkbook, String workSheetName)
    {
        return writableWorkbook.createSheet(workSheetName, 0);
    }

    public WritableSheet createWorkSheet(WritableWorkbook writableWorkbook, Sheet sheet)
    {
        return writableWorkbook.getSheet(sheet.getName());
    }

    public WritableSheet addLabelWithCells(WritableSheet sheet, int column, int row, String
            content) throws jxl.write.WriteException
    {
        Label lbl = new Label(column, row, content);

        WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10);

        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(Alignment.CENTRE);

        sheet.addCell(lbl);

        return sheet;
    }

    public void addNumberWithCells(WritableSheet sheet, int column, int row, double
            totalWorkTime) throws jxl.write.WriteException
    {
        Number number = new Number(column, row, totalWorkTime);

        WritableCellFormat cellFormat = new WritableCellFormat(NumberFormats.FLOAT);
        cellFormat.setAlignment(Alignment.CENTRE);

        number.setCellFormat(cellFormat);
        sheet.addCell(number);
    }

    public WritableSheet addDateTimeWithCells(WritableSheet sheet, int column, int row, Date
            date, String format) throws jxl.write.WriteException
    {
        DisplayFormat displayFormat = new DateFormat(format);

        WritableCellFormat dateFormat = new WritableCellFormat(displayFormat);
        dateFormat.setAlignment(Alignment.CENTRE);

        DateTime dateTime = new DateTime(column, row, date, dateFormat);

        sheet.addCell(dateTime);

        return sheet;
    }

    public Cell getCell(Sheet sheet, int column, int row)
    {
        return sheet.getCell(column, row);
    }

    public void writeCloseWorkBook(WritableWorkbook writableWorkbook) throws jxl.write
            .WriteException, IOException
    {
        writableWorkbook.write();
        writableWorkbook.close();
    }
}
