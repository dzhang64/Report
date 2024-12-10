import report.functions.teleinner.InHouseTools;
import report.functions.teleinner.KeyDate;
import report.utils.CommonTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class Test10 {
    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<KeyDate> keyDates = InHouseTools.readInhouseCsv("D:\\tp\\ExportData_220816_103539_FTPD@海洋大学本部1号女生宿舍楼_581_dl##_20220819092522.csv");
        BufferedImage back = ImageIO.read(new File("D:\\tp\\1.jpg"));
        File out = new File("D:\\tp\\1out.jpg");
        Graphics2D graphics = back.createGraphics();
        for (int i = 0; i < keyDates.size(); i++) {
            KeyDate keyDate = keyDates.get(i);
            int[] points = getPointFromGIS(keyDate.longtitude, keyDate.latitud);
            Color color = InHouseTools.selectRsrpColor(keyDate.nrRsrp);
            graphics.setColor(color);
            graphics.drawOval(points[0],points[1],8,8);
        }
        ImageIO.write(back,"jpg",out);
    }

    public static int[] getPointFromGIS(double ln,double la){
        //计算x
        double temp  =  (ln + 180) / 360;
        int pixX = (int) (temp * Math.pow(2,18) * 256);
        //计算y
        double sinLatitude = Math.sin(la * Math.PI / 180);
        temp =  0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);
        int pixelY = (int) (temp * Math.pow(2,18) * 256);
        return new int[]{pixX,pixelY};
    }
}
