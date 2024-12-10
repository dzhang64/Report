package report.models.function;

public class Function {

    private int fid;

    private String fname;

    private String furl;

    public Function(int fid, String fname, String furl) {
        this.fid = fid;
        this.fname = fname;
        this.furl = furl;
    }

    public Function() {
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFurl() {
        return furl;
    }

    public void setFurl(String furl) {
        this.furl = furl;
    }
}
