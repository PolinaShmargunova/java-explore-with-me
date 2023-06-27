package ru.practicum.explorewithme.utils;

import java.time.format.DateTimeFormatter;

public class CommonUtils {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final String PAGINATION_DEFAULT_FROM = "0";
    public static final String PAGINATION_DEFAULT_SIZE = "10";
}
