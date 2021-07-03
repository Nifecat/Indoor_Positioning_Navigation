package edu.xmu.inroomlocation.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    public static String getCurrentTimeString() {
        return formatter.format(new Date());
    }

}
