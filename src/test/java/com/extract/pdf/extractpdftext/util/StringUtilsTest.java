package com.extract.pdf.extractpdftext.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils Key-Value Extraction Tests")
public class StringUtilsTest {

    @BeforeEach
    void setUp() {
        // Setup before each test (if needed)
    }

    // ==================== Tests for extractKeyValuePair() ====================

    @Test
    @DisplayName("Should extract specific key-value pair from bullet-separated entries")
    void testExtractKeyValuePair_WithBulletDelimiter() {
        String input = "Name : Ramachandhltan • Name : Gnanam • Name : Ramesh •";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Name", "Ramesh", "•");

        assertNotNull(result, "Result should not be null");
        assertEquals("Name", result.getKey());
        assertEquals("Ramesh", result.getValue());
    }

    @Test
    @DisplayName("Should extract key-value pair with different delimiter (dash)")
    void testExtractKeyValuePair_WithDashDelimiter() {
        String input = "Father Name: Durairaj - Father Name: Jeyakumar - Husband Name: Vijay";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Father Name", "Jeyakumar", "-");

        assertNotNull(result, "Result should not be null");
        assertEquals("Father Name", result.getKey());
        assertEquals("Jeyakumar", result.getValue());
    }

    @Test
    @DisplayName("Should extract key-value pair with pipe delimiter")
    void testExtractKeyValuePair_WithPipeDelimiter() {
        String input = "Name: Ramesh | Age: 30 | City: Chennai";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Age", "30", "|");

        assertNotNull(result, "Result should not be null");
        assertEquals("Age", result.getKey());
        assertEquals("30", result.getValue());
    }

    @Test
    @DisplayName("Should return null when value not found")
    void testExtractKeyValuePair_ValueNotFound() {
        String input = "Name : Ramachandhltan • Name : Gnanam • Name : Ramesh •";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Name", "Nonexistent", "•");

        assertNull(result, "Result should be null when value not found");
    }

    @Test
    @DisplayName("Should return null when key not found")
    void testExtractKeyValuePair_KeyNotFound() {
        String input = "Name : Ramesh • Age : 30";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "City", "Chennai", "•");

        assertNull(result, "Result should be null when key not found");
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void testExtractKeyValuePair_NullInput() {
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(null, "Name", "Ramesh", "•");

        assertNull(result, "Result should be null for null input");
    }

    @Test
    @DisplayName("Should handle empty string input gracefully")
    void testExtractKeyValuePair_EmptyInput() {
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair("", "Name", "Ramesh", "•");

        assertNull(result, "Result should be null for empty input");
    }

    @Test
    @DisplayName("Should perform case-insensitive search for key")
    void testExtractKeyValuePair_CaseInsensitiveKey() {
        String input = "Name : Ramesh • Age : 30";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "name", "Ramesh", "•");

