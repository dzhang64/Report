package report.functions.teleinner;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import report.utils.CommonTools;
import report.utils.NetTools;
import report.utils.PicTools;
import report.utils.StaticStorage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;


public class InHouseTools {

    public static Font strFont = new Font("Times New Roman", Font.BOLD,16);

    public static int[] dlSpeed21Array = new int[]{20,40,60,80,100};
    public static int[] dlSpeed35Array  = new int[]{50,100,300,500,800};
    public static int[] pingDelayArray = new int[]{15,27,50,100,300};
    public static int[] rsrpArray = new int[]{-110,-95,-85,-75};
    public static int[] sinrArray = new int[]{-3,0,10,20};
    public static int[] ulSpeed21Array = new int[]{20,30,40,60,70};
    public static int[] ulSpeed35Array = new int[]{30,50,70,100,120};

    public static Color[] dlSpeed21ColorArray = new Color[]{
            PicTools.red,PicTools.orange,PicTools.yellow,PicTools.deepBlue,PicTools.skyBlue,PicTools.green
    };
    public static Color[] dlSpeed35ColorArray = new Color[]{
            PicTools.red,PicTools.orange,PicTools.yellow,PicTools.deepBlue,PicTools.skyBlue,PicTools.green
    };
    public static Color[] pingDelayColorArray = new Color[]{
            PicTools.green,PicTools.skyBlue,PicTools.deepBlue,PicTools.yellow,PicTools.red,PicTools.black
    };
    public static Color[] rsrpColorArray = new Color[]{
            PicTools.red,PicTools.yellow,PicTools.deepBlue,PicTools.skyBlue,PicTools.green
    };
    public static Color[] sinrColorArray = new Color[]{
            PicTools.red,PicTools.yellow,PicTools.deepBlue,PicTools.skyBlue,PicTools.green
    };
    public static Color[] ulSpeed21ColorArray = new Color[]{
            PicTools.red,PicTools.orange,PicTools.yellow,PicTools.deepBlue,PicTools.skyBlue,PicTools.green
    };
    public static Color[] ulSpeed35ColorArray = new Color[]{
            PicTools.red,PicTools.orange,PicTools.yellow,PicTools.deepBlue,PicTools.skyBlue,PicTools.green
    };

    //切换文件的位置
    public String handleFilePath;
    //记录文件名
    public String fileName;
    //用于记录读取CSV的点
    public  ArrayList<KeyDate> keyDates;

    //记录底图的高度
    public int height;
    //记录底图的宽度
    public int width;
    //记录X坐标偏移
    public  double offsetX;
    //记录Y坐标偏移
    public  double offsetY;
    //记录图片的收缩系数
    public int expand;
    //记录图片的切图区域
    public int[] cultMapArea;
    //记录图片的切图参数
    public int[] cultParameter;
    //记录报告的RSRP
    public  double reportRsrp;
    //记录报告的SINR
    public  double reportSinr;
    //记录报告的覆盖率
    public  double reprotCoverageRage;
    //记录报告的上行速率
    public  double reportULSpeed;
    //记录报告的下行速率
    public  double reportDLSpeed;
    //记录报告的室分PCI占比
    public  double reportIndoorPCIRate;
    //记录报告的室分PCI的数量
    public int pciInhouseCounter = 0;
    //记录PCI的所有点数
    public int pciAllPointNum = 0;
    //记录报告的Ping时延
    public double reportPingDelay;
    //记录报告的下行速率点
    public int reportDLPoint = 0;
    //记录报告的下行速率合格的点
    public int reportPassedDLPoint = 0;
    //用于记录画图的底图
    public File backImageFile;
    //用于存储底图的BufferedImage
    public BufferedImage image;
    //用于存储PCI值和颜色
    public HashMap<String, Color> pciColorMap = new HashMap<String, Color>();
    //规定初始点的大小
    public  int pointSize = 8;

