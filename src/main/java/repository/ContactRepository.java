package repository;

import domain.Contact;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class ContactRepository extends BaseRepository<Contact> {

    public ContactRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public Contact getByName(String contact) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Contact where name =:arg1", Contact.class);
        query.setParameter("arg1", contact);
        query.setMaxResults(1);

        return (Contact) query.getSingleResult();
    }

    public List<Contact> getAllProviders() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Contact where provider = 1", Contact.class);

        return query.getResultList();
    }
}
