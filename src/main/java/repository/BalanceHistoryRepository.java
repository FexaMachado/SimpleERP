package repository;

import domain.BalanceHistory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class BalanceHistoryRepository extends BaseRepository<BalanceHistory> {

    public BalanceHistoryRepository(EntityManagerFactory entityManagerFactory){
        super(entityManagerFactory);
    }

    public BalanceHistory getBalanceHistoryOfDate(int month,int year){
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("from BalanceHistory where month =:arg1 and year =:arg2", BalanceHistory.class);
        query.setParameter("arg1", month);
        query.setParameter("arg2", year);
        query.setMaxResults(1);

        return (BalanceHistory) query.getSingleResult();
    }

}
