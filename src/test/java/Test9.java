import report.utils.CommonTools;

import java.io.File;

public class Test9 {
    public static void main(String[] args) throws Exception {
        File file = new File("G:\\盐城项目\\单验\\盐城联通log报表\\864317_20220802_青墩地龙村\\ul\\10 ul 1-IN20220802-135404-FTPU(1)_0820011248.csv");

        double[] doubles = CommonTools.readCSVDate(file, "PHY Throughput UL", new String[]{"PHY Throughput UL"}, new int[]{51}, new int[]{0},"Event", 71, "FTP Upload First Data", "Event", 71, "FTP Upload Last Data",true);
        double[] rsrpArray = CommonTools.readCSVDate(file, "RSRP", new String[]{"RSRP"}, new int[]{51}, null,null, 71, "FTP Upload First Data", null, 71, "FTP Upload Last Data",false);


        for (int i = 0; i < rsrpArray.length; i++) {
            System.out.println(rsrpArray[i]);
        }

    }
}
