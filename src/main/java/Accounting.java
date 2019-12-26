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

        if (InSameMonth(start, end)) {
            return calculateBudget(
                    totalBudgets,
                    start.lengthOfMonth(),
                    end.getDayOfMonth() - start.getDayOfMonth() + 1
            );
        } else {
            //firstMonth
            List<Budget> getFirstMonthBudget = totalBudgets.stream().filter(bd1 -> {
                YearMonth d1 = YearMonth.parse(bd1.yearMonth, formatter);
                YearMonth startYM1 = YearMonth.from(start);
                return startYM1.equals(d1);
            }).collect(Collectors.toList());
            int firstMonthEffectiveDays = start.lengthOfMonth() - start.getDayOfMonth() + 1;
            double startMonthAmount = calculateBudget(
                    getFirstMonthBudget,
                    start.lengthOfMonth(),
                    firstMonthEffectiveDays
            );

            //last month
            List<Budget> getLastMonthBudget = totalBudgets.stream().filter(bd -> {
                YearMonth d = YearMonth.parse(bd.yearMonth, formatter);
                YearMonth startYM = YearMonth.from(end);
                return startYM.equals(d);
            }).collect(Collectors.toList());
            int lastMonthEffectiveDays = end.getDayOfMonth();
            double endMonthAmount = calculateBudget(
                    getLastMonthBudget,
                    end.lengthOfMonth(),
                    lastMonthEffectiveDays
            );

            // middle
            List<Budget> middleBudgets = totalBudgets;
            middleBudgets.removeAll(getFirstMonthBudget);
            middleBudgets.removeAll(getLastMonthBudget);

            double middleMonthAmount = middleBudgets.stream().mapToDouble(budget -> budget.amount).sum();

            return startMonthAmount + endMonthAmount + middleMonthAmount;
        }

    }

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

    private double calculateBudget(List<Budget> totalBudgets, int monthDays, int effectiveDays) {
        return totalBudgets.stream().mapToDouble(budget -> {
            int diff = effectiveDays;
            return budget.amount * (diff) / monthDays;
        }).sum();
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
