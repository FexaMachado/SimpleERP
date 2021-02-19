package repository;

import domain.Item;
import domain.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class ItemRepository extends BaseRepository<Item> {

    public ItemRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public Item findItemByProduct(Product id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Item where product =:arg1", Item.class);
        query.setParameter("arg1", id);
        query.setMaxResults(1);

        return (Item) query.getSingleResult();
    }
}
