import report.models.pic.GaoDePic;
import report.utils.CommonTools;
import report.utils.GaoDeMapTools;
import report.utils.PicTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Test3 {

    public static void main(String[] args) throws IOException {
        File file = new File ("G:\\盐城电信4G补测\\小区信息表.csv");
        BufferedImage bufferedImage = testGaoDePicByNodeb(file,15);

    }

    //测试下载和生成地图，从x为26969开始下载20张，y为12397开始下载15张，合并为一个图片
    public static void testGetAndCombineImage() throws IOException {
        GaoDeMapTools.level = 15;
        BufferedImage[][] areaImage = GaoDeMapTools.getAreaImage(26969, 20, 12397, 15);
        BufferedImage bufferedImage = PicTools.combinePicByBufferedImageArray(areaImage, 0, 0);
        ImageIO.write(bufferedImage,"jpg",new File("d:\\123\\64.jpg"));
    }

    //测试根据经纬度获取地图的图片
    public static void getPicByGIS() throws IOException {
        GaoDeMapTools.level = 18;
        double[] p1 = {117.151187,31.738979};
        double[] p2 = {117.166133,31.728773};
        BufferedImage bufferImageByGPS = GaoDeMapTools.getBufferImageByGPS(p1, p2);
        ImageIO.write(bufferImageByGPS,"jpg",new File("D:\\123\\test.jpg"));
    }
    //根据经纬度获取图片中某个像素点
    public static int[] getPix(){
        GaoDeMapTools.level = 18;
        return GaoDeMapTools.getPixByGIS(117.169559, 31.758077);
    }

    //根据经纬度获取一个图片，并且在图片中打点
    public static BufferedImage testGaoDePic(File file,double[] p1,double[] p2) throws IOException {
        GaoDeMapTools.level = 17;
        GaoDeMapTools.r = 50;
        GaoDePic gaoDePic = GaoDeMapTools.createGaoDePicByGPS(p1, p2);

        Color[] colors = {Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
        String record = reader.readLine();
        while ((record = reader.readLine()) != null) {
            String[] msg = record.split(",");
            double lon = Double.parseDouble(msg[2]);
            double la =  Double.parseDouble(msg[3]);
            String name = msg[0];
            String[] azimuthsStr = msg[1].split("-");
            ArrayList<Integer> integers = new ArrayList<>();
            for (int i = 0; i < azimuthsStr.length; i++) {
                integers.add(Integer.valueOf(azimuthsStr[i]));
            }
            Integer[] temp = integers.toArray(new Integer[0]);
            int[] azimuths = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                azimuths[i] = temp[i];
            }
            gaoDePic.drawCell(name,lon,la,azimuths);

        }
        return gaoDePic.image;
    }

    //根据基站经纬度获取照片
    public static BufferedImage testGaoDePicByNodeb(File file,int picType) throws IOException {

        class Cell {
            public String name;
            public int[] azimuth = new int[1];
            public double lon;
            public double la;

            public Cell(String name, int azimuth, double lon, double la) {
                this.name = name;
                this.azimuth[0] = azimuth;
                this.lon = lon;
                this.la = la;
            }
        }

        ArrayList<Cell> cellList = new ArrayList<>();


        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
        String record = reader.readLine();
        while ((record = reader.readLine()) != null) {
            String[] msg = record.split(",");
            int azimuth = Integer.parseInt(msg[1]);
            double lon = Double.parseDouble(msg[2]);
            double la =  Double.parseDouble(msg[3]);
            String name = msg[0];
            Cell cell = new Cell(name, azimuth, lon, la);
            cellList.add(cell);
        }

        double lowLon = cellList.get(0).lon;
        double lowLa = cellList.get(0).la;
        double highLon = cellList.get(0).lon;
        double highLa = cellList.get(0).la;
        for (int i = 1; i < cellList.size(); i++) {
            if(lowLon>cellList.get(i).lon){
                lowLon = cellList.get(i).lon;
            }
            if(lowLa>cellList.get(i).la){
                lowLa = cellList.get(i).la;
            }
            if(highLon<cellList.get(i).lon){
                highLon = cellList.get(i).lon;
            }
            if(highLa<cellList.get(i).la){
                highLa = cellList.get(i).la;
            }
        }

        double[] p1 = new double[]{lowLon-0.01,highLa+0.01};
        double[] p2 = new double[]{highLon+0.01,lowLa-0.01};


        GaoDeMapTools.level = picType;
        GaoDeMapTools.r = 20;
        GaoDePic.r = 20;

        GaoDePic gaoDePic = GaoDeMapTools.createGaoDePicByGPS(p1, p2);

        Color[] colors = {Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};

        for (int i = 0; i < cellList.size(); i++) {
            gaoDePic.drawCell(cellList.get(i).name,cellList.get(i).lon,cellList.get(i).la,cellList.get(i).azimuth);
        }
        File result = new File(file.getParentFile(),"pic.jpg");
        ImageIO.write(gaoDePic.image,"jpg",result);
        return gaoDePic.image;
    }



}
