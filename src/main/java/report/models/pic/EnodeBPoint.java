package report.models.pic;

import java.awt.image.BufferedImage;

public class EnodeBPoint {
    public String name;

    public double lon;

    public double la;

    public int[] azimuth;

    public int beamWidth = 60;

    public EnodeBPoint() {
    }

    public EnodeBPoint(String name, double lon, double la, int[] azimuth, int beamWidth) {
        this.name = name;
        this.lon = lon;
        this.la = la;
        this.azimuth = azimuth;
        this.beamWidth = beamWidth;
    }


}
