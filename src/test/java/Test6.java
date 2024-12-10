import report.models.net.PhuPeak;
import report.utils.CommonTools;
import report.utils.PicTools;
import sun.awt.SunHints;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Test6 {

    public static void main(String[] args) throws IOException, FontFormatException {
        File file = new File("D:\\tp\\s2.jpg");
        File file1 = new File("D:\\tp\\tp2.jpg");
        Font font1 = Font.createFont(Font.TRUETYPE_FONT, new File("G:\\Web\\font\\HarmonyOS_Sans_Regular.ttf"));
        font1 = font1.deriveFont(Font.BOLD, 60);
        Font font2 = Font.createFont(Font.TRUETYPE_FONT, new File("G:\\Web\\font\\HarmonyOS_Sans_Regular.ttf"));
        font2 = font2.deriveFont(Font.PLAIN, 38);
        PhuPeak phuPeak = new PhuPeak("540681", "20979", "435", "11",320711,"10MHZ","460","01","3745","8","TM4","864013041650860","460011845311990");
        ArrayList<Object> objects = makePingPic(phuPeak, file, font1, font2, "140201");
        BufferedImage bufferedImage = (BufferedImage) objects.get(0);
        String pingDelay = (String) objects.get(1);
        ImageIO.write(bufferedImage,"jpg",file1);
        System.out.println(pingDelay);

    }

    public static ArrayList<Object> makePingPic(PhuPeak phuPeak, File file, Font font1, Font font2, String time) throws IOException {
        Random random = new Random();
        double sum = 0.0;

        int houre = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(2,4));
        int seconds = Integer.parseInt(time.substring(4,6));
        String[] timeString = new String[9];


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



        BufferedImage image = ImageIO.read(file);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        graphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        graphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);



        graphics.setFont(font2);
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
        graphics.setFont(font2);
        graphics.drawString(delays[19]+"",1133,1172);
        graphics.drawString(delays[18]+"",1133,1270);
        graphics.drawString(delays[17]+"",1133,1368);
        graphics.drawString(delays[16]+"",1133,1466);
        graphics.drawString(delays[15]+"",1133,1564);
        graphics.drawString(delays[14]+"",1133,1662);
        graphics.drawString(delays[13]+"",1133,1760);
        graphics.drawString(delays[12]+"",1133,1858);
        graphics.drawString(delays[11]+"",1133,1956);

        graphics.setFont(font1);
        graphics.drawString("100.00",94,850);
        graphics.drawString(aver,748,850);
        graphics.setFont(font2);
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
}
