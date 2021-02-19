package repository;

import domain.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class ProductRepository extends BaseRepository<Product> {

    public ProductRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public Product findProductByName(String name){
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Product where name =:arg1", Product.class);
        query.setParameter("arg1", name);
        query.setMaxResults(1);

        return (Product) query.getSingleResult();
    }

    public List<Product> getAllActivatedProducts(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Product where activated = 1", Product.class);

        return  query.getResultList();
    }
}
