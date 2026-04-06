package com.extract.pdf.extractpdftext.util;

import java.util.*;

public class OCRFixer {

    private static final Map<Character, Character> CHAR_TO_DIGIT_MAP = new HashMap<>();

    static {
        CHAR_TO_DIGIT_MAP.put('O', '0');
        CHAR_TO_DIGIT_MAP.put('D', '0');
        CHAR_TO_DIGIT_MAP.put('Q', '0');
        CHAR_TO_DIGIT_MAP.put('I', '1');
        CHAR_TO_DIGIT_MAP.put('L', '1');
        CHAR_TO_DIGIT_MAP.put('Z', '2');;
        CHAR_TO_DIGIT_MAP.put('E', '3');
        CHAR_TO_DIGIT_MAP.put('A', '4');
        CHAR_TO_DIGIT_MAP.put('S', '5');
        CHAR_TO_DIGIT_MAP.put('G', '6');
        CHAR_TO_DIGIT_MAP.put('T', '7');
        CHAR_TO_DIGIT_MAP.put('B', '8');
        CHAR_TO_DIGIT_MAP.put('U', '0');
    }

    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    public static String fixOCR(String text) {
        StringBuilder fixedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (CHAR_TO_DIGIT_MAP.containsKey(c)) {
                fixedText.append(CHAR_TO_DIGIT_MAP.get(c));
            } else {
                fixedText.append(c);
            }
        }

        return fixedText.toString();
    }

    public static void main(String[] args) {
        System.out.println(fixOCR("ZUQ2579944"));

    }
}