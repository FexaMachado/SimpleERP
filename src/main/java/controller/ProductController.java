package controller;

import domain.Product;
import repository.ProductRepository;
import ui.Main;
import util.DataRefresher;

import java.util.List;

public class ProductController {

    private ProductRepository repo;
    private ItemController itemController;

    public ProductController() {
        this.repo = new ProductRepository(Main.getEntityManagerFactory());
        this.itemController = new ItemController();
    }

    public List<Product> getProducts() {
        return repo.getAll();
    }

    public void deleteProduct(Product p){
        repo.delete(p);
    }

    public void changeProductPrice(Product p, double price) {
        p.setPrice(price);
        repo.update(p);
    }

    public void changeProductName(Product p, String name) {
        String oldName = p.getName();
        p.setName(name);
        try {
            repo.update(p);
            DataRefresher.fireEvent(DataRefresher.Type.PRODUCT);
        } catch (Exception e) {
            p.setName(oldName);
            throw new IllegalArgumentException("Não pode haver produtos com o mesmo nome");
        }

    }

    public void changeProductCost(Product p, double cost) {
        p.setCost(cost);
        repo.update(p);
    }

    public void activateProduct(Product p) {
        p.setActivated(true);
        repo.update(p);
        itemController.createItem(p);
    }

    public void deactivateProduct(Product p) {
        p.setActivated(false);
        repo.update(p);
        itemController.deleteItem(p);
    }

    public void addProduct(Product p) {
        repo.add(p);
        itemController.createItem(p);
    }
}

