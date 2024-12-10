package report.models.pic;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PCIPoint {
    public HashMap<String, Color>  pciColorMap;

    public ArrayList<Point> points;

    public PCIPoint() {
    }

    public PCIPoint(HashMap<String, Color> pciColorMap, ArrayList<Point> points) {
        this.pciColorMap = pciColorMap;
        this.points = points;
    }
}
