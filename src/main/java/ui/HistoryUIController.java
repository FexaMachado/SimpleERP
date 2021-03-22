package ui;

import controller.HistoryController;
import controller.PurchaseController;
import controller.SaleController;
import domain.Sale;
import domain.SaleItem;
import domain.Transaction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DoubleStringConverter;
import util.DataRefresher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HistoryUIController {

    @FXML
    public TableView<Transaction> transactionTable;
    @FXML
    public TableColumn<Transaction, String> contactColumn;
    @FXML
    public TableColumn<Transaction, Double> priceColumn;
    @FXML
    public TableColumn<Transaction, String> dateColumn;

    @FXML
    public TableView<SaleItem> transactionItemTable;
    @FXML
    public TableColumn<SaleItem, String> itemColumn;
    @FXML
    public TableColumn<SaleItem, Double> itemAmountColumn;
    @FXML
    public TableColumn<SaleItem, Double> itemPriceColumn;
    @FXML
    public TableColumn<SaleItem, Double> itemTotalPriceColumn;

    private final HistoryController historyController = new HistoryController();


    public void initialize() {
        transactionTable.setEditable(true);
        transactionItemTable.setEditable(true);

        contactColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(param.getValue().getContact().getName());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        dateColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(param.getValue().getConfirmedDate().toString());
            } else {
                return new SimpleStringProperty("<no date>");
            }
        });

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
    }

    public void searchSalesClicked(ActionEvent actionEvent) {
        List<String> labels = new ArrayList<>();
        labels.add("Mes (numero)");
        labels.add("Ano");

        AddDialog dialog = new AddDialog("Procurar Vendas", "Preencha os campos abaixo", labels);

        Optional<List<String>> results = dialog.showAndWait();

        results.ifPresent(strings -> {
            try {
                transactionTable.getItems().setAll(historyController.getSalesOfDate(strings.get(0), strings.get(1)));
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });
    }

    public void searchPurchasesClicked(ActionEvent actionEvent) {
        List<String> labels = new ArrayList<>();
        labels.add("Mes (numero)");
        labels.add("Ano");

        AddDialog dialog = new AddDialog("Procurar Compras", "Preencha os campos abaixo", labels);

        Optional<List<String>> results = dialog.showAndWait();

        results.ifPresent(strings -> {
            try {
                transactionTable.getItems().setAll(historyController.getPurchasesOfDate(strings.get(0), strings.get(1)));
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        transactionTable.refresh();
    }


    public void transactionTableClicked(MouseEvent mouseEvent) {
        Transaction transaction = transactionTable.getSelectionModel().getSelectedItem();

        if (transaction != null) {
            transactionItemTable.getItems().setAll(transaction.getSaleItemList());
        }

        transactionItemTable.getSelectionModel().clearSelection();
    }

    public void deleteClicked(ActionEvent actionEvent) {
        Transaction transaction = transactionTable.getSelectionModel().getSelectedItem();
        historyController.deleteTransaction(transaction);
        transactionTable.getItems().remove(transaction);
        transactionTable.getSelectionModel().clearSelection();
        transactionItemTable.getItems().clear();

        DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
    }
}
