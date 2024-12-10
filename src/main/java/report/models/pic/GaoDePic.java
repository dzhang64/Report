package report.models.pic;

import report.models.net.Cell;
import report.utils.CommonTools;
import report.utils.GaoDeMapTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GaoDePic {
    //扇区的颜色
    public static Color[] colors = {Color.pink,Color.orange,Color.CYAN,Color.BLUE,Color.RED};

    //扇区名的偏移
    public static int offset = 10;

    //扇区的半径
    public static int r = 100;

    //扇区的扇角
    public static int beamWidth = 60;


    public int startX;

    public int endX;

    public int startY;

    public int endY;

    public BufferedImage image;

    public File file;

    public int pointSize = 20;

    public Graphics graphics;

    public  Font strFont = new Font("SimSun", Font.BOLD,16);

    public  Font mapFont = new Font("ALGER", Font.BOLD,20);


    public GaoDePic() {
    }

    public GaoDePic(int startX, int endX, int startY, int endY, BufferedImage image, File file) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.image = image;
        this.file = file;
        this.graphics = this.image.createGraphics();
        this.graphics.setFont(strFont);
    }

    public GaoDePic getCopy() throws IOException {
        GaoDePic gaoDePic = new GaoDePic();
        gaoDePic.startX = this.startX;
        gaoDePic.endX = this.endX;
        gaoDePic.startY = this.startY;
        gaoDePic.endY = this.endY;
        gaoDePic.file = this.file;
        gaoDePic.image = ImageIO.read(this.file);
        gaoDePic.graphics = gaoDePic.image.createGraphics();
        gaoDePic.graphics.setFont(strFont);
        return gaoDePic;
    }

    public void drawOver(double lon, double la, Color c){
        graphics.setColor(c);
        int[] point = calPoint(lon, la);
        graphics.fillOval(point[0],point[1],pointSize,pointSize);
    }



    public int[] calPoint(double lon,double la){
        int[] tileNumber = GaoDeMapTools.getTileNumber(lon, la);
        int xNum = tileNumber[0] - startX;
        int yNum = tileNumber[1] - startY;
        int[] pixByGIS = GaoDeMapTools.getPixByGIS(lon, la);
        int pixerX = xNum*255+pixByGIS[0];
        int pixerY = yNum*255+pixByGIS[1];
        return new int[]{pixerX,pixerY};
    }

    public BufferedImage drawOvers(ArrayList<Point> points){
        for (int i = 0; i<points.size(); i++) {
            drawOver(points.get(i).longtitude,points.get(i).latitude,points.get(i).color);
        }
        return image;
    }

    //画切换
    public BufferedImage drawHO(ArrayList<Point> points){
        Color color = points.get(0).color;
        for (int i = 0; i<points.size(); i++) {
            drawOver(points.get(i).longtitude,points.get(i).latitude,points.get(i).color);
        }

        //切换遍历2遍的原因是如果一次遍历画完，后面的点会覆盖前面的字
        graphics.setColor(Color.red);
        for (int i = 0; i<points.size(); i++) {
            if(color!=points.get(i).color){
                color = points.get(i).color;
                int[] ints = calPoint(points.get(i).longtitude, points.get(i).latitude);
                graphics.setFont(mapFont);
                graphics.drawString("HO",ints[0],ints[1]);
            }
        }
        return image;
    }
    //画删除，需要注意r是直径
    public void drawCell(String cellName,int offset,double lon,double la,int r,int[] azimuths,int beamWidth, Color[] colors){
        int[] ints = calPoint(lon, la);
        for (int i = 0; i < azimuths.length; i++) {
            graphics.setColor(colors[i]);
            //加270是因为数据给出的0度是正北，即360度，而在java中0度是水平的，实际是90度，所以加270度
            int spDgree = CommonTools.getSPDgress(azimuths[i]);
            graphics.fillArc(ints[0]-r/2,ints[1]-r/2,r,r,spDgree - beamWidth/2,beamWidth);
            graphics.setColor(Color.black);
            graphics.drawString(cellName,ints[0]+offset,ints[1]);
        }
    }

    public void drawCell(String cellName,double lon,double la,int[] azimuths){
        drawCell(cellName,offset,lon,la,r,azimuths,beamWidth,colors);
    }

    public void drawEnodeB(EnodeBPoint enodeB){
        drawCell(enodeB.name,offset,enodeB.lon,enodeB.la,r,enodeB.azimuth,enodeB.beamWidth,colors);
    }











}
