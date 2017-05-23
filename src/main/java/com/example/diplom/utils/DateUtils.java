package com.example.diplom.utils;

import java.util.Date;

public class DateUtils {
    public static String formatDate(Date date) {
        return String.format("%02d.%02d.%04d", date.getDay(), date.getMonth(), date.getYear());
    }
    
    public static String getMonthName(int month) {
        switch (month) {
            case 1: return "января";
            case 2: return "февраля";
            case 3: return "марта";
            case 4: return "апреля";
            case 5: return "мая";
            case 6: return "июня";
            case 7: return "июля";
            case 8: return "августа";
            case 9: return "сентября";
            case 10: return "октября";
            case 11: return "ноября";
            case 12: return "декабря";
            default: return "null";
        }
    }
}
