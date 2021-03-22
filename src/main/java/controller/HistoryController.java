package controller;

import domain.Purchase;
import domain.Sale;
import domain.Transaction;
import repository.PurchaseRepository;
import repository.SaleRepository;
import ui.Main;
import util.DateUtils;

import java.util.Date;
import java.util.List;

public class HistoryController {

    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;

    public HistoryController() {
        this.saleRepository = new SaleRepository(Main.getEntityManagerFactory());
        this.purchaseRepository = new PurchaseRepository(Main.getEntityManagerFactory());
    }

    public List<Sale> getSalesOfDate(String month, String year) {
        Date date = DateUtils.fromString(month, year);
        return saleRepository.getConfirmedSalesOfMonth(date);
    }

    public List<Purchase> getPurchasesOfDate(String month, String year) {
        Date date = DateUtils.fromString(month, year);
        return purchaseRepository.getConfirmedPurchasesOfMonth(date);
    }

    public void deleteTransaction(Transaction transaction) {
        if (transaction instanceof Sale) {
            saleRepository.delete((Sale) transaction);
        } else {
            purchaseRepository.delete((Purchase) transaction);
        }
    }
}
