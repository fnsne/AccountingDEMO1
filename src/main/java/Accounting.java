import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Accounting {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
    private IBudgetRepo db;

    public Accounting(IBudgetRepo db) {
        this.db = db;
    }

    public double QueryBudget(LocalDate start, LocalDate end) {
        double sum = 0;
        for (Budget budget : getBudgets(start, end)) {
            Period period1 = new Period(start, end);
            Period period2 = budget.createPeriod();
            Period period = overlappingPeriod(period1, period2);
            double budgetAmount = budget.budgetAmountOfPeriod(period);
            sum += budgetAmount;
        }
        return sum;

    }

    private Period overlappingPeriod(Period period1, Period period2) {
        LocalDate periodStartDay;
        LocalDate periodEndDay;
        periodStartDay = period1.getStart().isAfter(period2.getStart()) ? period1.getStart() : period2.getStart();
        periodEndDay = period1.getEnd().isBefore(period2.getEnd()) ? period1.getEnd() : period2.getEnd();
        if (InSameMonth(period1.getStart(), period2.getStart())) {
        } else if (InSameMonth(period1.getEnd(), period2.getStart())) {
        } else {
        }
        return new Period(periodStartDay, periodEndDay);
    }

    private List<Budget> getBudgets(LocalDate start, LocalDate end) {
        List<Budget> totalBudgets = new ArrayList<>();
        for (Budget budget : db.GetAll()) {
            if ((budget.getYearMonth().equals(YearMonth.from(start)) || budget.getYearMonth().isAfter(YearMonth.from(start)))
                    &&
                    (budget.getYearMonth().equals(YearMonth.from(end)) || budget.getYearMonth().isBefore(YearMonth.from(end)))) {
                totalBudgets.add(budget);
            }
        }
        return totalBudgets;
    }

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

}
