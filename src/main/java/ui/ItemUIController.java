package ui;

import controller.ItemController;
import domain.Item;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import util.DataRefreshable;
import util.DataRefresher;

public class ItemUIController implements DataRefreshable {

    @FXML
    public TableView<Item> itemTable;
    @FXML
    public TableColumn<Item, String> nameColumn;
    @FXML
    public TableColumn<Item, Double> stockColumn;
    @FXML
    public TableColumn<Item, Double> reservedColumn;

    private ItemController c = new ItemController();

    public void initialize() {
        DataRefresher.addListener(DataRefresher.Type.PRODUCT,this);
        DataRefresher.addListener(DataRefresher.Type.TRANSACTION,this);

        itemTable.setEditable(true);

        nameColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(param.getValue().getProduct().getName());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        reservedColumn.setCellValueFactory(new PropertyValueFactory<>("reserved"));

        stockColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        stockColumn.setOnEditCommit(t -> {
            Item i = t.getRowValue();
            try {
                c.changeItemStock(i,t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });


        itemTable.getItems().setAll(c.getItens());
    }

    public void refresh(){
        itemTable.getItems().setAll(c.getItens());
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        itemTable.refresh();
    }
}
