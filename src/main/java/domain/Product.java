package domain;

import javax.persistence.*;

@Entity
@Table( name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String name;
    private double price;
    private double cost;
    private boolean activated;

    protected Product() {}

    public Product(String name, double price,double cost) {
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.activated = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        if(name != null && !name.isEmpty()){
            this.name = name;
        }else{
            throw new IllegalArgumentException("Nome não pode estar vazio.");
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
