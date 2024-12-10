package report.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import report.models.function.Function;
import report.models.user.Role;
import report.service.FunctionService;
import report.utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 3:12 2021/12/8 0008
 */
public class UserController {

    @FXML
    private Label lpLabel1;

    @FXML
    private Label lpLabel2;

    @FXML
    private Label lpLabel3;

    @FXML
    private Button lpButton;

    @FXML
    private ComboBox functionBox;

    @FXML
    private TextArea tips;


    @FXML
    private Button updateButton;

    @FXML
    private Button enterButton;


    private FunctionService functionService = new FunctionService();

    private HashMap<String,String> functionMap = new HashMap<>();

    public UserController() {
    }

    @FXML
    private void initialize(){
        //获取根目录
        File directory = new File("");
        String dirPath = directory.getAbsolutePath();
        File rootFile = null;
        File src = new File(dirPath, "src");
        if(src.exists()){
            rootFile = new File(src,"config");
        }else {
            rootFile = new File(dirPath,"config");
        }
        StaticStorage.rootFile = rootFile;
        StaticStorage.tempDir = new File(rootFile,"temp");
        StaticStorage.fontRoot = new File(rootFile,"font");
        StaticStorage.backImageroot = new File(rootFile,"backImage");

        //读取功能配置信息，该表表示那些功能存在
        FileInputStream fileInputStream;
        Properties properties;
        URL url;
        String filePath;
        File tempFile;
        Set<String> keys;
        StaticStorage.functionFile = new File(rootFile,"function_config/function.properties");
        try {
            StaticStorage.readFunctionProperties(StaticStorage.functionFile.getPath());
        } catch (IOException e) {
            tips.setText(e.getMessage());
        }

        //读取功能配置和对应的fxml文件
        try {
            fileInputStream = new FileInputStream(new File(rootFile,"function_config/function_xml.properties"));
            properties = new Properties();
            properties.load(fileInputStream);
            keys = properties.stringPropertyNames();
            for (String s : keys) {
                String xmlFileName = properties.getProperty(s);
                StaticStorage.functionXMLMap.put(s,xmlFileName);
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            tips.setText(e.getMessage());
        } catch (IOException e) {
            tips.setText(e.getMessage());
        }

        //读取普通配置信息
        try {
            fileInputStream = new FileInputStream(new File(rootFile,"function_config/common_config.properties"));
            properties = new Properties();
            properties.load(fileInputStream);
            PicTools.bWidth = Integer.parseInt(properties.getProperty("thresh_width"));
            PicTools.bHeight = Integer.parseInt(properties.getProperty("thresh_hight"));
            //后面需要添加普通配置在这里添加

            //添加结束
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            tips.setText(e.getMessage());
        } catch (IOException e) {
            tips.setText(e.getMessage());
        }




        lpLabel1.setText(StaticStorage.loginUser.getUserName());
        lpLabel2.setText(Role.getRole(StaticStorage.loginUser.getUserRoleId()).getRoleStr());
        lpLabel3.setText(CommonTools.getTime());
        List<Function> allFunction = functionService.getALLFunction();
        String[] items = new String[allFunction.size()];
        for (int i = 0; i < allFunction.size(); i++) {
            Function temp = allFunction.get(i);
            String item = temp.getFid()+"_"+temp.getFname();
            String url1 = "http://"+temp.getFurl();
            functionMap.put(item,url1);
            items[i] = item;
        }
        functionBox.getItems().addAll(items);

        lpButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });

        updateButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                tips.setText("开始下载文件，请稍等！");
                String selectValue = (String) functionBox.getValue();
                String name = selectValue.split("_")[0];
                String describ = selectValue.split("_")[1];
                File file = new File(StaticStorage.rootFile,name+".zip");
                File file2 = new File(StaticStorage.rootFile,name);
                if(file.exists()){
                    CommonTools.delFile(file);
                }
                if(file2.exists()){
                    CommonTools.delFile(file2);
                }
                String s = functionMap.get(selectValue);
                try {
                    HttpTools.downLoadFromUrl(s,name+".zip",StaticStorage.rootFile.getPath());
                } catch (IOException e) {
                    tips.setText(e.getMessage());
                    return;
                }
                tips.setText("文件下载完成，正在更新文件！");
                try {
                    FileZipTools.deCompression(file.getPath(),StaticStorage.rootFile.getPath());
                    file.delete();
                } catch (IOException e) {
                    tips.setText(e.getMessage());
                    return;
                }
                StaticStorage.functionMap.put(name,describ);
                tips.setText("文件更新完成！");
                try {
                    StaticStorage.writeProperties(name,describ,StaticStorage.functionFile.getPath());
                } catch (IOException e) {
                    tips.setText(e.getMessage());
                }


            }
        });


        enterButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                String selectValue = (String) functionBox.getValue();
                String name = selectValue.split("_")[0];
                String describ = StaticStorage.functionMap.get(name);
                if(describ==null){
                    tips.setText("系统尚无该功能的配置文件，请先更新配置文件！");
                    return;
                }
                String xmlName = "fxml/function/"+StaticStorage.functionXMLMap.get(name);
                URL resource = getClass().getClassLoader().getResource(xmlName);
                StaticStorage.functionConfigFile = new File(StaticStorage.rootFile,name);
                AnchorPane root = null;
                try {
                    root = FXMLLoader.load(resource);
                } catch (IOException e) {
                    tips.setText(e.getMessage());
                }
                Scene scene = new Scene(root);
                StaticStorage.mainStage.hide();
                StaticStorage.mainStage.setScene(scene);
                StaticStorage.mainStage.show();

            }
        });

    }
}
