package repository;

import domain.Sale;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class SaleRepository extends BaseRepository<Sale> {

    public SaleRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public List<Sale> getNonConfirmedSales() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from Sale where confirmed = 0 ", Sale.class);

        return (List<Sale>) query.getResultList();
    }

    public List<Sale> getConfirmedSalesOfMonth(Date date) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Query query = entityManager.createQuery("from Sale where " +
                "MONTH(confirmedDate) =:arg1 AND YEAR(confirmedDate) =:arg2 ", Sale.class);
        query.setParameter("arg1", localDate.getMonthValue());
        query.setParameter("arg2", localDate.getYear());

        return (List<Sale>) query.getResultList();
    }
}
