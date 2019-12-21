import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Accounting {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
    private IBudgetRepo db;

    public Accounting(IBudgetRepo db) {
        this.db = db;
    }

    public double QueryBudget(LocalDate start, LocalDate end) {
        List<Budget> budgets = db.GetAll();

        List<Budget> totalBudgets = budgets.stream().filter(bd ->
        {
            YearMonth d = YearMonth.parse(bd.yearMonth, formatter);
            YearMonth startYM = YearMonth.from(start);
            YearMonth endYM = YearMonth.from(end);
            return (d.equals(startYM) || d.isAfter(startYM)) && (d.equals(endYM) || d.isBefore(endYM));
        }).collect(Collectors.toList());

        if (start.getMonth() == end.getMonth()) {
            double totalBudget = totalBudgets.stream().mapToDouble(budget -> {
                int diff = end.getDayOfMonth() - start.getDayOfMonth() + 1;
                return budget.amount * (diff) / start.lengthOfMonth();
            }).sum();

            return totalBudget;
        } else {
            //firstMonth
            List<Budget> startMonthBudget = totalBudgets.stream().filter(bd -> {
                YearMonth d = YearMonth.parse(bd.yearMonth, formatter);
                YearMonth startYM = YearMonth.from(start);
                return startYM.equals(d);
            }).collect(Collectors.toList());

            double startMonthAmount = startMonthBudget.stream().mapToDouble(budget -> {
                int diff = start.lengthOfMonth() - start.getDayOfMonth() + 1;
                return budget.amount * (diff) / start.lengthOfMonth();
            }).sum();


            //last month
            List<Budget> endMonthBudget = totalBudgets.stream().filter(bd -> {
                YearMonth d = YearMonth.parse(bd.yearMonth, formatter);
                YearMonth endYM = YearMonth.from(end);
                return endYM.equals(d);
            }).collect(Collectors.toList());

            double endMonthAmount = endMonthBudget.stream().mapToDouble(budget -> {
                int diff = end.getDayOfMonth();
                return budget.amount * (diff) / end.lengthOfMonth();
            }).sum();

            return startMonthAmount + endMonthAmount;
        }

    }
}
