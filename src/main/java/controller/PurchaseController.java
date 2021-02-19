package controller;

import domain.*;
import repository.ContactRepository;
import repository.ItemRepository;
import repository.ProductRepository;
import repository.PurchaseRepository;
import ui.Main;
import util.DataRefresher;

import java.util.List;
import java.util.stream.Collectors;

public class PurchaseController {

    private final PurchaseRepository purchaseRepository;
    private final ContactRepository contactRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;

    public PurchaseController() {
        this.purchaseRepository = new PurchaseRepository(Main.getEntityManagerFactory());
        this.contactRepository = new ContactRepository(Main.getEntityManagerFactory());
        this.itemRepository = new ItemRepository(Main.getEntityManagerFactory());
        this.productRepository = new ProductRepository(Main.getEntityManagerFactory());
    }

    public List<Purchase> getPurchases() {
        return purchaseRepository.getNonConfirmedPurchases();
    }

    public List<String> getContactNames() {
        return contactRepository.getAllProviders().stream().map(Contact::getName).collect(Collectors.toList());
    }

    public List<String> getProductNames() {
        return productRepository.getAllActivatedProducts().stream().map(Product::getName).collect(Collectors.toList());
    }

    public void addPurchase(String contact) {
        Contact c;
        try {
            c = contactRepository.getByName(contact);
            purchaseRepository.add(new Purchase(c));
        } catch (Exception e) {
            throw new IllegalArgumentException("Não ha contacto com esse nome");
        }
    }

    public void deletePurchase(Purchase purchase) {
        purchaseRepository.delete(purchase);
    }

    public void changeSaleItemQuantity(Purchase purchase, SaleItem saleItem, double qtd) {
        SaleItem newSaleItem = new SaleItem(saleItem.getName(), saleItem.getPrice(), qtd);

        purchase.changeSaleItem(saleItem, newSaleItem);
        purchaseRepository.update(purchase);
    }

    public void changeSaleItemPrice(Purchase purchase, SaleItem saleItem, double price) {
        SaleItem newSaleItem = new SaleItem(saleItem.getName(), price, saleItem.getNumber());

        purchase.changeSaleItem(saleItem, newSaleItem);
        purchaseRepository.update(purchase);
    }

    public void addSaleItem(Purchase purchase, String itemName, double qtd) {
        Product product = productRepository.findProductByName(itemName);
        SaleItem saleItem = new SaleItem(product.getName(),product.getCost(), qtd);
        purchase.addSaleItem(saleItem);
        purchaseRepository.update(purchase);
    }

    public void deleteSaleItem(Purchase purchase, SaleItem saleItem) {
        purchase.removeSaleItem(saleItem);
        purchaseRepository.update(purchase);
    }

    public void confirmPurchase(Purchase purchase) {
        purchase.confirmed();
        purchaseRepository.update(purchase);

        purchase.getSaleItemList().forEach(saleItem -> {
            Product product = productRepository.findProductByName(saleItem.getName());
            Item item = itemRepository.findItemByProduct(product);
            item.addStock(saleItem.getNumber());
            itemRepository.update(item);
        });

        DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
    }


}
