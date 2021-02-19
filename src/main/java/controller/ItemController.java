package controller;

import domain.Item;
import domain.Product;
import repository.ItemRepository;
import ui.Main;
import util.DataRefresher;

import java.util.List;

public class ItemController {

    private ItemRepository repo;

    public ItemController() {
        this.repo = new ItemRepository(Main.getEntityManagerFactory());
    }

    public List<Item> getItens() {
        return repo.getAll();
    }

    public void deleteItem(Product p){
        Item i = repo.findItemByProduct(p);
        repo.delete(i);
        DataRefresher.fireEvent(DataRefresher.Type.PRODUCT);
    }

    public void changeItemStock(Item i, double stock) {
        i.setStock(stock);
        repo.update(i);
    }

    public void changeItemReserved(Item i, double reserved){
        i.setReserved(reserved);
        repo.update(i);
    }

    public void createItem(Product p) {
        Item i = new Item(p);
        repo.add(i);
        DataRefresher.fireEvent(DataRefresher.Type.PRODUCT);
    }
}
