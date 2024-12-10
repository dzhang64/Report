package report.functions.teleinner;

import java.awt.*;

public class Point {

    public Color color;

    public String description;

    public double longtitude;

    public double latitude;

    public double gpsFixValue;

    public Point(Color color, String description, double longtitude, double latitude, double gpsFixValue) {
        this.color = color;
        this.description = description;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.gpsFixValue = gpsFixValue;
    }

    public Point() {
    }
}
