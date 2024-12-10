package report.models.net;

import report.utils.CommonTools;

import java.util.Random;

public class PhuPeak {

    public String imei;

    public String imsi;

    public String rfMode = "LTE";

    public String enodeBId;

    public String mcc = "460";

    public String mnc = "01";

    public String plmn = mcc+mnc;

    public String tac;

    public String pci;

    public String dlEarfcn = "3745";

    public String rsrp;

    public String sinr;

    public String rsrq;

    public String rssi;

    public String tm = "TM4";

    public String band = "3";

    public String bandWidth = "10MHz";

    public String randIndicator = "Rank 2";

    public String cqi;

    public String mcs0;

    public String mcs1;

    public String initialBler;

    public String residualBler;

    public String dlGrantCount;

    public String rbsPerSecond;

    public String rbsPerSFN;

    public String appSpeed;

    public String pdcpSpeed;

    public String rlcSpeed;

    public String macSpeed;

    public String phySpeed;

    public int subNet;

    public String ci;

    public PhuPeak() {
    }

    @Override
    public String toString() {
        return "PhuPeak{" +
                "imei='" + imei + '\'' +
                ", imsi='" + imsi + '\'' +
                ", rfMode='" + rfMode + '\'' +
                ", eci='" + enodeBId + '\'' +
                ", mcc='" + mcc + '\'' +
                ", mnc='" + mnc + '\'' +
                ", plmn='" + plmn + '\'' +
                ", tac='" + tac + '\'' +
                ", pci='" + pci + '\'' +
                ", dlEarfcn='" + dlEarfcn + '\'' +
                ", rsrp='" + rsrp + '\'' +
                ", sinr='" + sinr + '\'' +
                ", rsrq='" + rsrq + '\'' +
                ", rssi='" + rssi + '\'' +
                ", tm='" + tm + '\'' +
                ", band='" + band + '\'' +
                ", bandWidth='" + bandWidth + '\'' +
                ", randIndicator='" + randIndicator + '\'' +
                ", cqi='" + cqi + '\'' +
                ", mcs0='" + mcs0 + '\'' +
                ", mcs1='" + mcs1 + '\'' +
                ", initialBler='" + initialBler + '\'' +
                ", residualBler='" + residualBler + '\'' +
                ", dlGrantCount='" + dlGrantCount + '\'' +
                ", rbsPerSecond='" + rbsPerSecond + '\'' +
                ", rbsPerSFN='" + rbsPerSFN + '\'' +
                ", appSpeed='" + appSpeed + '\'' +
                ", pdcpSpeed='" + pdcpSpeed + '\'' +
                ", rlcSpeed='" + rlcSpeed + '\'' +
                ", macSpeed='" + macSpeed + '\'' +
                ", phySpeed='" + phySpeed + '\'' +
                ", subNet=" + subNet +
                '}';
    }

