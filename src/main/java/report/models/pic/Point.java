package report.models.pic;

import java.awt.*;
import java.util.ArrayList;

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

    public Point(double longtitude, double latitude,Color color) {
        this.color = color;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    public Point(double longtitude, double latitude) {
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    public Point() {
    }

}
