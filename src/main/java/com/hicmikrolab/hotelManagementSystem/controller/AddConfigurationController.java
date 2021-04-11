/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.controller;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.service.AppServiceI;
import com.hicmikrolab.hotelManagementSystem.utility.NodeState;
import com.hicmikrolab.hotelManagementSystem.utility.NodeStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;

@Controller
public class AddConfigurationController {
    @Autowired
    ApplicationContext applicationContext; //Spring injects its application context to this controller

    @Value("classpath:/AddConfigurationView.fxml")
    private Resource controlPlateViewFxml; //Spring injects the path to ControlPlateView.fxml as a resource path

    @Autowired
    private AppServiceI appServiceI;

    private Node node = null;

    @FXML
    TextField host_name_input;

    @FXML
    TextField ip_address_input;

    @FXML
    TextField node_position;

    @FXML
    TextField socket_port_input;

    @FXML
    Button configuration_submit_btn;

    @FXML
    public void initialize(){
        node = new Node();
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Node save alert");
        configuration_submit_btn.setOnAction(
                actionEvent ->{
                    node.setId(null);//this ensure new ID is generated before persisting
                    node.setNodeName(host_name_input.getText());
                    node.setIpAddress(ip_address_input.getText());
                    node.setSocketPort(Integer.parseInt(socket_port_input.getText()));
                    node.setNodeState(NodeState.off); //Every new Node will by default be off state in DB, a routine check will occur to update this
                    node.setNodeStatus(NodeStatus.offline);//Every new node will by default be offline in DB, a routine check will occur to update this
                    node.setPosition(node_position.getText());
                    node.setDeleted(false);
                    var p = appServiceI.saveNode(node);
                    if(node==null){
                        alert.setContentText("Save unsuccessful");
                        alert.show();
                    }else{
                        host_name_input.setText("");
                        ip_address_input.setText("");
                        socket_port_input.setText("");
                        node_position.setText("");
                    }
                }
        );
    }
}