    public PhuPeak(String endeBId, String tac, String pci,String ci,int subNet,String bandWidth,String mcc,String mnc,String dlEarfcn,String band,String tm,String imei,String imsi) {
        Random random = new Random();
        this.enodeBId = endeBId;
        this.tac = tac;
        this.pci = pci;
        this.subNet = subNet;
        this.ci = ci;
        this.bandWidth = bandWidth+"MHz";
        this.band = band;
        this.mcc = mcc;
        this.mnc = mnc;
        this.dlEarfcn = dlEarfcn;
        this.tm = tm;
        this.imei = imei;
        this.imsi = imsi;

        double rsrpNum = (0 - (64 + random.nextInt(10)) - (random.nextInt(98)+1) / 100.0);
        this.rsrp = rsrpNum + "";

        this.sinr = (random.nextInt(3)+20)+"";

        int c1 = random.nextInt(4)+16;
        int c2 = random.nextInt(80)+12;
        double c3 = c1 + c2/100.0;

        this.rssi = CommonTools.point2.format(rsrpNum + c3) +"";

        this.rsrq = "";

        this.cqi = (random.nextInt(3)+11)+".00";

        this.mcs0 = (random.nextInt(4)+23)+"";

        this.mcs1 = (random.nextInt(3)+23)+"";

        this.initialBler = (random.nextInt(2)+9+(random.nextInt(97)+1)/100.0)+"";

        this.residualBler = "0.00";

        this.dlGrantCount = random.nextInt(2)+1000 +"";

        this.rbsPerSecond = random.nextInt(99) + 39856 +"";

        this.rbsPerSFN = random.nextInt(1)+39 +"";


        double appSpeedNum = random.nextInt(3) + 41 + (random.nextInt(97)+1)/100.0;

        double pdcpSpeedNum = appSpeedNum + (random.nextInt(97)+1)/100.0;

        double rlcSpeedNum = pdcpSpeedNum + (random.nextInt(97)+1)/100.0/2;

        double macSpeedNum = rlcSpeedNum + (random.nextInt(97)+1)/100.0/3;

        double phySpeedNum = macSpeedNum + (random.nextInt(97)+1)/100.0;




        this.appSpeed = CommonTools.point2.format(appSpeedNum) +"";
        this.pdcpSpeed = CommonTools.point2.format(pdcpSpeedNum) +"";
        this.rlcSpeed = CommonTools.point2.format(rlcSpeedNum) +"";
        this.macSpeed = CommonTools.point2.format(macSpeedNum)+ "";
        this.phySpeed = CommonTools.point2.format(phySpeedNum) +"";
    }

    public PhuPeak copy(){
        Random random = new Random();
        PhuPeak phuPeak = new PhuPeak();
        phuPeak.enodeBId = enodeBId;
        phuPeak.tac = tac;
        phuPeak.pci = pci;
        phuPeak.subNet = subNet;
        phuPeak.ci = ci;
        phuPeak.subNet = subNet;
        phuPeak.imei = imei;
        phuPeak.imsi = imsi;

        double rsrpNum = (0 - (64 + random.nextInt(10)) - (random.nextInt(98)+1) / 100.0);
        phuPeak.rsrp = rsrpNum + "";

        phuPeak.sinr = (random.nextInt(3)+20)+"";

        int c1 = random.nextInt(4)+16;
        int c2 = random.nextInt(80)+12;
        double c3 = c1 + c2/100.0;

        phuPeak.rssi = CommonTools.point2.format(rsrpNum + c3) +"";

        phuPeak.rsrq = "";

        phuPeak.cqi = (random.nextInt(3)+11)+".00";

        phuPeak.mcs0 = (random.nextInt(4)+23)+"";

        phuPeak.mcs1 = (random.nextInt(3)+23)+"";

        phuPeak.initialBler = (random.nextInt(2)+9+(random.nextInt(97)+1)/100.0)+"";

        phuPeak.residualBler = "0.00";

        phuPeak.dlGrantCount = random.nextInt(2)+1000 +"";

        phuPeak.rbsPerSecond = random.nextInt(99) + 39856 +"";

        phuPeak.rbsPerSFN = random.nextInt(1)+39 +"";

        double appSpeedNum = random.nextInt(3) + 41 + (random.nextInt(97)+1)/100.0;

        double pdcpSpeedNum = appSpeedNum + (random.nextInt(97)+1)/100.0;

        double rlcSpeedNum = pdcpSpeedNum + (random.nextInt(97)+1)/100.0/2;

        double macSpeedNum = rlcSpeedNum + (random.nextInt(97)+1)/100.0/3;

        double phySpeedNum = macSpeedNum + (random.nextInt(97)+1)/100.0;



        phuPeak.appSpeed = CommonTools.point2.format(appSpeedNum) +"";
        phuPeak.pdcpSpeed = CommonTools.point2.format(pdcpSpeedNum) +"";
        phuPeak.rlcSpeed = CommonTools.point2.format(rlcSpeedNum) +"";
        phuPeak.macSpeed = CommonTools.point2.format(macSpeedNum)+ "";
        phuPeak.phySpeed = CommonTools.point2.format(phySpeedNum) +"";
        return phuPeak;
    }
}
