package com.extract.pdf.extractpdftext.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestClass {
    public static void main(String[] args) {
        String input = "Name: Durairaj - Father Name: Jeyakumar Husband Name: Vijay";
        
        System.out.println("=== Testing fetchKeyValueByValue ===");
        
        // Search for Durairaj
        String result1 = fetchKeyValueByValue(input, "Name: Durairaj");
        System.out.println("Search 'Durairaj': " + result1);
        
        // Search for Jeyakumar
        String result2 = fetchKeyValueByValue(input, "Jeyakumar");
        System.out.println("Search 'Jeyakumar': " + result2);
        
        // Search for Vijay
        String result3 = fetchKeyValueByValue(input, "Vijay");
        System.out.println("Search 'Vijay': " + result3);
        
        // Test with non-existent value
        String result4 = fetchKeyValueByValue(input, "NonExistent");
        System.out.println("Search 'NonExistent': " + result4);
    }
    
    /**
     * Fetches the value by searching for it in the input string
     * Supports multiple delimiters for flexibility
     * @param input The input string containing key-value pairs
     * @param searchValue The value to search for
     * @param delimiters Variable number of delimiters (e.g., " - ", " ", "-")
     * @return The value if found, null otherwise
     */
    public static String fetchValueBySearchValue(String input, String searchValue, String... delimiters) {
        if (delimiters == null || delimiters.length == 0) {
            delimiters = new String[]{" - ", " ", "-"};
        }
        
        for (String delimiter : delimiters) {
            String[] pairs = input.split("\\s*" + java.util.regex.Pattern.quote(delimiter) + "\\s*");
            
            for (String pair : pairs) {
                String[] segments = pair.trim().split("\\s+");
                
                for (String segment : segments) {
                    String cleanSegment = segment.trim().replaceAll("[,:;]", "");
                    
                    if (cleanSegment.equals(searchValue)) {
                        return searchValue;
                    }
                }
            }
        }
        
        return null; // Return null if value not found
    }
    
    /**
     * Fetches the complete key-value pair by searching for a value
     * @param input The input string containing key-value pairs
     * @param searchValue The value to search for
     * @return The complete key-value pair (e.g., "Father Name: Jeyakumar")
     */
    public static String fetchKeyValueByValue(String input, String searchValue) {
        Pattern pattern = Pattern.compile("([^:]+): ([^ -]+)");
        Matcher matcher = pattern.matcher(input);
        
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            
            if (value.equals(searchValue)) {
                return key + ": " + value;
            }
        }
        
        return null; // Return null if value not found
    }


    //Given String "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 19" as input it should return
    //as two strings first one as Assembly Constituency No and Name : 31-TAMBARAM and second one as PartNo.: 19

     public static void fetchKeyValuePairs(String input) {
        Pattern pattern = Pattern.compile("([^:]+): ([^ -]+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }
}
