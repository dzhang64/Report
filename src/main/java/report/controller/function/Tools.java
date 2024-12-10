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

public class Tools {

    @FXML
    private Button covertGCJ2WGS;

    @FXML
    private TextField gcjLon;

    @FXML
    private TextField gcjLa;

    @FXML
    private TextField wsg84Res;


    public Tools() {
    }

    @FXML
    private void initialize() throws IOException {

        covertGCJ2WGS.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                double longtitud  = Double.parseDouble(gcjLon.getText().trim());
                double latitude  = Double.parseDouble(gcjLa.getText().trim());
                double[] doubles = CommonTools.gcj2wgc(longtitud, latitude);
                String res = doubles[0]+","+doubles[1];
                wsg84Res.setText(res);
            }
        });



    }
}
