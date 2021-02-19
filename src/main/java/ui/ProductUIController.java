package ui;

import controller.ProductController;
import domain.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DoubleStringConverter;
import util.DataRefreshable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ProductUIController implements DataRefreshable {

    @FXML
    public Button deleteButton;
    @FXML
    public CheckBox toogleDeactivated;
    @FXML
    public Button activateButton;

    @FXML
    public TableView<Product> productTable;
    @FXML
    public TableColumn<Product, String> nameColumn;
    @FXML
    public TableColumn<Product, Double> priceColumn;
    @FXML
    public TableColumn<Product, Double> costColumn;


    private List<Product> products;

    private final ProductController c = new ProductController();

    public void initialize() {
        deleteButton.setVisible(false); //Product deletion has been discontinued

        productTable.setEditable(true);
        productTable.setRowFactory(param -> new TableRow<Product>() {
            @Override
            public void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (!item.isActivated()) {
                    setStyle("-fx-background-color: lightgray");
                } else {
                    setStyle("");
                }
            }
        });


        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));


        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(t -> {
            try {
                c.changeProductName(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });

        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(t -> {
            try {
                c.changeProductPrice(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });

        costColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        costColumn.setOnEditCommit(t -> {
            try {
                c.changeProductCost(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });


        this.products = c.getProducts();

        productTable.getItems().addAll(this.products.stream().filter(Product::isActivated).collect(Collectors.toList()));
    }


    public void newButtonClicked(MouseEvent mouseEvent) {

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Nome");
        labels.add("Preço");
        labels.add("Custo");

        AddDialog dialog = new AddDialog("Adicionar produto", "Preencha os campos abaixo", labels);

        Optional<List<String>> results = dialog.showAndWait();

        results.ifPresent(r -> {
            Product p = new Product(r.get(0), Double.parseDouble(r.get(1)), Double.parseDouble(r.get(2)));

            try {
                c.addProduct(p);
                this.products.add(p);
                productTable.getItems().add(p);
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });
    }

    public void toggleDeactivated(ActionEvent actionEvent) {
        if (toogleDeactivated.isSelected()) {
            productTable.getItems().addAll(this.products.stream().filter(product -> !product.isActivated()).collect(Collectors.toList()));
        } else {
            productTable.getItems().setAll(this.products.stream().filter(Product::isActivated).collect(Collectors.toList()));
        }
    }

    public void tableClicked(MouseEvent mouseEvent) {
        Product p = productTable.getSelectionModel().getSelectedItem();

        if (p != null) {
            if (p.isActivated()) {
                activateButton.setText("Desativar");
                deleteButton.setDisable(true);
            } else {
                activateButton.setText("Ativar");
                deleteButton.setDisable(false);
            }
        }
    }

    public void activateClicked(ActionEvent actionEvent) {
        Product p = productTable.getSelectionModel().getSelectedItem();

        if (activateButton.getText().equals("Ativar")) {

            try {
                c.activateProduct(p);
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }

        } else {
            try {
                c.deactivateProduct(p);
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        refresh();
    }

    public void deleteClicked(ActionEvent actionEvent) {
        Product p = productTable.getSelectionModel().getSelectedItem();

        try {
            c.deleteProduct(p);
            this.products.remove(p);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        refresh();
    }

    public void refresh() {
        productTable.getItems().clear();

        if (toogleDeactivated.isSelected()) {
            productTable.getItems().setAll(this.products);
        } else {
            productTable.getItems().setAll(this.products.stream().filter(Product::isActivated).collect(Collectors.toList()));
        }

        productTable.layout();
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        productTable.refresh();
    }
}
