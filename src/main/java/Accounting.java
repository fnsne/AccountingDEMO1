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
                Period period = getOverlappingPeriod(start, end, budget);
                double budgetAmount = budget.budgetAmountOfPeriod(period);
                sum += budgetAmount;
            }
            return sum;
        }

    }

    private Period getOverlappingPeriod(LocalDate start, LocalDate end, Budget budget) {
        Period period = new Period(start, end);
        LocalDate start1 = period.getStart();
        LocalDate end1 = period.getEnd();
        LocalDate periodStartDay;
        LocalDate periodEndDay;
        if (InSameMonth(start1, budget.firstDay())) {
            periodStartDay = start1.isAfter(budget.firstDay()) ? start1 : budget.firstDay();
            periodEndDay = end1.isBefore(budget.lastDay()) ? end1 : budget.lastDay();
        } else if (InSameMonth(end1, budget.firstDay())) {
            periodStartDay = budget.firstDay();
            periodEndDay = end1.isBefore(budget.lastDay()) ? end1 : budget.lastDay();
        } else {
            periodStartDay = budget.firstDay();
            periodEndDay = budget.lastDay();
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
