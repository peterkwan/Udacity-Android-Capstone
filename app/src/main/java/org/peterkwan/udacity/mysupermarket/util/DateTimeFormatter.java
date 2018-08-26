package org.peterkwan.udacity.mysupermarket.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    public static String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date);
    }

    public static String formatDateTime(Date date) {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault()).format(date);
    }

    public static String formatDateTimeShort(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(date);
    }
}
