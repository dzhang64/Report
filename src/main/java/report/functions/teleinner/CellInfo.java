package report.functions.teleinner;

import report.utils.CommonTools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CellInfo {

    public String pLMNId;
    public int tac;
    public int gNBId;
    public String nodeName;
    public int cellLocalId;
    public String gNBName;
    public String cellName;
    public int pci;
    public int band;
    public double ssbFrequency;
    public double frequency;
    public int  nrbandwidth;
    public int dlAntNum;
    public String vswName;
    public String aauName;
    public String portPower;
    public double powerPerRERef;
    public double longtitue;
    public double latitude;


    public static HashMap<String, CellInfo> cellPCIMap = new HashMap<String, CellInfo>();


    public static void init(String path) {

    }

    public static void initCSV(String path) throws IOException {
        System.out.println(path);
        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GB2312"));
        String record = file.readLine();
        while ((record = file.readLine()) != null) {
            String[] cellMsg = CommonTools.splitCSVLine(record);
            String pLMNId = cellMsg[8];
            if(!pLMNId.equals("460-11")){
                continue;
            }
            String tempTac = cellMsg[9];
            if(tempTac.trim().length()==0){
                continue;
            }
            int tac = Integer.parseInt(cellMsg[9]);
            int gNBId = Integer.parseInt(cellMsg[10]);
            int cellLocalId = Integer.parseInt(cellMsg[11]);
            String nodeName = cellMsg[13];
            String gNBName = cellMsg[13];
            String cellName = cellMsg[14];;
            int pci = Integer.parseInt(cellMsg[15]);;
            double ssbFrequency = Double.parseDouble(cellMsg[16]);;
            double frequency = Double.parseDouble(cellMsg[17]);;
            int nrbandwidth = Integer.parseInt(cellMsg[18]);;
            int band = Integer.parseInt(cellMsg[21]);;
            int dlAntNum = Integer.parseInt(cellMsg[29].split("_")[0]);
            String vswName = cellMsg[30];;
            String aauName =  cellMsg[35];
            String portPower =  null;
            double powerPerRERef = Integer.parseInt(cellMsg[38])*0.1;
            CellInfo cellInfo = new CellInfo(pLMNId,tac, gNBId, nodeName, cellLocalId, gNBName, cellName, pci, band,ssbFrequency, frequency, nrbandwidth, dlAntNum, vswName, aauName, portPower, powerPerRERef);
            String cellMark = gNBId +"-"+cellLocalId;
            cellPCIMap.put(cellMark,cellInfo);
        }

    }

    public static void setGPS(String path) throws IOException {
        System.out.println(path);
        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GB2312"));
        String record = file.readLine();
        while ((record = file.readLine()) != null) {
            String[] cellMsg = record.split(",");
            String plmn = cellMsg[16].trim();
            if (!plmn.equals("460-11")) {
                continue;
            }
            String cellMark = cellMsg[3].trim() + "-" + cellMsg[6].trim();
            CellInfo cellInfo = cellPCIMap.get(cellMark);
            if(cellInfo!=null){
                cellInfo.longtitue = Double.parseDouble(cellMsg[21].trim());
                cellInfo.latitude = Double.parseDouble(cellMsg[22].trim());
            }
        }
    }

    public CellInfo(String pLMNId, int tac, int gNBId, String nodeName, int cellLocalId, String gNBName, String cellName, int pci, int band, double ssbFrequency, double frequency, int nrbandwidth, int dlAntNum, String vswName, String aauName, String portPower, double powerPerRERef) {
        this.pLMNId = pLMNId;
        this.tac = tac;
        this.gNBId = gNBId;
        this.nodeName = nodeName;
        this.cellLocalId = cellLocalId;
        this.gNBName = gNBName;
        this.cellName = cellName;
        this.pci = pci;
        this.band = band;
        this.ssbFrequency = ssbFrequency;
        this.frequency = frequency;
        this.nrbandwidth = nrbandwidth;
        this.dlAntNum = dlAntNum;
        this.vswName = vswName;
        this.aauName = aauName;
        this.portPower = portPower;
        this.powerPerRERef = powerPerRERef;
    }

    public CellInfo() {
    }
}
