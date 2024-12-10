import report.models.net.PhuPeak;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import static report.utils.CommonTools.splitCSVLine;

public class Test8 {

    public static void main(String[] args) throws IOException, FontFormatException {
        File file = new File("D:\\lianyungang\\连云港小区.csv");
        File file1 = new File("D:\\tp\\s1.jpg");
        File file2 = new File("D:\\tp\\s2.jpg");
        File file3 = new File("D:\\tp\\s3.jpg");
        Font font1 = Font.createFont(Font.TRUETYPE_FONT, new File("G:\\Web\\font\\HarmonyOS_Sans_Regular.ttf"));
        Font font2 = font1.deriveFont(Font.PLAIN, 42);
        Font font3 = font1.deriveFont(Font.BOLD, 60);
        Font font4 = font2.deriveFont(Font.PLAIN, 38);
        File root = new File("D:\\lianyungang");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
        String record = reader.readLine();
        int j = 1;
        while ((record = reader.readLine()) != null) {
            j++;
            String[] cellMsg = splitCSVLine(record);
            String cellName = cellMsg[2];
            File cellRoot = new File(root, cellName);
            if(cellRoot.exists()){
                File[] files = cellRoot.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            if(!cellRoot.exists()){

                cellRoot.mkdir();
            }
            if(cellMsg[0].trim().length()==0){
                System.out.println(j);
                System.out.println(cellMsg[0]);
                break;
            }
            PhuPeak phuPeak = new PhuPeak("540681", "20979", "435", "11",320711,"10MHZ","460","01","3745","8","TM4","864013041650860","460011845311990");
            ArrayList<Object> objects = Test5.makePhuPeak(file1, phuPeak, font2);
            BufferedImage bufferedImage = (BufferedImage) objects.get(0);
            String peakValue = (String) objects.get(1);
            System.out.println(cellName + ":峰值速率:" + peakValue);
            File phuPeakFile = new File(root,cellName+"/peak.jpg");
            ImageIO.write(bufferedImage, "jpg", phuPeakFile);
            String time = getRandomTime();
            phuPeak = phuPeak.copy();
            objects = Test6.makePingPic(phuPeak, file2, font3, font4, time);
            File pingDelayFile = new File(root,cellName+"/pingDelay.jpg");
            bufferedImage = (BufferedImage) objects.get(0);
            String pingDelayValue = (String) objects.get(1);
            System.out.println(cellName + ":Ping时延:" + pingDelayValue);
            ImageIO.write(bufferedImage, "jpg", pingDelayFile);
            phuPeak = phuPeak.copy();
            objects = Test7.makeVoltePic(phuPeak, file3, font3, font4, time);
            bufferedImage = (BufferedImage) objects.get(0);
            String volteDelayValue = (String) objects.get(1);
            File volteDelayFile = new File(root,cellName+"/volteDelayFile.jpg");
            ImageIO.write(bufferedImage, "jpg", volteDelayFile);
            System.out.println(cellName + ":VOLTE时延:" + volteDelayValue);
        }


    }

    public static String getRandomTime(){
        Random random = new Random();
        String hourStr = String.valueOf(random.nextInt(9)+9);
        String minuteStr = String.valueOf(random.nextInt(59));
        String secondStr = String.valueOf(random.nextInt(59));
        if(hourStr.length()<2){
            hourStr = "0"+hourStr;
        }
        if(minuteStr.length()<2){
            minuteStr = "0"+minuteStr;
        }
        if(secondStr.length()<2){
            secondStr = "0"+secondStr;
        }
        return  hourStr+minuteStr+secondStr;
    }
}
