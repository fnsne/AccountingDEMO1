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
        if (InSameMonth(start, end)) {
            double sum = 0;
            for (Budget budget : getBudgets(start, end)) {
                double diff = end.getDayOfMonth() - start.getDayOfMonth() + 1;
                double dailyAmount = budget.getDailyAmount();
                sum += diff * dailyAmount;
            }
            return sum;
        } else {
            //firstMonth
            List<Budget> getFirstMonthBudget = new ArrayList<>();
            for (Budget budget : getBudgets(start, end)) {
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
            for (Budget budget : getBudgets(start, end)) {
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
            List<Budget> middleBudgets = getBudgets(start, end);
            middleBudgets.removeAll(getFirstMonthBudget);
            middleBudgets.removeAll(getLastMonthBudget);

            double middleMonthAmount = 0;
            for (Budget budget : middleBudgets) {
                middleMonthAmount += budget.getDailyAmount() * budget.getYearMonth().lengthOfMonth();
            }

            return startMonthAmount + endMonthAmount + middleMonthAmount;
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
