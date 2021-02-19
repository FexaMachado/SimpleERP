package domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SaleItem {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private double number;

    protected SaleItem(){}

    public SaleItem(Product p, double number){
        this.name = p.getName();
        this.price = p.getPrice();
        this.number = number;
    }

    public SaleItem(String name, double price, double number){
        this.name = name;
        this.price = price;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public double getNumber() {
        return number;
    }
}
