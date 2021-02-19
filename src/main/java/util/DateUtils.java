package util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date getCurrentDate(){
        Date dt = Calendar.getInstance().getTime();
        return dt;
    }

    public static Date getLastMonthDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-1);

        return c.getTime();
    }
}
