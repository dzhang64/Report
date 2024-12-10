package report.utils;

import report.models.net.Cell;
import report.models.pic.Point;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 16:40 2021/12/7 0007
 */
public class CommonTools {

    public static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static DecimalFormat point2 = new DecimalFormat("0.00");

    public static DecimalFormat percentPoint2 = new DecimalFormat("0.00%");

    //for the String formatter of the time
    public static String getTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    //for the String formatter of the time
    public static String getDateime() {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    public static void delFile(File file) {
        File[] files = file.listFiles();  //获取文件夹内的列表
        if (file.exists() && files == null) {
            file.delete();
            return;
        }
        for (File file11 : files) {
            //如果是文件就直接删除，否则就递归
            if (file11.isFile()) {
                file11.delete();
            } else {
                delFile(file11);
            }
        }
        file.delete(); //最后是空文件夹就删除
    }


    //for md5
    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }

    public static String getChineseWord(String s) {
        int position = -1;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < s.length(); i++) {
            String temp = s.substring(i, i + 1);
            if (temp.matches(chinese)) {
                position = i;
                break;
            }
        }
        if (position > -1) {
            return s.substring(position, s.length());
        }

        return null;
    }

    public static double calAverage(double[] datas) {
        double sum = 0.0;
        for (int i = 0; i < datas.length; i++) {
            sum = sum + datas[i];
        }
        return sum / datas.length;
    }

    public static double findMax(double[] datas) {
        double max = datas[0];
        for (int i = 1; i < datas.length; i++) {
            if (datas[i] > max) {
                max = datas[i];
            }
        }
        return max;
    }

    public static double findMin(double[] datas) {
        double min = datas[0];
        for (int i = 1; i < datas.length; i++) {
            if (min > datas[i]) {
                min = datas[i];
            }
        }
        return min;
    }

    //sections为区间，datas为数值数组，返回为比section小1个的数组，对应的是区间的数量
    public static int[] calNum(int[] sections, double[] datas) {
        int[] nums = new int[sections.length - 1];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = 0;
        }
        for (int i = 0; i < datas.length; i++) {
            double temp = datas[i];
            for (int j = 1; j < sections.length; j++) {
                if (temp <= sections[j]) {
                    nums[j - 1] = nums[j - 1] + 1;
                    break;
                }
            }
        }
        return nums;
    }

    /**
     * @param value     传入的值，在selection中找区域
     * @param selection 用于比较传输值的区间，需要注意该区间存在一个下限，一个上限,比较的右边是包含的
     * @param colors    该数值比selection多一个元素，第1个颜色对应小于selection第一个元素，第2个颜色对应小于selection第2个元素
     *                  最后一个颜色对应大于selection最后一个元素的颜色
     * @return
     */
    public static Color selectColorBySection(double value, int[] selection, Color[] colors) {
        for (int i = 0; i < selection.length; i++) {
            if (value <= selection[i]) {
                return colors[i];
            }
        }
        return colors[selection.length];
    }

    /**
     * 该方法是将一个数组值匹配到对应的颜色
     *
     * @param values 一组值，一般这些值存在重复
     * @param colors 一个颜色数组
     * @return 返回值和颜色对应对应的HashMap，key的String为值，Color是颜色
     */
    public static HashMap<String, Color> selectColorByValue(int[] values, Color[] colors) {
        HashSet<Integer> integers = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            integers.add(values[i]);
        }
        HashMap<String, Color> colorHashMap = new HashMap<>();
        int colorNum = colors.length;
        int colorPoint = 0;
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()) {
            if (colorPoint == colorNum) {
                colorPoint = 0;
            }
            Integer next = iterator.next();
            colorHashMap.put(next + "", colors[colorPoint]);
            colorPoint++;
        }
        return colorHashMap;
    }

    /**
     * @param points 一组点的数组
     * @return 这一组边界，包含2个元素，为左上的点和右下的点
     */
    public static Point[] getBoundPoint(ArrayList<Point> points) {
        double[] lon = new double[points.size()];
        double[] la = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            lon[i] = points.get(i).longtitude;
            la[i] = points.get(i).latitude;
        }
        double maxLon = findMax(lon);
        double minLon = findMin(lon);
        double maxLa = findMax(la);
        double minLa = findMin(la);
        Point point1 = new Point(minLon, maxLa);
        Point point2 = new Point(maxLon, minLa);
        return new Point[]{point1, point2};
    }

    /**
     * 收集某一列的数值变成一个double数组，同一列可以存在多个约束条件，只有不为空白操作支持字符串，其他都必须是数值
     *
     * @param file         需要读取的CSV文件
     * @param ctColName    需要收集的列的名称
     * @param columnNames  需要约束的列的名称
     * @param calMark      约束条件，1为等于，2为不等于，3为大于，4为小于，31为大于等于，41为小于等于，51为不为空白,61为是空白，71为文字相同，只有不为空白（51)、是空白(61）、文字相同（71）支持操作支持字符串，其他都必须是数值
     * @param controlValue 约束条件对应的值
     * @return
     * @throws IOException
     */
    public static double[] readCSVDate(File file, String ctColName, String[] columnNames, int[] calMark, int[] controlValue,String startColumnName,int startMark,String startValue,String endColumnName,int endMark,String endValue,boolean rejectFirst) throws Exception {
        if (!file.exists()) {
            throw new Exception(file.getName()+"不存在!");
        }

        ArrayList<Double> doublesArray = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
        String record = reader.readLine();
        String[] split = splitCSVLine(record);
        //声明一个对应列名的列位置
        int[] columns = new int[columnNames.length];
        //设置为-1,等遍历结束，如果还为-1，则抛出异常
        for (int i = 0; i < columns.length; i++) {
            columns[i] = -1;
        }
        int ctCol = -1;

        int startColumn = -2;
        int endColumn = -2;
        //遍历获取列名所在的列的位置,如果未找到则抛出异常
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            for (int j = 0; j < split.length; j++) {
                if (columnName.trim().equals(split[j])) {
                    columns[i] = j;
                    break;
                }
            }
            if (columns[i] == -1) {
                throw new Exception(file.getName() + "未找到列:" + columnNames[i]);
            }
        }

        //查看启动记录的列和关闭记录的列
        for (int i = 0; i < split.length; i++) {
            if(split[i].equals(startColumnName)){
                startColumn = i;
            }
        }

        for (int i = 0; i < split.length; i++) {
            if(split[i].equals(endColumnName)){
                endColumn = i;
            }
        }
        //如果启动列为null,则设置启动列的值为-1，避免后面判断抛出未找到该列的异常
        if(startColumnName==null){
            startColumn = -1;
        }
        //如果停止列为null,则设置停止列的值为-1，避免后面判断抛出未找到该列的异常
        if(endColumnName==null){
            endColumn = -1;
        }

        //如果启动和停止列为-2,则抛出异常未找到该列
        if(startColumn==-2){
            throw  new Exception("未找到启动记录列");
        }

        if(endColumn==-2){
            throw  new Exception("未找到停止记录列");
        }




        //遍历获取需要获取的列名在列中的位置
        for (int i = 0; i < split.length; i++) {
            if (ctColName.equals(split[i])) {
                ctCol = i;
            }
        }
        //如果未找到则抛出异常
        if (ctCol == -1) {
            throw new Exception(file.getName() + "未找到列:" + ctColName);
        }

        int startRow = 0;
        int endRow = 0;
        int line = 0;
        boolean findStartLine = true;
        boolean findEndLine = true;

        //如果传入的startColumnValue为null,则不需要找启动列
        if(startColumn==-1){
            findStartLine = false;
        }

        if(endColumn==-1){
            findEndLine = false;
        }

        mark1:
        while ((record = reader.readLine()) != null) {
            String[] cellMsg = splitCSVLine(record);
            if(findStartLine){
                    //如果是文字相同
                    if (startMark == 71) {
                        if (cellMsg[startColumn].trim().equals(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }
                    //如果条件是为空白
                    else if (startMark == 61) {
                        if (cellMsg[startColumn].trim().length() == 0) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }
                    //如果条件为不等于空白
                    else if (startMark == 51) {
                        if (cellMsg[startColumn].trim().length() != 0) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }

                    //如果条件是等于
                    else if (startMark == 1) {
                        double value1 = Double.parseDouble(cellMsg[startColumn]);
                        if (value1 == Double.parseDouble(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }

                    //如果条件是不等于
                    else if (startMark == 2) {
                        double value1 = Double.parseDouble(cellMsg[startColumn]);
                        if (value1 != Double.parseDouble(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }

                    //如果条件是大于
                    else if (startMark == 3) {
                        double value1 = Double.parseDouble(cellMsg[startColumn]);
                        if (value1 > Double.parseDouble(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }

                    //如果条件是小于
                    else if (startMark == 4) {
                        double value1 = Double.parseDouble(cellMsg[startColumn]);
                        if (value1 < Double.parseDouble(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }

                    //如果条件是大于等于
                    else if (startMark == 31) {
                        double value1 = Double.parseDouble(cellMsg[startColumn]);
                        if (value1 >= Double.parseDouble(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }

                    //如果条件是小于等于
                    else if (startMark == 41) {
                        double value1 = Double.parseDouble(cellMsg[startColumn]);
                        if (value1 <= Double.parseDouble(startValue)) {
                            startRow = line;
                            findStartLine = false;
                        }
                    }
            }

            if(findEndLine){
                //如果是文字相同
                if (endMark == 71) {
                    if (cellMsg[endColumn].trim().equals(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }
                //如果条件是为空白
                else if (endMark == 61) {
                    if (cellMsg[endColumn].trim().length() == 0) {
                        endRow = line;
                        findEndLine = false;
                    }
                }
                //如果条件为不等于空白
                else if (endMark == 51) {
                    if (cellMsg[endColumn].trim().length() != 0) {
                        endRow = line;
                        findEndLine = false;
                    }
                }

                //如果条件是等于
                else if (endMark == 1) {
                    double value1 = Double.parseDouble(cellMsg[endColumn]);
                    if (value1 == Double.parseDouble(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }

                //如果条件是不等于
                else if (endMark == 2) {
                    double value1 = Double.parseDouble(cellMsg[endColumn]);
                    if (value1 != Double.parseDouble(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }

                //如果条件是大于
                else if (endMark == 3) {
                    double value1 = Double.parseDouble(cellMsg[endColumn]);
                    if (value1 > Double.parseDouble(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }

                //如果条件是小于
                else if (endMark == 4) {
                    double value1 = Double.parseDouble(cellMsg[endColumn]);
                    if (value1 < Double.parseDouble(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }

                //如果条件是大于等于
                else if (endMark == 31) {
                    double value1 = Double.parseDouble(cellMsg[endColumn]);
                    if (value1 >= Double.parseDouble(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }

                //如果条件是小于等于
                else if (endMark == 41) {
                    double value1 = Double.parseDouble(cellMsg[endColumn]);
                    if (value1 <= Double.parseDouble(endValue)) {
                        endRow = line;
                        findEndLine = false;
                    }
                }
            }


            //控制是否循环，如果需要循环，则表示不需要记录
            if (columns != null) {
                mark2:
                for (int i = 0; i < columns.length; i++) {
                    //如果条件是为空白
                    if (calMark[i] == 61) {
                        if (cellMsg[columns[i]].trim().length() != 0) {
                            continue mark1;
                        }
                    }
                    //如果条件为不等于空白
                    if (calMark[i] == 51) {
                        if (cellMsg[columns[i]].trim().length() == 0) {
                            continue mark1;
                        }
                    }

                    //如果条件是等于
                    else if (calMark[i] == 1) {
                        double value1 = Double.parseDouble(cellMsg[columns[i]]);
                        if (value1 != controlValue[i]) {
                            continue mark1;
                        }
                    }

                    //如果条件是不等于
                    else if (calMark[i] == 2) {
                        double value1 = Double.parseDouble(cellMsg[columns[i]]);
                        if (value1 == controlValue[i]) {
                            continue mark1;
                        }
                    }

                    //如果条件是大于
                    else if (calMark[i] == 3) {
                        double value1 = Double.parseDouble(cellMsg[columns[i]]);
                        if (value1 <= controlValue[i]) {
                            continue mark1;
                        }
                    }

                    //如果条件是小于
                    else if (calMark[i] == 4) {
                        double value1 = Double.parseDouble(cellMsg[columns[i]]);
                        if (value1 >= controlValue[i]) {
                            continue mark1;
                        }
                    }

                    //如果条件是大于等于
                    else if (calMark[i] == 31) {
                        double value1 = Double.parseDouble(cellMsg[columns[i]]);
                        if (value1 < controlValue[i]) {
                            continue mark1;
                        }
                    }

                    //如果条件是小于等于
                    else if (calMark[i] == 41) {
                        double value1 = Double.parseDouble(cellMsg[columns[i]]);
                        if (value1 > controlValue[i]) {
                            continue mark1;
                        }
                    }

                }
            }
            doublesArray.add(Double.valueOf(cellMsg[ctCol]));
            line++;
        }
        reader.close();
        if(endRow==0){
            endRow = doublesArray.size();
        }

        if(rejectFirst){
            startRow = startRow +1;
        }
        ArrayList<Double> doublesArray1 = new ArrayList<>();
        for (int i = startRow; i < endRow; i++) {
            doublesArray1.add(doublesArray.get(i));
        }

        double[] doubles = new double[doublesArray1.size()];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = doublesArray1.get(i);
        }
        if (doubles.length == 0) {
            throw new Exception(file.getName() + "文件未能找到数据！");
        }
        return doubles;
    }

    /**
     * @param file 需要查找的csv文件
     * @param name 在csv文件中第一行中查找的列名
     * @return 该列名所在的列
     * @throws Exception
     */
    public static int getColumnPostionByName(File file, String name) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader streamReader = new InputStreamReader(fileInputStream, "GB2312");
        BufferedReader reader = new BufferedReader(streamReader);
        String record;
        //读取第一行，目前是为了获取表头
        record = reader.readLine();
        reader.close();
        streamReader.close();
        fileInputStream.close();
        int position = -1;
        String[] header = splitCSVLine(record);
        for (int i = 0; i < header.length; i++) {
            if (header[i].trim().equals(name)) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            throw new Exception(file.getName() + "文件中不存在名称为" + name + "的列");
        }
        return position;
    }

    public static String[] splitCSVLine(String line) {
        String[] res = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        int s= 0;
        for (int i = line.length()-1; i >=0; i--) {
            if(line.charAt(i)==','){
                s++;
            }else {
                break;
            }
        }
        String[] strings = new String[res.length + s];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = "";
        }
        for (int i = 0; i < res.length; i++) {
            strings[i] = res[i].replace("\"","");
        }
        return strings;
    }

    public static int getSPDgress(int fwDgree) {
        return 90 - fwDgree;
    }




    static double pi = 3.14159265358979324;
    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;
    public final static double x_pi = pi * 3000.0 / 180.0;

    /**
     *   WGS-84：是国际标准，GPS坐标（Google Earth使用、或者GPS模块）
     *   GCJ-02：中国坐标偏移标准，Google Map、高德、腾讯使用
     *   BD-09：百度坐标偏移标准，Baidu Map使用
     * @param lon GCJ-02的经度
     * @param lat GCJ-02高德地图的维度
     * @return BD-09的经度和维度构成的数组
     */
    public static double[] gcj2bd(double lon,double lat) {
        double z = Math.sqrt(lon * lon + lat * lat) + 0.00002 * Math.sin(lat * x_pi);
        double theta = Math.atan2(lat, lon) + 0.000003 * Math.cos(lon * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[] { bd_lon,bd_lat};
    }

    /**
     *
     * @param lon BD-09的经度
     * @param lat BD-09的维度
     * @return GCJ-02的经度和维度
     */
    public static double[] bd2gcj(double lon,double lat) {
        double x = lon - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[] {gg_lon,gg_lat};
    }

    /**
     *
     * @param lon   WGS-84的经度
     * @param lat   WGS-84的维度
     * @return  GCJ-02的精度为构成的数组
     */
    public static double[] wgs2gcj(double lon,double lat) {
        double dLat = transformLat(lat - 35.0, lon - 105.0);
        double dLon = transformLon(lat - 35.0, lon - 105.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{ mgLon,mgLat};
    }

    /**
     *
     * @param lon   GCJ-02的经度
     * @param lat   GCJ-02的维度
     * @return  WGC-84的经度和维度构成的数组
     */
    public static double[] gcj2wgc(double lon,double lat) {
        double dLat = transformLat(lat - 35.0, lon - 105.0);
        double dLon = transformLon(lat - 35.0, lon - 105.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        double lontitude = lon * 2 - mgLon;
        double latitude = lat * 2 - mgLat;
        return new double[]{lontitude, latitude};
    }

    /**
     *
     * @param lon   WGS-84的经度
     * @param lat   WGS-84的维度
     * @return BD-09的经度和维度
     */
    public static double[] wgs2bd(double lon,double lat) {
        double[] wgs2gcj = wgs2gcj(lon, lat);
        return gcj2bd(wgs2gcj[0], wgs2gcj[1]);
    }

    /**
     * 经纬度转换
     */
    private static double transformLat(double lon,double lat) {
        double ret = -100.0 + 2.0 * lat + 3.0 * lon + 0.2 * lon * lon + 0.1 * lat * lon + 0.2 * Math.sqrt(Math.abs(lat));
        ret = getRet(lon,lat,ret);
        ret += (160.0 * Math.sin(lon / 12.0 * pi) + 320 * Math.sin(lon * pi  / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double getRet(double lon,double lat, double ret) {
        ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lon * pi) + 40.0 * Math.sin(lon / 3.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double lon,double lat) {
        double ret = 300.0 + lat + 2.0 * lon + 0.1 * lat * lat + 0.1 * lat * lon + 0.1 * Math.sqrt(Math.abs(lat));
        ret = getRet(lon, lat, ret);
        ret += (150.0 * Math.sin(lat / 12.0 * pi) + 300.0 * Math.sin(lat / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static boolean judgeContainsStr(String cardNum) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }

    public static boolean judgeIsTime(String cardNum){
        //如果包含字母，则返回false
        boolean b = judgeContainsStr(cardNum);
        if(b){
            return false;
        }

        //如果不是时间格式，则返回false
        int houre = 0;
        int minute = 0;
        int seconds = 0;


        try{
            houre = Integer.parseInt(cardNum.substring(0,2));
            minute = Integer.parseInt(cardNum.substring(2,4));
            seconds = Integer.parseInt(cardNum.substring(4,6));
        }catch (Exception e){
            return false;
        }

        if(houre<0||houre>23){
            return false;
        }

        if(minute<0||minute>59){
            return false;
        }

        if(seconds<0||seconds>59){
            return false;
        }

        return true;
    }





}
