package report.functions.jsunicom.lte;

/**
 * 该类代表测试数据中的每个点
 */

public class TestInfo {

    public double lon;

    public double la;

    public double rsrp;

    public double sinr;

    public int pci;

    public double dlSpeed;

    public double ulSpeed;

    public TestInfo() {
    }

    public TestInfo(double lon, double la, double rsrp, double sinr, int pci, double dlSpeed, double ulSpeed) {
        this.lon = lon;
        this.la = la;
        this.rsrp = rsrp;
        this.sinr = sinr;
        this.pci = pci;
        this.dlSpeed = dlSpeed;
        this.ulSpeed = ulSpeed;
    }
}
