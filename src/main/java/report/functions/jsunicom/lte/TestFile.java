package report.functions.jsunicom.lte;

import report.models.net.Cell;
import report.models.pic.GaoDePic;
import report.models.pic.PCIPoint;
import report.models.pic.Point;
import report.utils.CommonTools;
import report.utils.GaoDeMapTools;
import report.utils.NetTools;
import report.utils.PicTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 该类代表每个测试Log导出的csv文件
 */
public class TestFile {

    public static int rsrpType = 1;

    public static int sinrType = 2;

    public static int dlSpeedType =3;

    public static int ulSpeedType =4;
    //0为上传，1为下载
    public int type;

    public List<TestInfo> testInfoList = new ArrayList<TestInfo>();

    public ArrayList<Point> testPoint;

    public TestFile() {
    }

    public TestFile(int type) {
        this.type = type;
    }

    /**
     *
     * @param selection  选项,对应上面的静态rsrpType，sinrType，表示需要选取那个元素来选择颜色
     * @param sections 区间，如果为10,20,30,40，则表示-INF，10,20,30,40，INF
     * @param colors 区间对应的颜色，根据上面区间的说明，需要明白颜色数值比区间数组多1，并且最后一个为大于区间最后一个值的颜色
     * @return
     */
    public ArrayList<Point> createPoint(int selection, int[] sections, Color[] colors){
        ArrayList<Point> points = new ArrayList<>();

        for (int i = 0; i < testInfoList.size(); i++) {
            TestInfo testInfo = testInfoList.get(i);
            Color color;
            if(selection==rsrpType){
               color = CommonTools.selectColorBySection(testInfo.rsrp, sections, colors);
            }else if(selection==sinrType){
                color = CommonTools.selectColorBySection(testInfo.sinr, sections, colors);
            }else if(selection==dlSpeedType){
                color = CommonTools.selectColorBySection(testInfo.dlSpeed, sections, colors);
            }else if(selection==ulSpeedType){
                color = CommonTools.selectColorBySection(testInfo.ulSpeed, sections, colors);
            }else {
                return null;
            }
            points.add(new Point(testInfo.lon,testInfo.la,color));
        }
        return points;
    }


    public ArrayList<Point> getColor(int selection, int[] sections, Color[] colors){
        ArrayList<Point> points = new ArrayList<>();

        for (int i = 0; i < testInfoList.size(); i++) {
            TestInfo testInfo = testInfoList.get(i);
            Color color;
            if(selection==rsrpType){
                color = CommonTools.selectColorBySection(testInfo.rsrp, sections, colors);
            }else if(selection==sinrType){
                color = CommonTools.selectColorBySection(testInfo.sinr, sections, colors);
            }else if(selection==dlSpeedType){
                color = CommonTools.selectColorBySection(testInfo.dlSpeed, sections, colors);
            }else if(selection==ulSpeedType){
                color = CommonTools.selectColorBySection(testInfo.ulSpeed, sections, colors);
            }else {
                return null;
            }
            points.add(new Point(testInfo.lon,testInfo.la,color));
        }
        return points;
    }



    /**
     *
     * @param selection  选项,对应上面的静态rsrpType，sinrType，表示需要选取那个元素来选择颜色
     * @param sections 区间，如果为10,20,30,40，则表示-INF，10,20,30,40，INF
     * @param colors 区间对应的颜色，根据上面区间的说明，需要明白颜色数值比区间数组多1，并且最后一个为大于区间最后一个值的颜色
     * @param PCI 小区的PCI
     * @return
     */
    public ArrayList<Point> createPointByPCI(int selection, int[] sections, Color[] colors,int PCI){
        ArrayList<Point> points = new ArrayList<>();

        for (int i = 0; i < testInfoList.size(); i++) {
            TestInfo testInfo = testInfoList.get(i);
            if(testInfo.pci!=PCI){
                continue;
            }
            Color color;
            if(selection==rsrpType){
                color = CommonTools.selectColorBySection(testInfo.rsrp, sections, colors);
            }else if(selection==sinrType){
                color = CommonTools.selectColorBySection(testInfo.sinr, sections, colors);
            }else if(selection==dlSpeedType){
                color = CommonTools.selectColorBySection(testInfo.dlSpeed, sections, colors);
            }else if(selection==ulSpeedType){
                color = CommonTools.selectColorBySection(testInfo.ulSpeed, sections, colors);
            }else {
                return null;
            }
            points.add(new Point(testInfo.lon,testInfo.la,color));
        }
        return points;
    }

