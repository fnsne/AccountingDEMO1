import java.time.LocalDate;
import java.time.Year;
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

                double budgetAmount = budget.overlappingAmount(new Period(periodStartDay, periodEndDay));
                sum += budgetAmount;
            }
            return sum;
        } else {
            double sum = 0;
            for (Budget budget : getBudgets(start, end)) {
                if (InSameMonth(start, budget.firstDay())) {
                    //firstMonth
                    LocalDate periodStartDay = start.isAfter(budget.firstDay()) ? start : budget.firstDay();
                    LocalDate periodEndDay = end.isBefore(budget.lastDay()) ? end : budget.lastDay();

                    double budgetAmount = budget.overlappingAmount(new Period(periodStartDay, periodEndDay));
                    sum += budgetAmount;
                } else if (InSameMonth(end, budget.firstDay())) {
                    //last month
                    LocalDate periodStartDay = budget.firstDay();
                    LocalDate periodEndDay = end.isBefore(budget.lastDay()) ? end : budget.lastDay();

                    double budgetAmount = budget.overlappingAmount(new Period(periodStartDay, periodEndDay));
                    sum += budgetAmount;
                } else {
                    // middle
                    LocalDate periodStartDay = budget.firstDay();
                    LocalDate periodEndDay = budget.lastDay();

                    double budgetAmount = budget.overlappingAmount(new Period(periodStartDay, periodEndDay));
                    sum += budgetAmount;
                }
            }
            return sum;
        }

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