    public InHouseTools(String path, String imageDir, String saveFileStr) {
        this.handleFilePath = path;
        File file = new File(path);
        File parent = file.getParentFile().getParentFile();
        fileName = file.getName().split("\\.")[0];
        File backImageDir = new File(parent, imageDir);
        String[] s = fileName.split("_");
        //String imageName = s[1]+"_"+s[2]+"_"+s[3];
        String imageName = s[1]+"_"+s[2];
        File[] imageFiles = backImageDir.listFiles();
        for (int i = 0; i < imageFiles.length; i++) {
            if(imageFiles[i].getName().contains(imageName)){
                imageName = imageFiles[i].getName();
                backImageFile = imageFiles[i];
                break;
            }
        }
        try {
            this.keyDates = readInhouseCsv(handleFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void init() throws IOException, ParseException {
        int rotateAngle = PicTools.getRotateAngle(backImageFile);
        String[] split = backImageFile.getName().split("\\.");
        String type = split[split.length-1];
        image = ImageIO.read(backImageFile);
        if(rotateAngle % 360 !=0){
            image = PicTools.rotateImage(image, rotateAngle);
            ImageIO.write(image,type,backImageFile);
        }
        height = image.getHeight();
        width = image.getWidth();
        offsetX = width/1000.0;
        offsetY =  height/1800.0;
        getImageRegion(height,width);
    }


    public static ArrayList<KeyDate> readInhouseCsv(String path) throws IOException, ParseException {
        int counter = 1;
        ArrayList<KeyDate> keyDates = new ArrayList<KeyDate>();
        int offset;
        Date date;
        int gnbId;
        int cellId;
        double longtitude;
        double latitud;
        double nrRsrp;
        int cellPci;
        double cellSinr;
        int cellBand;
        double pdcpDLSpeed;
        double pdcpULSpeed;
        double pingDelay;
        double lteRsrp;
        int voiceRequestNum;
        int voiceSuccessNum;
        double ftpULSpeed;
        double ftpDLSpeed;
        double gpsFixValue;
        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        int n =10;

        String tempSubStringName = new File(path).getName().split("_")[5];
        if(tempSubStringName.contains("moc")){
            n = 9;
        }
        if(tempSubStringName.contains("ho")){
            n = 8;
        }
        String record = file.readLine();
        maker:
        while ((record = file.readLine()) != null) {
            String[] cells = record.split(",");
            for (int i = 0; i < cells.length; i++) {
                cells[i] = cells[i].replace("\"","").trim();
            }
            for (int i = 0; i < n; i++) {
                if(cells[i].trim().length()==0){
                    continue maker;
                }
            }

            offset = counter;
            counter++;
            date = PicTools.dateFormat.parse(cells[0]);
            longtitude = changeDouble(cells[1])/10;
            latitud = changeDouble(cells[2])/10;
            gnbId = changeInt(cells[3]);
            cellId = changeInt(cells[4]);
            nrRsrp = changeDouble(cells[5]);
            cellPci = changeInt(cells[6]);
            cellSinr = changeDouble(cells[7]);
            cellBand = changeInt(cells[8]);
            pdcpDLSpeed = changeDouble(cells[9]);
            pdcpULSpeed = changeDouble(cells[10]);
            pingDelay = changeDouble(cells[11]);
            lteRsrp = changeDouble(cells[12]);
            ftpULSpeed = changeDouble(cells[13]);
            ftpDLSpeed = changeDouble(cells[14]);
            gpsFixValue = 97;
            voiceRequestNum = changeInt(cells[15]);
            voiceSuccessNum = changeInt(cells[16]);

            KeyDate keyDate = new KeyDate(offset, date, gnbId, cellId, longtitude, latitud, nrRsrp, cellPci, cellSinr, cellBand, pdcpDLSpeed, pdcpULSpeed, pingDelay, lteRsrp, voiceSuccessNum, voiceRequestNum, ftpULSpeed, ftpDLSpeed,gpsFixValue);
            keyDates.add(keyDate);
        }

        return keyDates;
    }

    public  ArrayList<Point> rsrpHandle(){
        ArrayList<Point> points = new ArrayList<Point>();
        double tempRsrp = 0.0;
        double tempSinr = 0.0;
        int badPoint = 0;
        if(keyDates.size()==0) {
            return null;
        };
        KeyDate keyDate = keyDates.get(0);
        //Color color = selectRsrpColor(keyDate.nrRsrp);
        Color color = CommonTools.selectColorBySection(keyDate.nrRsrp,rsrpArray,rsrpColorArray);
        Point point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
        points.add(point);
        tempRsrp = tempRsrp + keyDate.nrRsrp;
        tempSinr = tempSinr + keyDate.cellSinr;

        for (int i = 1; i < keyDates.size(); i++) {
            tempRsrp = tempRsrp + keyDates.get(i).nrRsrp;
            tempSinr = tempSinr + keyDates.get(i).cellSinr;
            if(keyDates.get(i).nrRsrp<-110||keyDates.get(i).cellSinr<3){
                badPoint = badPoint + 1;
            }
            if(keyDates.get(i).longtitude!=keyDate.longtitude||keyDates.get(i).latitud!=keyDate.latitud){
                keyDate = keyDates.get(i);
                color = CommonTools.selectColorBySection(keyDate.nrRsrp,rsrpArray,rsrpColorArray);
                point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
                points.add(point);
            }
        }
        reportRsrp = tempRsrp / keyDates.size();
        reportSinr = tempSinr / keyDates.size();
        reprotCoverageRage = 1 - badPoint / keyDates.size();
        return points;
    }

    public  ArrayList<Point>  sinrHandle(){
        ArrayList<Point> points = new ArrayList<Point>();
        if(keyDates.size()==0) {
            return null;
        };
        KeyDate keyDate = keyDates.get(0);
        //Color color = selectSinrColor(keyDate.cellSinr);
        Color color = CommonTools.selectColorBySection(keyDate.cellSinr,sinrArray,sinrColorArray);
        Point point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
        points.add(point);

        for (int i = 1; i < keyDates.size(); i++) {
            if(keyDates.get(i).longtitude!=keyDate.longtitude||keyDates.get(i).latitud!=keyDate.latitud){
                keyDate = keyDates.get(i);
                color = CommonTools.selectColorBySection(keyDate.cellSinr,sinrArray,sinrColorArray);
                point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
                points.add(point);
            }
        }
        return points;
    }

    public  ArrayList<Point>  dlHandle(int band,double dlThresh){
        if(keyDates.size()==0) {
            return null;
        };
        int start = 0;
        for (int i = 0; i < keyDates.size(); i++) {
            if(keyDates.get(i).ftpDLSpeed !=0){
                start = i;
                break;
            }
        }
        ArrayList<Point> points = new ArrayList<Point>();
        double tempDLSpeed = 0;

        KeyDate keyDate = keyDates.get(start);
        Color color = null;
        if(band ==1){
            //color = selectDL21Color(keyDate.pdcpDLSpeed);
            color = CommonTools.selectColorBySection(keyDate.pdcpDLSpeed,dlSpeed21Array,dlSpeed21ColorArray);
        }else {
            //color = selectDL35Color(keyDate.pdcpDLSpeed);
            color = CommonTools.selectColorBySection(keyDate.pdcpDLSpeed,dlSpeed35Array,dlSpeed35ColorArray);
        }
        Point point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
        points.add(point);
        tempDLSpeed = tempDLSpeed + keyDate.pdcpDLSpeed;
        for (int i = 1+start; i < keyDates.size(); i++) {
            tempDLSpeed = tempDLSpeed + keyDates.get(i).pdcpDLSpeed;
            if(keyDates.get(i).longtitude!=keyDate.longtitude||keyDates.get(i).latitud!=keyDate.latitud){
                keyDate = keyDates.get(i);
                if(band ==1){
                    color = CommonTools.selectColorBySection(keyDate.pdcpDLSpeed,dlSpeed21Array,dlSpeed21ColorArray);
                }else {
                    color = CommonTools.selectColorBySection(keyDate.pdcpDLSpeed,dlSpeed35Array,dlSpeed35ColorArray);
                }
                point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
                points.add(point);
            }
        }
        reportDLSpeed = ((double)tempDLSpeed) /(keyDates.size() - start);

        reportDLPoint = keyDates.size() - start;
        for (int i = start; i < keyDates.size(); i++) {
            if(keyDates.get(i).pdcpDLSpeed>dlThresh){
                reportPassedDLPoint++;
            }
        }
        return points;
    }

    public  ArrayList<Point>  ulHandle(int band){
        if(keyDates.size()==0) {
            return null;
        };
        int start = 0;
        for (int i = 0; i < keyDates.size(); i++) {
            if(keyDates.get(i).ftpULSpeed !=0){
                start = i;
                break;
            }
        }
        ArrayList<Point> points = new ArrayList<Point>();
        double tempULSpeed = 0;

        KeyDate keyDate = keyDates.get(start);
        Color color = null;
        if(band ==1){
            color = CommonTools.selectColorBySection(keyDate.pdcpULSpeed,ulSpeed21Array,ulSpeed21ColorArray);
        }else {
            color = CommonTools.selectColorBySection(keyDate.pdcpULSpeed,ulSpeed35Array,ulSpeed35ColorArray);
        }
        Point point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
        points.add(point);
        tempULSpeed = tempULSpeed + keyDate.pdcpDLSpeed;
        for (int i = 1+start; i < keyDates.size(); i++) {
            tempULSpeed = tempULSpeed + keyDates.get(i).pdcpULSpeed;
            if(keyDates.get(i).longtitude!=keyDate.longtitude||keyDates.get(i).latitud!=keyDate.latitud){
                keyDate = keyDates.get(i);
                if(band ==1){
                    //color = selectUL21Color(keyDate.pdcpULSpeed);
                    color = CommonTools.selectColorBySection(keyDate.pdcpULSpeed,ulSpeed21Array,ulSpeed21ColorArray);
                }else {
                    //color = selectUL35Color(keyDate.pdcpULSpeed);
                    color = CommonTools.selectColorBySection(keyDate.pdcpULSpeed,ulSpeed35Array,ulSpeed35ColorArray);
                }
                point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
                points.add(point);
            }
        }

        reportULSpeed = ((double)tempULSpeed) /(keyDates.size() - start);
        return points;
    }

    public  ArrayList<Point>  pciHandle(HashMap cellPCIMap){
        ArrayList<Point> points = new ArrayList<Point>();
        if(keyDates.size()==0) {
            return null;
        };
        int colorPoint = 0;
        HashSet<Integer> pciSet = new HashSet<Integer>();

        for (int i = 0; i < keyDates.size(); i++) {
            pciSet.add(keyDates.get(i).cellPci);
        }
        for (Integer integer : pciSet) {
            if(colorPoint==PicTools.colors.size()){
                colorPoint =0;
            }
            colorPoint++;
            pciColorMap.put(integer+"",PicTools.colors.get(colorPoint));
        }
        int counter =0;

        KeyDate keyDate = keyDates.get(0);
        Color color = selectPCIColor(cellPCIMap,keyDate.cellPci);
        if(cellPCIMap.get(keyDate.cellPci)!=null){
            counter++;
        }
        Point point = new Point(color, keyDate.cellPci+"", keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
        points.add(point);

        for (int i = 1; i < keyDates.size(); i++) {
            if(cellPCIMap.get(keyDates.get(i).cellPci)!=null){
                counter++;
            }
            if(keyDates.get(i).longtitude!=keyDate.longtitude||keyDates.get(i).latitud!=keyDate.latitud){
                keyDate = keyDates.get(i);
                color = pciColorMap.get(keyDates.get(i).cellPci+"");
                point = new Point(color, keyDate.cellPci+"", keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
                points.add(point);
            }
        }

        pciInhouseCounter = counter;
        pciAllPointNum = keyDates.size();
        reportIndoorPCIRate = ((double)counter) / keyDates.size();
        return points;
    }

    public  ArrayList<Point>  pingDelayHandle(){
        ArrayList<Point> points = new ArrayList<Point>();
        if(keyDates.size()==0) {
            return null;
        };
        int counter = 0;
        double pingDelaySum = 0.0;
        KeyDate keyDate = new KeyDate(100, 100);
        for (int i = 0; i < keyDates.size(); i++) {
            if(keyDates.get(i).pingDelay==0){
               continue;
            }
            counter++;
            pingDelaySum = pingDelaySum + keyDates.get(i).pingDelay;
            if(keyDates.get(i).longtitude!=keyDate.longtitude||keyDates.get(i).latitud!=keyDate.latitud){
                keyDate = keyDates.get(i);
                //Color color = selectPingDelayColor(keyDate.pingDelay);
                Color color = CommonTools.selectColorBySection(keyDate.pingDelay,pingDelayArray,pingDelayColorArray);
                Point point = new Point(color, null, keyDate.longtitude, keyDate.latitud,keyDate.gpsFixValue);
                points.add(point);
            }
        }
        reportPingDelay = pingDelaySum / counter;
        return points;
    }

    public  BufferedImage drawPCI(ArrayList<Point> pointArrayList) throws IOException {
        BufferedImage image = ImageIO.read(backImageFile);
        BufferedImage image1 = PicTools.getImage(image,cultParameter[0],cultParameter[1],cultParameter[2],cultParameter[3],expand);
        Graphics2D graphics1 = image1.createGraphics();
        int tempPCI = -1;

        for (int i = 2; i < pointArrayList.size()-2; i++) {
            Point p = pointArrayList.get(i);
            int[] point = getPoint(p.longtitude, p.latitude, height);
            graphics1.setColor(p.color);
            int x = ((int) (point[0]-point[0]*97*offsetX/width)-cultParameter[0])*expand;
            int y = ((int) (point[1]+97*offsetY) - cultParameter[1])*expand;
            graphics1.fillOval(x, y,pointSize, pointSize);
            if(Integer.parseInt(p.description)!=tempPCI){
                tempPCI = Integer.parseInt(p.description);
                graphics1.setColor(PicTools.red);
                graphics1.setFont(strFont);
                graphics1.drawOval(x, y,pointSize+3, pointSize+3);
                graphics1.drawString(p.description,x+3,y+3);
            }
        }
        BufferedImage reportImage = PicTools.addThresholdMapByType(image1,NetTools.pciName,pciColorMap);
        return PicTools.checkReturn(reportImage);
    }

    public BufferedImage drawSpeed(ArrayList<Point> pointArrayList,int type) throws IOException {
        BufferedImage image = null;
        image = ImageIO.read(backImageFile);
        BufferedImage image1 = PicTools.getImage(image,cultParameter[0],cultParameter[1],cultParameter[2],cultParameter[3],expand);
        Graphics2D graphics1 = image1.createGraphics();
        for (int i = 2; i < pointArrayList.size()-2; i++) {
            Point p = pointArrayList.get(i);
            int[] point = getPoint(p.longtitude, p.latitude, height);
            graphics1.setColor(p.color);
            /* graphics.fillArc(point[0]-97*3/2+12, point[1]+97*3/2+12, 12,12,0, 360);*/
            int x = ((int) (point[0]-point[0]*97*offsetX/width)-cultParameter[0])*expand;
            int y = ((int) (point[1]+97*offsetY) - cultParameter[1])*expand;
            graphics1.fillOval(x, y,pointSize, pointSize);
        }
        BufferedImage reportImag = addThresholdMap(image1, type);
        return PicTools.checkReturn(reportImag);
    }

    public BufferedImage drawNoBackGroup(ArrayList<Point> pointArrayList,int type) throws IOException {
        int size = pointArrayList.size();
        BufferedImage image = new BufferedImage(size*10+100,300, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(PicTools.dark);
        graphics.fillRect(0,0,image.getWidth(),image.getHeight());
        int x = 50;
        int y = 50;
        for (int i = 0; i < pointArrayList.size(); i++) {
            Point p = pointArrayList.get(i);
            graphics.setColor(p.color);
            x = x+10;
            graphics.fillOval(x, y,pointSize, pointSize);
        }
        if(type==NetTools.typeUlSpeed21){

        }
        BufferedImage reportImag = addThresholdMap(image, type);
        return PicTools.checkReturn(reportImag);
    }

    public  BufferedImage drawOval(ArrayList<Point> pointArrayList, int type) throws IOException {
        BufferedImage image = null;
        image = ImageIO.read(backImageFile);
        BufferedImage image1 = PicTools.getImage(image,cultParameter[0],cultParameter[1],cultParameter[2],cultParameter[3],expand);
        Graphics2D graphics1 = image1.createGraphics();
        for (int i = 2; i < pointArrayList.size()-2; i++) {
            Point p = pointArrayList.get(i);
            int[] point = getPoint(p.longtitude, p.latitude, height);
            graphics1.setColor(p.color);
            int x = ((int) (point[0]-point[0]*97*offsetX/width)-cultParameter[0])*expand;
            int y = ((int) (point[1]+97*offsetY) - cultParameter[1])*expand;
            graphics1.fillOval(x, y,pointSize, pointSize);
        }
        BufferedImage reportImag = addThresholdMap(image1, type);
        return PicTools.checkReturn(reportImag);
    }

    public  int handoverCheck(){
        int counter = 0;
        int cellId = keyDates.get(0).cellId;
        for (int i = 1; i < keyDates.size(); i++) {
            if(cellId != keyDates.get(i).cellId){
                cellId = keyDates.get(i).cellId;
                counter++;
            }
        }
        return counter;
    }

    public int leakageCheck(String[] cellIds){
        int counter = 0;
        for (int i = 0; i < keyDates.size(); i++) {
                for (int j = 0; j < cellIds.length; j++) {
                   if(cellIds[j].equals(keyDates.get(i).gnbId+"-"+keyDates.get(i).cellId)){
                       if(keyDates.get(i).nrRsrp>-110){
                           counter++;
                       }
                   }
                }

        }
        return counter;
    }


    /*获取图片坐标*/
    public static int[] getPoint(double longtitude,double latitude,int height){
        int x = (int) GetDistance(longtitude, 0, 0, 0);
        int y = (int) GetDistance(0,latitude,0,0);
        y = height -y;
        return new int[]{x,y};
    }

    public static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        GlobalCoordinates source = new GlobalCoordinates(lat1, lon1);
        GlobalCoordinates target = new GlobalCoordinates(lat2, lon2);
        //Sphere坐标的计算结果
        /* return getDistanceMeter(source,target, Ellipsoid.Sphere);*/
        //WGS84坐标系计算结果
        return getDistanceMeter(source,target, Ellipsoid.WGS84);
    }

    public static  double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid){
        //创建GeodeticCalculator,调用计算方法,传入坐标系,经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);
        return geoCurve.getEllipsoidalDistance();
    }

    public static double changeDouble(String s){
        if(s.trim().length() == 0){
            return 0;
        }
        return Double.parseDouble(s.trim());
    }

    public static int changeInt(String s){
        if(s.trim().length() == 0){
            return 0;
        }
        return Integer.parseInt(s.trim());
    }

    public static Color selectRsrpColor(double rsrp){
        if(rsrp<-115){
            return PicTools.black;
        }
        if(rsrp<-110){
            return PicTools.red;
        }
        if(rsrp<-95){
            return PicTools.yellow;
        }
        if(rsrp<-85){
            return PicTools.deepBlue;
        }
        if(rsrp<-75){
            return PicTools.skyBlue;
        }
        return PicTools.green;
    }

    public static Color selectSinrColor(double sinr){
        if(sinr<-6){
            return PicTools.black;
        }
        if(sinr<-3){
            return PicTools.red;
        }
        if(sinr<0){
            return PicTools.yellow;
        }
        if(sinr<10){
            return PicTools.deepBlue;
        }
        if(sinr<20){
            return PicTools.skyBlue;
        }
        return PicTools.green;
    }

    public static Color selectDL21Color(double speed){
        if(speed<20){
            return PicTools.black;
        }
        if(speed<30){
            return PicTools.red;
        }
        if(speed<40){
            return PicTools.orange;
        }
        if(speed<50){
            return PicTools.yellow;
        }
        if(speed<60){
            return PicTools.deepBlue;
        }
        if(speed<80){
            return PicTools.skyBlue;
        }
        return PicTools.green;
    }

    private Color selectDL35Color(double speed) {
        if(speed<50){
            return PicTools.black;
        }
        if(speed<80){
            return PicTools.red;
        }
        if(speed<100){
            return PicTools.orange;
        }
        if(speed<150){
            return PicTools.yellow;
        }
        if(speed<200){
            return PicTools.deepBlue;
        }
        if(speed<270){
            return PicTools.skyBlue;
        }
        return PicTools.green;
    }

    public static Color selectUL21Color(double speed){
        if(speed<20){
            return PicTools.black;
        }
        if(speed<30){
            return PicTools.red;
        }
        if(speed<40){
            return PicTools.orange;
        }
        if(speed<50){
            return PicTools.yellow;
        }
        if(speed<60){
            return PicTools.deepBlue;
        }
        if(speed<70){
            return PicTools.skyBlue;
        }
        return PicTools.green;
    }

    private Color selectUL35Color(double speed) {
        if(speed<30){
            return PicTools.black;
        }
        if(speed<50){
            return PicTools.red;
        }
        if(speed<70){
            return PicTools.orange;
        }
        if(speed<90){
            return PicTools.yellow;
        }
        if(speed<100){
            return PicTools.deepBlue;
        }
        if(speed<120){
            return PicTools.skyBlue;
        }
        return PicTools.green;
    }

    public static Color selectPCIColor(HashMap cellPCIMap,int testPCI){
        if(cellPCIMap.get(testPCI)!=null){
            return PicTools.green;
        }
        return PicTools.dark;
    }

    public Color selectPingDelayColor(double delay) {
        if(delay<15){
            return PicTools.green;
        }
        if(delay<27){
            return PicTools.skyBlue;
        }
        if(delay<50){
            return PicTools.deepBlue;
        }
        if(delay<100){
            return PicTools.yellow;
        }
        if(delay<300){
            return PicTools.red;
        }
        return PicTools.black;
    }

    public double[] getGPSRegion(){
        if(keyDates.size()==0){
            return null;
        }
        double longMax = keyDates.get(0).longtitude;
        double longMin = keyDates.get(0).longtitude;
        double laMax = keyDates.get(0).latitud;
        double laMin = keyDates.get(0).latitud;
        for (int i = 1; i < keyDates.size(); i++) {
            if(keyDates.get(i).longtitude>longMax){
                longMax = keyDates.get(i).longtitude;
            }
            if(keyDates.get(i).longtitude<longMin){
                longMin = keyDates.get(i).longtitude;
            }
            if(keyDates.get(i).latitud>laMax){
                laMax = keyDates.get(i).latitud;
            }
            if(keyDates.get(i).latitud<laMin){
                laMin = keyDates.get(i).latitud;
            }
        }

        return new double[]{longMax,longMin,laMax,laMin};
    }


    public void getImageRegion(int height,int width){
        double[] gpsRegion = getGPSRegion();
        //左下的点
        int[] point1 = getPoint(gpsRegion[1], gpsRegion[3], height);
        //右上的点
        int[] point2 = getPoint(gpsRegion[0], gpsRegion[2], height);
        setExpand(point1,point2 ,height,width);


        int temp;
        int i = 1;
        for (; i < 5; i++) {
            temp = point1[0] -50*i;
            if(temp<0){
                break;
            }
        }
        point1[0] = point1[0] -50*(i-1);

        i = 1;
        for (; i < 5; i++) {
            temp = point1[1] +50*i;
            if(temp>height){
                break;
            }
        }
        point1[1] = point1[1] +50*(i-1);

        i = 1;
        for (; i < 5; i++) {
            temp = point2[0] +50*i;
            if(temp>width){
                break;
            }
        }
        point2[0] = point2[0] +50*(i-1);
        i = 1;
        for (; i < 5; i++) {
            temp = point2[1] -50*i;
            if(temp<0){
                break;
            }
        }
        point2[1] = point2[1] -50*(i-1);

        cultMapArea = new int[]{point1[0],point1[1],point2[0],point2[1]};
        cultParameter = new int[]{point1[0],point2[1],point2[0]-point1[0],point1[1] - point2[1]};
    }

    public void setExpand(int[] point1,int[] point2 ,int height,int width){
        double hw = height/width;
        int xsize = 200;
        int ysize = (int) (hw*xsize);
        int xOffset = point2[0]-point1[0];
        int yOffset = point1[1]-point2[1];
        if(xOffset==0){
            expand = Math.max(ysize/yOffset,1);
            return;
        }
        if(yOffset==0){
            expand = Math.max(xsize/xOffset,1);
            return;
        }
        if(xOffset>yOffset){
            expand = Math.max(xsize/xOffset,1);
            return;
        }
        expand = Math.max(ysize/yOffset,1);
    }

    public double calAvgRsrp(){
        double rsrp = 0.0;
        for (int i = 0; i < keyDates.size(); i++) {
            rsrp = rsrp + keyDates.get(i).nrRsrp;
        }
        return rsrp/keyDates.size();
    }

    public double calAvgSinr(){
        double sinr = 0.0;
        for (int i = 0; i < keyDates.size(); i++) {
            sinr = sinr + keyDates.get(i).cellSinr;
        }
        return sinr/keyDates.size();
    }

    public BufferedImage addThresholdMap(BufferedImage image,int type){
        BufferedImage reportImage = null;
        if(type== NetTools.typeRsrp){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.rsrpName,rsrpArray,rsrpColorArray);
        }else if(type==NetTools.typeSinr){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.sinrName,sinrArray,sinrColorArray);
        }else if(type==NetTools.typeDlSpeed21){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.dlName,dlSpeed21Array,dlSpeed21ColorArray);
        }else if(type==NetTools.typeDlSpeed35){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.dlName,dlSpeed35Array,dlSpeed35ColorArray);
        }else if(type==NetTools.typeUlSpeed21){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.ulName,ulSpeed21Array,ulSpeed21ColorArray);
        }else if(type==NetTools.typeUlSpeed35){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.ulName,ulSpeed35Array,ulSpeed35ColorArray);
        }else if(type==NetTools.typeDelay){
            reportImage = PicTools.addThresholdMapBySection(image, NetTools.pingDelayName,pingDelayArray,pingDelayColorArray);
        }
        return  reportImage;
    }

}
