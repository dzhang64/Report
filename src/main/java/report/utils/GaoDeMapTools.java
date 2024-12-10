package report.utils;

import report.models.pic.EnodeBPoint;
import report.models.pic.GaoDePic;
import report.models.pic.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GaoDeMapTools {

    public static int level = 16;
    //图片类型，矢量图为style=7，影像底图为style=6 ，影像路网style=8
    public static int picType =7;
    //是否包含标记scl=1为包含标记，scl=2为不包含标记
    public static int needMark = 1;
    //扇区的颜色
    public static Color[] colors = {Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};
    //扇区名的偏移
    public static int offset = 10;

    //扇区的半径
    public static int r = 100;

    //扇区的扇角
    public static int beamWidth = 60;



    //矢量图为style=7，影像底图为style=6 ，影像路网style=8
    public static BufferedImage getTileMap(int level,int x,int y,int picType,int needMark) throws IOException {
        String ulr =  "http://wprd01.is.autonavi.com/appmaptile?x="+x+"&y="+y+"&z="+level+"&lang=zh_cn&size=1&scl="+needMark+"&style="+picType;
        return HttpTools.getBufferImageFromURL(ulr);
    }

    public static BufferedImage getTileMap(int x,int y) throws IOException {
        String ulr =  "http://wprd01.is.autonavi.com/appmaptile?x="+x+"&y="+y+"&z="+level+"&lang=zh_cn&size=1&scl="+needMark+"&style="+picType;

        try{
            return HttpTools.getBufferImageFromURL(ulr);
        }catch (Exception e){
            try{
                return HttpTools.getBufferImageFromURL(ulr);
            }catch (Exception e1){
                try{
                    return HttpTools.getBufferImageFromURL(ulr);
                }catch (Exception e2){
                    try{
                        return HttpTools.getBufferImageFromURL(ulr);
                    }catch (Exception e3){
                        try{
                            return HttpTools.getBufferImageFromURL(ulr);
                        }catch (Exception e4){
                            throw  e4;
                        }
                    }
                }
            }
        }
    }

    public static BufferedImage[][] getAreaImage(int x,int xNum,int y,int yNum) throws IOException {
        BufferedImage[][] bufferedImages = new BufferedImage[yNum][xNum];
        for (int j = 0; j < yNum; j++) {
            for (int i = 0; i < xNum; i++) {
                BufferedImage tileMap = getTileMap(i+x, j+y);
                bufferedImages[j][i] = tileMap;
            }
        }
        return bufferedImages;
    }

    //根据2个点获取图；
    public static BufferedImage[][] getAreaImageByTwoPoint(int x1,int y1,int x2,int y2) throws IOException {
        int xNum = x2 - x1 +1;
        int yNum = y2 - y1 +1;
        return getAreaImage(x1, xNum, y1, yNum);
    }

    public static int[] getTileNumber( double lon, double lat) {
        double[] doubles = CommonTools.wgs2gcj(lon, lat);
        lon = doubles[0];
        lat = doubles[1];
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<level) ) ;
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<level) ) ;
        if (xtile < 0)
            xtile=0;
        if (xtile >= (1<<level))
            xtile=((1<<level)-1);
        if (ytile < 0)
            ytile=0;
        if (ytile >= (1<<level))
            ytile=((1<<level)-1);
        return new int[]{xtile,ytile};
    }

    //p1为左上的点，p2为右下的点，截取一段图片
    public static BufferedImage getBufferImageByGPS(double[] p1,double[] p2) throws IOException {
        int[] tileNumber = getTileNumber(p1[0], p1[1]);
        int[] tileNumber1 = getTileNumber(p2[0], p2[1]);
        BufferedImage[][] areaImageByTwoPoint = getAreaImageByTwoPoint(tileNumber[0], tileNumber[1], tileNumber1[0], tileNumber1[1]);
        return PicTools.combinePicByBufferedImageArray(areaImageByTwoPoint,0,0);
    }

    //p1为左上的点，p2为右下的点，截取一段图片，创建高德地图GaoDePic类
    public static GaoDePic createGaoDePicByGPS(double[] p1,double[] p2) throws IOException {
        int[] tileNumber = getTileNumber(p1[0], p1[1]);
        int[] tileNumber1 = getTileNumber(p2[0], p2[1]);
        BufferedImage[][] areaImageByTwoPoint = getAreaImageByTwoPoint(tileNumber[0], tileNumber[1], tileNumber1[0], tileNumber1[1]);
        BufferedImage bufferedImage = PicTools.combinePicByBufferedImageArray(areaImageByTwoPoint, 0, 0);
        File file = new File("d:\\shuchu", System.currentTimeMillis() + ".jpg");
        ImageIO.write(bufferedImage,"jpg",file);
        GaoDePic gaoDePic = new GaoDePic();
        gaoDePic.startX = tileNumber[0];
        gaoDePic.startY = tileNumber[1];
        gaoDePic.endX = tileNumber1[0];
        gaoDePic.endY = tileNumber1[1];
        gaoDePic.image = bufferedImage;
        gaoDePic.file = file;
        gaoDePic.graphics = gaoDePic.image.createGraphics();
        gaoDePic.graphics.setFont(gaoDePic.strFont);
        return gaoDePic;
    }

    //p数组包含2个元素，第一个元素是经度，第2个元素是维度，返回值包含2个元素，第一个为像素x坐标，第2个为像素y坐标
    public static int[] getPixByGIS(double ln,double la){
        double[] doubles = CommonTools.wgs2gcj(ln, la);
        ln = doubles[0];
        la = doubles[1];
        //计算x
        double temp  =  (ln + 180) / 360;
        int pixX = (int) (temp * Math.pow(2,level) * 256 % 256);
        //计算y
        double sinLatitude = Math.sin(la * Math.PI / 180);
        temp =  0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);
        int pixelY = (int) (temp * Math.pow(2,level) * 256 % 256);
        return new int[]{pixX,pixelY};
    }

    public static GaoDePic createCellMapByPoints(ArrayList<Point> pointsList, EnodeBPoint enodeBPoint) throws IOException {
        Point[] boundPoint = CommonTools.getBoundPoint(pointsList);
        GaoDePic gaoDePic = new GaoDePic();
        double[] p1 = {boundPoint[0].longtitude,boundPoint[0].latitude};
        double[] p2 = {boundPoint[1].longtitude,boundPoint[1].latitude};
        int[] tileNumber = getTileNumber(p1[0], p1[1]);
        int[] tileNumber1 = getTileNumber(p2[0], p2[1]);
        gaoDePic.startX = tileNumber[0];
        gaoDePic.startY = tileNumber[1];
        gaoDePic.endX = tileNumber1[0];
        gaoDePic.endY = tileNumber1[1];
        BufferedImage[][] areaImageByTwoPoint = getAreaImageByTwoPoint(tileNumber[0], tileNumber[1], tileNumber1[0], tileNumber1[1]);
        BufferedImage bufferedImage = PicTools.combinePicByBufferedImageArray(areaImageByTwoPoint, 0, 0);
        Graphics2D graphics = bufferedImage.createGraphics();
        int[] ints = gaoDePic.calPoint(enodeBPoint.lon, enodeBPoint.la);
        int spDegree;
        for (int i = 0; i < enodeBPoint.azimuth.length; i++) {
            graphics.setColor(colors[i]);
            spDegree = CommonTools.getSPDgress(enodeBPoint.azimuth[i]);
            graphics.fillArc(ints[0]-r/2,ints[1]-r/2,r,r,spDegree+beamWidth/2,-beamWidth);
            graphics.setColor(Color.black);
            graphics.drawString(enodeBPoint.name,ints[0]+offset,ints[1]);
        }
        File file = new File(StaticStorage.tempDir, System.currentTimeMillis() + ".jpg");
        ImageIO.write(bufferedImage,"jpg",file);
        gaoDePic.image = bufferedImage;
        gaoDePic.file = file;
        gaoDePic.graphics = graphics;
        gaoDePic.graphics.setFont(gaoDePic.strFont);
        return gaoDePic;
    }



}
