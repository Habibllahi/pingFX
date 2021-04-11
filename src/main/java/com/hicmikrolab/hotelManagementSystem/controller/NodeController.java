/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.controller;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.service.AppServiceI;
import com.hicmikrolab.hotelManagementSystem.utility.NodeState;
import com.hicmikrolab.hotelManagementSystem.utility.NodeStatus;
import com.hicmikrolab.hotelManagementSystem.utility.TableViewCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class NodeController {

    @Autowired
    AppServiceI appServiceI;

    @Autowired
    ApplicationContext applicationContext; //Spring injects its application context to this controller

    @FXML
    ScrollPane root_pane;

    @FXML
    TableView<Node> node_table_view;



    @FXML
    public void initialize(){
        node_table_view.setEditable(true);
        //set table view to single selection mode
        var selectionModel = node_table_view.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        //create table columns
        TableColumn<Node, UUID> column_id = new TableColumn<>("ID");
        TableColumn<Node, String> column_node_name = new TableColumn<>("Name");
        TableColumn<Node, String> column_node_ip = new TableColumn<>("IP Address");
        TableColumn<Node, Integer> column_node_socket = new TableColumn<>("Socket port");
        TableColumn<Node, String> column_node_position = new TableColumn<>("Position");
        TableColumn<Node, NodeStatus> column_node_status = new TableColumn<>("status");
        TableColumn<Node, NodeState> column_node_state = new TableColumn<>("state");
        TableColumn<Node, Boolean> column_node_is_deleted = new TableColumn<>("is deleted");
        TableColumn<Node, Void> column_node_update_button = new TableColumn<>("Update DB");

        //create List of columns
        List columns = Arrays.asList(column_id,column_node_name,column_node_ip,column_node_socket,column_node_position,
                column_node_status,column_node_state,column_node_is_deleted,column_node_update_button);

        //Add columns to the table
        node_table_view.getColumns().addAll(columns);

        //set cell value factory for each columns. This factory extract value of stated property in a Node object representing a table row
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_node_name.setCellValueFactory(new PropertyValueFactory<>("nodeName"));
        column_node_ip.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        column_node_socket.setCellValueFactory(new PropertyValueFactory<>("socketPort"));
        column_node_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        column_node_status.setCellValueFactory(new PropertyValueFactory<>("nodeStatus"));
        column_node_state.setCellValueFactory(new PropertyValueFactory<>("nodeState"));
        column_node_is_deleted.setCellValueFactory(new PropertyValueFactory<>("deleted"));
        //render a custom button in to the table for this column
        column_node_update_button.setCellFactory(
                (tableColumn)-> new TableViewCell(appServiceI)
        );

        //set cell factory to a TextFieldTableCell in order to make the table cell editable
        column_node_name.setCellFactory(TextFieldTableCell.<Node>forTableColumn());
        column_node_ip.setCellFactory(TextFieldTableCell.<Node>forTableColumn());
        column_node_socket.setCellFactory(TextFieldTableCell.<Node,Integer>forTableColumn(new StringConverter<Integer>() {
            /**
             * Converts the object provided into its string form.
             * Format of the returned string is defined by the specific converter.
             *
             * @param object the object of type {@code T} to convert
             * @return a string representation of the object passed in.
             */
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            /**
             * Converts the string provided into an object defined by the specific converter.
             * Format of the string and type of the resulting object is defined by the specific converter.
             *
             * @param string the {@code String} to convert
             * @return an object representation of the string passed in.
             */
            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        column_node_position.setCellFactory(TextFieldTableCell.<Node>forTableColumn());

        column_node_is_deleted.setCellFactory(TextFieldTableCell.<Node,Boolean>forTableColumn(new StringConverter<Boolean>() {
            /**
             * Converts the object provided into its string form.
             * Format of the returned string is defined by the specific converter.
             *
             * @param object the object of type {@code T} to convert
             * @return a string representation of the object passed in.
             */
            @Override
            public String toString(Boolean object) {
                return object.toString();
            }

            /**
             * Converts the string provided into an object defined by the specific converter.
             * Format of the string and type of the resulting object is defined by the specific converter.
             *
             * @param string the {@code String} to convert
             * @return an object representation of the string passed in.
             */
            @Override
            public Boolean fromString(String string) {
                if(string.equalsIgnoreCase("true"))
                    return true;
                else
                    return false;
            }
        }));

        //Now lets implement cell editing event
        column_node_name.setOnEditCommit(
                t ->{
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setNodeName(t.getNewValue());
                }
        );
        column_node_ip.setOnEditCommit(
                t ->{
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setIpAddress(t.getNewValue());
                }
        );
        column_node_socket.setOnEditCommit(
                t ->{
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setSocketPort(t.getNewValue());
                }
        );
        column_node_position.setOnEditCommit(
                t ->{
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setPosition(t.getNewValue());
                }
        );
        column_node_is_deleted.setOnEditCommit(
                t ->{
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setDeleted(t.getNewValue());
                }
        );

        //populate the table view from database, using Observable list
        node_table_view.getItems().addAll(FXCollections.observableList(appServiceI.findAllNode()));

    }
}
