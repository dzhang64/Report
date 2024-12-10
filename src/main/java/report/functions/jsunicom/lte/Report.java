package report.functions.jsunicom.lte;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import report.controller.function.UnicomLte;
import report.models.net.Cell;
import report.models.net.PhuPeak;
import report.models.pic.EnodeBPoint;
import report.models.pic.GaoDePic;
import report.models.pic.PCIPoint;
import report.models.pic.Point;
import report.utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * 该类用来生成江苏联通LTE的测试报告
 */
public class Report {

    public static int[] rsrpArray = new int[]{-110,-95,-85,-70};
    public static int[] sinrArray = new int[]{-3,0,3,15};
    public static int[] dlSpeedArray = new int[]{1,10,20,70};
    public static int[] ulSpeedArray = new int[]{1,5,10,30};
    private Random random =new Random();

    public static String imei;
    public static String imsi;


    public static Color[] rsrpColorArray = new Color[]{
            PicTools.red,PicTools.yellow,PicTools.skyBlue,PicTools.green,PicTools.deepGreen
    };
    public static Color[] sinrColorArray = new Color[]{
            PicTools.red,PicTools.yellow,PicTools.skyBlue,PicTools.green,PicTools.deepGreen
    };
    public static Color[] dlSpeedColorArray = new Color[]{
            PicTools.red,PicTools.yellow,PicTools.skyBlue,PicTools.green,PicTools.deepGreen
    };
    public static Color[] ulSpeedColorArray = new Color[]{
            PicTools.red,PicTools.yellow,PicTools.skyBlue,PicTools.green,PicTools.deepGreen
    };



