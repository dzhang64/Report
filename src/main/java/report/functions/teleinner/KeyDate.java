package report.functions.teleinner;

import java.util.Date;

public class KeyDate {

    public int offset;

    public Date date;

    public int gnbId;

    public int cellId;

    public double longtitude;

    public double latitud;

    public double nrRsrp;

    public int cellPci;

    public double cellSinr;

    public int cellBand;

    public double pdcpDLSpeed;

    public double pdcpULSpeed;

    public double pingDelay;

    public double lteRsrp;

    public int voiceRequestNum;

    public int voiceSuccessNum;

    public double ftpULSpeed;

    public double ftpDLSpeed;

    public double gpsFixValue;

    public KeyDate(int offset, Date date, int gnbId, int cellId, double longtitude, double latitud, double nrRsrp, int cellPci, double cellSinr, int cellBand, double pdcpDLSpeed, double pdcpULSpeed, double pingDelay, double lteRsrp, int voiceRequestNum, int voiceSuccessNum, double ftpULSpeed, double ftpDLSpeed, double gpsFixValue) {
        this.offset = offset;
        this.date = date;
        this.gnbId = gnbId;
        this.cellId = cellId;
        this.longtitude = longtitude;
        this.latitud = latitud;
        this.nrRsrp = nrRsrp;
        this.cellPci = cellPci;
        this.cellSinr = cellSinr;
        this.cellBand = cellBand;
        this.pdcpDLSpeed = pdcpDLSpeed;
        this.pdcpULSpeed = pdcpULSpeed;
        this.pingDelay = pingDelay;
        this.lteRsrp = lteRsrp;
        this.voiceRequestNum = voiceRequestNum;
        this.voiceSuccessNum = voiceSuccessNum;
        this.ftpULSpeed = ftpULSpeed;
        this.ftpDLSpeed = ftpDLSpeed;
        this.gpsFixValue = gpsFixValue;
    }

    public KeyDate(double longtitude, double latitud) {
        this.longtitude = longtitude;
        this.latitud = latitud;
    }
}
