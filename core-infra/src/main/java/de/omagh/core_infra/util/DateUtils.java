package de.omagh.core_infra.util;

import android.annotation.SuppressLint;

import androidx.annotation.VisibleForTesting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Simple date helper utilities used across modules.
 *
 * <p>These methods are intentionally lightweight so they can run on both
 * Android and JVM unit tests. If more advanced functionality is required
 * consider migrating to java.time once minSdk allows.</p>
 */
@SuppressLint("SupportAnnotationUsage")
@VisibleForTesting
public class DateUtils {
    private static final String ISO_DATE = "yyyy-MM-dd";

    /**
     * Formats a timestamp (milliseconds since epoch) to {@code yyyy-MM-dd}.
     */
    public static String formatDate(long millis) {
        return new SimpleDateFormat(ISO_DATE, Locale.getDefault())
                .format(new Date(millis));
    }

    /**
     * Parses an ISO date string back to epoch milliseconds.
     */
    public static long parseDate(String date) throws ParseException {
        Date d = new SimpleDateFormat(ISO_DATE, Locale.getDefault()).parse(date);
        return d != null ? d.getTime() : 0L;
    }

    /**
     * Returns the difference between two timestamps in whole days.
     */
    public static long daysBetween(long startMillis, long endMillis) {
        return TimeUnit.MILLISECONDS.toDays(endMillis - startMillis);
    }
}
