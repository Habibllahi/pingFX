/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.controller;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.service.AppServiceI;
import com.hicmikrolab.hotelManagementSystem.utility.IpInvalidException;
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

import javax.validation.Valid;

@Controller
public class AddConfigurationController {

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
    Alert alert;

    @FXML
    public void initialize(){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Node save alert");
        configuration_submit_btn.setOnAction(
                actionEvent ->{
                    //do some validation
                    if(noFilledIsEmpty()){
                        try{
                            toSaveNode();
                        } catch (NumberFormatException | IpInvalidException e) {
                            if(e instanceof NumberFormatException)
                                alert.setContentText("Socket port must be integer");
                            if(e instanceof IpInvalidException)
                                alert.setContentText(e.getMessage());
                            alert.show();
                        }
                    }
                }
        );
    }

    private void toSaveNode() throws NumberFormatException, IpInvalidException{
        Node savedNode;
        node = new Node();
        node.setId(null);//this ensure new ID is generated before persisting
        node.setNodeName(host_name_input.getText());
        node.setIpAddress(ipStringValidation(ip_address_input.getText()));
        node.setNodeState(NodeState.off); //Every new Node will by default be off state in DB, a routine check will occur to update this
        node.setNodeStatus(NodeStatus.offline);//Every new node will by default be offline in DB, a routine check will occur to update this
        node.setSocketPort(Integer.parseInt(socket_port_input.getText()));
        node.setPosition(node_position.getText());
        node.setDeleted(false);
        savedNode = appServiceI.saveNode(node);
        if(savedNode==null){
            alert.setContentText("Save unsuccessful");
            alert.show();
        }else{
            host_name_input.setText("");
            ip_address_input.setText("");
            socket_port_input.setText("");
            node_position.setText("");
        }
    }

    private final boolean noFilledIsEmpty() {
        var state = false;
        if(host_name_input.getText().isEmpty()){
            alert.setContentText("Host Name can not be empty");
            alert.show();
        }else if(ip_address_input.getText().isEmpty()){
            alert.setContentText("Ip Address can not be empty");
            alert.show();
        }else if(socket_port_input.getText().isEmpty()){
            alert.setContentText("Socket port can not be empty");
            alert.show();
        }else if(node_position.getText().isEmpty()){
            alert.setContentText("Node position can not be empty");
            alert.show();
        }else
            state = true;
        return state;
    }

    private final String ipStringValidation(String input) throws IpInvalidException, NumberFormatException{
        var result = input.split("[.]");
        if(result.length !=4)
            throw new IpInvalidException("ip format must be:: x.x.x.x where x<= 255");

        for(String sub : result){
            if(Integer.parseInt(sub)>255)
                throw new IpInvalidException("ip format must be:: x.x.x.x where x<= 255");
        }
        return input;
    }
}
