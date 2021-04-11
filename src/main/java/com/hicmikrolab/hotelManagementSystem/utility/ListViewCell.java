/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.utility;

import com.hicmikrolab.hotelManagementSystem.controller.ControlPlateController;
import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.service.AppServiceI;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ListViewCell extends ListCell<Node> {

    private NetworkInterface network;
    private Label control_label;
    private Label node_status_label;
    private Button on_button;
    private Button off_button;
    private Button ping_button;
    private Label node_sate_label;
    private Circle node_status_indicator;
    private Circle node_sate_indicator;
    private AppServiceI appServiceI;

    public ListViewCell(NetworkInterface network, AppServiceI appServiceI) {
        this.network = network;
        this.appServiceI = appServiceI;
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);
        if (item!=null && !empty){
            Parent root = new ControlPlateController().init();
            //Ensure the root Node is not null
            if(root != null){
                //Lookup for necessary child node
                control_label = (Label) root.lookup("#control_label");
                node_status_label = (Label) root.lookup("#node_status_label");
                on_button = (Button)root.lookup("#on_button");
                off_button = (Button)root.lookup("#off_button");
                ping_button = (Button)root.lookup("#ping_button");
                node_sate_label = (Label)root.lookup("#node_sate_label");
                node_status_indicator = (Circle) root.lookup("#node_status_indicator");
                node_sate_indicator = (Circle) root.lookup("#node_sate_indicator");

                //perform some logic on the lookup nodes
                control_label.setText(item.getNodeName());
                node_status_label.setText(item.getNodeStatus().name());
                node_sate_label.setText(item.getNodeState().name());
                if(item.getNodeStatus().name().equalsIgnoreCase(NodeStatus.online.name()))
                    node_status_indicator.setFill(Color.GREEN);
                else
                    node_status_indicator.setFill(Color.RED);

                if(item.getNodeState().name().equalsIgnoreCase(NodeState.on.name()))
                    node_sate_indicator.setFill(Color.GREEN);
                else
                    node_sate_indicator.setFill(Color.RED);

                on_button.setOnAction(
                        actionEvent ->{
                            //disable button to visually tell user that the click is accepted and being process
                            offAllButtonForThisListCellInstance();
                            //Send ON instruction via Socket to this node in network
                            Task nodeOnTask = new NodeOnOrOffTask(network,item.getIpAddress(),item.getSocketPort(),"/update?relay="+item.getPosition()+"&state=0",
                                    item, appServiceI, true);
                            var nodeWriteOnThread = new Thread(nodeOnTask, "node write thread");
                            nodeWriteOnThread.setDaemon(true);
                            nodeWriteOnThread.start();
                        }
                );
                off_button.setOnAction(
                        actionEvent ->{
                            //disable button to visually tell user that the click is accepted and being process
                            offAllButtonForThisListCellInstance();
                            //Send off instruction via Socket to this node in network
                            Task nodeOffTask = new NodeOnOrOffTask(network,item.getIpAddress(),item.getSocketPort(),"/update?relay="+item.getPosition()+"&state=1",
                                    item, appServiceI, false);
                            var nodeWriteOffThread = new Thread(nodeOffTask,"node write thread");
                            nodeWriteOffThread.setDaemon(true);
                            nodeWriteOffThread.start();
                        }
                );

                ping_button.setOnAction(
                        actionEvent ->{
                            //disable button to visually tell user that the click is accepted and being process
                            offAllButtonForThisListCellInstance();
                            Task pingTask = new NodePingTask(network,item, appServiceI);
                            var nodePingingThread = new Thread(pingTask,"node pinging thread");
                            nodePingingThread.setDaemon(true);
                            nodePingingThread.start();
                        }
                );

                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(root);

            }
        }

    }

    /**
     * <p>
     *     This class is created inner class to the <code>ListViewCell</code> class in other to have access to the <code>Manin.fxml</code> views
     *     This technically possible because the <code>ListViewCell</code> class is accessible to the <code>MainController</code> class which are both
     *     executed on the UI Thread (JavaFX Application Thread).
     *
     *     This class defines some logics that have to be performed concurrently with the UI Thread. Performing this logic in the UI Thread will freeze the
     *     UI Thread.
     *
     *     The class then as well alter the UI SceneGraph using the <code>platform.runlater()</code>. Because of this reason this class resides within the
     *     class that have access to the UI views to be manipulated from a separate Thread that will run concurrently with UI Thread upon the occurrence of
     *     an action.
     *
     *     The logic implemented in the call method is a node ping logic.
     * </p>
     */
    public class NodePingTask extends Task<Boolean> {

        private final NetworkInterface network;
        private final Node item;
        private final AppServiceI appServiceI;

        public NodePingTask(NetworkInterface network, Node item, AppServiceI appServiceI) {
            this.network = network;
            this.item = item;
            this.appServiceI = appServiceI;
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
        protected Boolean call() throws Exception {
            var isOnline = network.sendPingRequest(item.getIpAddress());
            updateValue(isOnline);
            //Modify the Scene Graph from this thread. This call will do the runnable code on the JavaFX Application Thread (UI Thread)
            Platform.runLater(()->{
                if (isOnline){
                    //System.out.println("node online");
                    node_status_indicator.setFill(Color.GREEN);
                    node_status_label.setText("online");
                    item.setNodeStatus(NodeStatus.online);
                }else{
                    //System.out.println("node offline");
                    node_status_indicator.setFill(Color.RED);
                    node_status_label.setText("offline");
                    item.setNodeStatus(NodeStatus.offline);
                }
                appServiceI.onlyUpdateNodeStatus(item);
                onAllButtonForThisListCellInstance();
            });
            return isOnline;
        }
    }

    /**
     * <p>
     *     This class is created inner class to the <code>ListViewCell</code> class in other to have access to the <code>Manin.fxml</code> views
     *     This technically possible because the <code>ListViewCell</code> class is accessible to the <code>MainController</code> class which are both
     *     executed on the UI Thread (JavaFX Application Thread).
     *
     *     This class defines some logics that have to be performed concurrently with the UI Thread. Performing this logic in the UI Thread will freeze the
     *     UI Thread.
     *
     *     The class then as well alter the UI SceneGraph using the <code>platform.runlater()</code>. Because of this reason this class resides within the
     *     class that have access to the UI views to be manipulated from a separate Thread that will run concurrently with UI Thread upon the occurrence of
     *     an action.
     *
     *      The logic implemented in the call method is a node ON and OFF logic via a network
     * </p>
     */
    public class NodeOnOrOffTask extends Task<String> {

        private final NetworkInterface network;
        private final String message;
        private final String ipAddress;
        private final int socketPort;
        private final Node item;
        private final AppServiceI appServiceI;
        private final boolean isForOn;

        public NodeOnOrOffTask(NetworkInterface network, String ipAddress, int socketPort, String message, Node item, AppServiceI appServiceI, boolean isForOn) {
            this.network = network;
            this.message = message;
            this.ipAddress = ipAddress;
            this.socketPort = socketPort;
            this.item = item;
            this.appServiceI = appServiceI;
            this.isForOn = isForOn;
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
        protected String call() throws Exception {
            var result = network.httpClientApproach(ipAddress,socketPort,message);
            updateValue(result);
            updateMessage(result);
            //Modify the Scene Graph from this thread. This call will do the runnable code on the JavaFX Application Thread (UI Thread)
            Platform.runLater(()->{
                if(result.equalsIgnoreCase("OK") && isForOn){
                    node_sate_indicator.setFill(Color.GREEN);
                    node_sate_label.setText("on");
                    //enable the ON button to accept subsequent click
                    onAllButtonForThisListCellInstance();
                    item.setNodeState(NodeState.on);
                    appServiceI.onlyUpdateNodeState(item);
                }
                if(result.equalsIgnoreCase("FAILED") && isForOn){
                    node_sate_indicator.setFill(Color.RED);
                    node_sate_label.setText("off");
                    onAllButtonForThisListCellInstance();
                }
                if(result.equalsIgnoreCase("OK") && !isForOn){
                    node_sate_indicator.setFill(Color.RED);
                    node_sate_label.setText("off");
                    onAllButtonForThisListCellInstance();
                    item.setNodeState(NodeState.off);//enable the OFF button to accept subsequent click
                    appServiceI.onlyUpdateNodeState(item);
                }else{
                    onAllButtonForThisListCellInstance();
                }


            });
            return result;
        }
    }

    /**
     * <p>
     *     To make a mass enable of buttons for this ListCell instance
     *     This method will only affect the ListCell being interacted with by teh user and not all the ListCell
     *     The method is final so as to make it Thread safe
     *     The Idea is that, a user should not be able to perform two or more actions on a single node simultaneously
     *     Doing the ON process, he cant off nor Ping the node until the ON process succeed or fail. And Vise versa for OFF process and PING
     *     process
     * </p>
     */
    public final void onAllButtonForThisListCellInstance(){
        ping_button.setDisable(false);
        on_button.setDisable(false);
        off_button.setDisable(false);
    }
    /**
     * <p>
     *     To make a mass disable of buttons for this ListCell instance
     *     This method will only affect the ListCell being interacted with by teh user and not all the ListCell
     *     The method is final so as to make it Thread safe
     *     The Idea is that, a user should not be able to perform two or more actions on a single node simultaneously
     *     Doing the ON process, he cant off nor Ping the node until the ON process succeed or fail. And Vise versa for OFF process and PING
     *     process
     * </p>
     */
    public final void offAllButtonForThisListCellInstance(){
        ping_button.setDisable(true);
        on_button.setDisable(true);
        off_button.setDisable(true);
    }
}
