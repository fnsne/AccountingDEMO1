import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            double sum = 0;
            for (Budget budget : totalBudgets) {
                double diff = end.getDayOfMonth() - start.getDayOfMonth() + 1;
                double dailyAmount = budget.getDailyAmount();
                sum += diff * dailyAmount;
            }
            return sum;
        } else {
            //firstMonth
            List<Budget> getFirstMonthBudget = new ArrayList<>();
            for (Budget budget : totalBudgets) {
                YearMonth d1 = budget.getYearMonth();
                YearMonth startYM1 = YearMonth.from(start);
                if (startYM1.equals(d1)) {
                    getFirstMonthBudget.add(budget);
                }
            }

            int firstMonthEffectiveDays = start.lengthOfMonth() - start.getDayOfMonth() + 1;
            double startMonthAmount = 0;
            for (Budget budget : getFirstMonthBudget) {
                int diff1 = firstMonthEffectiveDays;
                startMonthAmount += diff1 * budget.getDailyAmount();
            }

            //last month
            List<Budget> getLastMonthBudget = new ArrayList<>();
            for (Budget budget : totalBudgets) {
                YearMonth d = budget.getYearMonth();
                YearMonth startYM = YearMonth.from(end);
                if (startYM.equals(d)) {
                    getLastMonthBudget.add(budget);
                }
            }
            int lastMonthEffectiveDays = end.getDayOfMonth();
            double endMonthAmount = 0;
            for (Budget budget : getLastMonthBudget) {
                int diff = lastMonthEffectiveDays;
                endMonthAmount += diff * budget.getDailyAmount();
            }
            // middle
            List<Budget> middleBudgets = totalBudgets;
            middleBudgets.removeAll(getFirstMonthBudget);
            middleBudgets.removeAll(getLastMonthBudget);

            double middleMonthAmount = 0;
            for (Budget budget : middleBudgets) {
                middleMonthAmount += budget.getDailyAmount() * budget.getYearMonth().lengthOfMonth();
            }

            return startMonthAmount + endMonthAmount + middleMonthAmount;
        }

    }

    private boolean InSameMonth(LocalDate start, LocalDate end) {
        return start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
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
