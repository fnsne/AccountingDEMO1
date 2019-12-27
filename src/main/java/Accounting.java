import java.time.LocalDate;

public class Accounting {
    private IBudgetRepo db;

    public Accounting(IBudgetRepo db) {
        this.db = db;
    }

    public double QueryBudget(LocalDate start, LocalDate end) {
        double sum = 0;
        for (Budget budget : db.GetAll()) {
            double diff = new Period(start, end).overlappingDays(budget);
            double budgetAmount = diff * budget.getDailyAmount();
            sum += budgetAmount;
        }
        return sum;
    }

}
