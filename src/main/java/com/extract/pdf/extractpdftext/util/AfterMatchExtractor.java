package com.extract.pdf.extractpdftext.util;
public class AfterMatchExtractor {
    public static void main(String[] args) {
        // Example invocation:
        // args = new String[] {
        //   "Section No and Name 1-Tambaram (C), Pulikoradu (R.V), Ward 32 Amman Koil Street",
        //   "Section No and Name"
        // };
        if (args == null || args.length < 2) {
            System.err.println("Usage: java AfterMatchExtractor <inputString> <matchString>");
            System.exit(1);
        }

        String input = args[0];
        String match = args[1];
        System.out.println(extractAfterMatch(input, match));
    }

    /**
     * Returns the substring of input that comes after the first occurrence of match.
     * If match is not found, returns an empty string.
     * Leading whitespace after the match is trimmed.
     */
    public static String extractAfterMatch(String input, String match) {
        if (input == null || match == null) return "";
        int idx = input.indexOf(match);
        if (idx == -1) return "";
        int start = idx + match.length();
        return input.substring(start).replaceAll("^\\s+", "");
    }
}
