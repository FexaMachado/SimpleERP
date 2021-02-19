package ui;

import controller.DashboardController;
import domain.BalanceHistory;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import util.DataRefreshable;
import util.DataRefresher;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Dashboard implements DataRefreshable {
    @FXML
    public StackedBarChart<String, Number> monthlyBalance;
    @FXML
    public PieChart topClients;
    @FXML
    public PieChart mostSoldProducts;

    @FXML
    public AreaChart allBalance;
    @FXML
    public NumberAxis yAxisAllBalance;

    private final DashboardController dashboardController = new DashboardController();


    public void initialize() {
        DataRefresher.addListener(DataRefresher.Type.TRANSACTION, this);
        DataRefresher.addListener(DataRefresher.Type.CONTACT, this);

        monthlyBalance.setTitle("Balanco Mensal");
        allBalance.setTitle("Balanco Global");
        topClients.setTitle("Melhores Clientes do Mes");
        mostSoldProducts.setTitle("Produtos mais vendidos do Mes");

        allBalance.setAnimated(false);
        topClients.setLabelsVisible(false);
        mostSoldProducts.setLabelsVisible(false);

        populateMonthlyBalance(true);
        populateTopClients();
        populateMostSoldProducts();
        populateAllBalance();

        addContextMenuToAllBalance();
        addContextMenuToMonthlyBalance();

        monthlyBalance.setAnimated(false);
        monthlyBalance.setLegendVisible(false);
    }

    private void populateMonthlyBalance(boolean currentMonth) {
        Double cost;
        Double profit;

        if (currentMonth) {
            cost = dashboardController.getCurrentMonthCosts();
            profit = dashboardController.getCurrentMonthProfit();
        } else {
            cost = dashboardController.getLastMonthCosts();
            profit = dashboardController.getLastMonthProfit();
        }


        XYChart.Series<String, Number> costSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();

        costSeries.getData().add(new XYChart.Data<>("Custos", cost));
        profitSeries.getData().add(new XYChart.Data<>("Vendas", profit));

        monthlyBalance.getData().clear();
        monthlyBalance.getData().add(costSeries);
        monthlyBalance.getData().add(profitSeries);


        for (Node n : monthlyBalance.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: red;");
        }
        for (Node n : monthlyBalance.lookupAll(".default-color1.chart-bar")) {
            n.setStyle("-fx-bar-fill: green;");
        }

        costSeries.getData().forEach(stringDoubleData -> {
            Tooltip.install(stringDoubleData.getNode(), new Tooltip(stringDoubleData.getXValue() + ":\n" +
                    stringDoubleData.getYValue()));
        });
        profitSeries.getData().forEach(stringDoubleData -> {
            Tooltip.install(stringDoubleData.getNode(), new Tooltip(stringDoubleData.getXValue() + ":\n" +
                    stringDoubleData.getYValue()));
        });

    }

    private void populateTopClients() {
        List<Map.Entry<String, Double>> data = dashboardController.getCurrentMonthClientsSales();
        topClients.getData().clear();

        data.forEach(stringDoubleEntry -> {
            topClients.getData().add(new PieChart.Data(stringDoubleEntry.getKey(), stringDoubleEntry.getValue()));
        });

        topClients.getData().forEach(data1 ->
                data1.nameProperty().bind(
                        Bindings.concat(data1.getName(), " : ", data1.pieValueProperty(), " \u20ac"))
        );
    }

    private void populateMostSoldProducts() {
        List<Map.Entry<String, Double>> data = dashboardController.getCurrentMonthMostSoldProducts();
        mostSoldProducts.getData().clear();

        data.forEach(stringDoubleEntry -> {
            mostSoldProducts.getData().add(new PieChart.Data(stringDoubleEntry.getKey(), stringDoubleEntry.getValue()));
        });

        mostSoldProducts.getData().forEach(data1 ->
                data1.nameProperty().bind(
                        Bindings.concat(data1.getName(), " : ", data1.pieValueProperty(), " \u20ac"))
        );

    }

    private void populateAllBalance() {
        allBalance.getData().clear();

        List<BalanceHistory> balanceHistories = dashboardController.getBalanceHistory();


        XYChart.Series<String, Double> costSeries = new XYChart.Series<>();
        costSeries.setName("Custos");

        XYChart.Series<String, Double> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Lucro bruto");

        balanceHistories.forEach(balanceHistory -> {
            costSeries.getData().add(new XYChart.Data<>(balanceHistory.toString(), balanceHistory.getCosts()));
            profitSeries.getData().add(new XYChart.Data<>(balanceHistory.toString(), balanceHistory.getProfit()));
        });

        allBalance.getData().add(costSeries);
        allBalance.getData().add(profitSeries);


        for (Node n : allBalance.lookupAll(".default-color0.chart-series-area-line")) {
            n.setStyle("-fx-stroke: red;");//red
        }
        for (Node n : allBalance.lookupAll(".default-color1.chart-series-area-line")) {
            n.setStyle("-fx-stroke: green;");//green
        }
    }

    private void addContextMenuToAllBalance() {
        final MenuItem addItem = new MenuItem("Add");
        addItem.setOnAction(event -> {
            ArrayList<String> labels = new ArrayList<>();
            labels.add("Mes");
            labels.add("Ano");
            labels.add("Custo");
            labels.add("Lucro bruto");

            AddDialog dialog = new AddDialog("Adicionar novos dados", "Preencha os campos abaixo", labels);

            Optional<List<String>> results = dialog.showAndWait();

            results.ifPresent(r -> {
                try {
                    dashboardController.addBalanceHistory(Integer.parseInt(r.get(0)),
                            Integer.parseInt(r.get(1)),
                            Double.parseDouble(r.get(3)),
                            Double.parseDouble(r.get(2)));
                    populateAllBalance();
                } catch (IllegalArgumentException e) {
                    displayErrorMessage(e.getMessage());
                }
            });
        });

        final MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(event -> {
            ArrayList<String> labels = new ArrayList<>();
            labels.add("Mes");
            labels.add("Ano");

            AddDialog dialog = new AddDialog("Qual os dados a retirar", "Preencha os campos abaixo", labels);

            Optional<List<String>> results = dialog.showAndWait();

            results.ifPresent(r -> {
                try {
                    dashboardController.removeBalanceHistory(Integer.parseInt(r.get(0)),
                            Integer.parseInt(r.get(1)));
                    populateAllBalance();
                } catch (IllegalArgumentException e) {
                    displayErrorMessage(e.getMessage());
                }
            });
        });

        final ContextMenu contextMenu = new ContextMenu(addItem, removeItem);

        allBalance.setOnMouseClicked(event -> {
            if (MouseButton.SECONDARY.equals(event.getButton())) {
                contextMenu.show(allBalance.getParent(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void addContextMenuToMonthlyBalance() {
        final MenuItem previous = new MenuItem("Mes anterior");
        final MenuItem current = new MenuItem("Mes atual");

        previous.setOnAction(event -> populateMonthlyBalance(false));
        current.setOnAction(event -> populateMonthlyBalance(true));

        ContextMenu contextMenu = new ContextMenu(previous, current);

        monthlyBalance.setOnMouseClicked(event -> {
            if (MouseButton.SECONDARY.equals(event.getButton())) {
                contextMenu.show(monthlyBalance.getParent(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    public void refresh() {
        populateMonthlyBalance(true);
        populateAllBalance();
        populateMostSoldProducts();
        populateTopClients();
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
