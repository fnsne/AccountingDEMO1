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
                LocalDate periodStartDay = start;
                LocalDate periodEndDay = end;

                double budgetAmount = budget.budgetAmountOfPeriod(new Period(periodStartDay, periodEndDay));
                sum += budgetAmount;
            }
            return sum;
        } else {
            double sum = 0;
            for (Budget budget : getBudgets(start, end)) {
                Period period = getOverlappingPeriod(budget, new Period(start, end));
                double budgetAmount = budget.budgetAmountOfPeriod(period);
                sum += budgetAmount;
            }
            return sum;
        }

    }

    private Period getOverlappingPeriod(Budget budget, Period period) {
        LocalDate periodStartDay;
        LocalDate periodEndDay;
        Period period1 = createPeriod(budget);
        if (InSameMonth(period.getStart(), period1.getStart())) {
            periodStartDay = period.getStart().isAfter(period1.getStart()) ? period.getStart() : period1.getStart();
            periodEndDay = period.getEnd().isBefore(period1.getEnd()) ? period.getEnd() : period1.getEnd();
        } else if (InSameMonth(period.getEnd(), period1.getStart())) {
            periodStartDay = period1.getStart();
            periodEndDay = period.getEnd().isBefore(period1.getEnd()) ? period.getEnd() : period1.getEnd();
        } else {
            periodStartDay = period1.getStart();
            periodEndDay = period1.getEnd();
        }
        return new Period(periodStartDay, periodEndDay);
    }

    private Period createPeriod(Budget budget) {
        return new Period(budget.firstDay(), budget.lastDay());
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
