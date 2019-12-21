import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Accounting {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
    private IBudgetRepo db;

    public Accounting(IBudgetRepo db) {
        this.db = db;
    }

    public double QueryBudget(LocalDate start, LocalDate end) {
        List<Budget> totalBudgets = getBudgetWithin(start, end);

        if (start.getYear() == end.getYear() && start.getMonth() == end.getMonth()) {
            return calculateBudget(
                    totalBudgets, start.lengthOfMonth(), end.getDayOfMonth() - start.getDayOfMonth() + 1);
        } else {
            //firstMonth
            List<Budget> startMonthBudget = getMonthBudgetOfInputDate(totalBudgets, start);
            double startMonthAmount = calculateBudget
                    (startMonthBudget, start.lengthOfMonth(), start.lengthOfMonth() - start.getDayOfMonth() + 1);

            //last month
            List<Budget> endMonthBudget = getMonthBudgetOfInputDate(totalBudgets, end);
            double endMonthAmount = calculateBudget(
                    endMonthBudget, end.lengthOfMonth(), end.getDayOfMonth());

            // middle
            List<Budget> middleBudgets = totalBudgets;
            middleBudgets.removeAll(startMonthBudget);
            middleBudgets.removeAll(endMonthBudget);

            double middleMonthAmount = middleBudgets.stream().mapToDouble(budget -> budget.amount).sum();

            return startMonthAmount + endMonthAmount + middleMonthAmount;
        }

    }

    private double calculateBudget(List<Budget> totalBudgets, int monthDays, int i) {
        return totalBudgets.stream().mapToDouble(budget -> {
            int diff = i;
            return budget.amount * (diff) / monthDays;
        }).sum();
    }

    private List<Budget> getMonthBudgetOfInputDate(List<Budget> totalBudgets, LocalDate start) {
        return totalBudgets.stream().filter(bd -> {
            YearMonth d = YearMonth.parse(bd.yearMonth, formatter);
            YearMonth startYM = YearMonth.from(start);
            return startYM.equals(d);
        }).collect(Collectors.toList());
    }

    private List<Budget> getBudgetWithin(LocalDate start, LocalDate end) {
        List<Budget> budgets = db.GetAll();

        return budgets.stream().filter(bd ->
        {
            YearMonth d = YearMonth.parse(bd.yearMonth, formatter);
            YearMonth startYM = YearMonth.from(start);
            YearMonth endYM = YearMonth.from(end);
            return (d.equals(startYM) || d.isAfter(startYM)) && (d.equals(endYM) || d.isBefore(endYM));
        }).collect(Collectors.toList());
    }
}
