package controller;

import domain.*;
import repository.BalanceHistoryRepository;
import repository.PurchaseRepository;
import repository.SaleRepository;
import ui.Main;
import util.DateUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DashboardController {

    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public DashboardController() {
        this.saleRepository = new SaleRepository(Main.getEntityManagerFactory());
        this.purchaseRepository = new PurchaseRepository(Main.getEntityManagerFactory());
        this.balanceHistoryRepository = new BalanceHistoryRepository(Main.getEntityManagerFactory());
    }

    public Double getCurrentMonthProfit() {
        Date currentDate = DateUtils.getCurrentDate();
        List<Sale> sales = saleRepository.getConfirmedSalesOfMonth(currentDate);

        return sumTransactions(sales);
    }

    public Double getCurrentMonthCosts() {
        Date currentDate = DateUtils.getCurrentDate();
        List<Purchase> purchases = purchaseRepository.getConfirmedPurchasesOfMonth(currentDate);

        return sumTransactions(purchases);
    }

    public Double getLastMonthProfit() {
        Date lastMonthDate = DateUtils.getLastMonthDate();
        List<Sale> sales = saleRepository.getConfirmedSalesOfMonth(lastMonthDate);

        return sumTransactions(sales);
    }

    public Double getLastMonthCosts() {
        Date lastMonthDate = DateUtils.getLastMonthDate();
        List<Purchase> purchases = purchaseRepository.getConfirmedPurchasesOfMonth(lastMonthDate);

        return sumTransactions(purchases);
    }

    private <T extends Transaction> Double sumTransactions(List<T> transactions) {
        AtomicReference<Double> result = new AtomicReference<>(0.0);

        transactions.stream().map(transaction -> {
            return transaction.getSaleItemList().stream().map(saleItem -> {
                return saleItem.getPrice() * saleItem.getNumber();
            }).reduce(Double::sum);
        }).forEach(aDouble -> {
            result.updateAndGet(v -> v + aDouble.orElse(0.0));
        });

        return result.get();
    }

    public List<Map.Entry<String, Double>> getCurrentMonthClientsSales() {
        Date currentDate = DateUtils.getCurrentDate();
        List<Sale> sales = saleRepository.getConfirmedSalesOfMonth(currentDate);

        Map<String, Double> map = sales.stream().collect(Collectors.groupingBy(sale -> sale.getContact().getName(), Collectors.summingDouble(Transaction::getTotalPrice)));

        List<Map.Entry<String, Double>> result = map.entrySet().stream().sorted(
                Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3).collect(Collectors.toList());

        Double otherProfit = sumTransactions(sales) - result.stream().map(Map.Entry::getValue).reduce(Double::sum).orElse(0.0);

        result.add(new AbstractMap.SimpleEntry<String, Double>("Outros", otherProfit));

        return result;
    }

    public List<Map.Entry<String, Double>> getCurrentMonthMostSoldProducts() {
        Date currentDate = DateUtils.getCurrentDate();
        List<Sale> sales = saleRepository.getConfirmedSalesOfMonth(currentDate);


        Map<String, Double> map = sales.stream().map(Transaction::getSaleItemList).flatMap(Collection::stream)
                .collect(Collectors.groupingBy(SaleItem::getName, Collectors.summingDouble(value -> value.getNumber() * value.getPrice())));

        return map.entrySet().stream().sorted(
                Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3).collect(Collectors.toList());

    }

    public List<BalanceHistory> getBalanceHistory() {
        return balanceHistoryRepository.getAll().stream().sorted(
                Comparator.comparingInt(BalanceHistory::getYear).thenComparingInt(BalanceHistory::getMonth)).collect(Collectors.toList());
    }

    public void addBalanceHistory(int month, int year, double profit, double cost) {
        BalanceHistory bh = new BalanceHistory(month, year, profit, cost);
        balanceHistoryRepository.add(bh);
    }

    public void removeBalanceHistory(int month, int year) {
        BalanceHistory balanceHistory = balanceHistoryRepository.getBalanceHistoryOfDate(month, year);
        balanceHistoryRepository.delete(balanceHistory);
    }
}
