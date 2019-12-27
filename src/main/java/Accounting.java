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
            double budgetAmount = 0;
            if (start.isAfter(budget.lastDay()) || end.isBefore(budget.firstDay())) {
            } else {
                Period period1 = new Period(start, end);
                Period period2 = budget.createPeriod();
                LocalDate periodStartDay;
                LocalDate periodEndDay;
                periodStartDay = period1.getStart().isAfter(period2.getStart()) ? period1.getStart() : period2.getStart();
                periodEndDay = period1.getEnd().isBefore(period2.getEnd()) ? period1.getEnd() : period2.getEnd();
                Period period = new Period(periodStartDay, periodEndDay);
                double diff = period.getDays();
                budgetAmount = diff * budget.getDailyAmount();
            }
            sum += budgetAmount;
        }
        return sum;

    }

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

}
