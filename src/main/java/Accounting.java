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
                double budgetAmount = diff * budget.getDailyAmount();
                sum += budgetAmount;
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
                LocalDate budgetStartDay = getBudgetFirstDay(budget);
                LocalDate budgetEndDay = getBudgetLastDay(budget);

                LocalDate periodStartDay = start.isAfter(budgetStartDay) ? start : budgetStartDay;
                LocalDate periodEndDay = end.isBefore(budgetEndDay) ? end : budgetEndDay;

                Period period = new Period(periodStartDay, periodEndDay);
                double diff = period.getDays();
                double budgetAmount = diff * budget.getDailyAmount();
                startMonthAmount += budgetAmount;
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
                double budgetAmount = diff * budget.getDailyAmount();
                endMonthAmount += budgetAmount;
            }
            // middle
            List<Budget> middleBudgets = getBudgets(start, end);
            middleBudgets.removeAll(getFirstMonthBudget);
            middleBudgets.removeAll(getLastMonthBudget);

            double middleMonthAmount = 0;
            for (Budget budget : middleBudgets) {
                YearMonth yearMonth = budget.getYearMonth();
                LocalDate budgetStartDay = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
                LocalDate budgetEndDay = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), yearMonth.lengthOfMonth());
                Period period = new Period(budgetStartDay, budgetEndDay);
                double diff = period.getDays();
                double budgetAmount = diff * budget.getDailyAmount();
                middleMonthAmount += budgetAmount;
            }

            return startMonthAmount + endMonthAmount + middleMonthAmount;
        }

    }

    private LocalDate getBudgetLastDay(Budget budget) {
        return LocalDate.of(budget.getYearMonth().getYear(), budget.getYearMonth().getMonth(), budget.getYearMonth().lengthOfMonth());
    }

    private LocalDate getBudgetFirstDay(Budget budget) {
        return LocalDate.of(budget.getYearMonth().getYear(), budget.getYearMonth().getMonth(), 1);
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
