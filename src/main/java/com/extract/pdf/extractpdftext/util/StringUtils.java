package com.extract.pdf.extractpdftext.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

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


    //Give String "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 19" need to fetch as key value pairs
    // key: Assembly Constituency No and Name, value: 31-TAMBARAM
    //key PartNo, value: 19
    public static List<String> fetchKeyValuePairs(String input) {
        Pattern pattern = Pattern.compile("([^:]+): ([^ -]+)");
        Matcher matcher = pattern.matcher(input);
        List<String> keyValuePairs = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            String keyValuePair = key+":"+value;
            System.out.println("Key: " + key + ", Value: " + value);
            keyValuePairs.add(keyValuePair);
        }
        return keyValuePairs;
    }


    public static String fetchEpicNo(String input, String searchValue) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{3}\\d{7}$");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()&&matcher.group().trim().equals(searchValue)) {
            String key = matcher.group().trim();
            return key;
        }

        return null; // Return null if value not found
    }

    /**
     * Converts delimiter string to regex pattern for split operation
     * Example: "•,-" becomes "[•,\\-]"
     * @param delimiters The delimiter(s) as a string
     * @return A regex pattern string that can be used with split()
     */
    private static String convertDelimitersToRegex(String delimiters) {
        if (delimiters == null || delimiters.isEmpty()) {
            return "•"; // Default delimiter
        }
        
        // Escape special regex characters
        StringBuilder regex = new StringBuilder("[");
        for (int i = 0; i < delimiters.length(); i++) {
            char c = delimiters.charAt(i);
            if (c == '-' || c == '^' || c == ']' || c == '\\') {
                regex.append("\\").append(c);
            } else {
                regex.append(c);
            }
        }
        regex.append("]");
        return regex.toString();
    }

    /**
     * Extracts a specific key-value pair from a line with multiple entries separated by delimiters
     * Example: "Name : Ramachandhltan • Name : Gnanam • Name : Ramesh •"
     * or "Name : Ramachandhltan - Name : Gnanam • Name : Ramesh •"
     * @param input The input line containing multiple key-value pairs separated by delimiter(s)
     * @param keyToSearch The key to search for (e.g., "Name")
     * @param valueToSearch The value to search for (e.g., "Ramesh")
     * @param delimiters Single or multiple delimiters (e.g., "•" or "•,-")
     * @return A Map.Entry with key and value, or null if not found
     */
    public static java.util.Map.Entry<String, String> extractKeyValuePair(String input, String keyToSearch, String valueToSearch, String delimiters) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        // Convert delimiters to regex pattern and split
        String delimiterPattern = convertDelimitersToRegex(delimiters);
        String[] entries = input.split(delimiterPattern);

        for (String entry : entries) {
            entry = entry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            // Extract key and value from each entry using regex
            Pattern pattern = Pattern.compile("([^:]+):\\s*(.+)");
            Matcher matcher = pattern.matcher(entry);

            if (matcher.find()) {
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();

                // Check if this entry matches our search criteria
                if (key.equalsIgnoreCase(keyToSearch) && value.equalsIgnoreCase(valueToSearch)) {
                    return new java.util.AbstractMap.SimpleEntry<>(key, value);
                }
            }
        }

        return null; // Return null if not found
    }

    /**
     * Generic method to extract all key-value pairs from a line with multiple entries
     * Example: "Name : Ramachandhltan • Name : Gnanam • Name : Ramesh •"
     * or "Name : Ramachandhltan - Name : Gnanam • Name : Ramesh •"
     * @param input The input line containing multiple key-value pairs
     * @param delimiters Single or multiple delimiters (e.g., "•" or "•,-")
     * @return A list of Map entries with all key-value pairs
     */
    public static List<java.util.Map.Entry<String, String>> extractAllKeyValuePairs(String input, String delimiters) {
        List<java.util.Map.Entry<String, String>> results = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return results;
        }

        // Convert delimiters to regex pattern and split
        String delimiterPattern = convertDelimitersToRegex(delimiters);
        String[] entries = input.split(delimiterPattern);

        for (String entry : entries) {
            entry = entry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            // Extract key and value from each entry using regex
            Pattern pattern = Pattern.compile("([^:]+):\\s*(.+)");
            Matcher matcher = pattern.matcher(entry);

            if (matcher.find()) {
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();
                results.add(new java.util.AbstractMap.SimpleEntry<>(key, value));
            }
        }

        return results;
    }

    /**
     * Extracts all key-value pairs from a single line where multiple key-value pairs
     * are not separated by delimiters but keys are identifiable (end with ':')
     * Example: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1"
     * Result: {Assembly Constituency No and Name=31-TAMBARAM, PartNo.=1}
     * @param input The input line containing multiple key-value pairs (no delimiter between them)
     * @return A list of Map entries with all key-value pairs found
     */
    public static List<java.util.Map.Entry<String, String>> extractKeyValuePairsFromLine(String input) {
        List<java.util.Map.Entry<String, String>> results = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return results;
        }

        // Pattern to match key-value pairs
        // Key: can contain letters, spaces, dots, hyphens, and digits, followed by ':'
        // Value: everything up to the next key pattern or end of string
        // Matches: "Key : value" or "Key: value" with variable spacing
        Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9\\s\\.'-]*?)\\s*:\\s*([^:]*?)(?=\\s+[a-zA-Z][a-zA-Z0-9\\s\\.'-]*\\s*:|$)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();

            if (!key.isEmpty() && !value.isEmpty()) {
                results.add(new java.util.AbstractMap.SimpleEntry<>(key, value));
            }
        }

        return results;
    }

    /**
     * Extracts a specific key-value pair from a single line where multiple key-value pairs
     * are not separated by delimiters
     * Example: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1"
     * @param input The input line containing multiple key-value pairs
     * @param keyToSearch The key to search for (e.g., "Assembly Constituency No and Name")
     * @return A Map entry with key and value, or null if not found
     */
    public static java.util.Map.Entry<String, String> extractKeyValuePairFromLine(String input, String keyToSearch) {
        if (input == null || input.isEmpty() || keyToSearch == null || keyToSearch.isEmpty()) {
            return null;
        }

        List<java.util.Map.Entry<String, String>> allPairs = extractKeyValuePairsFromLine(input);

        for (java.util.Map.Entry<String, String> entry : allPairs) {
            if (entry.getKey().equalsIgnoreCase(keyToSearch)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Splits a string at a given key/identifier
     * Example: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1"
     * With keyToSplitFrom = "PartNo." returns a Map with:
     * - "first" = "Assembly Constituency No and Name : 31-TAMBARAM "
     * - "second" = "PartNo.: 1"
     * @param input The input string to split
     * @param keyToSplitFrom The key/identifier to split from (e.g., "PartNo.")
     * @return A Map with "first" and "second" parts, or null if key not found
     */
    public static java.util.Map<String, String> splitStringByKey(String input, String keyToSplitFrom) {
        System.out.println("Splitting string by key: " + keyToSplitFrom);
        System.out.println("Input string: " + input);
        if (input == null || input.isEmpty() || keyToSplitFrom == null || keyToSplitFrom.isEmpty()) {
            return null;
        }

        int index = input.indexOf(keyToSplitFrom);

        if (index == -1) {
            return null; // Key not found
        }

        String firstPart = input.substring(0, index).trim();
        String secondPart = input.substring(index).trim();

        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("first", firstPart);
        result.put("second", secondPart);

        return result;
    }

    /**
     * Splits a string at a given key/identifier (case-insensitive)
     * Example: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1"
     * With keyToSplitFrom = "partno." returns a Map with:
     * - "first" = "Assembly Constituency No and Name : 31-TAMBARAM "
     * - "second" = "PartNo.: 1"
     * @param input The input string to split
     * @param keyToSplitFrom The key/identifier to split from (case-insensitive)
     * @return A Map with "first" and "second" parts, or null if key not found
     */
    public static java.util.Map<String, String> splitStringByKeyIgnoreCase(String input, String keyToSplitFrom) {
        if (input == null || input.isEmpty() || keyToSplitFrom == null || keyToSplitFrom.isEmpty()) {
            return null;
        }

        int index = input.toLowerCase().indexOf(keyToSplitFrom.toLowerCase());

        if (index == -1) {
            return null; // Key not found
        }

        String firstPart = input.substring(0, index).trim();
        String secondPart = input.substring(index).trim();

        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("first", firstPart);
        result.put("second", secondPart);

        return result;
    }

    /**
     * Splits a string at a given key and returns the parts as a List
     * Example: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1"
     * With keyToSplitFrom = "PartNo." returns a List: ["Assembly Constituency No and Name : 31-TAMBARAM", "PartNo.: 1"]
     * @param input The input string to split
     * @param keyToSplitFrom The key/identifier to split from
     * @return A List with first part at index 0 and second part at index 1, or null if key not found
     */
    public static List<String> splitStringByKeyAsList(String input, String keyToSplitFrom) {
        if (input == null || input.isEmpty() || keyToSplitFrom == null || keyToSplitFrom.isEmpty()) {
            return null;
        }

        int index = input.indexOf(keyToSplitFrom);

        if (index == -1) {
            return null; // Key not found
        }

        String firstPart = input.substring(0, index).trim();
        String secondPart = input.substring(index).trim();

        List<String> result = new ArrayList<>();
        result.add(firstPart);
        result.add(secondPart);

        return result;
    }

    /**
     * Extracts a substring between a starting string and an ending string
     * Example: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1"
     * With startString = "Name : " and endString = " PartNo" returns "31-TAMBARAM"
     * @param input The input string to search within
     * @param startString The starting delimiter string (not included in result)
     * @param endString The ending delimiter string (not included in result)
     * @return The substring between startString and endString, or null if not found
     */
    public static String extractStringBetween(String input, String startString, String endString) {
        if (input == null || input.isEmpty() || startString == null || startString.isEmpty() || endString == null || endString.isEmpty()) {
            return null;
        }

        int startIndex = input.indexOf(startString);
        if (startIndex == -1) {
            return null; // Start string not found
        }

        int contentStartIndex = startIndex + startString.length();
        int endIndex = input.indexOf(endString, contentStartIndex);

        if (endIndex == -1) {
            return null; // End string not found
        }

        return input.substring(contentStartIndex, endIndex).trim();
    }

    /**
     * Extracts a substring between a starting string and an ending string (case-insensitive)
     * @param input The input string to search within
     * @param startString The starting delimiter string (case-insensitive, not included in result)
     * @param endString The ending delimiter string (case-insensitive, not included in result)
     * @return The substring between startString and endString, or null if not found
     */
    public static String extractStringBetweenIgnoreCase(String input, String startString, String endString) {
        if (input == null || input.isEmpty() || startString == null || startString.isEmpty() || endString == null || endString.isEmpty()) {
            return null;
        }

        String lowerInput = input.toLowerCase();
        String lowerStartString = startString.toLowerCase();
        String lowerEndString = endString.toLowerCase();

        int startIndex = lowerInput.indexOf(lowerStartString);
        if (startIndex == -1) {
            return null; // Start string not found
        }

        int contentStartIndex = startIndex + startString.length();
        int endIndex = lowerInput.indexOf(lowerEndString, contentStartIndex);

        if (endIndex == -1) {
            return null; // End string not found
        }

        return input.substring(contentStartIndex, endIndex).trim();
    }

    /**
     * Extracts all substrings between starting and ending strings from a given input
     * Example: "Price: $100 End Date: 2025-12-31 End"
     * With startString = "$" and endString = " End" returns ["100"]
     * @param input The input string to search within
     * @param startString The starting delimiter string (not included in result)
     * @param endString The ending delimiter string (not included in result)
     * @return A List of all substrings found between startString and endString
     */
    public static List<String> extractAllStringsBetween(String input, String startString, String endString) {
        List<String> results = new ArrayList<>();

        if (input == null || input.isEmpty() || startString == null || startString.isEmpty() || endString == null || endString.isEmpty()) {
            return results;
        }

        int searchStartIndex = 0;

        while (true) {
            int startIndex = input.indexOf(startString, searchStartIndex);
            if (startIndex == -1) {
                break; // No more start strings found
            }

            int contentStartIndex = startIndex + startString.length();
            int endIndex = input.indexOf(endString, contentStartIndex);

            if (endIndex == -1) {
                break; // No end string found from this position
            }

            String extracted = input.substring(contentStartIndex, endIndex).trim();
            if (!extracted.isEmpty()) {
                results.add(extracted);
            }

            searchStartIndex = endIndex + endString.length();
        }

        return results;
    }

    /**
     * Extracts a substring between a starting string and an ending string without trimming
     * Example: "Assembly Constituency No and Name :  31-TAMBARAM  PartNo.: 1"
     * With startString = "Name :" and endString = "PartNo." returns "  31-TAMBARAM  " (with spaces)
     * @param input The input string to search within
     * @param startString The starting delimiter string (not included in result)
     * @param endString The ending delimiter string (not included in result)
     * @param trim Whether to trim the result
     * @return The substring between startString and endString
     */
    public static String extractStringBetween(String input, String startString, String endString, boolean trim) {
        if (input == null || input.isEmpty() || startString == null || startString.isEmpty() || endString == null || endString.isEmpty()) {
            return null;
        }

        int startIndex = input.indexOf(startString);
        if (startIndex == -1) {
            return null;
        }

        int contentStartIndex = startIndex + startString.length();
        int endIndex = input.indexOf(endString, contentStartIndex);

        if (endIndex == -1) {
            return null;
        }

        String result = input.substring(contentStartIndex, endIndex);
        return trim ? result.trim() : result;
    }

    /**
     * Generic method to extract a fixed string and its associated matching pattern
     * Handles optional spaces before and after the colon
     * Example: "PartNo. : 123" with fixedString = "PartNo." and numberPattern = "\\d{1,3}" returns "PartNo.:123"
     * Example: "Reference: ABC123" with fixedString = "Reference" and numberPattern = "[A-Z]\\d{1,3}" returns "Reference:ABC123"
     * @param input The input string containing the fixed string and matching pattern
     * @param fixedString The fixed string to search for (e.g., "PartNo.", "Assembly Constituency No")
     * @param numberPattern The regex pattern to match (e.g., "\\d{1,3}" for 1-3 digits, "[A-Z]{3}\\d{7}" for EPIC format)
     * @return A string in format "fixedString:matchedValue" if match found, null otherwise
     */
    public static String extractStringWithPattern(String input, String fixedString, String numberPattern) {
        if (input == null || input.isEmpty() || fixedString == null || fixedString.isEmpty() || numberPattern == null || numberPattern.isEmpty()) {
            return null;
        }

        // Pattern: fixedString + optional spaces + : + optional spaces + (custom regex pattern)
        // Escaping special regex characters in fixedString
        String escapedFixedString = Pattern.quote(fixedString);
        String pattern = escapedFixedString + numberPattern;
        
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        if (matcher.find()) {
            String matchedValue = matcher.group(1);
            return fixedString + " " + matchedValue;
        }

        return null; // No match found
    }


    /**
     * Generic method to extract multiple fixed strings and their associated matching patterns from input
     * Handles optional spaces before and after the colon
     * @param input The input string containing fixed strings and matching patterns
     * @param numberPattern The regex pattern to match for all fixed strings (e.g., "\\d{1,3}", "[A-Z]{3}\\d{7}")
     * @param fixedStrings Variable number of fixed strings to search for
     * @return A List of strings in format "fixedString:matchedValue" for each match found
     */
    public static List<String> extractAllStringsWithPattern(String input, String numberPattern, String... fixedStrings) {
        List<String> results = new ArrayList<>();

        if (input == null || input.isEmpty() || numberPattern == null || numberPattern.isEmpty() || fixedStrings == null || fixedStrings.length == 0) {
            return results;
        }

        for (String fixedString : fixedStrings) {
            if (fixedString != null && !fixedString.isEmpty()) {
                String result = extractStringWithPattern(input, fixedString, numberPattern);
                if (result != null) {
                    results.add(result);
                }
            }
        }

        return results;
    }



    public static String extractAfterDelimiter(String input, String delimiter) {
        if (input == null || delimiter == null) return "";
        int idx = input.indexOf(delimiter);
        if (idx == -1) return ""; // delimiter not found

        int start = idx + delimiter.length();

        // substring from start, then trim leading whitespace
        String tail = input.substring(start).trim();

        // stop at next comma or newline or semicolon (common separators)
        int endIdx = tail.length();
        for (int i = 0; i < tail.length(); i++) {
            char c = tail.charAt(i);
            if (c == ',' || c == '\n' || c == ';') { endIdx = i; break; }
        }

        String candidate = tail.substring(0, endIdx).trim();

        // If the value may be followed by other words separated by spaces and punctuation,
        // you can optionally limit to first token. Here we return the whole segment before separator.
        return candidate;
    }

    public static String extractAfterMatch(String input, String match) {
        if (input == null || match==null) {
           return "";
        }
        if (input == null || match == null) return "";
        int idx = input.indexOf(match);
        if (idx == -1) return "";
        int start = idx + match.length();
        return input.substring(start).replaceAll("^\\s+", "");
    }
}
