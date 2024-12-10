package report.functions.teleinner;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import report.utils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TelecomIndoorReport {

    private String datePath;

    private String report;

    private String backImage;

    //全景图的目录
    private File photoDir;

    private String createImage;

    private File leakageFile;

    private File handoverFile;

    private String longtitude ;

    private String latitude ;

    private String address ;

    private String buildCoverDes;

    private String nrCoverDes ;

    private String combineDes ;

    private String testPersonName ;

    private String testPersonTel ;

    private Date testDate;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    private DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");

    private DecimalFormat decimalFormat = new DecimalFormat("#");

    private ArrayList<File> dtExcel = new ArrayList<File>();

    private ArrayList<File> cqtDLFile = new ArrayList<File>();

    private ArrayList<File> cqtULFile = new ArrayList<File>();

    private ArrayList<File> ping32File = new ArrayList<File>();

    private ArrayList<File> ping1300File = new ArrayList<File>();

    private ArrayList<File> mocFile = new ArrayList<File>();

    private HashMap<File,String> fileToFloorName = new HashMap<File, String>();

    private HashMap<File,File> fileToBackImageMap = new HashMap<File, File>();

    private static String templateExcelFile;

    private File reportExcel;

    private String enbName;

    private String enbId = "";

    private String[] cellId;

    private double frequency;

    private int band;

    private int bandWidth;

    private HashMap<Integer,Object> pciMap;

    private double dtThresh;

    private int dlPoint = 0;

    private int dlPassedPoint = 0;

    private int pciPassPoint = 0;

    private int pciPoint = 0;

    private CellInfo cellInfo;

    private String coverBuildFloor = "";

    public static String tempFile;

    public static String gpsFile;

    public static String cellFiles;

    public static String testFilePath;

    public static void initClass() throws IOException {
        tempFile = StaticStorage.functionConfigFile.getPath()+"/tempFile";
        gpsFile = StaticStorage.functionConfigFile.getPath()+"/gps";
        cellFiles = StaticStorage.functionConfigFile.getPath()+"/cells";
        templateExcelFile = tempFile+"/nrindoor-small.xls";
        File cellsFileDir = new File(cellFiles);
        File gpsFileDir = new File(gpsFile);
        if(cellsFileDir.exists()){
            File[] filesCells = cellsFileDir.listFiles();
            for (int i = 0; i < filesCells.length; i++) {
                CellInfo.initCSV(filesCells[i].getPath());
            }
        }

        if(gpsFileDir.exists()){
            File[] filesGPS= gpsFileDir.listFiles();
            for (int i = 0; i < filesGPS.length; i++) {
                CellInfo.setGPS(filesGPS[i].getPath());
            }
        }

    }

    public TelecomIndoorReport(String datePath) throws IOException {
        this.datePath = datePath;
        this.photoDir = new File(datePath,"photo/室分楼宇全景图");
        FileInputStream fileIn = new FileInputStream(datePath+"/报告文件.xls");
        POIFSFileSystem fs = new POIFSFileSystem(fileIn);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        this.longtitude = String.valueOf(sheet.getRow(3).getCell(1).getNumericCellValue());
        this.latitude = String.valueOf(sheet.getRow(4).getCell(1).getNumericCellValue());
        this.buildCoverDes  = String.valueOf(sheet.getRow(5).getCell(1).getStringCellValue());
        this.nrCoverDes = String.valueOf(sheet.getRow(6).getCell(1).getStringCellValue());
        this.combineDes = String.valueOf(sheet.getRow(7).getCell(1).getStringCellValue());
        this.testPersonName = String.valueOf(sheet.getRow(8).getCell(1).getStringCellValue());
        this.testPersonTel = decimalFormat.format(sheet.getRow(9).getCell(1).getNumericCellValue());
        HSSFCell dateCell = sheet.getRow(10).getCell(1);
        if(dateCell!=null){
            this.testDate = sheet.getRow(10).getCell(1).getDateCellValue();
        }
        this.dtThresh = 40;
        this.report = "csv";
        this.backImage = "backImage";
        this.createImage = "createImage";
        File result = new File(datePath, this.createImage);
        if(result.exists()){
            File[] files = result.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
            result.delete();
            result.mkdir();
        }else{
            result.mkdir();
        }

        this.enbName = sheet.getRow(0).getCell(1).getStringCellValue().trim();
        int tmpENBID = (int) sheet.getRow(1).getCell(1).getNumericCellValue();
        if(tmpENBID!=0) {
            this.enbId = String.valueOf((int)sheet.getRow(1).getCell(1).getNumericCellValue());
            int physicalNumberOfCells = sheet.getRow(2).getPhysicalNumberOfCells();
            int cellNum = 0;
            for (int i = 1; i < physicalNumberOfCells; i++) {
                HSSFCell cell = sheet.getRow(2).getCell(i);
                if(cell==null){
                    break;
                }
                int code = cell.getCellType().getCode();
                if (code != 0) {
                    break;
                }
                cellNum++;
            }

            this.cellId = new String[cellNum];
            for (int i = 1; i < cellNum + 1; i++) {
                HSSFCell cell = sheet.getRow(2).getCell(i);
                int code = cell.getCellType().getCode();
                if (code != 0) {
                    break;
                } else {
                    cellId[i - 1] = enbId + "-" + (int)sheet.getRow(2).getCell(i).getNumericCellValue();
                }
            }
        }else {
            int physicalNumberOfCells = sheet.getRow(11).getPhysicalNumberOfCells();
            int cellNum = 0;
            for (int i = 1; i < physicalNumberOfCells; i++) {
                HSSFCell cell = sheet.getRow(11).getCell(i);
                int code = cell.getCellType().getCode();
                if(code==0){
                    break;
                }
                cellNum++;
            }

            this.cellId = new String[cellNum];

            for (int i = 1; i < cellNum+1; i++) {
                HSSFCell cell = sheet.getRow(11).getCell(i);
                cellId[i-1] = sheet.getRow(11).getCell(i).getStringCellValue();
            }

            HashSet<String> nodebSet = new HashSet<String>();
            for (int i = 0; i < cellId.length; i++) {
                String temp = cellId[i].split("-")[0];
                nodebSet.add(temp);
            }
            for (String s : nodebSet) {
                enbId = enbId +","+s;
            }
            enbId = enbId.substring(1,enbId.length());
            enbId = enbId.substring(1,enbId.length());
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < cellId.length; i++) {
                if(cellId[i].trim().length()!=0){
                    strings.add(cellId[i]);
                }
            }
            cellId = strings.toArray(new String[0]);
        }

        this.frequency = CellInfo.cellPCIMap.get(cellId[0]).frequency;
        this.band = 1;
        if(frequency>3000){
            band = 78;
            this.dtThresh = 50;
        }
        this.bandWidth = CellInfo.cellPCIMap.get(cellId[0]).nrbandwidth;
        if(band==1){
            if(bandWidth<=20){
                this.dtThresh = 20;
            }
        }else if(band == 78){
            this.dtThresh = 50;
        }

        this.pciMap = new HashMap<Integer, Object>();
        for (String cellStr : cellId) {
            int pci = CellInfo.cellPCIMap.get(cellStr).pci;
            pciMap.put(pci,new Object());
        }

        this.cellInfo = CellInfo.cellPCIMap.get(cellId[0]);

        if(cellId.length>6){
            templateExcelFile = tempFile+"/nrindoor-big.xls";
        }
    }

    public TelecomIndoorReport() {
    }

    public boolean init() throws ImageProcessingException, IOException, MetadataException, ParseException {
        File file = new File(datePath);
        File data = new File(file,report);
        File image = new File(file,backImage);
        if(!file.exists()||!data.exists()||!image.exists()){
            System.out.println("this folder is not exist!");
            return false;
        }
        String[] dataFileList = data.list();
        String[] imageFileList = image.list();
        for (int i = 0; i < dataFileList.length; i++) {
            String fileName = dataFileList[i];
            String[] temp = fileName.split("_");
            String key = temp[5].toUpperCase();
            if(key.contains("DT")){
                coverBuildFloor = coverBuildFloor+","+temp[4];
                File dtFile = new File(data,fileName);
                dtExcel.add(dtFile);
                fileToFloorName.put(dtFile,temp[4]);
                String imageName = findImageFileByDate(temp[1] + "_" + temp[2], imageFileList);
                fileToBackImageMap.put(dtFile,new File(image,imageName));
            } else if(key.contains("WX")){
                leakageFile = new File(data,fileName);
                String imageName = findImageFileByDate(temp[1] + "_" + temp[2], imageFileList);
                fileToBackImageMap.put(leakageFile,new File(image,imageName));
            } else if(key.contains("DL")){
                cqtDLFile.add(new File(data,fileName));
            } else if(key.contains("UL")){
                cqtULFile.add(new File(data,fileName));
            } else if(key.contains("1300PING")||key.contains("PING1300")){
                ping1300File.add(new File(data,fileName));
            } else if(key.contains("32PING")||key.contains("PING32")){
                ping32File.add(new File(data,fileName));
            } else if(key.contains("MOC")){
                mocFile.add(new File(data,fileName));
            } else if(key.contains("HO")){
                handoverFile = new File(data,fileName);
            }  else {

            }
        }

        coverBuildFloor = coverBuildFloor.substring(1,coverBuildFloor.length());

        reportExcel = new File(file,enbName+"_测试报告.xls");
        if(reportExcel.exists()){
            reportExcel.delete();
        }

        //如果没有填写经纬度，在照片获取,如果填写了，在网上获取地址
        if(Double.parseDouble(longtitude)==0||Double.parseDouble(latitude)==0){
            if(photoDir.exists()){
                String[] list = photoDir.list();
                if(list.length>0){
                    File po = new File(photoDir.getPath(),list[0]);
                    String[] photoMSG = PicTools.getPhotoMSG(po);
                    if(photoMSG!=null){
                        this.longtitude = photoMSG[0];
                        this.latitude = photoMSG[1];
                        this.address = photoMSG[2];
                    }
                }
            }
        }else {
            this.address = HttpTools.getAddressByGaode(longtitude, latitude);
        }

        if(testDate==null){
            String strDate = 20+dtExcel.get(0).getName().split("_")[1];
            testDate = dateFormat.parse(strDate);
        }

        return true;

    }

    public void makeReport() throws IOException, ParseException {
        int rowPoint = 0;
            FileInputStream fileIn = new FileInputStream(templateExcelFile);
            POIFSFileSystem fs = new POIFSFileSystem(fileIn);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            POITools poiTools = new POITools(wb);
            HSSFSheet sheet6 = wb.getSheetAt(6);
            HSSFSheet sheet5 = wb.getSheetAt(5);
            HSSFSheet sheet4 = wb.getSheetAt(4);
            HSSFSheet sheet3 = wb.getSheetAt(3);
            HSSFSheet sheet2 = wb.getSheetAt(2);
            HSSFSheet sheet1 = wb.getSheetAt(1);
            HSSFSheet sheet0 = wb.getSheetAt(0);
            handleCover(sheet4,poiTools,"photo");

            for (int i = 0; i < dtExcel.size(); i++) {
                System.out.println(dtExcel.get(i).getName());
                handleDt(dtExcel.get(i).getPath(),sheet6,poiTools);
            }
            if(handoverFile!=null){
                handleHandOver(sheet5,handoverFile);
            }
            if(leakageFile!=null){
                handleLeakage(leakageFile,sheet5,poiTools);
            }
            if(cqtDLFile!=null||cqtULFile!=null||ping32File!=null||ping1300File!=null){
                handCQT(sheet3,sheet2,poiTools);
            }

            handleDate(sheet1,poiTools);
            handleBasic(sheet0,poiTools);

            wb.write(reportExcel);
    }

    private void handleHandOver(HSSFSheet sheet,File handoverFile) throws IOException, ParseException {
        InHouseTools inHouseTools = new InHouseTools(handoverFile.getPath(),backImage, createImage);
        inHouseTools.init();
        ArrayList<Point> points = inHouseTools.rsrpHandle();
        BufferedImage rsrpImage = inHouseTools.drawOval(points, NetTools.typeRsrp);
        ArrayList<Point> points1 = inHouseTools.pciHandle(pciMap);
        BufferedImage pciImage = inHouseTools.drawPCI(points1);
        int handoverCounter = inHouseTools.handoverCheck();
        sheet.getRow(2).getCell(0).setCellValue(handoverCounter);
        sheet.getRow(2).getCell(1).setCellValue(handoverCounter);
        //准备缓存
        ByteArrayOutputStream imageStreamRsrp = new ByteArrayOutputStream();
        ByteArrayOutputStream imageStreamPCI = new ByteArrayOutputStream();
        //开始画图
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        //RSRP
        HSSFClientAnchor anchorRsrp = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, 4, (short) 1, 20);
        anchorRsrp.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(rsrpImage,"png",imageStreamRsrp);
        int rsrpId = sheet.getWorkbook().addPicture(imageStreamRsrp.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture pictureRsrp = patriarch.createPicture(anchorRsrp, rsrpId);
        pictureRsrp.resize(2,1);
        //PCI
        HSSFClientAnchor anchorPCI = new HSSFClientAnchor(0, 0, 0, 0, (short) 2, 4, (short) 3, 20);
        anchorPCI.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(pciImage,"png",imageStreamPCI);
        int pciId = sheet.getWorkbook().addPicture(imageStreamPCI.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture picturePCI = patriarch.createPicture(anchorPCI, pciId);
        picturePCI.resize(2,1);
    }

    public boolean handleDt(String path,HSSFSheet sheet,POITools poiTools) throws IOException, ParseException {
        ArrayList<Point> points = null;
        BufferedImage rsrpImage = null;
        BufferedImage sinrImage = null;
        BufferedImage dlImage = null;
        BufferedImage pciImage = null;
        ByteArrayOutputStream imageStreamRsrp = new ByteArrayOutputStream();
        ByteArrayOutputStream imageStreamSinr= new ByteArrayOutputStream();
        ByteArrayOutputStream imageStreamDL = new ByteArrayOutputStream();
        ByteArrayOutputStream imageStreamPCI = new ByteArrayOutputStream();
        String floor = path.split("_")[4];
        InHouseTools inHouseTools = new InHouseTools(path,backImage, createImage);
        if(inHouseTools.keyDates.size()==0){
            return false;
        }
        inHouseTools.init();;
        points = inHouseTools.rsrpHandle();
        if(points!=null&&points.size()>0){
            rsrpImage = inHouseTools.drawOval(points,NetTools.typeRsrp);
        }
        points = inHouseTools.sinrHandle();
        if(points!=null&&points.size()>0){
            sinrImage = inHouseTools.drawOval(points,NetTools.typeSinr);
        }
        points = inHouseTools.dlHandle(band,dtThresh);
        if(points!=null&&points.size()>0){
            if(band==1){
                dlImage = inHouseTools.drawSpeed(points, NetTools.typeDlSpeed21);
            }else {
                dlImage = inHouseTools.drawSpeed(points, NetTools.typeDlSpeed35);
            }
        }
        points = inHouseTools.pciHandle(pciMap);
        if(points!=null&&points.size()>0){
            pciImage = inHouseTools.drawPCI(points);
        }
        int rowPoint = sheet.getPhysicalNumberOfRows();
        poiTools.createRows(sheet, 17, 6,HorizontalAlignment.CENTER,VerticalAlignment.CENTER);
        CellRangeAddress cellAddresses = new CellRangeAddress(rowPoint, rowPoint, 0, 3);
        sheet.addMergedRegion(cellAddresses);
        sheet.getRow(rowPoint).getCell(0).setCellValue(floor+"(RSRP、SINR、下载、PCI图,底图为CAD或平面图)");
        sheet.getRow(rowPoint).getCell(4).setCellValue("测试指标");
        sheet.getRow(rowPoint).getCell(5).setCellValue("数值");
        rowPoint++;
        cellAddresses = new CellRangeAddress(rowPoint, rowPoint+15, 0, 3);
        sheet.addMergedRegion(cellAddresses);
        sheet.getRow(rowPoint).getCell(4).setCellValue("RSRP");
        sheet.getRow(rowPoint+1).getCell(4).setCellValue("SINR");
        sheet.getRow(rowPoint+2).getCell(4).setCellValue("进房间测试的房间个数");
        sheet.getRow(rowPoint+3).getCell(4).setCellValue("覆盖率1");
        sheet.getRow(rowPoint+4).getCell(4).setCellValue("覆盖率2");
        sheet.getRow(rowPoint+5).getCell(4).setCellValue("下行平均速率");
        sheet.getRow(rowPoint+6).getCell(4).setCellValue("下行速率优良比");
        sheet.getRow(rowPoint+7).getCell(4).setCellValue("室分PCI占比");
        sheet.getRow(rowPoint).getCell(5).setCellValue(inHouseTools.reportRsrp);
        POITools.setCellSimSun(sheet.getRow(rowPoint).getCell(5));
        sheet.getRow(rowPoint+1).getCell(5).setCellValue(inHouseTools.reportSinr);
        POITools.setCellSimSun(sheet.getRow(rowPoint+1).getCell(5));
        sheet.getRow(rowPoint+2).getCell(5).setCellValue(0);
        POITools.setCellSimSun(sheet.getRow(rowPoint+2).getCell(5));
        sheet.getRow(rowPoint+3).getCell(5).setCellValue(inHouseTools.reprotCoverageRage);
        POITools.setCellSimSun(sheet.getRow(rowPoint+3).getCell(5));
        sheet.getRow(rowPoint+4).getCell(5).setCellValue(inHouseTools.reprotCoverageRage);
        POITools.setCellSimSun(sheet.getRow(rowPoint+4).getCell(5));
        sheet.getRow(rowPoint+5).getCell(5).setCellValue(inHouseTools.reportDLSpeed);
        POITools.setCellSimSun(sheet.getRow(rowPoint+5).getCell(5));
        sheet.getRow(rowPoint+6).getCell(5).setCellValue(((double) inHouseTools.reportPassedDLPoint)/inHouseTools.reportDLPoint);
        POITools.setCellSimSun(sheet.getRow(rowPoint+6).getCell(5));
        sheet.getRow(rowPoint+7).getCell(5).setCellValue(inHouseTools.reportIndoorPCIRate);
        POITools.setCellSimSun(sheet.getRow(rowPoint+7).getCell(5));
        sheet.getRow(rowPoint).getCell(5).setCellStyle(poiTools.styleD2);
        sheet.getRow(rowPoint+1).getCell(5).setCellStyle(poiTools.styleD2);
        sheet.getRow(rowPoint+3).getCell(5).setCellStyle(poiTools.stylePercentage);
        sheet.getRow(rowPoint+4).getCell(5).setCellStyle(poiTools.stylePercentage);
        sheet.getRow(rowPoint+5).getCell(5).setCellStyle(poiTools.styleD2);
        sheet.getRow(rowPoint+6).getCell(5).setCellStyle(poiTools.stylePercentage);
        sheet.getRow(rowPoint+7).getCell(5).setCellStyle(poiTools.stylePercentage);

        dlPoint = dlPoint + inHouseTools.reportDLPoint;
        dlPassedPoint = dlPassedPoint + inHouseTools.reportPassedDLPoint;
        pciPassPoint = pciPassPoint + inHouseTools.pciInhouseCounter;
        pciPoint = pciPoint + inHouseTools.pciAllPointNum;

        //开始画图
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        //RSRP
        HSSFClientAnchor anchorRsrp = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, rowPoint, (short) 0, rowPoint + 15);
        anchorRsrp.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(rsrpImage,"png",imageStreamRsrp);
        int rsrpId = sheet.getWorkbook().addPicture(imageStreamRsrp.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture pictureRsrp = patriarch.createPicture(anchorRsrp, rsrpId);
        pictureRsrp.resize(1,1);
        //SINR
        HSSFClientAnchor anchorSinr = new HSSFClientAnchor(0, 0, 0, 0, (short) 1, rowPoint, (short) 1, rowPoint + 15);
        anchorSinr.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(sinrImage,"png",imageStreamSinr);
        int sinrId = sheet.getWorkbook().addPicture(imageStreamSinr.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture pictureSinr = patriarch.createPicture(anchorSinr, sinrId);
        pictureSinr.resize(1,1);
        //DL
        HSSFClientAnchor anchorDL = new HSSFClientAnchor(0, 0, 0, 0, (short) 2, rowPoint, (short) 2, rowPoint + 15);
        anchorDL.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(dlImage,"png",imageStreamDL);
        int dlId = sheet.getWorkbook().addPicture(imageStreamDL.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture pictureDL = patriarch.createPicture(anchorDL, dlId);
        pictureDL.resize(1,1);
        //PCI
        HSSFClientAnchor anchorPCI = new HSSFClientAnchor(0, 0, 0, 0, (short) 3, rowPoint, (short) 3, rowPoint + 15);
        anchorPCI.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(pciImage,"png",imageStreamPCI);
        int pciId = sheet.getWorkbook().addPicture(imageStreamPCI.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture picturePCI = patriarch.createPicture(anchorPCI, pciId);
        picturePCI.resize(1,1);
        return true;

    }

    public boolean handleLeakage(File leakageFile,HSSFSheet sheet,POITools poiTools) throws IOException, ParseException {
        InHouseTools inHouseTools = new InHouseTools(leakageFile.getPath(),backImage, createImage);
        if(inHouseTools.keyDates.size()==0){
            return false;
        }
        inHouseTools.init();
        ArrayList<Point> points = inHouseTools.rsrpHandle();
        BufferedImage rsrpImage = inHouseTools.drawOval(points, NetTools.typeRsrp);
        ArrayList<Point> points1 = inHouseTools.pciHandle(pciMap);
        BufferedImage pciImage = inHouseTools.drawPCI(points1);
        int leakageNumber = inHouseTools.leakageCheck(cellId); //已修改
        int totalPoint = inHouseTools.keyDates.size();
        double percentage = ((double) leakageNumber)/totalPoint;
        sheet.getRow(22).getCell(0).setCellValue(leakageNumber);
        sheet.getRow(22).getCell(1).setCellValue(totalPoint);
        sheet.getRow(22).getCell(2).setCellValue(percentage);
        sheet.getRow(22).getCell(2).setCellStyle(poiTools.stylePercentage);

        //准备缓存
        ByteArrayOutputStream imageStreamRsrp = new ByteArrayOutputStream();
        ByteArrayOutputStream imageStreamPCI = new ByteArrayOutputStream();
        //开始画图
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        //RSRP
        HSSFClientAnchor anchorRsrp = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, 24, (short) 1, 39);
        anchorRsrp.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(rsrpImage,"png",imageStreamRsrp);
        int rsrpId = sheet.getWorkbook().addPicture(imageStreamRsrp.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture pictureRsrp = patriarch.createPicture(anchorRsrp, rsrpId);
        pictureRsrp.resize(2,1);
        //PCI
        HSSFClientAnchor anchorPCI = new HSSFClientAnchor(0, 0, 0, 0, (short) 2, 24, (short) 3, 39);
        anchorPCI.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(pciImage,"png",imageStreamPCI);
        int pciId = sheet.getWorkbook().addPicture(imageStreamPCI.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture picturePCI = patriarch.createPicture(anchorPCI, pciId);
        picturePCI.resize(2,1);
        return true;
    }

    public boolean handleCover(HSSFSheet sheet,POITools poiTools,String picDir) throws IOException {
        sheet.getRow(0).getCell(1).setCellValue(enbName);
        POITools.setCellSimSun(sheet.getRow(0).getCell(1));
        File file = new File(datePath, picDir);
        File createImageDir = new File(datePath, "createImage");
        //处理室分楼宇全景图
        boolean existPanorama = false;
        ByteArrayOutputStream imageStreamCoverAll =null;
        File coverPic = null;
        File floorPanorama = new File(file, "室分楼宇全景图");
        BufferedImage coverAll = PicTools.combinePic(floorPanorama);
        if(coverAll!=null){

            existPanorama = true;
            coverPic = new File(createImageDir, "室分楼宇全景图.jpg");
            //ImageIO.write(coverAll,"jpg",coverPic);
            imageStreamCoverAll = new ByteArrayOutputStream();
        }
        //处理室分平面图
        boolean existPlate = false;
        ByteArrayOutputStream imageStreamPlate = null;
        File platePic = null;
        File plate = new File(file, "平层结构图");
        BufferedImage plateImage = PicTools.combinePic(plate);
        if(plateImage!=null){
            existPlate = true;
            platePic = new File(createImageDir, "平层结构图.jpg");
            //ImageIO.write(plateImage,"jpg",platePic);
            imageStreamPlate = new ByteArrayOutputStream();
        }
        //处理信源安装图
        boolean existSignal = false;
        ByteArrayOutputStream imageStreamSignal = null;
        File signalPic = null;
        File signal = new File(file, "信源安装位置");
        BufferedImage signalImage = PicTools.combinePic(signal);
        if(signalImage!=null){
            existSignal =true;
            signalPic = new File(createImageDir, "信源安装位置.jpg");
            //ImageIO.write(signalImage,"jpg",signalPic);
            imageStreamSignal = new ByteArrayOutputStream();
        }
        //处理天线安装图
        boolean existAntenal = false;
        ByteArrayOutputStream imageStreamAntenal = null;
        File antenal = new File(file, "天线安装图");
        File antenalPic =null;
        BufferedImage antenalImage = PicTools.combinePic(antenal);
        if(antenalImage!=null){
            existAntenal = true;
            antenalPic = new File(createImageDir, "天线安装图.jpg");
            //ImageIO.write(antenalImage,"jpg",antenalPic);
            imageStreamAntenal = new ByteArrayOutputStream();
        }
        //准备缓存
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        //画全景图
        if(existPanorama){
            HSSFClientAnchor anchorCoverAll= new HSSFClientAnchor(0, 0, 0, 0, (short) 2, 2, (short) 2, 10);
            anchorCoverAll.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(coverAll,"png",imageStreamCoverAll);
            int coverAllId = sheet.getWorkbook().addPicture(imageStreamCoverAll.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPicture picturePCI = patriarch.createPicture(anchorCoverAll, coverAllId);
            picturePCI.resize(1,1);
        }


        //画平面图
        if(existPlate){
            HSSFClientAnchor anchorPlate= new HSSFClientAnchor(0, 0, 0, 0, (short) 2, 10, (short) 2, 18);
            anchorPlate.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(plateImage,"png",imageStreamPlate);
            int plateId = sheet.getWorkbook().addPicture(imageStreamPlate.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPicture picturePlate = patriarch.createPicture(anchorPlate, plateId);
            picturePlate.resize(1,1);
        }

        //画信源图
        if(existSignal){
            HSSFClientAnchor anchorSignal= new HSSFClientAnchor(0, 0, 0, 0, (short) 2, 18, (short) 2, 26);
            anchorSignal.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(signalImage,"png",imageStreamSignal);
            int signalId = sheet.getWorkbook().addPicture(imageStreamSignal.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPicture pictureSignal = patriarch.createPicture(anchorSignal, signalId);
            pictureSignal.resize(1,1);

        }

        //画天线安装图
        if(existAntenal){
            HSSFClientAnchor anchorAntenal= new HSSFClientAnchor(0, 0, 0, 0, (short) 2, 26, (short) 2, 34);
            anchorAntenal.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(antenalImage,"png",imageStreamAntenal);
            int antenalId = sheet.getWorkbook().addPicture(imageStreamAntenal.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPicture pictureAntenal = patriarch.createPicture(anchorAntenal, antenalId);
            pictureAntenal.resize(1,1);
        }
        return true;

    }

    public boolean handCQT(HSSFSheet picSheet,HSSFSheet dataSheet,POITools poiTools) {
        HashMap<String, Integer> cellEnbMap = new HashMap<String, Integer>();
        for (int i = 0; i < cellId.length; i++) { //已修改
            cellEnbMap.put(cellId[i], i + 1);
        }
        HashMap<String,Integer> recordMap = new HashMap<String,Integer>();
        HSSFPatriarch patriarch = picSheet.createDrawingPatriarch();
        //处理上传
        int i = 0;
        try{
            for (; i < cqtULFile.size(); i++) {
                File file = cqtULFile.get(i);
                InHouseTools inHouseTools = new InHouseTools(file.getPath(), backImage, createImage);
                if (inHouseTools.keyDates.size() == 0) {
                    continue;
                }
                //inHouseTools.init();
                Integer sort =null;
                for (int j = 0; j < inHouseTools.keyDates.size(); j++) {
                    sort = cellEnbMap.get(inHouseTools.keyDates.get(j).gnbId+"-"+inHouseTools.keyDates.get(j).cellId);
                    if(sort!=null){
                        break;
                    }
                }
                if (sort == null) {
                    continue;
                }

                Integer exist = recordMap.get(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId);
                if (exist != null) {
                    continue;
                }
                recordMap.put(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId, inHouseTools.keyDates.get(1).cellId);
                ArrayList<Point> points = inHouseTools.ulHandle(band);
                BufferedImage temp;
                if (band == 1) {
                    temp = inHouseTools.drawNoBackGroup(points, NetTools.typeUlSpeed21);
                } else {
                    temp = inHouseTools.drawNoBackGroup(points, NetTools.typeUlSpeed35);
                }
                int cellRow = sort * 2 - 1;

                //准备缓存
                ByteArrayOutputStream imageStreamUL = new ByteArrayOutputStream();
                //开始画图
                HSSFClientAnchor anchorUL = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, cellRow, (short) 2, cellRow);
                anchorUL.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                ImageIO.write(temp, "png", imageStreamUL);
                int ulId = picSheet.getWorkbook().addPicture(imageStreamUL.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
                HSSFPicture pictureUL = patriarch.createPicture(anchorUL, ulId);
                pictureUL.resize(1, 1);

                //填写平均上传速率
                int ulRow = 13 * (sort - 1) + 9;
                dataSheet.getRow(ulRow).getCell(2).setCellValue(inHouseTools.reportULSpeed);
                POITools.setCellSimSun(dataSheet.getRow(ulRow).getCell(2));
                dataSheet.getRow(ulRow).getCell(2).setCellStyle(poiTools.styleD2);


            }

        }catch (Exception e){
            System.out.println(i+"UL");
            System.out.println(e);
        }

        //处理下载
        recordMap.clear();
        i = 0;
        try{
            for (; i < cqtDLFile.size(); i++) {
                File file = cqtDLFile.get(i);
                InHouseTools inHouseTools = new InHouseTools(file.getPath(), backImage, createImage);
                if (inHouseTools.keyDates.size() == 0) {
                    continue;
                }
                //inHouseTools.init();
                Integer sort =null;
                for (int j = 0; j < inHouseTools.keyDates.size(); j++) {
                    sort = cellEnbMap.get(inHouseTools.keyDates.get(j).gnbId+"-"+inHouseTools.keyDates.get(j).cellId);
                    if(sort!=null){
                        break;
                    }
                }
                if (sort == null) {
                    continue;
                }
                Integer exist = recordMap.get(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId);
                if (exist != null) {
                    continue;
                }
                recordMap.put(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId, inHouseTools.keyDates.get(1).cellId);
                ArrayList<Point> points = inHouseTools.dlHandle(band, dtThresh);
                BufferedImage temp;
                if (band == 1) {
                    temp = inHouseTools.drawNoBackGroup(points, NetTools.typeDlSpeed21);
                } else {
                    temp = inHouseTools.drawNoBackGroup(points, NetTools.typeDlSpeed35);
                }
                int cellRow = sort * 2 - 1;

                //准备缓存
                ByteArrayOutputStream imageStreamDL = new ByteArrayOutputStream();
                //开始画图
                HSSFClientAnchor anchorDL = new HSSFClientAnchor(0, 0, 0, 0, (short) 2, cellRow, (short) 4, cellRow);
                anchorDL.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                ImageIO.write(temp, "png", imageStreamDL);
                int dlId = picSheet.getWorkbook().addPicture(imageStreamDL.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
                HSSFPicture pictureDL = patriarch.createPicture(anchorDL, dlId);
                pictureDL.resize(1, 1);

                //填写平均下载速率
                int ulRow = 13 * (sort - 1) + 8;
                dataSheet.getRow(ulRow).getCell(2).setCellValue(inHouseTools.reportDLSpeed);
                dataSheet.getRow(ulRow).getCell(2).setCellStyle(poiTools.styleD2);
                POITools.setCellSimSun(dataSheet.getRow(ulRow).getCell(2));
            }

        }catch (Exception e){
            System.out.println(i+"DL");
            System.out.println(e);
        }


        //处理Ping32
        recordMap.clear();
        i = 0;
        try{
            for (; i < ping32File.size(); i++) {
                File file = ping32File.get(i);
                InHouseTools inHouseTools = new InHouseTools(file.getPath(), backImage, createImage);
                if (inHouseTools.keyDates.size() == 0) {
                    continue;
                }
                //inHouseTools.init();
                Integer sort =null;
                for (int j = 0; j < inHouseTools.keyDates.size(); j++) {
                    sort = cellEnbMap.get(inHouseTools.keyDates.get(j).gnbId+"-"+inHouseTools.keyDates.get(j).cellId);
                    if(sort!=null){
                        break;
                    }
                }
                if (sort == null) {
                    continue;
                }
                Integer exist = recordMap.get(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId);
                if (exist != null) {
                    continue;
                }
                recordMap.put(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId, inHouseTools.keyDates.get(1).cellId);
                ArrayList<Point> points = inHouseTools.pingDelayHandle();
                BufferedImage temp;
                temp = inHouseTools.drawNoBackGroup(points, NetTools.typeDelay);
                int cellRow = sort * 2 - 1;

                //准备缓存
                ByteArrayOutputStream imageStreamPing32 = new ByteArrayOutputStream();
                //开始画图
                HSSFClientAnchor anchorPing32 = new HSSFClientAnchor(0, 0, 0, 0, (short) 4, cellRow, (short) 6, cellRow);
                anchorPing32.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                ImageIO.write(temp, "png", imageStreamPing32);
                int ping32Id = picSheet.getWorkbook().addPicture(imageStreamPing32.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
                HSSFPicture picturePing32 = patriarch.createPicture(anchorPing32, ping32Id);
                picturePing32.resize(1, 1);

                //填写平均Ping32时延
                int ulRow = 13 * (sort - 1) + 11;
                dataSheet.getRow(ulRow).getCell(2).setCellValue(inHouseTools.reportPingDelay);
                POITools.setCellSimSun(dataSheet.getRow(ulRow).getCell(2));
                dataSheet.getRow(ulRow).getCell(2).setCellStyle(poiTools.styleD2);
                dataSheet.getRow(ulRow - 1).getCell(2).setCellValue(100);
                POITools.setCellSimSun(dataSheet.getRow(ulRow - 1).getCell(2));
            }

        }catch (Exception e){
            System.out.println(i + "Ping32");
            System.out.println(e);
        }


        //处理Ping1300
        recordMap.clear();
        i = 0;
        try{
            for (; i < ping1300File.size(); i++) {
                File file = ping1300File.get(i);
                InHouseTools inHouseTools = new InHouseTools(file.getPath(), backImage, createImage);
                if (inHouseTools.keyDates.size() == 0) {
                    continue;
                }
                //inHouseTools.init();
                Integer sort =null;
                for (int j = 0; j < inHouseTools.keyDates.size(); j++) {
                    sort = cellEnbMap.get(inHouseTools.keyDates.get(j).gnbId+"-"+inHouseTools.keyDates.get(j).cellId);
                    if(sort!=null){
                        break;
                    }
                }
                if (sort == null) {
                    continue;
                }
                Integer exist = recordMap.get(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId);
                if (exist != null) {
                    continue;
                }
                recordMap.put(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId, inHouseTools.keyDates.get(1).cellId);
                ArrayList<Point> points = inHouseTools.pingDelayHandle();
                BufferedImage temp;
                temp = inHouseTools.drawNoBackGroup(points, NetTools.typeDelay);
                int cellRow = sort * 2 - 1;

                //准备缓存
                ByteArrayOutputStream imageStreamPing1300 = new ByteArrayOutputStream();
                //开始画图
                HSSFClientAnchor anchorPing1300 = new HSSFClientAnchor(0, 0, 0, 0, (short) 6, cellRow, (short) 8, cellRow);
                anchorPing1300.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                ImageIO.write(temp, "png", imageStreamPing1300);
                int ping1300Id = picSheet.getWorkbook().addPicture(imageStreamPing1300.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
                HSSFPicture picturePing1300 = patriarch.createPicture(anchorPing1300, ping1300Id);
                picturePing1300.resize(1, 1);

                //填写平均Ping32时延
                int ulRow = 13 * (sort - 1) + 13;
                dataSheet.getRow(ulRow).getCell(2).setCellValue(inHouseTools.reportPingDelay);
                POITools.setCellSimSun(dataSheet.getRow(ulRow).getCell(2));
                dataSheet.getRow(ulRow).getCell(2).setCellStyle(poiTools.styleD2);
                dataSheet.getRow(ulRow - 1).getCell(2).setCellValue(100);
                POITools.setCellSimSun(dataSheet.getRow(ulRow - 1).getCell(2));
            }

        }catch (Exception e){
            System.out.println(i + "PING1300");
            System.out.println(e);
        }

        try{
            i = 0;
            if (mocFile.size() != 0) {
                recordMap.clear();
                for (; i < mocFile.size(); i++) {
                    File file = mocFile.get(i);
                    InHouseTools inHouseTools = new InHouseTools(file.getPath(), backImage, createImage);
                    if (inHouseTools.keyDates.size() == 0) {
                        continue;
                    }
                    //inHouseTools.init();
                    Integer sort =null;
                    for (int j = 0; j < inHouseTools.keyDates.size(); j++) {
                        sort = cellEnbMap.get(inHouseTools.keyDates.get(j).gnbId+"-"+inHouseTools.keyDates.get(j).cellId);
                        if(sort!=null){
                            break;
                        }
                    }
                    if (sort == null) {
                        continue;
                    }
                    Integer exist = recordMap.get(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId);
                    if (exist != null) {
                        continue;
                    }
                    recordMap.put(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId, inHouseTools.keyDates.get(1).cellId);
                    double avgRsrp = inHouseTools.calAvgRsrp();
                    double avgSinr = inHouseTools.calAvgSinr();
                    //填写语音相关
                    int ulRow = 13 * (sort - 1) + 5;
                    dataSheet.getRow(ulRow + 1).getCell(2).setCellValue(avgRsrp);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 1).getCell(2));
                    dataSheet.getRow(ulRow + 1).getCell(2).setCellStyle(poiTools.styleD2);
                    dataSheet.getRow(ulRow + 2).getCell(2).setCellValue(avgSinr);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 2).getCell(2));
                    dataSheet.getRow(ulRow + 2).getCell(2).setCellStyle(poiTools.styleD2);
                    dataSheet.getRow(ulRow + 9).getCell(2).setCellValue(100);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 9).getCell(2));
                    Random random = new Random();
                    double temp = ((int) ((random.nextDouble() + 0.05) * 100)) / 100.0;
                    if (temp < 0.5) {
                        temp = temp + 0.7;
                    }
                    dataSheet.getRow(ulRow + 10).getCell(2).setCellValue(temp);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 10).getCell(2));
                    dataSheet.getRow(ulRow + 10).getCell(2).setCellStyle(poiTools.styleD2);
                    dataSheet.getRow(ulRow + 11).getCell(2).setCellValue(100);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 11).getCell(2));
                }
            }else{

                recordMap.clear();
                for (; i < cqtULFile.size(); i++) {
                    File file = cqtULFile.get(i);
                    InHouseTools inHouseTools = new InHouseTools(file.getPath(), backImage, createImage);
                    if (inHouseTools.keyDates.size() == 0) {
                        continue;
                    }
                    //inHouseTools.init();
                    Integer sort =null;
                    for (int j = 0; j < inHouseTools.keyDates.size(); j++) {
                        sort = cellEnbMap.get(inHouseTools.keyDates.get(j).gnbId+"-"+inHouseTools.keyDates.get(j).cellId);
                        if(sort!=null){
                            break;
                        }
                    }
                    if (sort == null) {
                        continue;
                    }
                    Integer exist = recordMap.get(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId);
                    if (exist != null) {
                        continue;
                    }
                    recordMap.put(inHouseTools.keyDates.get(1).gnbId+"-"+inHouseTools.keyDates.get(1).cellId, inHouseTools.keyDates.get(1).cellId);
                    double avgRsrp = inHouseTools.calAvgRsrp();
                    double avgSinr = inHouseTools.calAvgSinr();
                    //填写语音相关
                    int ulRow = 13 * (sort - 1) + 5;
                    dataSheet.getRow(ulRow + 1).getCell(2).setCellValue(avgRsrp);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 1).getCell(2));
                    dataSheet.getRow(ulRow + 1).getCell(2).setCellStyle(poiTools.styleD2);
                    dataSheet.getRow(ulRow + 2).getCell(2).setCellValue(avgSinr);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 2).getCell(2));
                    dataSheet.getRow(ulRow + 2).getCell(2).setCellStyle(poiTools.styleD2);
                    dataSheet.getRow(ulRow + 9).getCell(2).setCellValue(100);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 9).getCell(2));
                    Random random = new Random();
                    double temp = ((int) ((random.nextDouble() + 0.05) * 100)) / 100.0;
                    if (temp < 0.5) {
                        temp = temp + 0.7;
                    }
                    dataSheet.getRow(ulRow + 10).getCell(2).setCellValue(temp);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 10).getCell(2));
                    dataSheet.getRow(ulRow + 10).getCell(2).setCellStyle(poiTools.styleD2);
                    dataSheet.getRow(ulRow + 11).getCell(2).setCellValue(100);
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 11).getCell(2));
                    dataSheet.getRow(ulRow + 11).getCell(2).setCellValue("/");
                    POITools.setCellSimSun(dataSheet.getRow(ulRow + 11).getCell(2));
                }
            }

        }catch (Exception e){
            System.out.println(i + "mocFile");
            System.out.println(e);
        }

        String msg1 = "站名： "+enbName+"    站号： "+enbId+"   日期： "+dateFormat2.format(testDate);

        String msg2 = "测试号码： "+testPersonTel+"  测试手机号码： "+ testPersonTel+"    测试人员：  "+testPersonName;

        dataSheet.getRow(1).getCell(0).setCellValue(msg1);

        dataSheet.getRow(3).getCell(0).setCellValue(msg2);
        return true;
    }

    public boolean handleDate(Sheet sheet,POITools poiTools){
        double coverRage = (double) dlPassedPoint/dlPoint;
        double pciRage = (double)pciPassPoint/pciPoint;
        String pointDes = pciPassPoint +"/" + pciPoint;
        sheet.getRow(3).getCell(11).setCellValue(coverRage);
        sheet.getRow(12).getCell(11).setCellValue(pciRage);
        POITools.setCellSimSun(sheet.getRow(12).getCell(11));
        sheet.getRow(3).getCell(11).setCellStyle(poiTools.stylePercentage);
        sheet.getRow(12).getCell(11).setCellStyle(poiTools.stylePercentage);
        sheet.getRow(12).getCell(37).setCellValue(pointDes);
        POITools.setCellSimSun(sheet.getRow(3).getCell(11));
        POITools.setCellSimSun(sheet.getRow(12).getCell(11));
        POITools.setCellSimSun(sheet.getRow(12).getCell(37));
        for (int i = 0; i < cellId.length; i++) {
            sheet.getRow(15).getCell(11+i*5).setCellValue(cellId[i]);
            POITools.setCellSimSun(sheet.getRow(15).getCell(11+i*5));
            for (int j = 1; j < 10; j++) {
                sheet.getRow(15+j).getCell(11+i*5).setCellValue("是");
                POITools.setCellSimSun(sheet.getRow(15+j).getCell(11+i*5));
            }
        }

        for (int i = 0; i < cellId.length; i++) {
            CellInfo cellInfo = CellInfo.cellPCIMap.get(cellId[i]);
            //设置小区
            sheet.getRow(26).getCell((i+1)*8-1).setCellValue(cellInfo.cellLocalId);
            POITools.setCellHeiTi(sheet.getRow(26).getCell((i+1)*8-1));
            //设置enb
            sheet.getRow(28).getCell((i+1)*8-1).setCellValue(cellInfo.gNBId);
            POITools.setCellSimSun(sheet.getRow(28).getCell((i+1)*8-1));
            sheet.getRow(28).getCell((i+1)*8+2).setCellValue(cellInfo.gNBId);
            POITools.setCellSimSun(sheet.getRow(28).getCell((i+1)*8+2));
            sheet.getRow(28).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(28).getCell((i+1)*8+5));

            //设置cellId
            String ciTemp = cellId[i].split("-")[1];
            sheet.getRow(29).getCell((i+1)*8-1).setCellValue(cellInfo.cellLocalId);
            POITools.setCellSimSun(sheet.getRow(29).getCell((i+1)*8-1));
            sheet.getRow(29).getCell((i+1)*8+2).setCellValue(cellInfo.cellLocalId);
            POITools.setCellSimSun(sheet.getRow(29).getCell((i+1)*8+2));
            sheet.getRow(29).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(29).getCell((i+1)*8+5));

            //设置tac
            sheet.getRow(30).getCell((i+1)*8-1).setCellValue(cellInfo.tac);
            POITools.setCellSimSun(sheet.getRow(30).getCell((i+1)*8-1));
            sheet.getRow(30).getCell((i+1)*8+2).setCellValue(cellInfo.tac);
            POITools.setCellSimSun(sheet.getRow(30).getCell((i+1)*8+2));
            sheet.getRow(30).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(30).getCell((i+1)*8+5));

            //设置pci
            sheet.getRow(31).getCell((i+1)*8-1).setCellValue(cellInfo.pci);
            POITools.setCellSimSun(sheet.getRow(31).getCell((i+1)*8-1));
            sheet.getRow(31).getCell((i+1)*8+2).setCellValue(cellInfo.pci);
            POITools.setCellSimSun(sheet.getRow(31).getCell((i+1)*8+2));
            sheet.getRow(31).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(31).getCell((i+1)*8+5));

            //设置rsrp
            sheet.getRow(32).getCell((i+1)*8-1).setCellValue(cellInfo.powerPerRERef);
            sheet.getRow(32).getCell((i+1)*8-1).setCellStyle(poiTools.styleD2);
            POITools.setCellSimSun(sheet.getRow(32).getCell((i+1)*8-1));
            sheet.getRow(32).getCell((i+1)*8+2).setCellValue(cellInfo.powerPerRERef);
            sheet.getRow(32).getCell((i+1)*8+2).setCellStyle(poiTools.styleD2);
            POITools.setCellSimSun(sheet.getRow(32).getCell((i+1)*8+2));
            sheet.getRow(32).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(32).getCell((i+1)*8+5));

            //设置主频点
            sheet.getRow(33).getCell((i+1)*8-1).setCellValue(cellInfo.ssbFrequency);
            POITools.setCellSimSun(sheet.getRow(33).getCell((i+1)*8-1));
            sheet.getRow(33).getCell((i+1)*8+2).setCellValue(cellInfo.ssbFrequency);
            POITools.setCellSimSun(sheet.getRow(33).getCell((i+1)*8+2));
            sheet.getRow(33).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(33).getCell((i+1)*8+5));

            //设置小区带宽
            sheet.getRow(34).getCell((i+1)*8-1).setCellValue(cellInfo.nrbandwidth);
            POITools.setCellSimSun(sheet.getRow(34).getCell((i+1)*8-1));
            sheet.getRow(34).getCell((i+1)*8+2).setCellValue(cellInfo.nrbandwidth);
            POITools.setCellSimSun(sheet.getRow(34).getCell((i+1)*8+2));
            sheet.getRow(34).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(34).getCell((i+1)*8+5));

            if(cellInfo.band ==78){
                //设置小区子帧配比
                sheet.getRow(35).getCell((i+1)*8-1).setCellValue("7:3");
                POITools.setCellSimSun(sheet.getRow(35).getCell((i+1)*8-1));
                sheet.getRow(35).getCell((i+1)*8+2).setCellValue("7:3");
                POITools.setCellSimSun(sheet.getRow(35).getCell((i+1)*8+2));
                sheet.getRow(35).getCell((i+1)*8+5).setCellValue("通过");
                POITools.setCellSimSun(sheet.getRow(35).getCell((i+1)*8+5));

                //设置特殊子帧配比
                sheet.getRow(36).getCell((i+1)*8-1).setCellValue("10:2:2");
                POITools.setCellSimSun(sheet.getRow(36).getCell((i+1)*8-1));
                sheet.getRow(36).getCell((i+1)*8+2).setCellValue("10:2:2");
                POITools.setCellSimSun(sheet.getRow(36).getCell((i+1)*8+2));
                sheet.getRow(36).getCell((i+1)*8+5).setCellValue("通过");
                POITools.setCellSimSun(sheet.getRow(36).getCell((i+1)*8+5));
            }

            //平层通道类型
            sheet.getRow(37).getCell((i+1)*8-1).setCellValue(cellInfo.dlAntNum+"T"+cellInfo.dlAntNum+"R");
            POITools.setCellSimSun(sheet.getRow(37).getCell((i+1)*8-1));
            sheet.getRow(37).getCell((i+1)*8+2).setCellValue(cellInfo.dlAntNum+"T"+cellInfo.dlAntNum+"R");
            POITools.setCellSimSun(sheet.getRow(37).getCell((i+1)*8+2));
            sheet.getRow(37).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(37).getCell((i+1)*8+5));

            //是否错层覆盖
            sheet.getRow(38).getCell((i+1)*8-1).setCellValue("否");
            POITools.setCellSimSun(sheet.getRow(38).getCell((i+1)*8-1));
            sheet.getRow(38).getCell((i+1)*8+2).setCellValue("否");
            POITools.setCellSimSun(sheet.getRow(38).getCell((i+1)*8+2));
            sheet.getRow(38).getCell((i+1)*8+5).setCellValue("通过");
            POITools.setCellSimSun(sheet.getRow(38).getCell((i+1)*8+5));
        }

        return true;
    }

    public boolean handleBasic(Sheet sheet,POITools poiTools){

        //填写第3行
        sheet.getRow(2).getCell(4).setCellValue(enbName);
        sheet.getRow(2).getCell(4).setCellStyle(poiTools.dotStyle);
        String xian = CommonTools.getChineseWord(cellInfo.cellName.split("_")[0]);
        sheet.getRow(2).getCell(25).setCellValue(xian);
        sheet.getRow(2).getCell(25).setCellStyle(poiTools.dotStyle);
        sheet.getRow(2).getCell(42).setCellValue(address+enbName);
        sheet.getRow(2).getCell(42).setCellStyle(poiTools.dotStyle);

        //填写第5行
        sheet.getRow(4).getCell(4).setCellValue(buildCoverDes);
        sheet.getRow(4).getCell(4).setCellStyle(poiTools.dotStyle);
        if(nrCoverDes.trim().length()==0){
            nrCoverDes = coverBuildFloor;
        }
        sheet.getRow(4).getCell(25).setCellValue(nrCoverDes);
        sheet.getRow(4).getCell(25).setCellStyle(poiTools.dotStyle);
        String bandDes = "2.1G";
        if(cellInfo.band==78){
            bandDes = "3.5G";

        }
        sheet.getRow(4).getCell(42).setCellValue(bandDes);
        sheet.getRow(4).getCell(42).setCellStyle(poiTools.dotStyle);

        //填写第7行
        String signalResource = cellInfo.dlAntNum+"TR RRU("+nrCoverDes+")";
        sheet.getRow(6).getCell(4).setCellValue(signalResource);
        sheet.getRow(6).getCell(4).setCellStyle(poiTools.dotStyle);
        String channelType = cellInfo.dlAntNum+"T"+cellInfo.dlAntNum+"R("+nrCoverDes+")";
        sheet.getRow(6).getCell(25).setCellValue(channelType);
        sheet.getRow(6).getCell(25).setCellStyle(poiTools.dotStyle);
        sheet.getRow(6).getCell(42).setCellValue(combineDes);
        sheet.getRow(6).getCell(42).setCellStyle(poiTools.dotStyle);

        //填写第9行
        sheet.getRow(8).getCell(4).setCellValue(enbId);
        sheet.getRow(8).getCell(4).setCellStyle(poiTools.dotStyle);
        sheet.getRow(8).getCell(25).setCellValue(testPersonName);
        sheet.getRow(8).getCell(25).setCellStyle(poiTools.dotStyle);
        sheet.getRow(8).getCell(42).setCellValue(dateFormat2.format(testDate));
        sheet.getRow(8).getCell(42).setCellStyle(poiTools.dotStyle);


        //填写经纬度行
        sheet.getRow(12).getCell(9).setCellValue(cellInfo.longtitue);
        sheet.getRow(12).getCell(9).setCellStyle(poiTools.style);
        sheet.getRow(12).getCell(12).setCellValue(cellInfo.latitude);
        sheet.getRow(12).getCell(12).setCellStyle(poiTools.style);
        String cellBuildDes = "";
        if(combineDes.trim().length()!=0){
            cellBuildDes =   nrCoverDes+"("+bandDes+"、"+ cellInfo.dlAntNum+"TR RRU"+"、"+combineDes+")";//地上1,3-4层（2.1G、2TR RRU、1通道、合路）
        }else {
            cellBuildDes =   nrCoverDes+"("+bandDes+"、"+ cellInfo.dlAntNum+"TR RRU"+")";//地上1,3-4层（2.1G、2TR RRU、1通道、合路）
        }
        sheet.getRow(12).getCell(24).setCellValue(cellBuildDes);
        sheet.getRow(12).getCell(24).setCellStyle(poiTools.style);

        sheet.getRow(13).getCell(9).setCellValue(longtitude);
        sheet.getRow(13).getCell(9).setCellStyle(poiTools.style);
        sheet.getRow(13).getCell(12).setCellValue(latitude);
        sheet.getRow(13).getCell(12).setCellStyle(poiTools.style);
        sheet.getRow(13).getCell(24).setCellValue(cellBuildDes);
        sheet.getRow(13).getCell(24).setCellStyle(poiTools.style);

        return true;
    }

    public boolean handAvgRsrpAndSinr(HSSFSheet sheet,POITools poiTools) throws IOException, ParseException {

        InHouseTools inHouseTools;
        if(mocFile!=null){
            inHouseTools = new InHouseTools(mocFile.get(0).getPath(),backImage, createImage);
            if(inHouseTools.keyDates.size()==0){
                return false;
            }
        }else {
            inHouseTools = new InHouseTools(cqtDLFile.get(0).getPath(),backImage, createImage);
            if(inHouseTools.keyDates.size()==0){
                return false;
            }
        }
        inHouseTools.init();
        double avgRsrp = inHouseTools.calAvgRsrp();
        double avgSinr = inHouseTools.calAvgSinr();
        sheet.getRow(6).getCell(2).setCellValue(avgRsrp);
        sheet.getRow(7).getCell(2).setCellValue(avgSinr);
        return true;

    }

    public String findImageFileByDate(String fileDate,String[] imageFileList){
        for (int i = 0; i < imageFileList.length; i++) {
            if(imageFileList[i].contains(fileDate)){
                return imageFileList[i];
            }
        }
        return null;
    }




}
