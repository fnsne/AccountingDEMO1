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
        if (InSameMonth(start, end)) {
            double sum = 0;
            for (Budget budget : getBudgets(start, end)) {
                double budgetAmount = budget.budgetAmountOfPeriod(new Period(start, end));
                sum += budgetAmount;
            }
            return sum;
        } else {
            double sum = 0;
            for (Budget budget : getBudgets(start, end)) {
                Period period = getOverlappingPeriod(new Period(start, end), budget.createPeriod());
                double budgetAmount = budget.budgetAmountOfPeriod(period);
                sum += budgetAmount;
            }
            return sum;
        }

    }

    private Period getOverlappingPeriod(Period period1, Period period2) {
        LocalDate periodStartDay;
        LocalDate periodEndDay;
        if (InSameMonth(period1.getStart(), period2.getStart())) {
            periodStartDay = period1.getStart().isAfter(period2.getStart()) ? period1.getStart() : period2.getStart();
            periodEndDay = period1.getEnd().isBefore(period2.getEnd()) ? period1.getEnd() : period2.getEnd();
        } else if (InSameMonth(period1.getEnd(), period2.getStart())) {
            periodStartDay = period2.getStart();
            periodEndDay = period1.getEnd().isBefore(period2.getEnd()) ? period1.getEnd() : period2.getEnd();
        } else {
            periodStartDay = period2.getStart();
            periodEndDay = period2.getEnd();
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
