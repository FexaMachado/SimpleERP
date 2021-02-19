package domain;

import util.DateUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public abstract class Transaction {

    @ManyToOne(fetch = FetchType.EAGER)
    private Contact contact;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    private List<SaleItem> saleItemList;
    private double totalPrice;

    private boolean confirmed;
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedDate;

    protected Transaction() {
    }

    public Transaction(Contact c) {
        this.contact = c;
        saleItemList = new ArrayList<>();
        totalPrice = 0.0;
        confirmed = false;
        confirmedDate = null;
    }

    public Contact getContact() {
        return contact;
    }

    public List<SaleItem> getSaleItemList() {
        return saleItemList;
    }

    public void addSaleItem(SaleItem saleItem) {
        saleItemList.add(saleItem);
        totalPrice += saleItem.getPrice() * saleItem.getNumber();
    }

    public void removeSaleItem(SaleItem saleItem) {
        saleItemList.remove(saleItem);
        totalPrice -= saleItem.getPrice() * saleItem.getNumber();
    }

    public void changeSaleItem(SaleItem oldItem, SaleItem newItem) {
        int pos = saleItemList.indexOf(oldItem);
        if (pos != -1) {
            saleItemList.set(pos, newItem);
        }
        totalPrice -= oldItem.getPrice() * oldItem.getNumber();
        totalPrice += newItem.getPrice() * newItem.getNumber();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void confirmed() {
        this.confirmed = true;
        this.confirmedDate = DateUtils.getCurrentDate();
    }

    public Date getConfirmedDate(){
        return confirmedDate;
    }
}
