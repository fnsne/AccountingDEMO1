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

                sum += overlappingAmount(budget, periodStartDay, periodEndDay);
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
                LocalDate periodStartDay = start.isAfter(budget.firstDay()) ? start : budget.firstDay();
                LocalDate periodEndDay = end.isBefore(budget.lastDay()) ? end : budget.lastDay();

                startMonthAmount += overlappingAmount(budget, periodStartDay, periodEndDay);
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
                LocalDate periodStartDay = budget.firstDay();
                LocalDate periodEndDay = end.isBefore(budget.lastDay()) ? end : budget.lastDay();

                endMonthAmount += overlappingAmount(budget, periodStartDay, periodEndDay);
            }
            // middle
            List<Budget> middleBudgets = getBudgets(start, end);
            middleBudgets.removeAll(getFirstMonthBudget);
            middleBudgets.removeAll(getLastMonthBudget);

            double middleMonthAmount = 0;
            for (Budget budget : middleBudgets) {
                LocalDate budgetStartDay = budget.firstDay();
                LocalDate budgetEndDay = budget.lastDay();

                middleMonthAmount += overlappingAmount(budget, budgetStartDay, budgetEndDay);
            }

            return startMonthAmount + endMonthAmount + middleMonthAmount;
        }

    }

    private double overlappingAmount(Budget budget, LocalDate periodStartDay, LocalDate periodEndDay) {
        Period period = new Period(periodStartDay, periodEndDay);
        double diff = period.getDays();
        return diff * budget.getDailyAmount();
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