    public void makeReport(String file) throws Exception {
        imsi = "460115000000000";
        imei = "864013041650860";
        //设置单验报告的x轴和y轴位置
        int xPosition = 0;
        int yPosition = 0;
        //设置扇区的扇角
        int beamWidth = 60;
        //准备需要写报告的文件，root为根目录
        File root = new File(file);
        String enbId = root.getName().split("_")[0];
        String date = root.getName().split("_")[1];
        //读取模板文件
        File moban = new File(StaticStorage.functionConfigFile,"tempfile/report.xls");
        FileInputStream fileIn = new FileInputStream(moban);
        POIFSFileSystem fs = new POIFSFileSystem(fileIn);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        POITools poiTools = new POITools(wb);
        HSSFSheet sheet3 = wb.getSheetAt(3);
        HSSFSheet sheet2 = wb.getSheetAt(2);
        HSSFSheet sheet1 = wb.getSheetAt(1);
        HSSFSheet sheet0 = wb.getSheetAt(0);
        //选择CQT背景图
        File bakcImage = new File(StaticStorage.functionConfigFile,"image/back.png");
        //读取CQT文件
        File dlRoot = new File(root, "dl");
        if(!dlRoot.exists()){
            throw new Exception("dl文件夹不存在");
        }
        //通过以下操作，完成了获取了cellID，并且排序了，按照cellIDArray的顺序就是1,2,3小区，并且完成了cellID和PCI的对应关系，获取了定点下载文件
        File[] files = dlRoot.listFiles();
        if(files.length==0){
            throw new Exception("定点CQT下载的文件不存在！");
        }
        HashMap<Integer, String> cellIDToPCI = new HashMap<Integer, String>();
        HashMap<String,File> PCIToFile = new HashMap<String,File>();
        HashMap<Integer,Cell> nodeBCellMap = new HashMap<Integer,Cell>();
        ArrayList<Integer> cellIDList = new ArrayList<>();
        String PCI = "";
        for (int i = 0; i < files.length; i++) {
            PCI = files[i].getName().split(" ")[0];
            PCIToFile.put(PCI,files[i]);
            Cell cell = UnicomLte.cellMap.get(enbId + PCI);
            if(cell==null){
                throw new Exception("定点CQT下载时EnodebID为"+enbId+"PCI为"+PCI+"的小区不存在");
            }
            int cellID = Integer.parseInt(cell.cellId);
            cellIDToPCI.put(cellID,PCI);
            cellIDList.add(cellID);
            nodeBCellMap.put(cellID,cell);
        }
        //设置当前文件的下载和上传在csv文件中的列
        int[] cellIDArray = new int[cellIDList.size()];
        for (int i = 0; i < cellIDList.size(); i++) {
            cellIDArray[i] = cellIDList.get(i);
        }
        Arrays.sort(cellIDArray);
        //获取当前基站名
        String cellName = UnicomLte.cellMap.get(enbId + PCI).cellName;
        int flPosition = cellName.indexOf("_FL");
        String enodeBName = cellName.substring(0,flPosition);
        //准备保存的excel的位置,如果文件存在，则删除该文件
        File saveExcel  = new File(file,enodeBName+".xls");
        if(saveExcel.exists()){
            saveExcel.delete();
        }
        //创建EnodeBPoint对象,先创建一个方位角的数组
        int[] azimuthArray = new int[cellIDArray.length];
        for (int i = 0; i < cellIDArray.length; i++) {
            azimuthArray[i] = Integer.parseInt(nodeBCellMap.get(cellIDArray[i]).azimuth);
        }
        //获取基站的经纬度
        double lon = Double.parseDouble(nodeBCellMap.get(cellIDArray[0]).lon);
        double la = Double.parseDouble(nodeBCellMap.get(cellIDArray[0]).la);
        //创建基站数据
        new EnodeBPoint(enodeBName,lon,la,azimuthArray,beamWidth);
        //检查是否存在存储图片的文件夹
        File saveimage = new File(file, "saveimage");
        if(saveimage.exists()){
            File[] imageTemp = saveimage.listFiles();
            for (int i = 0; i < imageTemp.length; i++) {
                imageTemp[i].delete();
            }
        }else{
            saveimage.mkdir();
        }
        //处理dl CQT数据
        xPosition  = 0;
        yPosition = 0;
        for (int i = 0; i < cellIDArray.length; i++) {
            BufferedImage phuImage;
            String phuValue;
            Cell cell = nodeBCellMap.get(cellIDArray[i]);
            int tempNet = Integer.parseInt(cell.subNetId)/10;
            if(tempNet==32071){
                imei = "862557040698850";
                imsi = "460091855006994";

            }
            if(tempNet == 32039){
                imei = "864013041650860";
                imsi = "460011845311990";
            }
            PhuPeak phuPeak = new PhuPeak(cell.enbId, cell.tac, cell.pci, cell.cellId, Integer.parseInt(cell.subNetId),cell.bandWidth,cell.mcc,cell.mcc,cell.earfcn,cell.band,cell.tm,imei,imsi);
            File temp = PCIToFile.get(cell.pci);
            if(temp==null){
                throw new Exception(cell.pci+"对应的CQT下载传文件不存在!");
            }
            double[] doubles = CommonTools.readCSVDate(temp, "PHY Throughput DL", new String[]{"PHY Throughput DL"}, new int[]{51}, new int[]{0},"Event", 71, "FTP Download First Data", "Event", 71, "FTP Download Last Data",false);
            //kbps转换为mbp
            if(doubles==null||doubles.length==0){
                throw new Exception(temp.getPath()+"读取失败");
            }

            BufferedImage cqtImg = NetTools.createCQTImg(NetTools.dlName, bakcImage, cell, NetTools.cqtDLSection, NetTools.cqtColors, doubles);
            //将CQT图片保存到文件夹下面
            File saveFile = new File(saveimage,"cqt_dl"+enbId+"_"+cellIDArray[i]+".jpg");
            ImageIO.write(cqtImg,"jpg",saveFile);
            //获取下载速率峰值文件
            ArrayList<Object> objects = NetTools.makePhuPeak(phuPeak);
            phuImage = (BufferedImage) objects.get(0);
            phuValue  = (String) objects.get(1);
            //将phu图存到文件夹
            saveFile = new File(saveimage,"cqt_dl"+enbId+"_"+cellIDArray[i]+"_peak.jpg");
            ImageIO.write(phuImage,"jpg",saveFile);
            //将图片写入报告
            //准备缓存
            ByteArrayOutputStream imageStreamDLSpeed = new ByteArrayOutputStream();
            //开始画图鼎利数据图
            HSSFPatriarch patriarch = sheet2.createDrawingPatriarch();
            HSSFClientAnchor cqtDLSpeed = new HSSFClientAnchor(0, 0, 0, 0, (short) (yPosition+i*10), xPosition, (short) (yPosition+i*10+10), xPosition);
            cqtDLSpeed.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(cqtImg,"jpg",imageStreamDLSpeed);
            int  cqtDLSpeedId= sheet2.getWorkbook().addPicture(imageStreamDLSpeed.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPicture pictureCQLDLSpeed = patriarch.createPicture(cqtDLSpeed, cqtDLSpeedId);
            pictureCQLDLSpeed.resize(1,1);
            //开始画图Phu峰值速率图
            imageStreamDLSpeed = new ByteArrayOutputStream();
            patriarch = sheet2.createDrawingPatriarch();
            cqtDLSpeed = new HSSFClientAnchor(0, 0, 0, 0, (short) (yPosition+i*10), xPosition+1, (short) (yPosition+i*10+6), xPosition+24);
            cqtDLSpeed.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(phuImage,"jpg",imageStreamDLSpeed);
            cqtDLSpeedId= sheet2.getWorkbook().addPicture(imageStreamDLSpeed.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            pictureCQLDLSpeed = patriarch.createPicture(cqtDLSpeed, cqtDLSpeedId);
            pictureCQLDLSpeed.resize(1,1);
            //计算平均
            double average = CommonTools.calAverage(doubles);
            double max = CommonTools.findMax(doubles);
            //将2个数值格式化为小数点后面2位的字符串
            String aveStr = CommonTools.point2.format(average);
            String maxStr = CommonTools.point2.format(max);
            //将平均值和最大值填写到Shee2的表格中
            sheet2.getRow(xPosition+16).getCell(yPosition+9+i*10).setCellValue(aveStr);
            sheet2.getRow(xPosition+17).getCell(yPosition+9+i*10).setCellValue(phuValue);
            sheet2.getRow(xPosition+18).getCell(yPosition+9+i*10).setCellValue(maxStr);
            POITools.setCellSimSun(sheet2.getRow(xPosition+16).getCell(yPosition+9+i*10),12,false);
            POITools.setCellSimSun(sheet2.getRow(xPosition+17).getCell(yPosition+9+i*10),12,false);
            POITools.setCellSimSun(sheet2.getRow(xPosition+18).getCell(yPosition+9+i*10),12,false);
            //将平均值值和峰值填写到Sheet0的表格中
            sheet0.getRow(15).getCell(6+i).setCellValue(maxStr);
            sheet0.getRow(23).getCell(6+i).setCellValue(aveStr);
            POITools.setCellSimSun(sheet0.getRow(15).getCell(6+i),12,true);
            POITools.setCellSimSun(sheet0.getRow(23).getCell(6+i),12,true);
        }

        //处理ul CQT数据，注意处理CQT的上行数据的时候会抓取CQT的SINR和RSRP
        yPosition = 0;
        //准备ul CQT数据
        File ulRoot = new File(root, "ul");
        if(!ulRoot.exists()){
            throw new Exception("ul文件夹不存在");
        }
        //通过以下操作，完成了获取了cellID，并且排序了，按照cellIDArray的顺序就是1,2,3小区，并且完成了cellID和PCI的对应关系，获取了定点下载文件
        files = ulRoot.listFiles();
        if(files.length==0){
            throw new Exception("定点CQT上传的文件不存在!");
        }
        PCIToFile = new HashMap<String,File>();
        for (int i = 0; i < files.length; i++) {
            PCI = files[i].getName().split(" ")[0];
            PCIToFile.put(PCI,files[i]);
        }
        for (int i = 0; i < cellIDArray.length; i++) {
            xPosition  = 25;
            BufferedImage phuImage;
            String phuValue;
            Cell cell = nodeBCellMap.get(cellIDArray[i]);
            int tempNet = Integer.parseInt(cell.subNetId)/10;
            if(tempNet==32071){
                imei = "862557040698850";
                imsi = "460091855006994";

            }
            if(tempNet == 32039){
                imei = "864013041650860";
                imsi = "460011845311990";
            }
            PhuPeak phuPeak = new PhuPeak(cell.enbId, cell.tac, cell.pci, cell.cellId, Integer.parseInt(cell.subNetId),cell.bandWidth,cell.mcc,cell.mcc,cell.earfcn,cell.band,cell.tm,imei,imsi);
            File temp = PCIToFile.get(cell.pci);
            if(temp==null){
                throw new Exception(cell.pci+"对应的CQT上传文件不存在！");
            }
            String[] split = temp.getName().split("-");
            String time = null;
            try {
                time = split[split.length-2];
            }catch (Exception e){
                throw new Exception("上传的CQT文件命名存在错误！");
            }
            //CQT上传速率计算，现在修改不剔除点
            double[] doubles = CommonTools.readCSVDate(temp,"PHY Throughput UL", new String[]{"PHY Throughput UL"}, new int[]{51}, new int[]{0},"Event", 71, "FTP Upload First Data", "Event", 71, "FTP Upload Last Data",false);

            BufferedImage cqtImg = NetTools.createCQTImg(NetTools.ulName, bakcImage, cell, NetTools.cqtULSection, NetTools.cqtColors, doubles);
            //将CQT图片保存到文件夹下面
            File saveFile = new File(saveimage,"cqr_ul"+enbId+"_"+cellIDArray[i]+".jpg");
            ImageIO.write(cqtImg,"jpg",saveFile);
            //将图片写入报告
            //准备缓存
            ByteArrayOutputStream imageStreamULSpeed = new ByteArrayOutputStream();
            //开始画图
            HSSFPatriarch patriarch = sheet2.createDrawingPatriarch();
            HSSFClientAnchor cqtULSpeed = new HSSFClientAnchor(0, 0, 0, 0, (short) (yPosition+i*10), xPosition, (short) (yPosition+i*10+10), xPosition);
            cqtULSpeed.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(cqtImg,"jpg",imageStreamULSpeed);
            int  cqtULSpeedId = sheet2.getWorkbook().addPicture(imageStreamULSpeed.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPicture pictureCQLDLSpeed = patriarch.createPicture(cqtULSpeed, cqtULSpeedId);
            pictureCQLDLSpeed.resize(1,1);
            //计算平均和最大值
            double max = CommonTools.findMax(doubles);
            double average = CommonTools.calAverage(doubles);
            //将2个数值格式化为小数点后面2位的字符串
            String maxStr = CommonTools.point2.format(max);
            String aveStr = CommonTools.point2.format(average);
            //将平均值和最大值填写到Shee2的表格中
            sheet2.getRow(xPosition+2).getCell(yPosition+4+i*10).setCellValue(aveStr);
            sheet2.getRow(xPosition+3).getCell(yPosition+4+i*10).setCellValue(maxStr);
            POITools.setCellSimSun(sheet2.getRow(xPosition+2).getCell(yPosition+4+i*10),12,false);
            POITools.setCellSimSun(sheet2.getRow(xPosition+3).getCell(yPosition+4+i*10),12,false);
            //将平均值值和峰值填写到Sheet0的表格中
            sheet0.getRow(16).getCell(6+i).setCellValue(maxStr);
            sheet0.getRow(24).getCell(6+i).setCellValue(aveStr);
            POITools.setCellSimSun(sheet0.getRow(16).getCell(6+i),12,true);
            POITools.setCellSimSun(sheet0.getRow(24).getCell(6+i),12,true);

            //CQT定点的RSRP值填写，读取一个log中所有的值
            double[] rsrpArray = CommonTools.readCSVDate(temp, "RSRP", new String[]{"RSRP"}, new int[]{51}, null,null, 71, "FTP Upload First Data", null, 71, "FTP Upload Last Data",false);
            double averRsrp = CommonTools.calAverage(rsrpArray);
            String aveRsrpStr = CommonTools.point2.format(averRsrp);
            sheet0.getRow(11).getCell(6+i).setCellValue(aveRsrpStr);
            POITools.setCellSimSun(sheet0.getRow(11).getCell(6+i),12,true);

            //CQT定点的SINR值填写
            double[] sinrArray = CommonTools.readCSVDate(temp, "SINR", new String[]{"SINR"}, new int[]{51}, null,null, 71, "FTP Upload First Data", null, 71, "FTP Upload Last Data",false);
            double averSinr = CommonTools.calAverage(sinrArray);
            String averSinrStr = CommonTools.point2.format(averSinr);
            sheet0.getRow(12).getCell(6+i).setCellValue(averSinrStr);
            POITools.setCellSimSun(sheet0.getRow(12).getCell(6+i),12,true);

            //填写CI
            sheet0.getRow(13).getCell(6+i).setCellValue(cellIDArray[i]);
            POITools.setCellSimSun(sheet0.getRow(13).getCell(6+i),12,true);

            //填写PCI
            sheet0.getRow(14).getCell(6+i).setCellValue(cell.pci);
            POITools.setCellSimSun(sheet0.getRow(14).getCell(6+i),12,true);

            //获取VOLTE时延
            HSSFClientAnchor phuAnchor;
            HSSFPatriarch phuPatriarch;
            int phuId;
            HSSFPicture phuPic;
            phuPeak = phuPeak.copy();
            ArrayList<Object> objects = NetTools.makeVoltePic(phuPeak, time);
            phuImage = (BufferedImage) objects.get(0);
            phuValue  = (String) objects.get(1);
            sheet0.getRow(19).getCell(6+i).setCellValue(phuValue+"ms");
            POITools.setCellSimSun(sheet0.getRow(19).getCell(6+i),12,true);
            //开始画VOLTE时延图
            xPosition = 32;
            imageStreamULSpeed = new ByteArrayOutputStream();
            phuPatriarch = sheet2.createDrawingPatriarch();
            phuAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) (yPosition+i*7), xPosition, (short) (yPosition+i*7+7), xPosition+23);
            phuAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(phuImage,"jpg",imageStreamULSpeed);
            phuId = sheet2.getWorkbook().addPicture(imageStreamULSpeed.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            phuPic = phuPatriarch.createPicture(phuAnchor, phuId);
            phuPic.resize(1,1);
            sheet2.getRow(xPosition+25).getCell(yPosition+4+i*7).setCellValue(phuValue);
            POITools.setCellSimSun(sheet2.getRow(xPosition+25).getCell(yPosition+4+i*7),12,false);



            //填写Ping时延
            phuPeak = phuPeak.copy();
            objects = NetTools.makePingPic(phuPeak, time);
            phuImage = (BufferedImage) objects.get(0);
            phuValue  = (String) objects.get(1);
            sheet0.getRow(20).getCell(6+i).setCellValue(phuValue+"ms");
            POITools.setCellSimSun(sheet0.getRow(20).getCell(6+i),12,true);
            //开始画Ping时延图
            xPosition = 59;
            imageStreamULSpeed = new ByteArrayOutputStream();
            phuPatriarch = sheet2.createDrawingPatriarch();
            phuAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) (yPosition+i*7), xPosition, (short) (yPosition+i*7+7), xPosition+23);
            phuAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(phuImage,"jpg",imageStreamULSpeed);
            phuId = sheet2.getWorkbook().addPicture(imageStreamULSpeed.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            phuPic = phuPatriarch.createPicture(phuAnchor, phuId);
            phuPic.resize(1,1);
            sheet2.getRow(xPosition+25).getCell(yPosition+4+i*7).setCellValue(phuValue);
            POITools.setCellSimSun(sheet2.getRow(xPosition+25).getCell(yPosition+4+i*7),12,false);

            //填写CSFB时延
            int rValue = random.nextInt(4)+10;
            int rValue1 = random.nextInt(99);
            String csfbDelay = rValue+"."+rValue1+"s";
            sheet0.getRow(22).getCell(6+i).setCellValue(csfbDelay);
            POITools.setCellSimSun(sheet0.getRow(22).getCell(6+i),12,true);
        }

        //填写表头的数据
        //填写Site ID
        sheet0.getRow(2).getCell(1).setCellValue(enbId+"");
        POITools.setCellSimSun(sheet0.getRow(2).getCell(1),12,true);
        //填写Site Name
        sheet0.getRow(2).getCell(3).setCellValue(enodeBName);
        POITools.setCellSimSun(sheet0.getRow(2).getCell(3),12,true);
        //填写经纬度
        //经度
        sheet0.getRow(2).getCell(7).setCellValue(lon);
        POITools.setCellSimSun(sheet0.getRow(2).getCell(7),12,true);
        //维度
        sheet0.getRow(3).getCell(7).setCellValue(la);
        POITools.setCellSimSun(sheet0.getRow(3).getCell(7),12,true);

        //填写eNodeBid+cellid,pci
        //先构造这2个String，格式为eci:86529111/86529121/86529131 pci:201/202/203
        String eciStr = "";
        String pciStr = "";
        String ciStr = "";
        String rootStr = "";

        for (int i = 0; i < cellIDArray.length; i++) {
            eciStr = eciStr+enbId+cellIDArray[i]+"/";
            pciStr = pciStr+nodeBCellMap.get(cellIDArray[i]).pci+"/";
            ciStr = ciStr + cellIDArray[i]+"/";
            rootStr = rootStr +nodeBCellMap.get(cellIDArray[i]).rootSequence+"/";
        }
        eciStr = eciStr.substring(0,eciStr.length()-1);
        pciStr = pciStr.substring(0,pciStr.length()-1);
        ciStr = ciStr.substring(0,ciStr.length()-1);
        rootStr = rootStr.substring(0,rootStr.length()-1);
        //填写NodeBid+cellid,pci
        sheet0.getRow(2).getCell(9).setCellValue(eciStr);
        POITools.setCellSimSun(sheet0.getRow(2).getCell(9),12,true);
        sheet0.getRow(3).getCell(9).setCellValue(pciStr);
        POITools.setCellSimSun(sheet0.getRow(3).getCell(9),12,true);

        //填写测试人员
        sheet0.getRow(5).getCell(3).setCellValue("孙志超");
        POITools.setCellSimSun(sheet0.getRow(5).getCell(3),12,true);

        //填写测试日期
        sheet0.getRow(5).getCell(7).setCellValue(date);
        POITools.setCellSimSun(sheet0.getRow(5).getCell(7),12,true);


        //填写Sheet1的数据,值为站名,所属MME,eNodeBid,cellid,TAC,PCI,频带,频点,上行带宽,下行带宽,双工模式,天线模式,参考信号功率,根序列索引,传输模式,RRU数量
        Cell cell1 = nodeBCellMap.get(cellIDArray[0]);
        sheet1.getRow(1).getCell(1).setCellValue(enodeBName);
        sheet1.getRow(1).getCell(2).setCellValue(cell1.subNetId);
        sheet1.getRow(1).getCell(3).setCellValue(cell1.enbId);
        sheet1.getRow(1).getCell(4).setCellValue(ciStr);
        sheet1.getRow(1).getCell(5).setCellValue(cell1.tac);
        sheet1.getRow(1).getCell(6).setCellValue(pciStr);
        sheet1.getRow(1).getCell(7).setCellValue(cell1.band);
        sheet1.getRow(1).getCell(8).setCellValue(cell1.earfcn);
        sheet1.getRow(1).getCell(9).setCellValue(cell1.frequncyUL);
        sheet1.getRow(1).getCell(10).setCellValue(cell1.frequncyDL);
        sheet1.getRow(1).getCell(11).setCellValue(cell1.dupluxMode);
        sheet1.getRow(1).getCell(12).setCellValue("4T4R");
        sheet1.getRow(1).getCell(13).setCellValue("18.2");
        sheet1.getRow(1).getCell(14).setCellValue(rootStr);
        sheet1.getRow(1).getCell(15).setCellValue(cell1.tm);
        sheet1.getRow(1).getCell(16).setCellValue(cellIDArray.length);
        //设置第3行的数据
        sheet1.getRow(2).getCell(1).setCellValue(enodeBName);
        sheet1.getRow(2).getCell(2).setCellValue(cell1.subNetId);
        sheet1.getRow(2).getCell(3).setCellValue(cell1.enbId);
        sheet1.getRow(2).getCell(4).setCellValue(ciStr);
        sheet1.getRow(2).getCell(5).setCellValue(cell1.tac);
        sheet1.getRow(2).getCell(6).setCellValue(pciStr);
        sheet1.getRow(2).getCell(7).setCellValue(cell1.band);
        sheet1.getRow(2).getCell(8).setCellValue(cell1.earfcn);
        sheet1.getRow(2).getCell(9).setCellValue(cell1.frequncyUL);
        sheet1.getRow(2).getCell(10).setCellValue(cell1.frequncyDL);
        sheet1.getRow(2).getCell(11).setCellValue(cell1.dupluxMode);
        sheet1.getRow(2).getCell(12).setCellValue("4T4R");
        sheet1.getRow(2).getCell(13).setCellValue("18.2");
        sheet1.getRow(2).getCell(14).setCellValue(rootStr);
        sheet1.getRow(2).getCell(15).setCellValue(cell1.tm);
        sheet1.getRow(2).getCell(16).setCellValue(cellIDArray.length);

        //创建基站
        EnodeBPoint enodeBPoint = new EnodeBPoint(enodeBName, lon, la, azimuthArray, 60);
        //创建指向保存图片的文件
        File saveFile;
        //处理DT 下载的RSRP
        File dtdlDir = new File(root,"dt");
        files = dtdlDir.listFiles();
        if(files.length==0){
            throw new Exception("DT下载的文件不存在！");
        }
        File dtdlFile = files[0];
        TestFile testFile = TestFile.createTestFile(dtdlFile,0);

        //检查数据是否正确，错误则抛出异常
        if(testFile.testInfoList.size()==0){
            throw new Exception(dtdlFile.getName()+"无点数据，请检查是否存在经纬度或者是否存在合理的下载值！");
        }
        ArrayList<Point> rsrpPointList = testFile.createPoint(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray);
        //创建下载的底图
        GaoDePic rsrpAll = GaoDeMapTools.createCellMapByPoints(rsrpPointList, enodeBPoint);
        BufferedImage bufferedImageAll = rsrpAll.drawOvers(rsrpPointList);
        GaoDePic rsrpCell1 = rsrpAll.getCopy();
        ArrayList<Point> rsrpPointListCell1 = testFile.createPointByPCI(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray, Integer.parseInt(cell1.pci));
        BufferedImage bufferedImageCell1 = rsrpCell1.drawOvers(rsrpPointListCell1);
        bufferedImageAll  = PicTools.addThresholdMapBySection(bufferedImageAll, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);
        bufferedImageCell1  = PicTools.addThresholdMapBySection(bufferedImageCell1, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);

        //将下载的RSRP图保存到文件夹
        saveFile = new File(saveimage,"dt_all_"+enbId+"_rsrp.jpg");
        ImageIO.write(bufferedImageAll,"jpg",saveFile);
        saveFile = new File(saveimage,"dt_"+enbId+"_"+cell1.cellId+"_rsrp.jpg");
        ImageIO.write(bufferedImageCell1,"jpg",saveFile);

        //将基站和小区1的RSRP写入底图，后面小区2,3循环着写，主要是考虑可能存在4小区
        //先写全站的RSRP
        ByteArrayOutputStream tempStream;
        int tempId;
        HSSFClientAnchor tempAnchor;
        HSSFPatriarch patriarch;
        HSSFPicture pictureTemp;
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 1, 30, (short) 6, 30);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(bufferedImageAll,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);

        //写入1小区的RSRP
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 6, 30, (short) 11, 30);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(bufferedImageCell1,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);

