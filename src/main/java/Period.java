import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Period {
    private final LocalDate start;
    private final LocalDate end;

    public Period(LocalDate start, LocalDate end) {

        this.start = start;
        this.end = end;
    }

    public long getDays() {
        return DAYS.between(start, end) + 1;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public double overlappingDays(Budget budget) {
        double diff;
        if (getStart().isAfter(budget.lastDay()) || getEnd().isBefore(budget.firstDay())) {
            diff = 0;
        } else {
            LocalDate periodStartDay = getStart().isAfter(budget.createPeriod().getStart())
                    ? getStart()
                    : budget.createPeriod().getStart();

            LocalDate periodEndDay = getEnd().isBefore(budget.createPeriod().getEnd())
                    ? getEnd()
                    : budget.createPeriod().getEnd();

            diff = new Period(periodStartDay, periodEndDay).getDays();
        }
        return diff;
    }
}
