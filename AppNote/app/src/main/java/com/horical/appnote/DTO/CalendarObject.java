package com.horical.appnote.DTO;

/**
 * Created by dutn on 27/07/2015.
 */
public class CalendarObject {

    private int mDay;
    private int mMonth;
    private int mYear;
    private int mShow;
    private int mReminder;

    public CalendarObject() {
    }

    public CalendarObject(int day, int show) {
        this.mDay = day;
        this.mShow = show;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        this.mDay = day;
    }

    public int getShow() {
        return mShow;
    }

    public void setShow(int show) {
        this.mShow = show;
    }

    public int getReminder() {
        return mReminder;
    }

    public void setReminder(int reminder) {
        this.mReminder = reminder;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        this.mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        this.mYear = year;
    }

    @Override
    public String toString() {
        return "CalendarObject{" +
                "day=" + mDay +
                ", show=" + mShow +
                ", reminder=" + mReminder +
                ", month=" + mMonth +
                ", year=" + mYear +
                '}';
    }

}
