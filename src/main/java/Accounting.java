import java.time.LocalDate;

public class Accounting {
    private IBudgetRepo db;

    public Accounting(IBudgetRepo db) {
        this.db = db;
    }

    public double QueryBudget(LocalDate start, LocalDate end) {
        return db.GetAll().stream()
                .mapToDouble(budget -> budget.intervalAmount(new Period(start, end)))
                .sum();
    }

}
