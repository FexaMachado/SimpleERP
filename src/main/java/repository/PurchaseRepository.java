package repository;

import domain.Purchase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PurchaseRepository extends BaseRepository<Purchase> {

    public PurchaseRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public List<Purchase> getNonConfirmedPurchases() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Purchase where confirmed = 0 ", Purchase.class);

        return (List<Purchase>) query.getResultList();
    }

    public List<Purchase> getConfirmedPurchasesOfMonth(Date date) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Query query = entityManager.createQuery("from Purchase where " +
                "MONTH(confirmedDate) =:arg1 AND YEAR(confirmedDate) =:arg2 ", Purchase.class);
        query.setParameter("arg1", localDate.getMonthValue());
        query.setParameter("arg2", localDate.getYear());

        return (List<Purchase>) query.getResultList();
    }
}
