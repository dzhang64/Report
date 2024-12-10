package report.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class POITools {

    public HSSFCellStyle style;

    public HSSFCellStyle stylePercentage;

    public HSSFCellStyle styleD2;

    public HSSFCellStyle dotStyle;

    public HSSFWorkbook wb;

    public POITools(HSSFWorkbook wb) {
        init(wb);
    }

    public void init(HSSFWorkbook wb){
        short color = 64;
        style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(color);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(color);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(color);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(color);
        this.wb = wb;
        stylePercentage = wb.createCellStyle();
        stylePercentage.cloneStyleFrom(style);
        stylePercentage.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        styleD2 = wb.createCellStyle();
        styleD2.cloneStyleFrom(style);
        styleD2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        HSSFCellStyle cellStyleAt = wb.getCellStyleAt(0);
        cellStyleAt.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleAt.setAlignment(HorizontalAlignment.CENTER);
        Font font = wb.createFont();
        font.setFontName("SimSun");
        font.setFontHeightInPoints((short) 10);
        cellStyleAt.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        dotStyle = wb.createCellStyle();
        dotStyle.setFont(font);
        dotStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dotStyle.setAlignment(HorizontalAlignment.CENTER);
        dotStyle.setBorderBottom(BorderStyle.DOTTED);
        dotStyle.setBottomBorderColor(color);
        dotStyle.setBorderLeft(BorderStyle.DOTTED);
        dotStyle.setLeftBorderColor(color);
        dotStyle.setBorderRight(BorderStyle.DOTTED);
        dotStyle.setRightBorderColor(color);
        dotStyle.setBorderTop(BorderStyle.DOTTED);
        dotStyle.setTopBorderColor(color);
    }


    public  void createCells(Row row, int num,HorizontalAlignment hAlign,VerticalAlignment aAlign){
        int physicalNumberOfCells = row.getPhysicalNumberOfCells();
        int limit = physicalNumberOfCells + num;
        for (int i = physicalNumberOfCells; i < limit; i++) {
            Cell cell = row.createCell(i);
            style.setVerticalAlignment(aAlign);
            style.setAlignment(hAlign);
            cell.setCellStyle(style);
        }
    }

    public  void createRows(Sheet sheet,int rowNum,int cellNum,HorizontalAlignment hAlign,VerticalAlignment aAlign){
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        int limit = physicalNumberOfRows + rowNum;
        for (int i = physicalNumberOfRows; i < limit; i++) {
            Row row = sheet.createRow(i);
            createCells(row,cellNum,hAlign,aAlign);
        }
    }

    public static void setCellSimSun(Cell cell){
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = cell.getRow().getSheet().getWorkbook().createFont();
        font.setFontName("SimSun");
        font.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font);
    }

    public static void setCellSimSun(Cell cell,int size,boolean bold){
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = cell.getRow().getSheet().getWorkbook().createFont();
        font.setFontName("SimSun");
        font.setFontHeightInPoints((short) size);
        font.setBold(bold);
        cellStyle.setFont(font);
    }

    public static void setCellHeiTi(Cell cell){
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = cell.getRow().getSheet().getWorkbook().createFont();
        font.setFontName("SimHEI");
        font.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font);
    }

    public static void insertPic(BufferedImage img, HSSFSheet sheet, int x1, int x2, int y1, int y2) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HSSFPatriarch drawingPatriarch = sheet.createDrawingPatriarch();
        HSSFClientAnchor hssfClientAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) y1, x1, (short) y2, x2);
        hssfClientAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(img,"png",byteArrayOutputStream);
        int id = sheet.getWorkbook().addPicture(byteArrayOutputStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture picture = drawingPatriarch.createPicture(hssfClientAnchor, id);
        picture.resize(1,1);
    }


}
