import report.models.net.PhuPeak;
import report.utils.PicTools;
import sun.awt.SunHints;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Test5 {
    public static void main(String[] args) throws IOException, FontFormatException {
        File file = new File("D:\\tp\\s1.jpg");
        File file1 = new File("D:\\tp\\tp1.jpg");
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File("G:\\Web\\font\\HarmonyOS_Sans_Regular.ttf"));
        font = font.deriveFont(Font.PLAIN, 42);
        PhuPeak phuPeak = new PhuPeak("540681", "20979", "435", "11",320711,"10MHZ","460","01","3745","8","TM4","864013041650860","460011845311990");

        ArrayList<Object> objects = makePhuPeak(file, phuPeak, font);
        BufferedImage bufferedImage = (BufferedImage) objects.get(0);
        String peakValue = (String) objects.get(1);
        ImageIO.write(bufferedImage, "jpg", file1);
        System.out.println(peakValue);


    }

    public static ArrayList<Object> makePhuPeak(File file, PhuPeak phuPeak, Font font) throws IOException, FontFormatException {
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
        graphics.setColor(PicTools.phuBlue);
        graphics.setFont(font);
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
}




