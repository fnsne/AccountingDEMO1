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
            int endDay = end.getDayOfMonth();
            int beginDay = start.getDayOfMonth();

            double totalBudget = totalBudgets.stream().mapToDouble(budget -> {
                int diff = endDay - beginDay + 1;
                return budget.amount * (diff) / start.lengthOfMonth();
            }).sum();

            return totalBudget;
        } else {
            int beginDay = start.getDayOfMonth();
            int endDay = end.getDayOfMonth();

            //firstMonth
            List<Budget> startMonthBudget = getMonthBudgetOfInputDate(totalBudgets, start);

            double startMonthAmount = startMonthBudget.stream().mapToDouble(budget -> {
                int diff = start.lengthOfMonth() - beginDay + 1;
                return budget.amount * (diff) / start.lengthOfMonth();
            }).sum();


            //last month
            List<Budget> endMonthBudget = getMonthBudgetOfInputDate(totalBudgets, end);

            double endMonthAmount = endMonthBudget.stream().mapToDouble(budget -> {
                int diff = endDay;
                return budget.amount * (diff) / end.lengthOfMonth();
            }).sum();

            // middle
            List<Budget> middleBudgets = totalBudgets;
            middleBudgets.removeAll(startMonthBudget);
            middleBudgets.removeAll(endMonthBudget);

            double middleMonthAmount = middleBudgets.stream().mapToDouble(budget -> budget.amount).sum();

            return startMonthAmount + endMonthAmount + middleMonthAmount;
        }

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
