package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        if (startTime != null && endTime != null) {
            return !lt.isBefore(startTime) && lt.isBefore(endTime);
        }
        return startTime == null
                ? endTime == null || lt.isBefore(endTime)
                : !lt.isBefore(startTime);
    }

    public static boolean isBetweenInclusive(LocalDate ld, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return !ld.isBefore(startDate) && !ld.isAfter(endDate);
        }
        return startDate == null
                ? endDate == null || !ld.isAfter(endDate)
                : !ld.isBefore(startDate);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalDate parseDate(String date) {
        return date == null || date.trim().isEmpty()
                ? null
                :  LocalDate.parse(date);
    }

    public static LocalTime parseTime(String time) {
        return time == null || time.trim().isEmpty()
                ? null
                : LocalTime.parse(time);
    }
}

