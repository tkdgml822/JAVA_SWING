package kr.co.ync.project.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Util {
    public static LocalDate strToLocalDate(String date) {
        String birth = removeWhitespace(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(birth, formatter);
    }

    public static String removeWhitespace(String str) {
        return str.replace(" ", "");
    }
}
