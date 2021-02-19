package ui;

import controller.PurchaseController;
import domain.Purchase;
import domain.SaleItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
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

public class PurchaseUIController implements DataRefreshable {

    public TableView<Purchase> purchaseTable;
    public TableColumn<Purchase, String> contactColumn;
    public TableColumn<Purchase, Double> priceColumn;

    public Button newItemButton;

    public TableView<SaleItem> saleItemTable;
    public TableColumn<SaleItem, Double> itemColumn;
    public TableColumn<SaleItem, Double> itemTotalPriceColumn;
    public TableColumn<SaleItem, Double> itemAmountColumn;
    public TableColumn<SaleItem, Double> itemPriceColumn;

    private final PurchaseController purchaseController = new PurchaseController();

    public void initialize() {
        DataRefresher.addListener(DataRefresher.Type.CONTACT,this);
        DataRefresher.addListener(DataRefresher.Type.PRODUCT,this);

        purchaseTable.setEditable(true);
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
            Purchase s = purchaseTable.getSelectionModel().getSelectedItem();
            if (s != null) {
                try {
                    purchaseController.changeSaleItemQuantity(s, t.getRowValue(), t.getNewValue());
                    refresh(purchaseTable.getSelectionModel().getSelectedIndex());
                } catch (IllegalArgumentException e) {
                    displayErrorMessage(e.getMessage());
                }
            }
        });

        itemPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        itemPriceColumn.setOnEditCommit(t -> {
            Purchase s = purchaseTable.getSelectionModel().getSelectedItem();
            if (s != null) {
                try {
                    purchaseController.changeSaleItemPrice(s, t.getRowValue(), t.getNewValue());
                    refresh(purchaseTable.getSelectionModel().getSelectedIndex());
                } catch (IllegalArgumentException e) {
                    displayErrorMessage(e.getMessage());
                }
            }
        });

        purchaseTable.getItems().setAll(purchaseController.getPurchases());
        newItemButton.setDisable(true);
    }


    public void confirmClicked(ActionEvent actionEvent) {
        Purchase p = purchaseTable.getSelectionModel().getSelectedItem();

        if(p != null){
            purchaseController.confirmPurchase(p);
            refresh(-1);
        }
    }

    public void deleteClicked(ActionEvent actionEvent) {
        Purchase p = purchaseTable.getSelectionModel().getSelectedItem();
        SaleItem si = saleItemTable.getSelectionModel().getSelectedItem();

        if (p != null) {
            if (si == null) {
                purchaseController.deletePurchase(p);
                refresh(-1);
            } else {
                purchaseController.deleteSaleItem(p, si);
                refresh(purchaseTable.getSelectionModel().getSelectedIndex());
            }
        }
    }

    public void newPurchaseButtonClicked(MouseEvent mouseEvent) {
        List<String> choices = new ArrayList<>(purchaseController.getContactNames());

        if (choices.size() <= 0) {
            displayErrorMessage("Nao ha fornecedores");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Criar Compra");
        dialog.setContentText("Escolha o fornecedor");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            purchaseController.addPurchase(s);
            refresh(-1);
        });
    }

    public void newItemButtonClicked(MouseEvent mouseEvent) {
        Purchase p = purchaseTable.getSelectionModel().getSelectedItem();
        List<String> choices = new ArrayList<>(purchaseController.getProductNames());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Adicionar novo item");
        dialog.setContentText("Escolha o item");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            purchaseController.addSaleItem(p, s, 1.0);
            refresh(purchaseTable.getSelectionModel().getSelectedIndex());
        });
    }

    public void purchaseTableClicked(MouseEvent mouseEvent) {
        Purchase p = purchaseTable.getSelectionModel().getSelectedItem();

        if (p == null) {
            newItemButton.setDisable(true);
        } else {
            newItemButton.setDisable(false);

            saleItemTable.getItems().setAll(p.getSaleItemList());
        }

        saleItemTable.getSelectionModel().clearSelection();

    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        purchaseTable.refresh();
    }

    private void refresh(int index) {
        purchaseTable.getItems().setAll(purchaseController.getPurchases());

        if (index != -1) {
            purchaseTable.getSelectionModel().select(index);
            purchaseTable.refresh();
            saleItemTable.getItems().setAll(purchaseTable.getSelectionModel().getSelectedItem().getSaleItemList());
            saleItemTable.refresh();
        } else {
            purchaseTable.refresh();
            saleItemTable.getItems().clear();
            saleItemTable.refresh();
        }
    }

    public void refresh() {
        refresh(-1);
    }
}
