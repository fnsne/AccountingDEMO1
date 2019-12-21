import org.junit.Test;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestAccounting {

    private IBudgetRepo db = mock(IBudgetRepo.class);

    @Test
    public void testQuerySingleMonth() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201901", 10));
        data.add(new Budget("201902", 20));
        data.add(new Budget("201903", 30));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 1, 1);
        LocalDate end = LocalDate.of(2019, 1, 31);
        assertEquals(10.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryCrossMonth() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201901", 10));
        data.add(new Budget("201903", 50));
        data.add(new Budget("201902", 20));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 1, 1);
        LocalDate end = LocalDate.of(2019, 2, 28);
        assertEquals(30.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryPartialSingleMonth() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201901", 31));
        data.add(new Budget("201903", 50));
        data.add(new Budget("201902", 20));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 1, 1);
        LocalDate end = LocalDate.of(2019, 1, 15);
        assertEquals(15.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryPartialCrossTwoMonth() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201901", 31));
        data.add(new Budget("201903", 50));
        data.add(new Budget("201902", 56));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 1, 31);
        LocalDate end = LocalDate.of(2019, 2, 1);
        assertEquals(3.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryPartialCrossMoreThanTwoMonth() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201901", 31));
        data.add(new Budget("201903", 93));
        data.add(new Budget("201902", 56));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 1, 31);
        LocalDate end = LocalDate.of(2019, 3, 1);
        assertEquals(60.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryPartialCrossMoreThanTwoMonthWithLostOneMonth() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201901", 31));
        data.add(new Budget("201903", 93));
        data.add(new Budget("201902", 56));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 2, 28);
        LocalDate end = LocalDate.of(2019, 4, 1);
        assertEquals(95.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryCrossOneYear() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201908", 31));
        data.add(new Budget("201910", 93));
        data.add(new Budget("201912", 56));
        data.add(new Budget("202001", 124));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 8, 31);
        LocalDate end = LocalDate.of(2020, 1, 1);
        assertEquals(154.0, accounting.QueryBudget(start, end));
    }

    @Test
    public void testQueryCrossSameMonthOfDifferentYear() {
        Accounting accounting = new Accounting(db);
        List<Budget> data = new ArrayList<Budget>();
        data.add(new Budget("201908", 31));
        data.add(new Budget("201910", 93));
        data.add(new Budget("201912", 56));
        data.add(new Budget("202008", 124));

        when(db.GetAll()).thenReturn(data);
        LocalDate start = LocalDate.of(2019, 8, 30);
        LocalDate end = LocalDate.of(2020, 8, 4);
        assertEquals(167.0, accounting.QueryBudget(start, end));
    }

}
