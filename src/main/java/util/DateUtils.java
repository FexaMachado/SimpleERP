package util;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static Date getCurrentDate() {
        Date dt = Calendar.getInstance().getTime();
        return dt;
    }

    public static Date getLastMonthDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);

        return c.getTime();
    }

    public static Date fromString(String month, String year) {
        return new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month) - 1, 1).getTime();
    }
}
