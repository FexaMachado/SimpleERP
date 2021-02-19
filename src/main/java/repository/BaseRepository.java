package repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseRepository<T> {

    EntityManagerFactory entityManagerFactory;

    public BaseRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public T getByID(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Class<T> type = ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        T obj = entityManager.find(type, id);
        entityManager.close();
        return obj;
    }

    public void add(T obj) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(obj);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void update(T obj) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.merge(obj);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<T> getAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Class<T> type = ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);

        Query query = entityManager.createQuery("SELECT e FROM " + type.getSimpleName() + " e", type);
        List<T> list = query.getResultList();

        entityManager.close();

        return list;
    }

    public void delete(T obj) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.remove( entityManager.contains(obj) ? obj : entityManager.merge(obj));

        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
