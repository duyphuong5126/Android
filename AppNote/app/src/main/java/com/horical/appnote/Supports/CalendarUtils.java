package com.horical.appnote.Supports;

import com.horical.appnote.DTO.CalendarObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dutn on 27/07/2015.
 */
public class CalendarUtils {

    private static final String TAG = CalendarUtils.class.getSimpleName();
    private static String dayOfWeek[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static String monthOfYear[] = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    public static String checkTimeStamp(Calendar calendar) {
        Calendar today = StringToCalendar(getToday());
        today.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        if (calendar.equals(today)) {
            return LanguageUtils.getThisWeekString();
        }
        if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                if (today.get(Calendar.WEEK_OF_MONTH) == calendar.get(Calendar.WEEK_OF_MONTH)) {
                    if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        return LanguageUtils.getThisWeekString();
                    } else {
                        if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                            return LanguageUtils.getThisWeekString();
                        } else {
                            return LanguageUtils.getLastWeekString();
                        }
                    }
                } else {
                    if (today.get(Calendar.WEEK_OF_MONTH) - calendar.get(Calendar.WEEK_OF_MONTH) == 1) {
                        if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                            return LanguageUtils.getThisWeekString();
                        } else {
                            return LanguageUtils.getLastWeekString();
                        }
                    } else {
                        return getWeekStamp(calendar) + " - " + monthOfYear[calendar.get(Calendar.MONTH)];
                    }
                }
            } else {
                return getWeekStamp(calendar) + " - " + monthOfYear[calendar.get(Calendar.MONTH)];
            }
        }
        return calendar.get(Calendar.MONTH) +"/"+ calendar.get(Calendar.YEAR);
    }

    private static String getWeekStamp(Calendar calendar) {
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        switch (calendar.get(Calendar.WEEK_OF_MONTH)) {
            case 1:
                return "Start of";
            case 2:
                return "Week 2";
            case 3:
                return "Week 3";
            default:
                return "End of";
        }
    }

    public static String checkWeekend() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return LanguageUtils.getSundayString();
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return LanguageUtils.getSaturdayString();
        }
        return "";
    }

    public static int checkTimeStamp(){
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return 6;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            return 0;
        }
        if (hour >= 6 && hour < 8) {
            return 1;
        }
        if (hour >= 8 && hour < 12) {
            return 2;
        }
        if (hour == 12) {
            return 3;
        }
        if (hour >= 13 && hour < 17) {
            return 4;
        }
        if (hour >= 17 && hour < 22) {
            return 5;
        }
        if (hour >= 22) {
            return 0;
        }
        return 0;
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        return calendar.get(Calendar.YEAR) + "-" + ((month < 10)?"0"+month:""+month) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return getToday() + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    public static Calendar StringToCalendar(String datetime) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            calendar.setTime(simpleDateFormat.parse(datetime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static boolean checkToday(String date) {
        Calendar today = Calendar.getInstance();
        Calendar day = StringToCalendar(date);
        return (
                (today.get(Calendar.YEAR) == day.get(Calendar.YEAR)) &&
                (today.get(Calendar.MONTH) == day.get(Calendar.MONTH)) &&
                (today.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH))
        );
    }

    public static int currentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static ArrayList setCalendarToArrayList(Calendar calendar) {
        Calendar clone = (Calendar) calendar.clone();
        clone.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        int totalDayOfClone = clone.getActualMaximum(Calendar.DAY_OF_MONTH);
        ArrayList arrayList = new ArrayList();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int totalDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        CalendarObject obj;
        int count;
        switch (day) {
            case 0:
                for (int i = 5; i > 0; i--) {
                    obj = new CalendarObject();
                    obj.setDay(totalDayOfClone - i);
                    obj.setShow(0);
                    arrayList.add(obj);
                }
                break;
            case 1:
                for (int i = 5; i >= 0; i--) {
                    obj = new CalendarObject();
                    obj.setDay(totalDayOfClone - i);
                    obj.setShow(0);
                    arrayList.add(obj);
                }
                break;
            default:
                count = 2;
                int cloneDay = day - 3;
                while (count < day) {
                    obj = new CalendarObject();
                    obj.setDay(totalDayOfClone - cloneDay);
                    obj.setShow(0);
                    arrayList.add(obj);
                    count++;
                    cloneDay--;
                }
                break;
        }
        for (int i = 1; i <= totalDayOfMonth; i++) {
            obj = new CalendarObject();
            obj.setDay(i);
            obj.setShow(1);
            obj.setReminder(0);
            obj.setMonth(calendar.get(Calendar.MONTH) + 1);
            obj.setYear(calendar.get(Calendar.YEAR));
            arrayList.add(obj);
        }
        count = 0;
        while (arrayList.size() < 42) {
            count++;
            obj = new CalendarObject();
            obj.setDay(count);
            obj.setShow(2);
            arrayList.add(obj);
        }
        return arrayList;
    }

    public static String getTimeNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    public static String addZero(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return i + "";
        }
    }

    public static Calendar getDateFromString(String strDate){
        Calendar date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date.setTime(simpleDateFormat.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int compareDate(String date1, String date2) {
        Calendar calendar1 = CalendarUtils.StringToCalendar(date1);
        Calendar calendar2 = CalendarUtils.StringToCalendar(date2);
        return calendar1.compareTo(calendar2);
    }
}
