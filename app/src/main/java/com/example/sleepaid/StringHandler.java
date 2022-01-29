package com.example.sleepaid;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHandler {
    public static List<Integer> getIntsFromString(String s) {
        Matcher matcher = Pattern.compile("\\d+").matcher(s);

        List<Integer> numbers = new ArrayList<>();

        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group()));
        }

        return numbers;
    }
}