    /**
     *
     * @param colors 颜色数组
     * @return 根据颜色数组，将PCI数据和颜色匹配，返回值为PCIPoint实例，该类包含2个元素
     * 1.第一个元素是PCI和颜色的对应关系HashMap<String, Color>
     * 2. 第二个元素是Point数组，是每个PCI的颜色
     *
     */
    public PCIPoint createPCIPoint(Color[] colors){
        int[] pciArray = new int[testInfoList.size()];
        for (int i = 0; i < testInfoList.size(); i++){
            pciArray[i] = testInfoList.get(i).pci;
        }
        HashMap<String, Color> colorHashMap = CommonTools.selectColorByValue(pciArray, PicTools.colorArray);
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < testInfoList.size(); i++) {
            TestInfo testInfo = testInfoList.get(i);
            Color color = colorHashMap.get(testInfo.pci+"");
            Point point = new Point(testInfo.lon, testInfo.la, color);
            points.add(point);
        }
        return new PCIPoint(colorHashMap,points);
    }



    /**
     *
     * @param file 上传或者下载导出的csv文件，该文件包含表头ComputerTime,HandsetTime,Timestamp,Longitude,Latitude,RSRP,SINR,PCI,PHY Throughput DL,PHY Throughput UL
     * @param
     * @return
     * @throws IOException
     */
    public static TestFile createTestFile(File file,int type) throws Exception {
        TestFile testFile = new TestFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader streamReader = new InputStreamReader(fileInputStream, "GB2312");
        BufferedReader reader = new BufferedReader(streamReader);
        String record ;
        //读取第一行，目前是为了跳过表头
        record = reader.readLine();
        //根据表头获取位置
        int dlSpeedP = 0;
        int ulSpeedP = 0;
        int rsrpP = 0;
        int sinrP = 0;
        int pciP= 0;
        int loP = 0;
        int laP = 0;
        String[] header = CommonTools.splitCSVLine(record);
        int columnNumber = header.length;
        for (int i = 0; i < header.length; i++) {
            if(header[i].trim().equals("Longitude")){
                loP = i;
            }else if(header[i].trim().equals("Latitude")){
                laP = i;
            }else if(header[i].trim().equals("RSRP")){
                rsrpP = i;
            }else if(header[i].trim().equals("SINR")){
                sinrP = i;
            }else if(header[i].trim().equals("PCI")){
                pciP = i;
            }else if(header[i].trim().equals("PHY Throughput UL")){
                ulSpeedP = i;
            }else if(header[i].trim().equals("PHY Throughput DL")){
                dlSpeedP = i;
            }
        }

        //判断是否找到了列，如果没找到抛出异常
        if(dlSpeedP==0){
            throw new Exception(file.getName()+"文件，PHY Throughput DL"+"列不存在！");
        }
        if(ulSpeedP==0){
            throw new Exception(file.getName()+"文件，PHY Throughput UL"+"列不存在！");
        }
        if(rsrpP==0){
            throw new Exception(file.getName()+"文件，RSRP"+"列不存在！");
        }
        if(sinrP==0){
            throw new Exception(file.getName()+"文件，SINR"+"列不存在！");
        }
        if(pciP==0){
            throw new Exception(file.getName()+"文件，PCI"+"列不存在！");
        }
        if(loP==0){
            throw new Exception(file.getName()+"文件，Longitude"+"列不存在！");
        }
        if(laP==0){
            throw new Exception(file.getName()+"文件，Latitude"+"列不存在！");
        }


        //把所有的数据读取到ArrayList中
        ArrayList<String[]> testMsgs = new ArrayList<>();
        while ((record = reader.readLine()) != null) {
            String[] testMsg = CommonTools.splitCSVLine(record);
            testMsgs.add(testMsg);
        }
        reader.close();
        streamReader.close();
        fileInputStream.close();

        //计算起始行，起始行应该是经纬度不为空白的行
        int startLine = 0;
        int collectLine = 0;
        for (int i = 0; i < testMsgs.size(); i++) {
            if(testMsgs.get(i)[loP].trim().length()!=0){
                startLine = i;
                break;
            }
        }

        //找到第一个有效值dlSpeed
        double dlSpeed = 0;

        //如果是下载，则找到第一个下载的有效值
        if(type==0){
            for (int i = startLine; i < testMsgs.size(); i++) {
                if(testMsgs.get(i)[dlSpeedP].trim().length()!=0&&Double.parseDouble(testMsgs.get(i)[dlSpeedP])>2){
                    dlSpeed = Double.parseDouble(testMsgs.get(i)[dlSpeedP]);
                    collectLine = i;
                    break;
                }
            }

        }



        //找到第一个有效值ulSpeed
        double ulSpeed = 0;
        //如果是上传，则找到第一个上传的有效值
        if(type ==1){
            for (int i = startLine; i < testMsgs.size(); i++) {
                if(testMsgs.get(i)[ulSpeedP].trim().length()!=0&&Double.parseDouble(testMsgs.get(i)[ulSpeedP])>1){
                    ulSpeed = Double.parseDouble(testMsgs.get(i)[ulSpeedP]);
                    collectLine = i;
                    break;
                }
            }
        }



        //从存在数据传输开始找到第一个有效值Rsrp
        double rsrp = 0;
        for (int i = collectLine; i < testMsgs.size(); i++) {
            if(testMsgs.get(i)[rsrpP].trim().length()!=0){
                rsrp = Double.parseDouble(testMsgs.get(i)[rsrpP]);
                break;
            }
        }

        //从存在数据传输开始找到第一个有效值Sinr
        double sinr = 0;
        for (int i = collectLine; i < testMsgs.size(); i++) {
            if(testMsgs.get(i)[sinrP].trim().length()!=0){
                sinr = Double.parseDouble(testMsgs.get(i)[sinrP]);
                break;
            }
        }
        //从存在数据传输开始找到第一个有效值PCI
        int pci = 0;
        for (int i = collectLine; i < testMsgs.size(); i++) {
            if(testMsgs.get(i)[pciP].trim().length()!=0){
                pci = Integer.parseInt(testMsgs.get(i)[pciP]);
                break;
            }
        }

        //开始采集数据
        try{
            for (int i = collectLine; i < testMsgs.size(); i++) {
                if (testMsgs.get(i)[3].trim().length()==0&&testMsgs.get(i)[4].trim().length()==0) {
                    continue;
                }

                //采集经纬度
                double tempLon = Double.parseDouble(testMsgs.get(i)[loP].trim());
                double tempLa = Double.parseDouble(testMsgs.get(i)[laP].trim());
                //采集rsrp
                double tempRsrp = rsrp;
                if(testMsgs.get(i)[rsrpP].trim().length()!=0){
                    tempRsrp = Double.parseDouble(testMsgs.get(i)[rsrpP].trim());
                    rsrp = tempRsrp;
                }
                //采集sinr
                double tempSinr = sinr;
                if(testMsgs.get(i)[sinrP].trim().length()!=0){
                    tempSinr = Double.parseDouble(testMsgs.get(i)[sinrP].trim());
                    sinr = tempSinr;
                }
                //采集pci
                int tempPCI = pci;
                if(testMsgs.get(i)[pciP].trim().length()!=0){
                    tempPCI = Integer.parseInt(testMsgs.get(i)[pciP].trim());
                    pci = tempPCI;
                }
                //采集下行速率
                double tempDLSpeed = dlSpeed;
                if(testMsgs.get(i)[dlSpeedP].trim().length()!=0&&Double.parseDouble(testMsgs.get(i)[dlSpeedP])>2){
                    tempDLSpeed = Double.parseDouble(testMsgs.get(i)[dlSpeedP].trim());
                    dlSpeed = tempDLSpeed;
                }

                //采集上行速率
                double tempULSpeed = ulSpeed;
                if(testMsgs.get(i)[ulSpeedP].trim().length()!=0&&Double.parseDouble(testMsgs.get(i)[ulSpeedP])>1){
                    tempULSpeed =  Double.parseDouble(testMsgs.get(i)[ulSpeedP].trim());
                    ulSpeed = tempULSpeed;
                }

                TestInfo testInfo = new TestInfo(tempLon, tempLa, tempRsrp, tempSinr, tempPCI, tempDLSpeed, tempULSpeed);
                testFile.testInfoList.add(testInfo);
            }

        }catch (Exception e){
            throw new Exception(file.getName()+"读取时失败，失败原因:"+e.getMessage());
        }


        return testFile;
    }




}
