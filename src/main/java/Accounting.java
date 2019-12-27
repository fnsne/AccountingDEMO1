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
            double diff = overlappingDays(budget, new Period(start, end));
            double budgetAmount = diff * budget.getDailyAmount();
            sum += budgetAmount;
        }
        return sum;

    }

    private double overlappingDays(Budget budget, Period period) {
        double diff;
        LocalDate start1 = period.getStart();
        LocalDate end1 = period.getEnd();
        if (start1.isAfter(budget.lastDay()) || end1.isBefore(budget.firstDay())) {
            diff = 0;
        } else {
            Period period1 = period;
            Period period2 = budget.createPeriod();
            LocalDate periodStartDay;
            LocalDate periodEndDay;
            periodStartDay = period1.getStart().isAfter(period2.getStart()) ? period1.getStart() : period2.getStart();
            periodEndDay = period1.getEnd().isBefore(period2.getEnd()) ? period1.getEnd() : period2.getEnd();
            diff = new Period(periodStartDay, periodEndDay).getDays();
        }
        return diff;
    }

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

}
