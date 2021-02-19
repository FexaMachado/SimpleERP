package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddDialog extends Dialog<List<String>> {

    private List<TextField> results;

    public AddDialog(String title,String header, List<String> labels) {
        this.results = new ArrayList<>();
        this.setTitle("Adicionar produto");
        this.setHeaderText("Preencha os campos abaixo");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int i = 0;
        for (String s : labels) {
            grid.add(new Label(s), 0, i);
            TextField textField = new TextField();
            results.add(textField);
            grid.add(textField, 1, i++);
        }

        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return results.stream().map(TextInputControl::getText).collect(Collectors.toList());
            }
            return null;
        });
    }
}
