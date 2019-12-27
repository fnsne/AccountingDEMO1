import java.time.LocalDate;

public class Accounting {
    private IBudgetRepo db;

    public Accounting(IBudgetRepo db) {
        this.db = db;
    }

    public double QueryBudget(LocalDate start, LocalDate end) {
        return db.GetAll().stream()
                .mapToDouble(budget -> intervalAmount(start, end, budget))
                .sum();
    }

    private double intervalAmount(LocalDate start, LocalDate end, Budget budget) {
        return new Period(start, end).overlappingDays(budget) * budget.getDailyAmount();
    }

}
