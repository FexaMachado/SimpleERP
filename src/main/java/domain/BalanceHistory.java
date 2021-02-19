package domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int month;
    private int year;
    private double profit;
    private double costs;

    protected BalanceHistory(){
    }

    public BalanceHistory(int month, int year, double profit, double costs) {
        this.month = month;
        this.year = year;
        this.profit = profit;
        this.costs = costs;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public double getProfit() {
        return profit;
    }

    public double getCosts() {
        return costs;
    }

    public String toString(){
        return "" + month + " / " + year;
    }
}
