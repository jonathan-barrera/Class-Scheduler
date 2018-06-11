package com.example.android.classscheduler;

import android.app.IntentService;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by jonathanbarrera on 6/10/18.
 */

public class DateUtils {

    // Helper method for converting from milliseconds to date string
    // ex. "January 01, 2018"
    public static String convertDateLongToString(long birthdate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(birthdate);
        String[] dateIntegerStrings = dateString.split("/");

        int day = Integer.parseInt(dateIntegerStrings[0]);
        int month = Integer.parseInt(dateIntegerStrings[1]) - 1;
        int year = Integer.parseInt(dateIntegerStrings[2]);

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String dateStringFinal = dateFormatSymbols.getMonths()[month] + " " + day + ", " + year;
        return dateStringFinal;
    }

    // Helper method for getting the age of a student
    public static String getAge(long birthdate) {
        // Birthdate
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(birthdate);
        String[] dateIntegerStrings = dateString.split("/");

        int birthDay = Integer.parseInt(dateIntegerStrings[0]);
        int birthMonth = Integer.parseInt(dateIntegerStrings[1]);
        int birthYear = Integer.parseInt(dateIntegerStrings[2]);

        // Today's date
        String todaysDateString = formatter.format(Calendar.getInstance().getTime());
        String[] todaysDateIntegerStrings = todaysDateString.split("/");

        int todayDay = Integer.parseInt(todaysDateIntegerStrings[0]);
        int todayMonth = Integer.parseInt(todaysDateIntegerStrings[1]);
        int todayYear = Integer.parseInt(todaysDateIntegerStrings[2]);

        // Algorithm for calculating age
        int age = todayYear - birthYear - 1;
        if (todayMonth > birthMonth || (todayMonth == birthMonth && todayDay >= birthDay)) {
            age = age + 1;
        }

        return String.valueOf(age);
    }

    // Check if the chosen date is later than today's date
    public static boolean isChosenDateAfterToday(long chosenDate) {
        long rightNow = System.currentTimeMillis();
        return chosenDate > rightNow;
    }
}
