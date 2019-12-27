import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Budget {
    String yearMonth;
    int amount;

    public Budget(String yearMonth, int amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public YearMonth getYearMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return YearMonth.parse(yearMonth, formatter);
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getDailyAmount() {
        return (double) amount / getYearMonth().lengthOfMonth();
    }

    public LocalDate getBudgetFirstDay() {
        return LocalDate.of(getYearMonth().getYear(), getYearMonth().getMonth(), 1);
    }

    public LocalDate getBudgetLastDay() {
        return LocalDate.of(getYearMonth().getYear(), getYearMonth().getMonth(), getYearMonth().lengthOfMonth());
    }
}
