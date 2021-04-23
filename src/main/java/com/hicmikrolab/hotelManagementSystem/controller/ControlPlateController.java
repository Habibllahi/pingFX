/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class ControlPlateController {

    @FXML
    public VBox control_plate_root_view;

    @FXML
    public VBox indicator_plate;

    @FXML
    public HBox control_plate;

    @FXML
    public Label control_label;

    @FXML
    public Button on_button;

    @FXML
    public Button off_button;

    @FXML
    public Button ping_button;

    @FXML
    public Circle node_status_indicator;

    @FXML
    public Circle node_sate_indicator;

    @FXML
    public Label node_status_label;

    @FXML
    public Label node_sate_label;

    /**
     * This method shall be called explicitly in a subclass of ListCell. This whole View and its controller will function
     * withing a listCell of a ListView
     * @return
     */
    public Parent init(){
        Parent parent = null;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/ControlPlateView.fxml"));
            parent = fxmlLoader.load();
        }catch (IOException e){
            e.printStackTrace();
        }
        return parent;
    }


}
