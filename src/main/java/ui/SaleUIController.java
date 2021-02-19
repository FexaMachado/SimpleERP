package ui;

import controller.SaleController;
import domain.Sale;
import domain.SaleItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DoubleStringConverter;
import util.DataRefreshable;
import util.DataRefresher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleUIController implements DataRefreshable {
    @FXML
    public TableView<Sale> saleTable;
    @FXML
    public TableColumn<Sale, String> contactColumn;
    @FXML
    public TableColumn<Sale, Double> priceColumn;

    @FXML
    public TableView<SaleItem> saleItemTable;
    @FXML
    public TableColumn<SaleItem, String> itemColumn;
    @FXML
    public TableColumn<SaleItem, Double> itemAmountColumn;
    @FXML
    public TableColumn<SaleItem, Double> itemPriceColumn;
    @FXML
    public TableColumn<SaleItem, Double> itemTotalPriceColumn;

    @FXML
    public Button newItemButton;

    private final SaleController saleController = new SaleController();

    public void initialize() {
        DataRefresher.addListener(DataRefresher.Type.CONTACT,this);
        DataRefresher.addListener(DataRefresher.Type.PRODUCT,this);

        saleTable.setEditable(true);
        saleItemTable.setEditable(true);

        contactColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(param.getValue().getContact().getName());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        itemTotalPriceColumn.setCellValueFactory(param -> {
            SaleItem saleItem = param.getValue();
            if (saleItem != null) {
                return new SimpleDoubleProperty(saleItem.getNumber() * saleItem.getPrice()).asObject();
            } else {
                return new SimpleDoubleProperty(0.0).asObject();
            }
        });


        itemAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        itemAmountColumn.setOnEditCommit(t -> {
            Sale s = saleTable.getSelectionModel().getSelectedItem();
            if (s != null) {
                try {
                    saleController.changeSaleItemQuantity(s, t.getRowValue(), t.getNewValue());
                    refresh(saleTable.getSelectionModel().getSelectedIndex());
                } catch (IllegalArgumentException e) {
                    displayErrorMessage(e.getMessage());
                }
            }
        });

        itemPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        itemPriceColumn.setOnEditCommit(t -> {
            Sale s = saleTable.getSelectionModel().getSelectedItem();
            if (s != null) {
                try {
                    saleController.changeSaleItemPrice(s, t.getRowValue(), t.getNewValue());
                    refresh(saleTable.getSelectionModel().getSelectedIndex());
                } catch (IllegalArgumentException e) {
                    displayErrorMessage(e.getMessage());
                }
            }
        });

        //saleTable.getItems().setAll(FXCollections.observableArrayList(saleController.getSales()));
        saleTable.getItems().setAll(saleController.getSales());
        newItemButton.setDisable(true);
    }

    public void saleTableClicked(MouseEvent mouseEvent) {
        Sale s = saleTable.getSelectionModel().getSelectedItem();

        if (s == null) {
            newItemButton.setDisable(true);
        } else {
            newItemButton.setDisable(false);

            saleItemTable.getItems().setAll(s.getSaleItemList());
        }

        saleItemTable.getSelectionModel().clearSelection();
    }

    public void deleteClicked(ActionEvent actionEvent) {
        Sale s = saleTable.getSelectionModel().getSelectedItem();
        SaleItem si = saleItemTable.getSelectionModel().getSelectedItem();

        if (s != null) {
            if (si == null) {
                saleController.deleteSale(s);
                refresh(-1);
            } else {
                saleController.deleteSaleItem(s, si);
                refresh(saleTable.getSelectionModel().getSelectedIndex());
            }
        }
    }

    public void newSaleButtonClicked(MouseEvent mouseEvent) {
        List<String> choices = new ArrayList<>(saleController.getContactNames());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Criar Venda");
        dialog.setContentText("Escolha o cliente da venda");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            saleController.addSale(s);
            refresh(-1);
        });
    }

    public void newSaleItemButtonClicked(MouseEvent mouseEvent) {
        Sale sale = saleTable.getSelectionModel().getSelectedItem();
        List<String> choices = new ArrayList<>(saleController.getProductNames());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Adicionar novo item");
        dialog.setContentText("Escolha o item");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            saleController.addSaleItem(sale, s, 1.0);
            refresh(saleTable.getSelectionModel().getSelectedIndex());
        });
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        saleTable.refresh();
    }

    private void refresh(int index) {
        saleTable.getItems().setAll(saleController.getSales());

        if (index != -1) {
            saleTable.getSelectionModel().select(index);
            saleTable.refresh();
            saleItemTable.getItems().setAll(saleTable.getSelectionModel().getSelectedItem().getSaleItemList());
            saleItemTable.refresh();
        } else {
            saleTable.refresh();
            saleItemTable.getItems().clear();
            saleItemTable.refresh();
        }
    }

    public void refresh() {
        refresh(-1);
    }

    public void confirmClicked(ActionEvent actionEvent) {
        Sale s = saleTable.getSelectionModel().getSelectedItem();

        if (s != null) {
            try {
                saleController.confirmSale(s);
                refresh(-1);
            } catch (IllegalArgumentException ex) {
                displayErrorMessage(ex.getMessage());
            }
        }

    }
}
