import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Period {
    private final LocalDate start;
    private final LocalDate end;

    public Period(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public double overlappingDays(Period period) {
        if (getStart().isAfter(period.getEnd()) || getEnd().isBefore(period.getStart())) {
            return 0;
        } else {
            LocalDate periodStartDay = getStart().isAfter(period.getStart())
                    ? getStart()
                    : period.getStart();

            LocalDate periodEndDay = getEnd().isBefore(period.getEnd())
                    ? getEnd()
                    : period.getEnd();

            return DAYS.between(periodStartDay, periodEndDay) + 1;
        }
    }
}