        assertNotNull(result, "Should find match with different case");
        assertEquals("Name", result.getKey());
        assertEquals("Ramesh", result.getValue());
    }

    @Test
    @DisplayName("Should perform case-insensitive search for value")
    void testExtractKeyValuePair_CaseInsensitiveValue() {
        String input = "Name : Ramesh • Age : 30";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Name", "ramesh", "•");

        assertNotNull(result, "Should find match with different case");
        assertEquals("Name", result.getKey());
        assertEquals("Ramesh", result.getValue());
    }

    @Test
    @DisplayName("Should handle extra spaces around colons")
    void testExtractKeyValuePair_ExtraSpaces() {
        String input = "Name   :   Ramesh • Age   :   30";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Name", "Ramesh", "•");

        assertNotNull(result, "Should handle extra spaces");
        assertEquals("Name", result.getKey());
        assertEquals("Ramesh", result.getValue());
    }

    @Test
    @DisplayName("Should extract first matching entry when multiple entries have same key-value pair")
    void testExtractKeyValuePair_MultipleMatches() {
        String input = "Name : Ramesh • Name : Ramesh • Name : Gnanam";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "Name", "Ramesh", "•");

        assertNotNull(result, "Should return first match");
        assertEquals("Name", result.getKey());
        assertEquals("Ramesh", result.getValue());
    }

    // ==================== Tests for extractAllKeyValuePairs() ====================

    @Test
    @DisplayName("Should extract all key-value pairs from bullet-separated entries")
    void testExtractAllKeyValuePairs_WithBulletDelimiter() {
        String input = "Name : Ramachandhltan - Name : Gnanam • Name : Ramesh •";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "•,-");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Ramachandhltan", results.get(0).getValue());
        assertEquals("Name", results.get(1).getKey());
        assertEquals("Gnanam", results.get(1).getValue());
        assertEquals("Name", results.get(2).getKey());
        assertEquals("Ramesh", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should extract all key-value pairs with different delimiters")
    void testExtractAllKeyValuePairs_WithDifferentDelimiters() {
        String input = "Name: Ramesh | Age: 30 | City: Chennai";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "|");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Ramesh", results.get(0).getValue());
        assertEquals("Age", results.get(1).getKey());
        assertEquals("30", results.get(1).getValue());
        assertEquals("City", results.get(2).getKey());
        assertEquals("Chennai", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should extract all key-value pairs with dash delimiter")
    void testExtractAllKeyValuePairs_WithDashDelimiter() {
        String input = "Father Name: Durairaj - Father Name: Jeyakumar - Husband Name: Vijay";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "-");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("Father Name", results.get(0).getKey());
        assertEquals("Durairaj", results.get(0).getValue());
        assertEquals("Husband Name", results.get(2).getKey());
        assertEquals("Vijay", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should extract all key-value pairs with multiple delimiters (dash and bullet)")
    void testExtractAllKeyValuePairs_WithMultipleDelimiters() {
        String input = "Name: Ramesh - Age: 30 • City: Chennai - Country: India";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "-•");

        assertNotNull(results, "Results should not be null");
        assertEquals(4, results.size(), "Should extract 4 pairs");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Ramesh", results.get(0).getValue());
        assertEquals("Age", results.get(1).getKey());
        assertEquals("30", results.get(1).getValue());
        assertEquals("City", results.get(2).getKey());
        assertEquals("Chennai", results.get(2).getValue());
        assertEquals("Country", results.get(3).getKey());
        assertEquals("India", results.get(3).getValue());
    }

    @Test
    @DisplayName("Should extract key-value pair with multiple delimiters (dash and bullet)")
    void testExtractKeyValuePair_WithMultipleDelimiters() {
        String input = "Name: Ramesh - Age: 30 • City: Chennai";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePair(input, "City", "Chennai", "-•");

        assertNotNull(result, "Result should not be null");
        assertEquals("City", result.getKey());
        assertEquals("Chennai", result.getValue());
    }

    @Test
    @DisplayName("Should extract all key-value pairs with multiple delimiters including pipe")
    void testExtractAllKeyValuePairs_WithMultipleDelimitersAndPipe() {
        String input = "Name: Ramesh | Age: 30 - City: Chennai • Country: India";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "-•|");

        assertNotNull(results, "Results should not be null");
        assertEquals(4, results.size(), "Should extract 4 pairs");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Age", results.get(1).getKey());
        assertEquals("City", results.get(2).getKey());
        assertEquals("Country", results.get(3).getKey());
    }

    @Test
    @DisplayName("Should return empty list for null input")
    void testExtractAllKeyValuePairs_NullInput() {
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(null, "•");

        assertNotNull(results, "Should return empty list, not null");
        assertEquals(0, results.size(), "Should be empty for null input");
    }

    @Test
    @DisplayName("Should return empty list for empty string input")
    void testExtractAllKeyValuePairs_EmptyInput() {
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs("", "•");

        assertNotNull(results, "Should return empty list, not null");
        assertEquals(0, results.size(), "Should be empty for empty input");
    }

    @Test
    @DisplayName("Should handle string with only delimiters")
    void testExtractAllKeyValuePairs_OnlyDelimiters() {
        String input = "•••";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "•");

        assertNotNull(results, "Should return empty list");
        assertEquals(0, results.size(), "Should be empty when only delimiters present");
    }

    @Test
    @DisplayName("Should handle entries with extra spaces")
    void testExtractAllKeyValuePairs_ExtraSpaces() {
        String input = "  Name   :   Ramesh  •  Age   :   30  •  City: Chennai  ";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "•");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("Ramesh", results.get(0).getValue());
        assertEquals("30", results.get(1).getValue());
        assertEquals("Chennai", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should extract single key-value pair")
    void testExtractAllKeyValuePairs_SinglePair() {
        String input = "Name: Ramesh";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "•");

        assertNotNull(results, "Results should not be null");
        assertEquals(1, results.size(), "Should extract 1 pair");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Ramesh", results.get(0).getValue());
    }

    @Test
    @DisplayName("Should handle complex values with multiple words")
    void testExtractAllKeyValuePairs_ComplexValues() {
        String input = "Address: No 5 Tamil Nadu Street • Name: Ramachandran Kumar";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "•");

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should extract 2 pairs");
        assertEquals("No 5 Tamil Nadu Street", results.get(0).getValue());
        assertEquals("Ramachandran Kumar", results.get(1).getValue());
    }

    @Test
    @DisplayName("Should skip empty entries after splitting")
    void testExtractAllKeyValuePairs_SkipEmpty() {
        String input = "Name: Ramesh ••• Age: 30";
        List<Map.Entry<String, String>> results = StringUtils.extractAllKeyValuePairs(input, "•");

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should extract 2 pairs, skipping empty entries");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Age", results.get(1).getKey());
    }

    // ==================== Tests for extractKeyValuePairsFromLine() ====================

    @Test
    @DisplayName("Should extract multiple key-value pairs from single line without delimiters")
    void testExtractKeyValuePairsFromLine_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine(input);

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should extract 2 pairs");
        assertEquals("Assembly Constituency No and Name", results.get(0).getKey());
        assertEquals("31-TAMBARAM", results.get(0).getValue());
        assertEquals("PartNo.", results.get(1).getKey());
        assertEquals("1", results.get(1).getValue());
    }

    @Test
    @DisplayName("Should extract key-value pairs with extra spaces")
    void testExtractKeyValuePairsFromLine_ExtraSpaces() {
        String input = "Name   :   Ramesh   Age   :   30   City   :   Chennai";
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine(input);

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("Name", results.get(0).getKey());
        assertEquals("Ramesh", results.get(0).getValue());
        assertEquals("Age", results.get(1).getKey());
        assertEquals("30", results.get(1).getValue());
        assertEquals("City", results.get(2).getKey());
        assertEquals("Chennai", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should extract single key-value pair from line")
    void testExtractKeyValuePairsFromLine_SinglePair() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM";
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine(input);

        assertNotNull(results, "Results should not be null");
        assertEquals(1, results.size(), "Should extract 1 pair");
        assertEquals("Assembly Constituency No and Name", results.get(0).getKey());
        assertEquals("31-TAMBARAM", results.get(0).getValue());
    }

    @Test
    @DisplayName("Should extract pairs with numeric values")
    void testExtractKeyValuePairsFromLine_NumericValues() {
        String input = "PartNo.: 1 Age: 25 Year: 2023";
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine(input);

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("1", results.get(0).getValue());
        assertEquals("25", results.get(1).getValue());
        assertEquals("2023", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should extract pairs with hyphens and special characters in values")
    void testExtractKeyValuePairsFromLine_SpecialCharacters() {
        String input = "Constituency : 31-TAMBARAM Area : South-Chennai Code : AC-001";
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine(input);

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 pairs");
        assertEquals("31-TAMBARAM", results.get(0).getValue());
        assertEquals("South-Chennai", results.get(1).getValue());
        assertEquals("AC-001", results.get(2).getValue());
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void testExtractKeyValuePairsFromLine_NullInput() {
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine(null);

        assertNotNull(results, "Should return empty list, not null");
        assertEquals(0, results.size(), "Should be empty for null input");
    }

    @Test
    @DisplayName("Should handle empty string input gracefully")
    void testExtractKeyValuePairsFromLine_EmptyInput() {
        List<Map.Entry<String, String>> results = StringUtils.extractKeyValuePairsFromLine("");

        assertNotNull(results, "Should return empty list, not null");
        assertEquals(0, results.size(), "Should be empty for empty input");
    }

    // ==================== Tests for extractKeyValuePairFromLine() ====================

    @Test
    @DisplayName("Should extract specific key-value pair from line")
    void testExtractKeyValuePairFromLine_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, "Assembly Constituency No and Name");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No and Name", result.getKey());
        assertEquals("31-TAMBARAM", result.getValue());
    }

    @Test
    @DisplayName("Should extract PartNo. from line")
    void testExtractKeyValuePairFromLine_ExtractPartNo() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals("PartNo.", result.getKey());
        assertEquals("1", result.getValue());
    }

    @Test
    @DisplayName("Should perform case-insensitive key search")
    void testExtractKeyValuePairFromLine_CaseInsensitive() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, "partno.");

        assertNotNull(result, "Result should not be null");
        assertEquals("PartNo.", result.getKey());
        assertEquals("1", result.getValue());
    }

    @Test
    @DisplayName("Should return null when key not found")
    void testExtractKeyValuePairFromLine_KeyNotFound() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, "NonExistent");

        assertNull(result, "Result should be null when key not found");
    }

    @Test
    @DisplayName("Should return null for null input")
    void testExtractKeyValuePairFromLine_NullInput() {
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(null, "PartNo.");

        assertNull(result, "Result should be null for null input");
    }

    @Test
    @DisplayName("Should return null for empty input")
    void testExtractKeyValuePairFromLine_EmptyInput() {
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine("", "PartNo.");

        assertNull(result, "Result should be null for empty input");
    }

    @Test
    @DisplayName("Should return null when key is null")
    void testExtractKeyValuePairFromLine_NullKey() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, null);

        assertNull(result, "Result should be null when key is null");
    }

    @Test
    @DisplayName("Should return null when key is empty")
    void testExtractKeyValuePairFromLine_EmptyKey() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, "");

        assertNull(result, "Result should be null when key is empty");
    }

    @Test
    @DisplayName("Should extract from line with complex keys and values")
    void testExtractKeyValuePairFromLine_ComplexKeysAndValues() {
        String input = "Voter Full Name : Ramachandran Kumar Age Group : 45-55 Constituency Name : Chennai Central";
        Map.Entry<String, String> result = StringUtils.extractKeyValuePairFromLine(input, "Constituency Name");

        assertNotNull(result, "Result should not be null");
        assertEquals("Constituency Name", result.getKey());
        assertEquals("Chennai Central", result.getValue());
    }


    @Test
    @DisplayName("Should extract from line with complex keys")
    void testExtractKeyValuePairFromLine0() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        List<Map.Entry<String, String>> result = StringUtils.extractAllKeyValuePairs(input, "%\\s*:\\s*");
    }

    // ==================== Tests for splitStringByKey() ====================

    @Test
    @DisplayName("Should split string by key - basic case")
    void testSplitStringByKey_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKey(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM", result.get("first"));
        assertEquals("PartNo.: 1", result.get("second"));
    }

    @Test
    @DisplayName("Should split string by key - different key")
    void testSplitStringByKey_DifferentKey() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKey(input, "Assembly");

        assertNotNull(result, "Result should not be null");
        assertEquals("", result.get("first"));
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1", result.get("second"));
    }

    @Test
    @DisplayName("Should return null when key not found")
    void testSplitStringByKey_KeyNotFound() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKey(input, "NonExistent");

        assertNull(result, "Should return null when key not found");
    }

    @Test
    @DisplayName("Should return null for null input")
    void testSplitStringByKey_NullInput() {
        Map<String, String> result = StringUtils.splitStringByKey(null, "PartNo.");

        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null for empty input")
    void testSplitStringByKey_EmptyInput() {
        Map<String, String> result = StringUtils.splitStringByKey("", "PartNo.");

        assertNull(result, "Should return null for empty input");
    }

    @Test
    @DisplayName("Should return null when key is null")
    void testSplitStringByKey_NullKey() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKey(input, null);

        assertNull(result, "Should return null when key is null");
    }

    @Test
    @DisplayName("Should return null when key is empty")
    void testSplitStringByKey_EmptyKey() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKey(input, "");

        assertNull(result, "Should return null when key is empty");
    }

    @Test
    @DisplayName("Should handle key at the beginning")
    void testSplitStringByKey_KeyAtBeginning() {
        String input = "PartNo.: 1 Assembly Constituency No and Name : 31-TAMBARAM";
        Map<String, String> result = StringUtils.splitStringByKey(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals("", result.get("first"));
        assertEquals("PartNo.: 1 Assembly Constituency No and Name : 31-TAMBARAM", result.get("second"));
    }

    @Test
    @DisplayName("Should handle key at the end")
    void testSplitStringByKey_KeyAtEnd() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.";
        Map<String, String> result = StringUtils.splitStringByKey(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM", result.get("first"));
        assertEquals("PartNo.", result.get("second"));
    }

    @Test
    @DisplayName("Should trim whitespace in split parts")
    void testSplitStringByKey_TrimsWhitespace() {
        String input = "   Assembly Constituency No and Name : 31-TAMBARAM    PartNo.:    1   ";
        Map<String, String> result = StringUtils.splitStringByKey(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM", result.get("first"));
        assertEquals("PartNo.:    1", result.get("second"));
    }

    // ==================== Tests for splitStringByKeyIgnoreCase() ====================

    @Test
    @DisplayName("Should split string by key (case-insensitive)")
    void testSplitStringByKeyIgnoreCase_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKeyIgnoreCase(input, "partno.");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM", result.get("first"));
        assertEquals("PartNo.: 1", result.get("second"));
    }

    @Test
    @DisplayName("Should split string by key (case-insensitive with mixed case)")
    void testSplitStringByKeyIgnoreCase_MixedCase() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PARTNO.: 1";
        Map<String, String> result = StringUtils.splitStringByKeyIgnoreCase(input, "partno.");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM", result.get("first"));
        assertEquals("PARTNO.: 1", result.get("second"));
    }

    @Test
    @DisplayName("Should return null for null input (case-insensitive)")
    void testSplitStringByKeyIgnoreCase_NullInput() {
        Map<String, String> result = StringUtils.splitStringByKeyIgnoreCase(null, "PartNo.");

        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null when key not found (case-insensitive)")
    void testSplitStringByKeyIgnoreCase_KeyNotFound() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        Map<String, String> result = StringUtils.splitStringByKeyIgnoreCase(input, "NonExistent");

        assertNull(result, "Should return null when key not found");
    }

    // ==================== Tests for splitStringByKeyAsList() ====================

    @Test
    @DisplayName("Should split string by key and return as list")
    void testSplitStringByKeyAsList_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        List<String> result = StringUtils.splitStringByKeyAsList(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should have 2 parts");
        assertEquals("Assembly Constituency No and Name : 31-TAMBARAM", result.get(0));
        assertEquals("PartNo.: 1", result.get(1));
    }

    @Test
    @DisplayName("Should return null for null input (list version)")
    void testSplitStringByKeyAsList_NullInput() {
        List<String> result = StringUtils.splitStringByKeyAsList(null, "PartNo.");

        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null when key not found (list version)")
    void testSplitStringByKeyAsList_KeyNotFound() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        List<String> result = StringUtils.splitStringByKeyAsList(input, "NonExistent");

        assertNull(result, "Should return null when key not found");
    }

    @Test
    @DisplayName("Should split string and return as list with key at beginning")
    void testSplitStringByKeyAsList_KeyAtBeginning() {
        String input = "PartNo.: 1 Assembly Constituency No and Name : 31-TAMBARAM";
        List<String> result = StringUtils.splitStringByKeyAsList(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should have 2 parts");
        assertEquals("", result.get(0));
        assertEquals("PartNo.: 1 Assembly Constituency No and Name : 31-TAMBARAM", result.get(1));
    }

    @Test
    @DisplayName("Should trim whitespace in list result")
    void testSplitStringByKeyAsList_TrimsWhitespace() {
        String input = "  Assembly Constituency No  PartNo.:  1  ";
        List<String> result = StringUtils.splitStringByKeyAsList(input, "PartNo.");

        assertNotNull(result, "Result should not be null");
        assertEquals("Assembly Constituency No", result.get(0));
        assertEquals("PartNo.:  1", result.get(1));
    }

    // ==================== Tests for extractStringBetween() ====================

    @Test
    @DisplayName("Should extract string between start and end delimiters")
    void testExtractStringBetween_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
        String result = StringUtils.extractStringBetween(input, "Name : ", " PartNo");

        assertNotNull(result, "Result should not be null");
        assertEquals("31-TAMBARAM", result);
    }

    @Test
    @DisplayName("Should extract string with colon delimiters")
    void testExtractStringBetween_WithColons() {
        String input = "Name: Ramesh Age: 30 City: Chennai";
        String result = StringUtils.extractStringBetween(input, "Name: ", " Age");

        assertNotNull(result, "Result should not be null");
        assertEquals("Ramesh", result);
    }

    @Test
    @DisplayName("Should extract string between numeric values")
    void testExtractStringBetween_NumericDelimiters() {
        String input = "Price: $100 Amount: $500 Total: $1000";
        String result = StringUtils.extractStringBetween(input, "$", " Amount");

        assertNotNull(result, "Result should not be null");
        assertEquals("100", result);
    }

    @Test
    @DisplayName("Should return null when start string not found")
    void testExtractStringBetween_StartNotFound() {
        String input = "Name: Ramesh Age: 30";
        String result = StringUtils.extractStringBetween(input, "City: ", " Age");

        assertNull(result, "Should return null when start string not found");
    }

    @Test
    @DisplayName("Should return null when end string not found")
    void testExtractStringBetween_EndNotFound() {
        String input = "Name: Ramesh Age: 30";
        String result = StringUtils.extractStringBetween(input, "Name: ", "Unknown");

        assertNull(result, "Should return null when end string not found");
    }

    @Test
    @DisplayName("Should return null for null input")
    void testExtractStringBetween_NullInput() {
        String result = StringUtils.extractStringBetween(null, "Start", "End");

        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null for empty input")
    void testExtractStringBetween_EmptyInput() {
        String result = StringUtils.extractStringBetween("", "Start", "End");

        assertNull(result, "Should return null for empty input");
    }

    @Test
    @DisplayName("Should return null when start string is null")
    void testExtractStringBetween_NullStartString() {
        String input = "Name: Ramesh Age: 30";
        String result = StringUtils.extractStringBetween(input, null, "Age");

        assertNull(result, "Should return null when start string is null");
    }

    @Test
    @DisplayName("Should return null when end string is null")
    void testExtractStringBetween_NullEndString() {
        String input = "Name: Ramesh Age: 30";
        String result = StringUtils.extractStringBetween(input, "Name: ", null);

        assertNull(result, "Should return null when end string is null");
    }

    @Test
    @DisplayName("Should trim result by default")
    void testExtractStringBetween_TrimsWhitespace() {
        String input = "Start:   content with spaces   End";
        String result = StringUtils.extractStringBetween(input, "Start:", "End");

        assertNotNull(result, "Result should not be null");
        assertEquals("content with spaces", result);
    }

    // ==================== Tests for extractStringBetweenIgnoreCase() ====================

    @Test
    @DisplayName("Should extract string between delimiters (case-insensitive)")
    void testExtractStringBetweenIgnoreCase_Basic() {
        String input = "Assembly Constituency No and Name : 31-TAMBARAM PARTNO.: 1";
        String result = StringUtils.extractStringBetweenIgnoreCase(input, "name : ", " partno");

        assertNotNull(result, "Result should not be null");
        assertEquals("31-TAMBARAM", result);
    }

    @Test
    @DisplayName("Should extract with mixed case delimiters (case-insensitive)")
    void testExtractStringBetweenIgnoreCase_MixedCase() {
        String input = "NAMe: Ramesh AGE: 30 City: Chennai";
        String result = StringUtils.extractStringBetweenIgnoreCase(input, "name: ", " age");

        assertNotNull(result, "Result should not be null");
        assertEquals("Ramesh", result);
    }

    @Test
    @DisplayName("Should return null when start string not found (case-insensitive)")
    void testExtractStringBetweenIgnoreCase_StartNotFound() {
        String input = "Name: Ramesh Age: 30";
        String result = StringUtils.extractStringBetweenIgnoreCase(input, "CITY: ", " AGE");

        assertNull(result, "Should return null when start string not found");
    }

    @Test
    @DisplayName("Should return null for null input (case-insensitive)")
    void testExtractStringBetweenIgnoreCase_NullInput() {
        String result = StringUtils.extractStringBetweenIgnoreCase(null, "Start", "End");

        assertNull(result, "Should return null for null input");
    }

    // ==================== Tests for extractAllStringsBetween() ====================

    @Test
    @DisplayName("Should extract all substrings between delimiters")
    void testExtractAllStringsBetween_Basic() {
        String input = "Price: $100 End Date: 2025-12-31 End Amount: $500 End";
        List<String> results = StringUtils.extractAllStringsBetween(input, "$", " End");

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should extract 2 substrings");
        assertEquals("100", results.get(0));
        assertEquals("500", results.get(1));
    }

    @Test
    @DisplayName("Should extract multiple names between delimiters")
    void testExtractAllStringsBetween_MultipleNames() {
        String input = "Name: Ramesh End Name: Gnanam End Name: Ramachandran End";
        List<String> results = StringUtils.extractAllStringsBetween(input, "Name: ", " End");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should extract 3 names");
        assertEquals("Ramesh", results.get(0));
        assertEquals("Gnanam", results.get(1));
        assertEquals("Ramachandran", results.get(2));
    }

    @Test
    @DisplayName("Should return empty list for null input")
    void testExtractAllStringsBetween_NullInput() {
        List<String> results = StringUtils.extractAllStringsBetween(null, "Start", "End");

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should return empty list when start string not found")
    void testExtractAllStringsBetween_StartNotFound() {
        String input = "Name: Ramesh End Name: Gnanam End";
        List<String> results = StringUtils.extractAllStringsBetween(input, "Age: ", " End");

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should return empty list when no matching pairs found")
    void testExtractAllStringsBetween_NoMatchingPairs() {
        String input = "Name: Ramesh Age: 30";
        List<String> results = StringUtils.extractAllStringsBetween(input, "Start", "End");

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should skip empty extracted values")
    void testExtractAllStringsBetween_SkipEmpty() {
        String input = "Value: test1 Delimiter:  Delimiter: test2 Delimiter:";
        List<String> results = StringUtils.extractAllStringsBetween(input, "Value: ", " Delimiter:");

        assertNotNull(results, "Results should not be null");
    }

    // ==================== Tests for extractStringBetween() with trim parameter ====================

    @Test
    @DisplayName("Should extract string without trimming when trim is false")
    void testExtractStringBetweenWithTrim_NoTrim() {
        String input = "Start:   content with spaces   End";
        String result = StringUtils.extractStringBetween(input, "Start:", "End", false);

        assertNotNull(result, "Result should not be null");
        assertEquals("   content with spaces   ", result);
    }

    @Test
    @DisplayName("Should extract string with trimming when trim is true")
    void testExtractStringBetweenWithTrim_WithTrim() {
        String input = "Start:   content with spaces   End";
        String result = StringUtils.extractStringBetween(input, "Start:", "End", true);

        assertNotNull(result, "Result should not be null");
        assertEquals("content with spaces", result);
    }

    @Test
    @DisplayName("Should return null when start string not found (with trim parameter)")
    void testExtractStringBetweenWithTrim_StartNotFound() {
        String input = "Start: content End";
        String result = StringUtils.extractStringBetween(input, "Unknown:", "End", true);

        assertNull(result, "Should return null when start string not found");
    }

    @Test
    @DisplayName("Should return null when end string not found (with trim parameter)")
    void testExtractStringBetweenWithTrim_EndNotFound() {
        String input = "Start: content End";
        String result = StringUtils.extractStringBetween(input, "Start:", "Unknown", false);

        assertNull(result, "Should return null when end string not found");
    }

    @Test
    @DisplayName("Should preserve internal spaces with trim parameter")
    void testExtractStringBetweenWithTrim_PreservesInternalSpaces() {
        String input = "Start: hello   world   End";
        String result = StringUtils.extractStringBetween(input, "Start:", "End", true);

        assertNotNull(result, "Result should not be null");
        assertEquals("hello   world", result);
    }

    // ==================== Tests for extractStringWithPattern() ====================

    @Test
    @DisplayName("Should extract fixed string with custom pattern - 1-3 digits")
    void testExtractStringWithPattern_ThreeDigits() {
        String input = "District: 123 State: 45 City: 1";
        String result = StringUtils.extractStringWithPattern(input, "District", "\\d{1,3}");

        assertNotNull(result, "Result should not be null");
        assertEquals("District:123", result);
    }

    @Test
    @DisplayName("Should extract fixed string with EPIC pattern")
    void testExtractStringWithPattern_EPICFormat() {
        String input = "EPIC No: ABC1234567 Name: John";
        String result = StringUtils.extractStringWithPattern(input, "EPIC No", "[a-zA-Z]{3}\\d{7}");

        assertNotNull(result, "Result should not be null");
        assertEquals("EPIC No:ABC1234567", result);
    }

    @Test
    @DisplayName("Should extract fixed string with alphanumeric pattern")
    void testExtractStringWithPattern_Alphanumeric() {
        String input = "Reference: ABC123 and more text";
        String result = StringUtils.extractStringWithPattern(input, "Reference", "[A-Z]{3}\\d{1,3}");

        assertNotNull(result, "Result should not be null");
        assertEquals("Reference:ABC123", result);
    }

    @Test
    @DisplayName("Should extract fixed string with ZU pattern (ZU + 8 digits)")
    void testExtractStringWithPattern_ZUPattern() {
        String input = "ID: ZU12345678 More data";
        String result = StringUtils.extractStringWithPattern(input, "ID", "ZU\\d{8}");

        assertNotNull(result, "Result should not be null");
        assertEquals("ID:ZU12345678", result);
    }

    @Test
    @DisplayName("Should handle multiple spaces with custom pattern")
    void testExtractStringWithPattern_MultipleSpaces() {
        String input = "Code   :   XYZ789";
        String result = StringUtils.extractStringWithPattern(input, "Code", "[A-Z]{3}\\d{3}");

        assertNotNull(result, "Result should not be null");
        assertEquals("Code:XYZ789", result);
    }

    @Test
    @DisplayName("Should return null when pattern doesn't match")
    void testExtractStringWithPattern_PatternMismatch() {
        String input = "Serial: 12345";  // 5 digits, but pattern expects 3
        String result = StringUtils.extractStringWithPattern(input, "Serial", "\\d{3}");

        assertNull(result, "Should return null when pattern doesn't match");
    }

    @Test
    @DisplayName("Should return null when input is null")
    void testExtractStringWithPattern_NullInput() {
        String result = StringUtils.extractStringWithPattern(null, "Serial", "\\d{3}");

        assertNull(result, "Should return null when input is null");
    }

    @Test
    @DisplayName("Should return null when fixedString is null")
    void testExtractStringWithPattern_NullFixedString() {
        String input = "Serial: 123";
        String result = StringUtils.extractStringWithPattern(input, null, "\\d{3}");

        assertNull(result, "Should return null when fixedString is null");
    }

    @Test
    @DisplayName("Should return null when pattern is null")
    void testExtractStringWithPattern_NullPattern() {
        String input = "Serial: 123";
        String result = StringUtils.extractStringWithPattern(input, "Serial", null);

        assertNull(result, "Should return null when pattern is null");
    }

    // ==================== Tests for extractAllStringsWithPattern() ====================

    @Test
    @DisplayName("Should extract multiple fixed strings with 1-3 digits pattern")
    void testExtractAllStringsWithPattern_Digits() {
        String input = "PartNo. : 1 SerialNo : 99 IdNo: 999";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
                "PartNo.", "SerialNo", "IdNo");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should find 3 matches");
        assertTrue(results.contains("PartNo.:1"));
        assertTrue(results.contains("SerialNo:99"));
        assertTrue(results.contains("IdNo:999"));
    }

    @Test
    @DisplayName("Should extract EPIC format for multiple entries")
    void testExtractAllStringsWithPattern_EPICMultiple() {
        String input = "Voter1: ABC1234567 Voter2: XYZ7654321 Voter3: DEF1111111";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "[a-zA-Z]{3}\\d{7}",
                "Voter1", "Voter2", "Voter3");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should find 3 matches");
        assertEquals("Voter1:ABC1234567", results.get(0));
        assertEquals("Voter2:XYZ7654321", results.get(1));
        assertEquals("Voter3:DEF1111111", results.get(2));
    }

    @Test
    @DisplayName("Should extract ZU format for multiple entries")
    void testExtractAllStringsWithPattern_ZUMultiple() {
        String input = "PrimaryID: ZU12345678 SecondaryID: ZU87654321";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "ZU\\d{8}",
                "PrimaryID", "SecondaryID");

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should find 2 matches");
        assertTrue(results.contains("PrimaryID:ZU12345678"));
        assertTrue(results.contains("SecondaryID:ZU87654321"));
    }

    @Test
    @DisplayName("Should return empty list when no matches for pattern")
    void testExtractAllStringsWithPattern_NoMatches() {
        String input = "Invalid: 12345 Other: ABCDE";  // Numbers exceed 3 digits
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
                "Invalid", "Other");

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should handle partial matches in pattern extraction")
    void testExtractAllStringsWithPattern_PartialMatches() {
        String input = "Item1 : 45 Item2: ABC Item3: 1";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
                "Item1", "Item2", "Item3");

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should find only 2 matches (Item2 has no digits)");
        assertTrue(results.contains("Item1:45"));
        assertTrue(results.contains("Item3:1"));
    }

    @Test
    @DisplayName("Should return empty list for null input in extractAllStringsWithPattern")
    void testExtractAllStringsWithPattern_NullInput() {
        List<String> results = StringUtils.extractAllStringsWithPattern(null, "\\d{1,3}",
                "Item1", "Item2");

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should return empty list for null pattern in extractAllStringsWithPattern")
    void testExtractAllStringsWithPattern_NullPattern() {
        String input = "Item1: 123";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, null,
                "Item1");

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should handle alphanumeric patterns across multiple entries")
    void testExtractAllStringsWithPattern_Alphanumeric() {
        String input = "Code1: ABC123 Code2: XYZ456 Code3: DEF789";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "[A-Z]{3}\\d{3}",
                "Code1", "Code2", "Code3");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should find 3 matches");
        assertEquals("Code1:ABC123", results.get(0));
        assertEquals("Code2:XYZ456", results.get(1));
        assertEquals("Code3:DEF789", results.get(2));
    }

    @Test
    @DisplayName("Should handle special characters in fixed strings with custom pattern")
    void testExtractAllStringsWithPattern_SpecialChars() {
        String input = "Sr. No. : 5 Part-No : 10 Serial_Id: 999";
        List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
                "Sr. No.", "Part-No", "Serial_Id");

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should find 3 matches");
        assertTrue(results.contains("Sr. No.:5"));
        assertTrue(results.contains("Part-No:10"));
        assertTrue(results.contains("Serial_Id:999"));
    }
}
