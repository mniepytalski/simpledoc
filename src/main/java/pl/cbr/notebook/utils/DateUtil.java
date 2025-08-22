package pl.cbr.notebook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String format(Date date, String mask) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mask);
        return simpleDateFormat.format(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd HH:mm");
        return simpleDateFormat.format(date);
    }
}
