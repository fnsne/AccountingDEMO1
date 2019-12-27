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

    public double getDailyAmount() {
        return (double) amount / getYearMonth().lengthOfMonth();
    }

    public LocalDate firstDay() {
        return LocalDate.of(getYearMonth().getYear(), getYearMonth().getMonth(), 1);
    }

    public LocalDate lastDay() {
        return LocalDate.of(getYearMonth().getYear(), getYearMonth().getMonth(), getYearMonth().lengthOfMonth());
    }

    public Period createPeriod() {
        return new Period(firstDay(), lastDay());
    }

    public double intervalAmount(LocalDate start, LocalDate end) {
        return new Period(start, end).overlappingDays(this) * getDailyAmount();
    }
}
