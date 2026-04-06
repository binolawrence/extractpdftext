package com.extract.pdf.extractpdftext.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WardExtractor {
    public static void main(String[] args) {
        String input = ", Ward 453 Amman Koil Street";

        // Pattern that matches "Ward" followed by optional spaces and a number
        Pattern patternFull = Pattern.compile("Ward\\s*\\d+\\b");
        // Pattern that captures just the number after "Ward"
        Pattern patternNumber = Pattern.compile("\\bWard\\s*(\\d+)\\b");

        Matcher mFull = patternFull.matcher(input);
        if (mFull.find()) {
            System.out.println("Matched text: " + mFull.group()); // prints "Ward 32"
        } else {
            System.out.println("No full match found.");
        }

        Matcher mNum = patternNumber.matcher(input);
        if (mNum.find()) {
            System.out.println("Captured number: " + mNum.group(1)); // prints "32"
        } else {
            System.out.println("No number captured.");
        }
    }
}
