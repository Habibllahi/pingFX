/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.controller;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.service.AppServiceI;
import com.hicmikrolab.hotelManagementSystem.utility.ListViewCell;
import com.hicmikrolab.hotelManagementSystem.utility.NetworkInterface;
import com.hicmikrolab.hotelManagementSystem.utility.NodeState;
import com.hicmikrolab.hotelManagementSystem.utility.NodeStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * <p>
 *     This controller is referenced in the <code>MainView.fxml</code> view file, using the
 *     property <code>fx:controller</code> and assigning it value <code>"com.hicmikrolab.hotelManagementSystem.controller.MainController"</code>.
 *     By this association in the view file, The JavaFX application context can use this class bean (from the spring application context) to respond
 *     to client events on the associated view as programmed in this class.
 *
 *    This class bean is managed by Spring framework.
 * </p>
 */
@Controller
public class MainController {

    @Autowired
    AppServiceI appServiceI;

    @Value("classpath:/AddConfigurationView.fxml")
    private Resource addConfigurationViewFxml; //Spring injects the path to AddConfigurationView.fxml as a resource path

    @Value("classpath:/NodeView.fxml")
    private Resource nodeViewFxml;

    @Autowired
    ApplicationContext applicationContext; //Spring injects its application context to this controller

    @Autowired
    NetworkInterface networkInterface;

    ObservableList<Node> observableList = FXCollections.observableArrayList();

    private boolean startUpMode = true;




    @FXML
    public VBox root_view;

    @FXML
    public MenuBar menu_bar;

    @FXML
    public AnchorPane control_pane;

    @FXML
    public Menu configure_menu;

    @FXML
    public Menu view_menu;

    @FXML
    public MenuItem add_menu_item;

    @FXML
    public MenuItem refresh_menu_item;

    @FXML
    public MenuItem node_table_menu_item;

    @FXML
    public ListView<Node> list_view;

    @FXML
    public ScrollPane scroll_pane;

