package controller;

import domain.*;
import javafx.util.Pair;
import repository.ContactRepository;
import repository.ItemRepository;
import repository.ProductRepository;
import repository.SaleRepository;
import ui.Main;
import util.DataRefresher;

import java.util.List;
import java.util.stream.Collectors;

public class SaleController {

    private final SaleRepository repo;
    private final ProductRepository productRepository;
    private final ContactRepository contactRepository;
    private final ItemRepository itemRepository;

    public SaleController() {
        this.repo = new SaleRepository(Main.getEntityManagerFactory());
        this.contactRepository = new ContactRepository(Main.getEntityManagerFactory());
        this.productRepository = new ProductRepository(Main.getEntityManagerFactory());
        this.itemRepository = new ItemRepository(Main.getEntityManagerFactory());
    }

    public List<Sale> getSales() {
        return repo.getNonConfirmedSales();
    }

    public List<String> getContactNames() {
        return contactRepository.getAll().stream().map(Contact::getName).collect(Collectors.toList());
    }

    public List<String> getProductNames() {
        return productRepository.getAllActivatedProducts().stream().map(Product::getName).collect(Collectors.toList());
    }

    public void addSale(String contact) {
        Contact c;
        try {
            c = contactRepository.getByName(contact);
            repo.add(new Sale(c));
        } catch (Exception e) {
            throw new IllegalArgumentException("Não ha contacto com esse nome");
        }
    }

    public void deleteSale(Sale s) {
        s.getSaleItemList().forEach(this::removeReserved);
        repo.delete(s);
        DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
    }

    public void changeSaleItemQuantity(Sale s, SaleItem itemName, double qtd) {
        SaleItem saleItem = new SaleItem(itemName.getName(), itemName.getPrice(), qtd);

        s.changeSaleItem(itemName, saleItem);
        repo.update(s);

        //Reservar quantidade
        Product p = productRepository.findProductByName(itemName.getName());
        Item item = itemRepository.findItemByProduct(p);
        double dif = saleItem.getNumber() - itemName.getNumber();

        if (dif >= 0) {
            item.addReserved(dif);
        } else {
            dif *= -1;
            item.removeReserved(dif);
        }

        itemRepository.update(item);
        DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
    }

    public void changeSaleItemPrice(Sale s, SaleItem itemName, double price) {
        SaleItem saleItem = new SaleItem(itemName.getName(), price, itemName.getNumber());

        s.changeSaleItem(itemName, saleItem);
        repo.update(s);
    }

    public void addSaleItem(Sale s, String itemName, double qtd) {
        Product p = productRepository.findProductByName(itemName);
        SaleItem saleItem = new SaleItem(p, qtd);
        s.addSaleItem(saleItem);
        repo.update(s);

        //Reservar item
        Item item = itemRepository.findItemByProduct(p);
        item.addReserved(saleItem.getNumber());
        itemRepository.update(item);
        DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
    }

    public void deleteSaleItem(Sale s, SaleItem itemName) {
        s.removeSaleItem(itemName);
        repo.update(s);

        removeReserved(itemName);
        DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
    }

    private void removeReserved(SaleItem saleItem) {
        Product product = productRepository.findProductByName(saleItem.getName());
        Item item = itemRepository.findItemByProduct(product);
        item.removeReserved(saleItem.getNumber());
        itemRepository.update(item);
    }

    private void removeStock(SaleItem saleItem) {
        Product product = productRepository.findProductByName(saleItem.getName());
        Item item = itemRepository.findItemByProduct(product);
        item.removeStock(saleItem.getNumber());
        item.removeReserved(saleItem.getNumber());
        itemRepository.update(item);
    }

    public void confirmSale(Sale s) {

        List<Pair<SaleItem, Item>> list = s.getSaleItemList().stream().map(saleItem -> {
            Product product = productRepository.findProductByName(saleItem.getName());
            Item item = itemRepository.findItemByProduct(product);
            return new Pair<>(saleItem, item);
        }).filter(saleItemItemPair -> saleItemItemPair.getValue().canRemoveStock(saleItemItemPair.getKey().getNumber())).collect(Collectors.toList());

        if (list.size() != s.getSaleItemList().size()) {
            throw new IllegalArgumentException("Nao ha items suficientes para confirmar a encomenda");
        } else {
            s.getSaleItemList().forEach(this::removeStock);

            s.confirmed();
            repo.update(s);
            DataRefresher.fireEvent(DataRefresher.Type.TRANSACTION);
        }

    }
}