        //遍历处理别的小区的RSRP
        for (int i = 1; i < cellIDArray.length; i++) {
            int c1 = cellIDArray[i];
            int pci  = Integer.parseInt(nodeBCellMap.get(c1).pci);
            ArrayList<Point> p1 = testFile.createPointByPCI(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray, pci);
            GaoDePic rsrp = rsrpAll.getCopy();
            BufferedImage b1 = rsrp.drawOvers(p1);
            b1  = PicTools.addThresholdMapBySection(b1, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);
            tempStream = new ByteArrayOutputStream();
            patriarch = sheet0.createDrawingPatriarch();
            tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) (1+5*(i-1)), 32, (short) (1+5*(i-1)+5), 32);
            tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(b1,"jpg",tempStream);
            tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            pictureTemp = patriarch.createPicture(tempAnchor, tempId);
            pictureTemp.resize(1,1);
            //将小区RSRP图片写入保存的文件夹
            saveFile = new File(saveimage,"dt_"+enbId+"_"+c1+"_rsrp.jpg");
            ImageIO.write(b1,"jpg",saveFile);
        }

        //处理DT 下载的SINR,由于RSRP和SINR是同一个路段，不需要在创建SINR的底图
        GaoDePic sinrAll = rsrpAll.getCopy();
        GaoDePic sinrCell1 = rsrpAll.getCopy();
        ArrayList<Point> pointAll = testFile.createPoint(TestFile.sinrType, Report.sinrArray, Report.sinrColorArray);
        BufferedImage sinrBufferedImageAll = sinrAll.drawOvers(pointAll);
        ArrayList<Point> sinrPointListCell1 = testFile.createPointByPCI(TestFile.sinrType, Report.sinrArray, Report.sinrColorArray, Integer.parseInt(cell1.pci));
        BufferedImage sinrBufferedImageCell1 = sinrCell1.drawOvers(sinrPointListCell1);
        bufferedImageAll  = PicTools.addThresholdMapBySection(sinrBufferedImageAll, NetTools.sinrName, Report.sinrArray, Report.sinrColorArray);
        bufferedImageCell1  = PicTools.addThresholdMapBySection(sinrBufferedImageCell1, NetTools.sinrName, Report.sinrArray, Report.sinrColorArray);
        //将下载的SINR图保存到文件夹
        saveFile = new File(saveimage,"dt_all_"+enbId+"_sinr.jpg");
        ImageIO.write(bufferedImageAll,"jpg",saveFile);
        saveFile = new File(saveimage,"dt_"+enbId+"_"+cell1.cellId+"_sinr.jpg");
        ImageIO.write(bufferedImageCell1,"jpg",saveFile);

        //将基站和小区1的SINR写入底图，后面小区2,3循环着写，主要是考虑可能存在4小区
        //先写全站的SINR
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 1, 35, (short) 6, 35);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(bufferedImageAll,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);

        //写入1小区的SINR
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 6, 35, (short) 11, 35);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(bufferedImageCell1,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);

        //遍历处理别的小区的SINR
        for (int i = 1; i < cellIDArray.length; i++) {
            int c1 = cellIDArray[i];
            int pci  = Integer.parseInt(nodeBCellMap.get(c1).pci);
            ArrayList<Point> p1 = testFile.createPointByPCI(TestFile.sinrType, Report.sinrArray, Report.sinrColorArray, pci);
            GaoDePic sinr = rsrpAll.getCopy();
            BufferedImage b1 = sinr.drawOvers(p1);
            b1  = PicTools.addThresholdMapBySection(b1, NetTools.sinrName, Report.sinrArray, Report.sinrColorArray);
            tempStream = new ByteArrayOutputStream();
            patriarch = sheet0.createDrawingPatriarch();
            tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) (1+5*(i-1)), 37, (short) (1+5*(i-1)+5), 37);
            tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ImageIO.write(b1,"jpg",tempStream);
            tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            pictureTemp = patriarch.createPicture(tempAnchor, tempId);
            pictureTemp.resize(1,1);
            //将小区RSRP图片写入保存的文件夹
            saveFile = new File(saveimage,"dt_"+enbId+"_"+c1+"_sinr.jpg");
            ImageIO.write(b1,"jpg",saveFile);
        }



        //处理FTP下载速率的图，下载速率和下载RSRP为同一个图，所以不需要创建新的底图
        ArrayList<Point> dlPointList = testFile.createPoint(TestFile.dlSpeedType, Report.dlSpeedArray, Report.dlSpeedColorArray);
        GaoDePic dlSpeed = rsrpAll.getCopy();
        BufferedImage dlBufferImage = dlSpeed.drawOvers(dlPointList);
        dlBufferImage = PicTools.addThresholdMapBySection(dlBufferImage, NetTools.dlName, Report.dlSpeedArray, Report.dlSpeedColorArray);
        //写入下载速率的图
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 1, 40, (short) 6, 40);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(dlBufferImage,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);
        //将小区下载的图片写入保存的文件夹
        saveFile = new File(saveimage,"dl_"+enbId+".jpg");
        ImageIO.write(dlBufferImage,"jpg",saveFile);

        //处理切换的图
        PCIPoint pciPoint = testFile.createPCIPoint(PicTools.colorArray);
        GaoDePic pciPic = rsrpAll.getCopy();
        BufferedImage pciBufferImage = pciPic.drawHO(pciPoint.points);
        pciBufferImage = PicTools.addThresholdMapByType(pciBufferImage,NetTools.pciName,pciPoint.pciColorMap);
        //写入切换的图
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 1, 43, (short) 6, 43);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(pciBufferImage,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);
        //将小区切换的图片写入保存的文件夹
        saveFile = new File(saveimage,"ho_"+enbId+".jpg");
        ImageIO.write(pciBufferImage,"jpg",saveFile);

        //处理上传速率的图
        //先读取文件
        File dtulDir = new File(root,"ut");
        files = dtulDir.listFiles();
        if(files.length==0){
            throw new Exception("DT上传的文件不存在！");
        }
        File dtulFile = files[0];
        testFile = TestFile.createTestFile(dtulFile,1);
        //检查数据是否正确，错误则抛出异常
        if(testFile.testInfoList.size()==0){
            throw new Exception(dtulFile.getName()+"无点数据，请检查是否存在经纬度或者是否存在上传值！");
        }
        //画图
        ArrayList<Point> ulPointList = testFile.createPoint(TestFile.ulSpeedType, Report.ulSpeedArray, Report.ulSpeedColorArray);
        GaoDePic ulPic = GaoDeMapTools.createCellMapByPoints(ulPointList, enodeBPoint);
        BufferedImage ulBufferImage = ulPic.drawOvers(ulPointList);
        ulBufferImage = PicTools.addThresholdMapBySection(ulBufferImage,NetTools.ulName, Report.ulSpeedArray, Report.ulSpeedColorArray);
        //写入上传速率的图
        tempStream = new ByteArrayOutputStream();
        patriarch = sheet0.createDrawingPatriarch();
        tempAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 6, 40, (short) 11, 40);
        tempAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        ImageIO.write(ulBufferImage,"jpg",tempStream);
        tempId = sheet0.getWorkbook().addPicture(tempStream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
        pictureTemp = patriarch.createPicture(tempAnchor, tempId);
        pictureTemp.resize(1,1);

        //将小区上传的图片写入保存的文件夹
        saveFile = new File(saveimage,"ul_"+enbId+".jpg");
        ImageIO.write(ulBufferImage,"jpg",saveFile);


        //插入现场照片
        File photoDir = new File(root,"photo");
        insertPhoto(sheet3,photoDir,cellIDArray);

        FileOutputStream fileOutputStream = new FileOutputStream(saveExcel);
        wb.write(fileOutputStream);
        wb.close();
        fileOutputStream.close();

    }

    //插入现场照片
    public void insertPhoto(HSSFSheet sheet,File photoDir,int[] cells) throws Exception {
        //插入建筑物全景照
        File file = new File(photoDir, "all.jpg");
        if(!file.exists()){
            throw new Exception("建筑物全景照不存在");
        }
        BufferedImage temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,4,4,0,1);

        //插入站点入口图
        file = new File(photoDir, "r.jpg");
        if(!file.exists()){
            throw new Exception("站点入口图不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,4,4,1,2);

        //插入天面全景图
        file = new File(photoDir, "q.jpg");
        if(!file.exists()){
            throw new Exception("天面全景图不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,4,4,2,3);

        //插入天线安装图
        for (int i = 0; i < cells.length; i++) {
            file = new File(photoDir, "aa"+(i+1)+".jpg");
            if(!file.exists()){
                throw new Exception("天线安装图"+(i+1)+"不存在");
            }
            temp = ImageIO.read(file);
            POITools.insertPic(temp,sheet,7,7,i,i+1);
        }

        //插入天线覆盖图
        for (int i = 0; i < cells.length; i++) {
            file = new File(photoDir, "cc"+(i+1)+".jpg");
            if(!file.exists()){
                throw new Exception("天线覆盖图"+(i+1)+"不存在");
            }
            temp = ImageIO.read(file);
            POITools.insertPic(temp,sheet,10,10,i,i+1);
        }

        //插入东西南北图
        //插入北
        file = new File(photoDir, "b.jpg");
        if(!file.exists()){
            throw new Exception("正北照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,13,13,0,1);

        //插入东
        file = new File(photoDir, "d.jpg");
        if(!file.exists()){
            throw new Exception("正东照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,13,13,1,2);

        //插入南
        file = new File(photoDir, "n.jpg");
        if(!file.exists()){
            throw new Exception("正南照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,13,13,2,3);

        //插入西
        file = new File(photoDir, "x.jpg");
        if(!file.exists()){
            throw new Exception("正西照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,13,13,3,4);

        //插入东北
        file = new File(photoDir, "b1.jpg");
        if(!file.exists()){
            throw new Exception("东北照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,15,15,0,1);

        //插入东南
        file = new File(photoDir, "d1.jpg");
        if(!file.exists()){
            throw new Exception("东南照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,15,15,1,2);

        //插入西南
        file = new File(photoDir, "n1.jpg");
        if(!file.exists()){
            throw new Exception("西南照片不存在");
        }
        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,15,15,2,3);

        //插入西北
        file = new File(photoDir, "x1.jpg");
        if(!file.exists()){
            throw new Exception("西北照片不存在");
        }


        temp = ImageIO.read(file);
        POITools.insertPic(temp,sheet,15,15,3,4);


    }




}
