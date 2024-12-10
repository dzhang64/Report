package report.models.net;

public class Cell {

    public String technology;

    public String dupluxMode;

    public String subNetId;

    public String mcc;

    public String ncc;

    public String tac;

    public String enbId;

    public String enbName;

    public String cellId;

    public String cellName;

    public String band;

    public String earfcn;

    public String pci;

    public String bandWidth;

    public String frequncyDL;

    public String frequncyUL;

    public String lon;

    public String la;

    public String tm;

    public String rootSequence;

    public String azimuth;

    public Cell() {
    }

    public Cell(String subNetIdString, String technology, String dupluxMode, String mcc, String ncc, String tac, String enbId, String enbName, String cellId, String cellName, String band, String earfcn, String pci, String bandWidth, String frequncyDL, String frequncyUL, String lon, String la,String tm,String rootSequence,String azimuth) {
        this.subNetId = subNetIdString;
        this.technology = technology;
        this.dupluxMode = dupluxMode;
        this.mcc = mcc;
        this.ncc = ncc;
        this.tac = tac;
        this.enbId = enbId;
        this.enbName = enbName;
        this.cellId = cellId;
        this.cellName = cellName;
        this.band = band;
        this.earfcn = earfcn;
        this.pci = pci;
        this.bandWidth = bandWidth;
        this.frequncyDL = frequncyDL;
        this.frequncyUL = frequncyUL;
        this.lon = lon;
        this.la = la;
        this.tm = tm;
        this.rootSequence = rootSequence;
        this.azimuth = azimuth;
    }
}
