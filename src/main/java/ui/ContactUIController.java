package ui;

import controller.ContactController;
import domain.Contact;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactUIController {
    @FXML
    public TableColumn<Contact, String> nameColumn;
    @FXML
    public TableColumn<Contact, String> addressColumn;
    @FXML
    public TableColumn<Contact, String> telephoneColumn;
    @FXML
    public TableColumn<Contact, String> emailColumn;
    @FXML
    public TableColumn<Contact, String> fornecedorColumn;

    @FXML
    public TableView<Contact> contactTable;


    private ContactController c = new ContactController();

    public void initialize() {
        contactTable.setEditable(true);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        fornecedorColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                if(param.getValue().isProvider()){
                    return new SimpleStringProperty("Sim");
                }else{
                    return new SimpleStringProperty("Nao");
                }

            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(t -> {
            try {
                c.changeContactName(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });

        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setOnEditCommit(t -> {
            try {
                c.changeContactAddress(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });

        telephoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        telephoneColumn.setOnEditCommit(t -> {
            try {
                c.changeContactTelephone(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });

        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(t -> {
            try {
                c.changeContactEmail(t.getRowValue(), t.getNewValue());
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });

        contactTable.getItems().setAll(c.getContacts());

    }

    public void deleteClicked(ActionEvent actionEvent) {
        Contact p = contactTable.getSelectionModel().getSelectedItem();

        try {
            c.deleteContact(p);
            contactTable.getItems().remove(p);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        contactTable.refresh();
    }

    public void newButtonClicked(MouseEvent mouseEvent) {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Nome");
        labels.add("Telefone");
        labels.add("Morada");
        labels.add("Email");

        AddDialog dialog = new AddDialog("Adicionar contacto", "Preencha os campos abaixo", labels);

        Optional<List<String>> results = dialog.showAndWait();

        results.ifPresent(r -> {
            Contact p = new Contact(r.get(0), r.get(1), r.get(2), r.get(3));

            try {
                c.addContact(p);
                contactTable.getItems().add(p);
            } catch (IllegalArgumentException e) {
                displayErrorMessage(e.getMessage());
            }
        });
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        contactTable.refresh();
    }


    public void providerClicked(MouseEvent mouseEvent) {
        Contact contact = contactTable.getSelectionModel().getSelectedItem();

        if(contact != null){
            c.changeContactProvider(contact);
            contactTable.refresh();
        }
    }
}
