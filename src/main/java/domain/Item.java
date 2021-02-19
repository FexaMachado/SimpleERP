package domain;

import javax.persistence.*;

@Entity
@Table(name = "item", uniqueConstraints = {
        @UniqueConstraint(name = "myconst", columnNames = {"product_id"})
})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
    private double stock;
    private double reserved;

    protected Item() {
    }

    public Item(Product p) {
        this.product = p;
        this.stock = 0.0;
        this.reserved = 0.0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        if (stock >= 0.0) {
            this.stock = stock;
        } else {
            throw new IllegalArgumentException("Stock não pode ser negativo");
        }

    }

    public double getReserved() {
        return reserved;
    }

    public void setReserved(double reserved) {
        if (reserved >= 0.0) {
            this.reserved = reserved;
        } else {
            throw new IllegalArgumentException("Numero de itens reservados não pode ser negativo");
        }
    }

    public void addReserved(double reserved) {
        if (reserved >= 0.0) {
            this.reserved += reserved;
        } else {
            throw new IllegalArgumentException("Numero de itens reservados não pode ser negativo");
        }
    }

    public void removeReserved(double reserved) {
        if (reserved >= 0.0) {
            double result = this.reserved - reserved;
            if (result >= 0.0) {
                this.reserved = result;
            } else {
                throw new IllegalArgumentException("Nao e possivel remover o numero indicado de itens reservados");
            }

        } else {
            throw new IllegalArgumentException("Numero de itens reservados não pode ser negativo");
        }
    }

    public void removeStock(double stock) {
        if (stock >= 0.0) {
            double result = this.stock - stock;
            if (result >= 0.0) {
                this.stock = result;
            } else {
                throw new IllegalArgumentException("Nao e possivel remover o numero indicado de itens em stock");
            }
        } else {
            throw new IllegalArgumentException("Nao e possivel remover um numero negativo de stock");
        }
    }

    public void addStock(double stock) {
        if (stock >= 0.0) {
            this.stock += stock;
        } else {
            throw new IllegalArgumentException("Nao e possivel adicionar um numero negativo de stock");
        }
    }

    public boolean canRemoveStock(double stock){
        return !(this.stock - stock < 0);
    }
}
