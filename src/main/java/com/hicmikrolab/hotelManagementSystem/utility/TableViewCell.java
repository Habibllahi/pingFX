/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.utility;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.service.AppServiceI;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;

public class TableViewCell extends javafx.scene.control.TableCell<Node,Void> {
    AppServiceI appServiceI;

    public TableViewCell(AppServiceI appServiceI) {
        this.appServiceI = appServiceI;
    }



    /**
     * The updateItem method should not be called by developers, but it is the
     * best method for developers to override to allow for them to customise the
     * visuals of the cell. To clarify, developers should never call this method
     * in their code (they should leave it up to the UI control, such as the
     * {@link ListView} control) to call this method. However,
     * the purpose of having the updateItem method is so that developers, when
     * specifying custom cell factories (again, like the ListView
     * {@link ListView#cellFactoryProperty() cell factory}),
     * the updateItem method can be overridden to allow for complete customisation
     * of the cell.
     *
     * <p>It is <strong>very important</strong> that subclasses
     * of Cell override the updateItem method properly, as failure to do so will
     * lead to issues such as blank cells or cells with unexpected content
     * appearing within them. Here is an example of how to properly override the
     * updateItem method:
     *
     * <pre>
     * protected void updateItem(T item, boolean empty) {
     *     super.updateItem(item, empty);
     *
     *     if (empty || item == null) {
     *         setText(null);
     *         setGraphic(null);
     *     } else {
     *         setText(item.toString());
     *     }
     * }
     * </pre>
     *
     * <p>Note in this code sample two important points:
     * <ol>
     *     <li>We call the super.updateItem(T, boolean) method. If this is not
     *     done, the item and empty properties are not correctly set, and you are
     *     likely to end up with graphical issues.</li>
     *     <li>We test for the <code>empty</code> condition, and if true, we
     *     set the text and graphic properties to null. If we do not do this,
     *     it is almost guaranteed that end users will see graphical artifacts
     *     in cells unexpectedly.</li>
     * </ol>
     *  @param item The new item for the cell.
     *
     * @param empty whether or not this cell represents data from the list. If it
     *              is empty, then it does not represent any domain data, but is a cell
     */
    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){
            Button button = new Button("Update");
            Parent root = new FlowPane(button);
            button.setOnAction(
                    actionEVent->{
                        var tableView = getTableView();
                        Node selectedNode = tableView.getItems().get(getIndex()); //obtain the selected node
                        appServiceI.updateNode(selectedNode);
                        //System.out.println(selectedNode.toString());
                    }
            );
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(root);
        }
    }


}
