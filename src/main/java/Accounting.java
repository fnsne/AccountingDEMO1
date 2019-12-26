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
                Period period = new Period(start, end);
                double diff = period.getDays();
                sum += diff * budget.getDailyAmount();
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

            double startMonthAmount = 0;
            for (Budget budget : getFirstMonthBudget) {
                LocalDate lastDay = LocalDate.of(start.getYear(), start.getMonth(), start.lengthOfMonth());
                Period period = new Period(start, lastDay);
                double diff = period.getDays();
                startMonthAmount += (double) diff * budget.getDailyAmount();
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
            double endMonthAmount = 0;
            for (Budget budget : getLastMonthBudget) {
                LocalDate firstDay = LocalDate.of(end.getYear(), end.getMonth(), 1);
                Period period = new Period(firstDay, end);
                double diff = period.getDays();
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
