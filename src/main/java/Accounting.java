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
        if (period.getStart().isAfter(budget.lastDay()) || period.getEnd().isBefore(budget.firstDay())) {
            diff = 0;
        } else {
            LocalDate periodStartDay;
            LocalDate periodEndDay;
            periodStartDay = period.getStart().isAfter(budget.createPeriod().getStart())
                    ? period.getStart()
                    : budget.createPeriod().getStart();
            periodEndDay = period.getEnd().isBefore(budget.createPeriod().getEnd())
                    ? period.getEnd()
                    : budget.createPeriod().getEnd();
            diff = new Period(periodStartDay, periodEndDay).getDays();
        }
        return diff;
    }

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

}
