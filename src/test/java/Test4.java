import net.coobird.thumbnailator.Thumbnails;
import report.functions.jsunicom.lte.Report;
import report.functions.jsunicom.lte.TestFile;
import report.models.net.Cell;
import report.models.pic.GaoDePic;
import report.models.pic.Point;
import report.utils.CommonTools;
import report.utils.GaoDeMapTools;
import report.utils.NetTools;
import report.utils.PicTools;
import sun.awt.SunHints;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test4 {

    public static void main(String[] args) throws IOException {
        String a = "YC_TH_ZX_亭湖青墩月青_FL9_A_1";
        int fl = a.indexOf("_FL");
        System.out.println(a.substring(0, fl));


    }

    public static BufferedImage[] createRsrpImage(File file,int level) throws Exception {
        GaoDeMapTools.level = level;
        TestFile testFile = TestFile.createTestFile(file,0);
        ArrayList<Point> rsrpPointList = testFile.createPoint(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray);
        Point[] boundPoint = CommonTools.getBoundPoint(rsrpPointList);
        double[] p1 = {boundPoint[0].longtitude,boundPoint[0].latitude};
        double[] p2 = {boundPoint[1].longtitude,boundPoint[1].latitude};
        GaoDePic gaoDePic = GaoDeMapTools.createGaoDePicByGPS(p1, p2);
        gaoDePic.drawOvers(rsrpPointList);
        BufferedImage bufferedImage = PicTools.addThresholdMapBySection(gaoDePic.image, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);
        File file1 = new File("D:/123/dt.jpg");
        ImageIO.write(bufferedImage,"jpg",file1);

        gaoDePic = gaoDePic.getCopy();
        rsrpPointList = testFile.createPointByPCI(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray,234);
        gaoDePic.drawOvers(rsrpPointList);
        bufferedImage = PicTools.addThresholdMapBySection(gaoDePic.image, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);
        file1 = new File("D:/123/dt-1.jpg");
        ImageIO.write(bufferedImage,"jpg",file1);

        gaoDePic = gaoDePic.getCopy();
        rsrpPointList = testFile.createPointByPCI(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray,235);
        gaoDePic.drawOvers(rsrpPointList);
        bufferedImage = PicTools.addThresholdMapBySection(gaoDePic.image, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);
        file1 = new File("D:/123/dt-2.jpg");
        ImageIO.write(bufferedImage,"jpg",file1);

        gaoDePic = gaoDePic.getCopy();
        rsrpPointList = testFile.createPointByPCI(TestFile.rsrpType, Report.rsrpArray, Report.rsrpColorArray,236);
        gaoDePic.drawOvers(rsrpPointList);
        bufferedImage = PicTools.addThresholdMapBySection(gaoDePic.image, NetTools.rsrpName, Report.rsrpArray, Report.rsrpColorArray);
        file1 = new File("D:/123/dt-3.jpg");
        ImageIO.write(bufferedImage,"jpg",file1);
        return null;

    }

    public static void testCreateShowImg() throws IOException {
        File file = new File("D:/123/back.png");
        File file1 = new File("D:/123/back1.png");
        String name = "PDCP Throughput DL";
        Cell cell = new Cell("20342","LTE", "FDD", "460", "01", "29022", "86234", "移动公司", "21", "移动公司-21", "8", "3745", "234", "10", "954.5", "909.5", "120.001631", "33.283161","TM4","17","120");
        int[] section = {0,10,20,30,40,50};
        Color[] colors = {Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};
        double[] datas = {7424,33458,32668,32498,32835,34505,32831,32352,33411,33490,45045,43845,34090,34618,33976,34873,34646,34174,34785,34513,33311,32244,32128,33642,33238,33603,34675,33600,34087,33133,33806,32014,31673,30960,30683,30865,30435,29441,28967,30095,31234,29544,31990,32675,27099,27279,29170,26124,6093,3,34,57};
        for (int i = 0; i < datas.length; i++) {
            datas[i] = datas[i]/1024;
        }
        BufferedImage showImg = createShowImg(name,file, cell,section, colors, datas);
        ImageIO.write(showImg,"jpg",file1);
    }



    public static BufferedImage createShowImg(String name,File file, Cell cell,int[] section,Color[] sectionColor,double[] datas) throws IOException {

        //开始代码
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
            graphics.drawString((0+100/section.length*i)+"%",453,390-i*41);
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


}
