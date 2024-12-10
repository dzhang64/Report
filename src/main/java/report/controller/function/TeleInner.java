package report.controller.function;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import report.functions.teleinner.TelecomIndoorReport;
import report.models.user.Role;
import report.utils.StaticStorage;
import report.utils.CommonTools;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class TeleInner {

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

    private String filePath = "";

    public TeleInner() {
    }

    @FXML
    private void initialize() throws IOException {
        lpLabel1.setText(StaticStorage.loginUser.getUserName());
        lpLabel2.setText(Role.getRole(StaticStorage.loginUser.getUserRoleId()).getRoleStr());
        lpLabel3.setText(CommonTools.getTime());
        TelecomIndoorReport.initClass();


        lpButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });

        folderChooseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(StaticStorage.mainStage);
                filePath = file.getPath();//
                singleFile.setText(filePath);
                singleFile.setDisable(true);
            }
        });


        singleRun.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (filePath.trim().length() == 0) {
                    result.setText("请选择文件夹！");
                    return;
                }
                try {
                    TelecomIndoorReport telecomIndoorReport = new TelecomIndoorReport(filePath);
                    telecomIndoorReport.init();
                    telecomIndoorReport.makeReport();
                    result.setText("处理完毕！");
                } catch (Exception e) {
                    result.setText(e.getMessage());
                }
            }
        });

        multiRun.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (filePath.trim().length() == 0) {
                    result.setText("请选择文件夹！");
                    return;
                }
                File file = new File(filePath);
                if (file.exists()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        try {
                            result.appendText("处理：" + files[i].getName() + "\n");
                            String datePath = files[i].getPath();
                            TelecomIndoorReport telecomIndoorReport = new TelecomIndoorReport(datePath);
                            boolean init = telecomIndoorReport.init();
                            telecomIndoorReport.makeReport();
                            result.appendText(files[i].getName() + "处理完成！" + "\n");
                            result.appendText("_______________________________________________________________________" + "\n");
                        }catch(Exception e){
                            result.appendText(e.getMessage() + "\n");
                            result.appendText("_______________________________________________________________________" + "\n");
                        }
                    }
                }
            }
        });

    }
}
