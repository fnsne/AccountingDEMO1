import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Accounting {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
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

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

}
