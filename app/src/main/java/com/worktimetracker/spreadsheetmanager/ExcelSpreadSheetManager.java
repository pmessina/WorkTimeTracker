package com.worktimetracker.spreadsheetmanager;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.DisplayFormat;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Admin on 5/23/2015.
 */
public class ExcelSpreadSheetManager
{
    private static String envPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private String directoryName, spreadSheetName, workSheetName;

    public ExcelSpreadSheetManager(String directoryName, String spreadSheetName, String workSheetName)
    {
        this.directoryName = directoryName;
        this.spreadSheetName = spreadSheetName;
        this.workSheetName = workSheetName;
    }

    public ExcelSpreadSheetManager()
    {

    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public String getSpreadSheetName()
    {
        return spreadSheetName;
    }

    public String getWorkSheetName()
    {
        return workSheetName;
    }

    public String getWorkbookDirectory()
    {
        return envPath;
    }

    public WritableWorkbook createOrGetWorkBook(String directoryName, String spreadSheetName) throws IOException, jxl.read.biff.BiffException
    {
        boolean isSdCardWritable = this.isExternalStorageWritable();

        File file = new File(directoryName + "/" + spreadSheetName);

        WritableWorkbook writableWorkbook = Workbook.createWorkbook(file);

        //If workbook exists, fetch it and get it ready for writing
//        if (file.exists())
//        {
//            Workbook workbook = Workbook.getWorkbook(file);
//
//            writableWorkbook = Workbook.createWorkbook(file);
//        }
//
//        else
//        {
//            writableWorkbook = Workbook.createWorkbook(file);
//        }

        return writableWorkbook;
    }

//    public static Uri GetSpreadsheetLocation()
//    {
//        return Uri.parse(envPath + "/output.xls");
//    }

    public boolean WorkbookFileIsDeleted(File file)
    {
        boolean fileDeleted = false;

        return fileDeleted = file.delete();
    }

    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    public WritableSheet createWorkSheet(WritableWorkbook writableWorkbook, String workSheetName)
    {
        return writableWorkbook.createSheet(workSheetName, 0);
    }

    public Sheet getWorkSheet(WritableWorkbook writableWorkbook)
    {
        Sheet sheet = writableWorkbook.getSheet(0);

        return sheet;
    }

    public void addLabelWithCells(WritableSheet sheet, int column, int row, String content) throws jxl.write.WriteException
    {
        Label lbl = new Label(column, row, content);
        sheet.addCell(lbl);
    }

    public void addNumberWithCells(WritableSheet sheet, int column, int row, String content) throws jxl.write.WriteException
    {
        Number number = new Number(3, 4, 3.1459);
        sheet.addCell(number);
    }

    public void addDateWithCells(WritableSheet sheet, int column, int row, Date date) throws jxl.write.WriteException
    {

        DisplayFormat displayFormat = new DateFormat("hh:m a");

        WritableCellFormat dateFormat = new WritableCellFormat(displayFormat);
        DateTime dateTime = new DateTime(column, row, date, dateFormat);

        sheet.addCell(dateTime);
    }


    public Cell getCell(Sheet sheet, int column, int row)
    {
        return sheet.getCell(column, row);
    }

    public String getCellContents(Cell cell)
    {
        return cell.getContents();
    }

    public void writeCloseWorkBook(WritableWorkbook writableWorkbook) throws jxl.write.WriteException, IOException
    {
        writableWorkbook.write();
        writableWorkbook.close();
    }


}
