package de.omagh.core_infra.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

public class DateUtilsTest {
    @Before
    public void setUp() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void formatDate_formatsToIso() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.APRIL, 15, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String formatted = DateUtils.formatDate(cal.getTimeInMillis());
        assertEquals("2024-04-15", formatted);
    }

    @Test
    public void parseDate_parsesIsoString() throws Exception {
        long millis = DateUtils.parseDate("2024-04-15");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.APRIL, 15, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTimeInMillis(), millis);
    }

    @Test
    public void daysBetween_computesDifference() {
        Calendar start = Calendar.getInstance();
        start.set(2024, Calendar.JANUARY, 1);
        Calendar end = Calendar.getInstance();
        end.set(2024, Calendar.JANUARY, 11);
        long days = DateUtils.daysBetween(start.getTimeInMillis(), end.getTimeInMillis());
        assertEquals(10, days);
    }
}