    @FXML
    public void initialize(){
        node_table_menu_item.setOnAction(
                actionEvent->{
                    try{
                        var stage = new Stage();
                        var url = this.nodeViewFxml.getURL();
                        FXMLLoader fxmlLoader = new FXMLLoader(url);
                        fxmlLoader.setControllerFactory(applicationContext::getBean);//This FXML Loader controller is set to use the Spring Application context instead
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root,900,600);
                        stage.setTitle("Node Table");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        add_menu_item.setOnAction(
                (actionEvent)->{
                    try {
                        var stage = new Stage();
                        var url = this.addConfigurationViewFxml.getURL();
                        FXMLLoader fxmlLoader = new FXMLLoader(url);
                        fxmlLoader.setControllerFactory(applicationContext::getBean);//This FXML Loader controller is set to use the Spring Application context instead
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root,600,150);
                        stage.setTitle("Add Node");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );
        refresh_menu_item.setOnAction(
                actionEvent ->{
                    var refreshPing = new Thread(new NonContinuousPingTask(),"refresh ping thread");
                    refreshPing.setDaemon(true);
                    refreshPing.start();
                }
        );
        if(startUpMode){
            var startupPing = new Thread(new NonContinuousPingTask(),"start up ping thread");
            startupPing.setDaemon(true);
            startupPing.start();
            startUpMode = false;
        }

        //Commence ContinuousPingTask on a worker Thread
        var asyncPings = new Thread(new ContinuousPingTask(appServiceI, networkInterface),"routine ping thread");
        asyncPings.setDaemon(true);
        asyncPings.start();
    }

    public final void populateListView(){
        //Populate the observable list by fetching All Node in the DB
        observableList.clear(); //this prevents duplicate item in observable list
        observableList.addAll(appServiceI.findAllNode().stream().filter(node-> node.isDeleted()==false).collect(Collectors.toList()));

        //System.out.println(observableList.stream().map(Node::toString).collect(Collectors.toList()));// Just for debug purpose
        list_view.setItems(observableList);
        list_view.setCellFactory(
                (ListView<Node> listView ) -> new ListViewCell(networkInterface, appServiceI)
        );
    }

    /**
     * <p>
     *     This class is created inner class to the <code>MainControler</code> class in other to have access to the <code>populateListView()</code> method
     *     which populate the ListView on the UI Thread.
     *
     *     This class defines some logics that have to be performed concurrently with the UI Thread. Performing this logic in the UI Thread will freeze the
     *     UI Thread.
     *
     *     The class then as well alter the UI SceneGraph using the <code>platform.runlater()</code>. Because of this reason this class resides within the
     *     class that have access to the UI views to be manipulated from a separate Thread that will run concurrently with UI Thread upon the occurrence of
     *     an action.
     *
     *      The logic implemented in the call method is a startup pings of all registered nodes that are not flag deleted.
     *      It then update the ListView after pinging all, reflecting the state of each Node on its corresponding ListVIewCell
     * </p>
     */
    public class NonContinuousPingTask extends Task<Boolean>{

        /**
         * Invoked when the Task is executed, the call method must be overridden and
         * implemented by subclasses. The call method actually performs the
         * background thread logic. Only the updateProgress, updateMessage, updateValue and
         * updateTitle methods of Task may be called from code within this method.
         * Any other interaction with the Task from the background thread will result
         * in runtime exceptions.
         *
         * @return The result of the background work, if any.
         * @throws Exception an unhandled exception which occurred during the
         *                   background operation
         */
        @Override
        protected Boolean call() throws Exception {
            pingJob();
            Platform.runLater(()-> populateListView());
            return true;
        }
    }

    /**
     * <p>
     *     This class is created inner class to the <code>MainControler</code> class in other to have access to the <code>populateListView()</code> method
     *     which populate the ListView on the UI Thread.
     *
     *     This class defines some logics that have to be performed concurrently with the UI Thread. Performing this logic in the UI Thread will freeze the
     *     UI Thread.
     *
     *     The class then as well alter the UI SceneGraph using the <code>platform.runlater()</code>. Because of this reason this class resides within the
     *     class that have access to the UI views to be manipulated from a separate Thread that will run concurrently with UI Thread upon the occurrence of
     *     an action.
     *
     *      The logic implemented in the call method is an infinite loop that periodically pings all registered nodes that are not flag deleted.
     *      It then update the ListView after pinging all, reflecting the state of each Node on its corresponding ListVIewCell
     * </p>
     */
    public class ContinuousPingTask extends Task<Void>{
        private final AppServiceI appServiceI;
        private final NetworkInterface networkInterface;

        public ContinuousPingTask(AppServiceI appServiceI, NetworkInterface networkInterface) {
            this.appServiceI = appServiceI;
            this.networkInterface = networkInterface;
        }

        /**
         * Invoked when the Task is executed, the call method must be overridden and
         * implemented by subclasses. The call method actually performs the
         * background thread logic. Only the updateProgress, updateMessage, updateValue and
         * updateTitle methods of Task may be called from code within this method.
         * Any other interaction with the Task from the background thread will result
         * in runtime exceptions.
         *
         * @return The result of the background work, if any.
         * @throws Exception an unhandled exception which occurred during the
         *                   background operation
         */
        @Override
        protected Void call() throws Exception {
            while(true){
                Thread.sleep(60000L); //Sleep for 5 Minute
                //System.out.println("I am going to run ping test");
                pingJob();
                //System.out.println("will refresh the MainView Now");
                Platform.runLater(()-> populateListView());
            }
        }
    }

    /**
     * This method reads all the nodes, iterate through the nodes and ping them one after the other, updating the node status for each node
     */
    public final void pingJob(){
        var nodes = appServiceI.findAllNode().stream().filter(node-> node.isDeleted()==false).collect(Collectors.toList());
        if(!nodes.isEmpty()){
            for(var node : nodes){
                if(networkInterface.sendPingRequest(node.getIpAddress())){
                    //Request if the node is currently ON or OFF
                    var result = networkInterface.httpClientOnOffChecker(node.getIpAddress(),node.getSocketPort(),"/update?status=1");
                    if(result.equalsIgnoreCase("ACTIVE")){
                        node.setNodeState(NodeState.on);
                    }else if(result.equalsIgnoreCase("INACTIVE")){
                            node.setNodeState(NodeState.off);
                    }
                    node.setNodeStatus(NodeStatus.online);
                }
                else{
                    node.setNodeStatus(NodeStatus.offline);
                    node.setNodeState(NodeState.off);
                }

                appServiceI.updateStateAndStatus(node);
            }
        }
    }
}
