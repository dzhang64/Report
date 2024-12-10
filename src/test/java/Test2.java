import report.utils.PicTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Test2 {
    public static void main(String[] args) throws IOException {
        test();
        test1();
    }

    //测试在图上添加阈值,使用区间模式
    public static void test() throws IOException {
        File file = new File("D:\\2.jpg");
        BufferedImage bufferedImage = PicTools.readPhoto(file);
        int[] ints = {15, 27, 50, 100, 300};
        Color[] colors = {
                PicTools.green, PicTools.skyBlue, PicTools.deepBlue, PicTools.yellow, PicTools.red, PicTools.black
        };
        File file1 = new File("D:\\3.jpg");
        BufferedImage bufferedImage1 = PicTools.addThresholdMapBySection(bufferedImage, "Ping Deley(ms)", ints, colors);
        ImageIO.write(bufferedImage1,"jpg",file1);
    }

    //测试在图上添加阈值，使用集合模式，如PCI
    public static void test1() throws IOException {
        File file = new File("D:\\2.jpg");
        BufferedImage bufferedImage = PicTools.readPhoto(file);
        HashMap<String, Color> pciMap = new HashMap<>();
        pciMap.put("257",Color.BLUE);
        pciMap.put("258",Color.CYAN);
        pciMap.put("259",Color.red);
        pciMap.put("260",Color.green);
        File file1 = new File("D:\\4.jpg");
        BufferedImage bufferedImage1 = PicTools.addThresholdMapByType(bufferedImage, "LTE PCI", pciMap);
        ImageIO.write(bufferedImage1,"jpg",file1);
    }
}
