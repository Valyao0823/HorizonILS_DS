package com.mylibrary;

import java.util.Calendar;

/**
 * Created by hesolutions on 2016-01-05.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
