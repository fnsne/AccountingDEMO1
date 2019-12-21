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

}
