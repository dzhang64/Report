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

public class Test7 {
    public static void main(String[] args) throws IOException, FontFormatException {
        File file = new File("D:\\tp\\s3.jpg");
        File file1 = new File("D:\\tp\\tp3.jpg");
        Font font1 = Font.createFont(Font.TRUETYPE_FONT, new File("G:\\Web\\font\\HarmonyOS_Sans_Regular.ttf"));
        font1 = font1.deriveFont(Font.BOLD, 60);
        Font font2 = Font.createFont(Font.TRUETYPE_FONT, new File("G:\\Web\\font\\HarmonyOS_Sans_Regular.ttf"));
        font2 = font2.deriveFont(Font.PLAIN, 38);
        PhuPeak phuPeak = new PhuPeak("540681", "20979", "435", "11",320711,"10MHZ","460","01","3745","8","TM4","864013041650860","460011845311990");
        ArrayList<Object> objects = makeVoltePic(phuPeak, file, font1, font2, "140201");
        BufferedImage bufferedImage = (BufferedImage) objects.get(0);
        String volteDelay = (String) objects.get(1);
        ImageIO.write(bufferedImage,"jpg",file1);
    }

    public static ArrayList<Object> makeVoltePic(PhuPeak phuPeak, File file, Font font1, Font font2, String time) throws IOException {
        Random random = new Random();
        double sum = 0.0;

        int houre = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(2,4));
        int seconds = Integer.parseInt(time.substring(4,6));
        String[] timeString = new String[5];


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


        graphics.setFont(font2);
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





        graphics.setFont(font1);
        graphics.drawString("100.00",81,835);
        graphics.drawString(aver,734,835);
        graphics.drawString("0.00",81,1196);
        graphics.setFont(font2);
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
