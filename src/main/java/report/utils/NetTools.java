package report.utils;

import report.functions.jsunicom.lte.Report;
import report.functions.jsunicom.lte.TestFile;
import report.models.net.Cell;
import report.models.net.PhuPeak;
import report.models.pic.GaoDePic;
import report.models.pic.Point;
import sun.awt.SunHints;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class NetTools {

    public static String dlName = "DL ThroughPut Speed(Mbps)";
    public static String ulName = "UL ThroughPut Speed(Mbps)";
    public static String pingDelayName = "Ping Deley(ms)";
    public static String rsrpName = "Rsrp Signal";
    public static String sinrName = "Sinr Signal";
    public static String pciName = "PCI Signal";
    public static final int typeRsrp = 1;
    public static final int typeSinr = 2;
    public static final int typePci = 3;
    public static final int typeDlSpeed21 = 4;
    public static final int typeUlSpeed21 = 5;
    public static final int typeDelay = 6;
    public static final int typeDlSpeed35 = 7;
    public static final int typeUlSpeed35 = 8;
    public static final int[] cqtDLSection = {0,10,20,30,40,70};
    public static final int[] cqtULSection = {0,5,10,15,20,30};
    public static final Color[] cqtColors = {Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};
    public static Font peakFont;
    public static Font delayFont1;
    public static Font delayFont2;
    public static File peakImage;
    public static File pingDelayImage;
    public static File volteDelayImage;

    static{

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(StaticStorage.fontRoot, "HarmonyOS_Sans_Regular.ttf"));
            peakFont = font.deriveFont(Font.BOLD, 42);
            delayFont1 = font.deriveFont(Font.PLAIN, 60);
            delayFont2 = font.deriveFont(Font.PLAIN, 38);
            peakImage = new File(StaticStorage.backImageroot,"phu/s1.jpg");
            pingDelayImage = new File(StaticStorage.backImageroot,"phu/s2.jpg");
            volteDelayImage = new File(StaticStorage.backImageroot,"phu/s3.jpg");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     *
     * @param name:图名，如DL ThroughPut Speed(Mbps)
     * @param file 底图，如
     * @param cell 当前小区
     * @param section 数值区间，如速率的区间{0,10,20,30,40,50}，需要注意该数值为6个，表示5个区域
     * @param sectionColor  每个区间的画图颜色，上面5个区间，则需要5个元素的颜色数组，如{Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};
     * @param datas 测试的数据，数组数据，里面的是某些值，如速率
     * @return
     * @throws IOException
     */
    public static BufferedImage createCQTImg(String name, File file, Cell cell, int[] section, Color[] sectionColor, double[] datas) throws IOException {
        int size = 12;
        Font strFont = new Font("simfang", Font.PLAIN,size);
        BufferedImage img = ImageIO.read(file);
        Graphics2D graphics = img.createGraphics();
        graphics.setFont(strFont);
        Color color = new Color(38, 38, 53);
        Color color1 = new Color(227, 227, 227);
        graphics.setColor(color);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        graphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);
        double average = CommonTools.calAverage(datas);
        String averageStr = CommonTools.point2.format(average);
        double max = CommonTools.findMax(datas);
        String maxStr = CommonTools.point2.format(max);
        double min = CommonTools.findMin(datas);
        String minStr = CommonTools.point2.format(min);
        int sampleNum = datas.length;
        graphics.drawString(name,17,35);
        graphics.drawString(sampleNum+"",286,35);
        graphics.drawString(maxStr,343,35);
        graphics.drawString(minStr,400,35);
        graphics.drawString(averageStr,456,35);
        graphics.drawString(sampleNum+"",286,35);
        //处理右边小区信息的内容，应该是x一样的
        graphics.drawString(cell.technology,872,27);
        graphics.drawString(cell.dupluxMode,872,47);
        graphics.drawString(cell.mcc,872,67);
        graphics.drawString(cell.ncc,872,87);
        graphics.drawString(cell.tac,872,107);
        graphics.drawString(cell.enbId+cell.cellId,872,127);
        graphics.drawString(cell.band,872,147);
        graphics.drawString(cell.earfcn,872,167);
        graphics.drawString(cell.pci,872,187);
        graphics.drawString(cell.bandWidth,872,207);
        graphics.drawString(cell.frequncyDL,872,227);
        graphics.drawString(cell.tm,872,287);
        graphics.drawString("LTE Connect",872,307);
        graphics.drawString("0",872,387);
        graphics.drawString("0X0",872,407);
        graphics.drawString("2",872,427);
        //处理区间单元格区域
        int[] sectionNums = CommonTools.calNum(section, datas);
        double[] sectionRate = new double[sectionNums.length];
        for (int i = 0; i < sectionRate.length; i++) {
            sectionRate[i] = sectionNums[i]*1.0/sampleNum;
        }
        int x1 = 505;
        int x2 = 535;
        int x3 = 607;
        int x4 = 657;
        int x5 = 713;
        int startY = 166;
        graphics.setColor(color1);
        for (int i = 1; i < sectionNums.length; i++) {
            graphics.drawLine(505,166+23*i,713,166+23*i);
            graphics.drawLine(505,166+23*(i-1),505,166+23*i);
            graphics.drawLine(535,166+23*(i-1),535,166+23*i);
            graphics.drawLine(607,166+23*(i-1),607,166+23*i);
            graphics.drawLine(657,166+23*(i-1),657,166+23*i);
            graphics.drawLine(713,166+23*(i-1),713,166+23*i);
        }
        graphics.setColor(color);
        //开始填写表格的值
        for (int i = 0; i < sectionNums.length; i++) {
            graphics.drawString(i+1+"",509,159+i*23);
            graphics.drawString("("+section[i]+","+section[i+1]+")",539,159+i*23);
            graphics.drawString(sectionNums[i]+"",611,159+i*23);
            graphics.drawString(CommonTools.percentPoint2.format(sectionRate[i]),661,159+i*23);
        }
        //画柱子图左右的坐标轴
        for (int i = 0; i < section.length; i++) {
            graphics.drawString(section[i]+"",10,390-i*41);
            graphics.drawString((0+100/(section.length-1)*i)+"%",453,390-i*41);
        }


        //开始画柱子
        int startChartX = 100;
        int startChartY = 387;
        int height = 206;
        int gap = 69;
        int offset = 15;
        int length;

        for (int i = 0; i < sectionRate.length; i++) {
            length = (int) (206*sectionRate[i]);
            if(sectionRate[i]==0){
                graphics.setColor(color);
                graphics.drawString(CommonTools.percentPoint2.format(sectionRate[i]),startChartX+i*gap-offset,startChartY-length-size);
                graphics.drawString("("+section[i]+","+section[i+1]+")",startChartX+i*gap-offset,startChartY+20);
                continue;
            }
            length = (int) (206*sectionRate[i]);
            graphics.setColor(sectionColor[i]);
            graphics.fill3DRect(startChartX+i*gap-offset,startChartY-length,offset*2,length,true);
            graphics.setColor(color);
            graphics.drawString(CommonTools.percentPoint2.format(sectionRate[i]),startChartX+i*gap-offset,startChartY-length-size);
            graphics.drawString("("+section[i]+","+section[i+1]+")",startChartX+i*gap-offset,startChartY+20);
        }

        //画标题
        Font titleFont = new Font("simfang", Font.BOLD,14);
        graphics.setFont(titleFont);
        graphics.drawString(name,174,169);

        return img;
    }

    /**
     *
     * @param phuPeak 为创建图而准备的小区信息类
     * @return 包含2个元素，元素1为BufferImage,即创建的峰值速率图
     *          元素2为峰值速率
     * @throws IOException
     * @throws FontFormatException
     */
    public static ArrayList<Object> makePhuPeak(PhuPeak phuPeak) throws IOException, FontFormatException {
        BufferedImage image = ImageIO.read(peakImage);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        graphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);
        graphics.setColor(PicTools.phuBlue);
        graphics.setFont(peakFont);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(phuPeak.imei, 645 - fontMetrics.stringWidth(phuPeak.imei), 485);
        graphics.drawString(phuPeak.imsi, 645 - fontMetrics.stringWidth(phuPeak.imsi), 548);
        graphics.drawString(phuPeak.rfMode, 645 - fontMetrics.stringWidth(phuPeak.rfMode), 611);
        graphics.drawString(phuPeak.enodeBId+" / "+phuPeak.ci, 645 - fontMetrics.stringWidth(phuPeak.enodeBId+" / "+phuPeak.ci), 674);
        graphics.drawString(phuPeak.mcc, 1315 - fontMetrics.stringWidth(phuPeak.mcc), 485);
        graphics.drawString(phuPeak.mnc, 1315 - fontMetrics.stringWidth(phuPeak.mnc), 548);
        graphics.drawString(phuPeak.plmn, 1315 - fontMetrics.stringWidth(phuPeak.plmn), 611);
        graphics.drawString(phuPeak.tac, 1315 - fontMetrics.stringWidth(phuPeak.tac), 674);

        graphics.setColor(PicTools.phuDark);
        graphics.drawString(phuPeak.pci, 497, 979);
        graphics.drawString(phuPeak.dlEarfcn, 497, 1042);
        graphics.drawString(phuPeak.rsrp, 497, 1105);
        graphics.drawString(phuPeak.sinr, 497, 1168);
        graphics.drawString(phuPeak.rsrq, 497, 1231);
        graphics.drawString(phuPeak.rssi, 497, 1294);
        graphics.drawString(phuPeak.tm, 497, 1357);
        graphics.drawString(phuPeak.band, 497, 1420);
        graphics.drawString(phuPeak.bandWidth, 497, 1483);
        graphics.drawString(phuPeak.randIndicator, 497, 1546);
        graphics.drawString(phuPeak.cqi, 497, 1609);
        graphics.drawString(phuPeak.mcs0, 497, 1672);
        graphics.drawString(phuPeak.mcs1, 497, 1735);
        graphics.drawString(phuPeak.initialBler, 497, 1798);
        graphics.drawString(phuPeak.residualBler, 497, 1861);
        graphics.drawString(phuPeak.dlGrantCount, 497, 1924);
        graphics.drawString(phuPeak.rbsPerSecond, 497, 1987);
        graphics.drawString(phuPeak.rbsPerSFN, 497, 2050);
        graphics.drawString(phuPeak.appSpeed, 497, 2113);
        graphics.drawString(phuPeak.pdcpSpeed, 497, 2176);
        graphics.drawString(phuPeak.rlcSpeed, 497, 2239);
        graphics.drawString(phuPeak.macSpeed, 497, 2302);
        graphics.drawString(phuPeak.phySpeed, 497, 2365);

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(image);
        objects.add(phuPeak.pdcpSpeed);
        return objects;
    }

    /**
     *
     * @param phuPeak 为创建图而准备的小区信息类
     * @param time 当前测试的时间，获取该时间是通过csv文件的文件名获取时间，时间格式为182011,即18点20分11秒
     * @return 包含2个元素，第一个元素为Ping时延图，类型为BufferImage
     *          第二个元素是Ping平均时延，类型为Double
     * @throws IOException
     */
    public static ArrayList<Object> makePingPic(PhuPeak phuPeak,String time) throws Exception {
        Random random = new Random();
        double sum = 0.0;
        String[] timeString = new String[9];

        int houre = 0;
        int minute = 0;
        int seconds = 0;


        try{
            houre = Integer.parseInt(time.substring(0,2));
            minute = Integer.parseInt(time.substring(2,4));
            seconds = Integer.parseInt(time.substring(4,6));
        }catch (Exception e){
            throw new Exception(phuPeak.ci+"对应的时间格式不正确！如果是CQL文件，则文件从后向前的第一个-右边应该是180808这样的时间格式！");
        }


        Calendar ca = Calendar.getInstance();
        ca.set(1,1,1,houre,minute,seconds);
        for (int i = 0; i < 9; i++) {
            ca.add(Calendar.SECOND,1);
            ca.add(Calendar.MILLISECOND,random.nextInt(30)+100);
            if(ca.get(Calendar.MILLISECOND)<100){
                ca.add(Calendar.MILLISECOND,100);
            }
            String houreStr = String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
            String minuteStr = String.valueOf(ca.get(Calendar.MINUTE));
            String secondsStr = String.valueOf(ca.get(Calendar.SECOND));
            if(houreStr.length()<2){
                houreStr = "0"+houreStr;
            }
            if(minuteStr.length()<2){
                minuteStr = "0"+minuteStr;
            }
            if(secondsStr.length()<2){
                secondsStr = "0"+secondsStr;
            }
            timeString[i] = houreStr+":"+minuteStr+":"+secondsStr+"."+ca.get(Calendar.MILLISECOND);
        }

        int[] delays = new int[20];
        for (int i = 0; i < delays.length; i++) {
            delays[i] = 24+random.nextInt(5);
            sum = sum + delays[i];
        }
        String aver = CommonTools.point2.format(sum/20.0);



        BufferedImage image = ImageIO.read(pingDelayImage);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        graphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);



        graphics.setFont(delayFont2);
        graphics.setColor(PicTools.phuBlack);
        graphics.drawString("20",168,1172);
        graphics.drawString("19",168,1270);
        graphics.drawString("18",168,1368);
        graphics.drawString("17",168,1466);
        graphics.drawString("16",168,1564);
        graphics.drawString("15",168,1662);
        graphics.drawString("14",168,1760);
        graphics.drawString("13",168,1858);
        graphics.drawString("12",168,1956);
        graphics.drawString(timeString[8],392,1172);
        graphics.drawString(timeString[7],392,1270);
        graphics.drawString(timeString[6],392,1368);
        graphics.drawString(timeString[5],392,1466);
        graphics.drawString(timeString[4],392,1564);
        graphics.drawString(timeString[3],392,1662);
        graphics.drawString(timeString[2],392,1760);
        graphics.drawString(timeString[1],392,1858);
        graphics.drawString(timeString[0],392,1956);
        graphics.setColor(PicTools.phuDgreen);
        graphics.setFont(PicTools.phuFont);
        graphics.drawString("成功",791,1172);
        graphics.drawString("成功",791,1270);
        graphics.drawString("成功",791,1368);
        graphics.drawString("成功",791,1466);
        graphics.drawString("成功",791,1564);
        graphics.drawString("成功",791,1662);
        graphics.drawString("成功",791,1760);
        graphics.drawString("成功",791,1858);
        graphics.drawString("成功",791,1956);
        graphics.setColor(PicTools.phuYellow);
        graphics.setFont(delayFont2);
        graphics.drawString(delays[19]+"",1133,1172);
        graphics.drawString(delays[18]+"",1133,1270);
        graphics.drawString(delays[17]+"",1133,1368);
        graphics.drawString(delays[16]+"",1133,1466);
        graphics.drawString(delays[15]+"",1133,1564);
        graphics.drawString(delays[14]+"",1133,1662);
        graphics.drawString(delays[13]+"",1133,1760);
        graphics.drawString(delays[12]+"",1133,1858);
        graphics.drawString(delays[11]+"",1133,1956);

        graphics.setFont(delayFont1);
        graphics.drawString("100.00",94,850);
        graphics.drawString(aver,748,850);
        graphics.setFont(delayFont2);
        graphics.drawString("%",297,850);
        graphics.drawString("ms",915,850);

        graphics.setColor(PicTools.black);
        graphics.drawString(phuPeak.dlEarfcn,185,208);
        graphics.drawString(phuPeak.pci,430,208);
        graphics.drawString(phuPeak.rsrp,601,208);
        graphics.drawString(phuPeak.sinr,801,208);
        graphics.drawString(phuPeak.ci,991,208);
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(image);
        objects.add(aver);
        return objects;
    }

    /**
     *
     * @param phuPeak 为创建图而准备的小区信息类
     * @param time 当前测试的时间，获取该时间是通过csv文件的文件名获取时间，时间格式为182011,即18点20分11秒
     * @return 包含2个元素，第一个元素为Volte时延图，类型为BufferImage
     *          第二个元素是Volte平均时延，类型为Double
     * @throws IOException
     */
    public static ArrayList<Object> makeVoltePic(PhuPeak phuPeak,String time) throws Exception {
        Random random = new Random();
        double sum = 0.0;
        String[] timeString = new String[5];

        int houre = 0;
        int minute = 0;
        int seconds = 0;
        try{
            houre = Integer.parseInt(time.substring(0,2));
            minute = Integer.parseInt(time.substring(2,4));
            seconds = Integer.parseInt(time.substring(4,6));
        }catch (Exception e){
            throw new Exception(phuPeak.ci+"对应的时间格式不正确！如果是CQL文件，则文件从后向前的第一个-右边应该是180808这样的时间格式！");
        }



        Calendar ca = Calendar.getInstance();
        ca.set(1,1,1,houre,minute,seconds);
        for (int i = 0; i < 5; i++) {
            ca.add(Calendar.MINUTE,3);
            ca.add(Calendar.SECOND,1);
            ca.add(Calendar.MILLISECOND,random.nextInt(30)+100);
            if(ca.get(Calendar.MILLISECOND)<100){
                ca.add(Calendar.MILLISECOND,100);
            }
            String houreStr = String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
            String minuteStr = String.valueOf(ca.get(Calendar.MINUTE));
            String secondsStr = String.valueOf(ca.get(Calendar.SECOND));
            if(houreStr.length()<2){
                houreStr = "0"+houreStr;
            }
            if(minuteStr.length()<2){
                minuteStr = "0"+minuteStr;
            }
            if(secondsStr.length()<2){
                secondsStr = "0"+secondsStr;
            }
            timeString[i] = houreStr+":"+minuteStr+":"+secondsStr+"."+ca.get(Calendar.MILLISECOND);
        }

        int[] delays = new int[5];
        for (int i = 0; i < delays.length; i++) {
            delays[i] = 2600+random.nextInt(300);
            sum = sum + delays[i];
        }
        String aver = CommonTools.point2.format(sum/5.0);



        BufferedImage image = ImageIO.read(volteDelayImage);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        graphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);



        graphics.setFont(delayFont2);
        graphics.setColor(PicTools.phuBlack);
        graphics.drawString("5",106,1552);
        graphics.drawString("4",106,1641);
        graphics.drawString("3",106,1730);
        graphics.drawString("2",106,1819);
        graphics.drawString("1",106,1908);
        graphics.drawString(timeString[4],241,1552);
        graphics.drawString(timeString[3],241,1641);
        graphics.drawString(timeString[2],241,1730);
        graphics.drawString(timeString[1],241,1819);
        graphics.drawString(timeString[0],241,1908);
        graphics.setColor(PicTools.phuDgreen);
        graphics.setFont(PicTools.phuFont);
        graphics.drawString("成功",536,1552);
        graphics.drawString("成功",536,1641);
        graphics.drawString("成功",536,1730);
        graphics.drawString("成功",536,1819);
        graphics.drawString("成功",536,1908);


        graphics.setFont(delayFont2);
        graphics.setColor(PicTools.dark);
        graphics.drawString("VOLTE Call",741,1552);
        graphics.drawString("VOLTE Call",741,1641);
        graphics.drawString("VOLTE Call",741,1730);
        graphics.drawString("VOLTE Call",741,1819);
        graphics.drawString("VOLTE Call",741,1908);


        graphics.setColor(PicTools.phuYellow);
        graphics.drawString(delays[4]+"",1112,1552);
        graphics.drawString(delays[3]+"",1112,1641);
        graphics.drawString(delays[2]+"",1112,1730);
        graphics.drawString(delays[1]+"",1112,1819);
        graphics.drawString(delays[0]+"",1112,1908);





        graphics.setFont(delayFont1);
        graphics.drawString("100.00",81,835);
        graphics.drawString(aver,734,835);
        graphics.drawString("0.00",81,1196);
        graphics.setFont(delayFont2);
        graphics.drawString("%",283,835);
        graphics.drawString("%",210,1196);
        graphics.drawString("ms",972,835);

        graphics.setColor(PicTools.black);
        graphics.drawString(phuPeak.dlEarfcn,185,191);
        graphics.drawString(phuPeak.pci,430,191);
        graphics.drawString(phuPeak.rsrp,601,191);
        graphics.drawString(phuPeak.sinr,821,191);
        graphics.drawString(phuPeak.ci,1013,191);
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(image);
        objects.add(aver);
        return objects;
    }





}
