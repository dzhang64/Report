package report.utils.jsunicom;

import report.functions.teleinner.CellInfo;
import report.models.net.Cell;
import report.utils.CommonTools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    //从肖月娟的文件中读取4G的工参,标签为endId+pci
    public static void getCellMapFromExcel(File file,Map<String, Cell> cellMap) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader streamReader = new InputStreamReader(fileInputStream,"GB2312");
        BufferedReader reader = new BufferedReader(streamReader);
        String record = reader.readLine();
        while ((record = reader.readLine()) != null) {
            String[] cellMsg = CommonTools.splitCSVLine(record);
            Cell cell = new Cell(cellMsg[3],"LTE", "FDD", "460", "01", cellMsg[10], cellMsg[5], cellMsg[15], cellMsg[8], cellMsg[7], cellMsg[11], "3745", cellMsg[9], "10", cellMsg[13], cellMsg[12], cellMsg[16], cellMsg[17],"TM4",cellMsg[18],cellMsg[19]);
            cellMap.put(cell.enbId+cell.pci,cell);
        }
        reader.close();
        streamReader.close();
        fileInputStream.close();
    }
}
