package report.utils;

import javafx.stage.Stage;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import report.models.function.Function;
import report.models.user.User;

import java.io.*;
import java.util.*;

/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 19:06 2021/12/7 0007
 */
public class StaticStorage {

    public static  SqlSessionFactory sessionFactory ;

    public static Stage mainStage;

    public static User loginUser;

    public static Map<String,String> functionMap = new HashMap<>();

    public static Map<String,String> functionXMLMap = new HashMap<>();

    public static Function selectFunction;

    public static File functionFile;

    //对应每个功能的配置根目录，如1则是在1下面
    public static File functionConfigFile;

    public static File tempDir;

    public static File rootFile;

    public static File fontRoot;

    public static File backImageroot;

    static {
        try {
            sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("config/mybatis-config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LinkedList<Stage> stageslist = new LinkedList<Stage>();

    //file为相对地址
    public static Map<String,String> readFunctionProperties(String file) throws IOException {
        functionMap.clear();
        InputStream in = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(in);
        Set<String> strings = properties.stringPropertyNames();
        for (String key : strings) {
            String property = properties.getProperty(key);
            functionMap.put(key,property);
        }
        return functionMap;
    }

    //file为相对地址
    public static void writeProperties(String key,String value,String file) throws IOException {
        InputStream in = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(in);
        properties.put(key,value);
        in.close();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
        String dateime = CommonTools.getDateime();
        properties.store(out, dateime);
        out.close();
        fileOutputStream.close();
    }


}
