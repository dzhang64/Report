package report.controller.function;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import report.functions.jsunicom.lte.Report;
import report.models.net.Cell;
import report.models.user.Role;
import report.utils.CommonTools;
import report.utils.StaticStorage;
import report.utils.jsunicom.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UnicomLte {

    @FXML
    private Label lpLabel1;

    @FXML
    private Label lpLabel2;

    @FXML
    private Label lpLabel3;

    @FXML
    private Button lpButton;

    @FXML
    private TextField singleFile;

    @FXML
    private TextArea result;

    @FXML
    private Button folderChooseButton;

    @FXML
    private Button singleRun;

    @FXML
    private Button multiRun;

    private String filePath="";

    public static Map<String, Cell> cellMap = new HashMap<String, Cell>();

    public UnicomLte() {
    }

    @FXML
    private void initialize() throws IOException {
        lpLabel1.setText(StaticStorage.loginUser.getUserName());
        lpLabel2.setText(Role.getRole(StaticStorage.loginUser.getUserRoleId()).getRoleStr());
        lpLabel3.setText(CommonTools.getTime());
        File cellsDir = new File(StaticStorage.functionConfigFile, "cells");
        StaticStorage.tempDir = new File(StaticStorage.functionConfigFile, "temp");
        File[] tempImages = StaticStorage.tempDir.listFiles();
        for (int i = 0; i < tempImages.length; i++) {
            tempImages[i].delete();
        }
        File[] files = cellsDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            CommonUtils.getCellMapFromExcel(files[i],cellMap);
        }

        lpButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });

        folderChooseButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File file = directoryChooser.showDialog(StaticStorage.mainStage);
                filePath = file.getPath();//
                singleFile.setText(filePath);
                singleFile.setDisable(true);
            }
        });


        singleRun.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                if(filePath.trim().length()==0){
                    result.setText("请选择文件夹！");
                    return;
                }
                try {
                    result.setText("");
                    Report report = new Report();
                    report.makeReport(filePath);
                    result.setText("处理完毕！");
                } catch (Exception e) {
                    result.setText(e.getMessage());
                }
            }
        });

        multiRun.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                if(filePath.trim().length()==0){
                    result.setText("请选择文件夹！");
                    return;
                }
                result.setText("");
                File file = new File(filePath);
                if(file.exists()){
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        result.appendText("处理："+files[i].getName()+"\n");
                        String datePath = files[i].getPath();
                        try{
                            Report report = new Report();
                            report.makeReport(datePath);
                            result.appendText(files[i].getName()+"处理完成！"+"\n");
                            result.appendText("_______________________________________________________________________"+"\n");

                        } catch (Exception e) {
                            result.appendText(e.getMessage()+"\n");
                            result.appendText("_______________________________________________________________________"+"\n");
                        }

                    }
                }


            }
        });

    }
}
