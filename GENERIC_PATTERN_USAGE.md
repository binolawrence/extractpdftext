# Generic Pattern Extraction - Usage Guide

## Overview
Two new generic methods have been added to `StringUtils` to extract fixed strings with custom regex patterns:
1. `extractStringWithPattern()` - Extract a single fixed string with custom pattern
2. `extractAllStringsWithPattern()` - Extract multiple fixed strings with custom pattern

These methods are more flexible than the existing `extractStringWithNumbers()` and `extractAllStringsWithNumbers()` methods, as they allow you to specify any custom regex pattern.

---

## Method Signatures

### Single Pattern Extraction
```java
public static String extractStringWithPattern(String input, String fixedString, String numberPattern)
```

**Parameters:**
- `input` - The input string to search within
- `fixedString` - The fixed string identifier (e.g., "PartNo.", "Assembly Constituency No")
- `numberPattern` - The regex pattern to match (e.g., "\\d{1,3}" for 1-3 digits)

**Returns:** String in format "fixedString:matchedValue" if match found, null otherwise

---

### Multiple Pattern Extraction
```java
public static List<String> extractAllStringsWithPattern(String input, String numberPattern, String... fixedStrings)
```

**Parameters:**
- `input` - The input string to search within
- `numberPattern` - The regex pattern to match for all fixed strings
- `fixedStrings` - Variable number of fixed string identifiers to search for

**Returns:** List of strings in format "fixedString:matchedValue" for each match found

---

## Usage Examples

### Example 1: Extract 1-3 Digits (Default Behavior)
```java
String input = "PartNo. : 123 SerialNo: 45 ItemId:999";

// Extract single value
String result1 = StringUtils.extractStringWithPattern(input, "PartNo.", "\\d{1,3}");
// Result: "PartNo.:123"

String result2 = StringUtils.extractStringWithPattern(input, "SerialNo", "\\d{1,3}");
// Result: "SerialNo:45"

// Extract multiple values
List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
    "PartNo.", "SerialNo", "ItemId");
// Results: ["PartNo.:123", "SerialNo:45", "ItemId:999"]
```

### Example 2: Extract EPIC Format (3 Letters + 7 Digits)
```java
String input = "Voter1: ABC1234567 Voter2: XYZ7654321 Voter3: DEF1111111";

String result = StringUtils.extractStringWithPattern(input, "Voter1", "[a-zA-Z]{3}\\d{7}");
// Result: "Voter1:ABC1234567"

List<String> results = StringUtils.extractAllStringsWithPattern(input, "[a-zA-Z]{3}\\d{7}",
    "Voter1", "Voter2", "Voter3");
// Results: ["Voter1:ABC1234567", "Voter2:XYZ7654321", "Voter3:DEF1111111"]
```

### Example 3: Extract ZU Format (ZU + 8 Digits)
```java
String input = "PrimaryID: ZU12345678 SecondaryID: ZU87654321 TertiaryID: ZU11111111";

String result = StringUtils.extractStringWithPattern(input, "PrimaryID", "ZU\\d{8}");
// Result: "PrimaryID:ZU12345678"

List<String> results = StringUtils.extractAllStringsWithPattern(input, "ZU\\d{8}",
    "PrimaryID", "SecondaryID", "TertiaryID");
// Results: ["PrimaryID:ZU12345678", "SecondaryID:ZU87654321", "TertiaryID:ZU11111111"]
```

### Example 4: Extract Alphanumeric Codes
```java
String input = "Code1: ABC123 Code2: XYZ456 Code3: DEF789";

String result = StringUtils.extractStringWithPattern(input, "Code1", "[A-Z]{3}\\d{3}");
// Result: "Code1:ABC123"

List<String> results = StringUtils.extractAllStringsWithPattern(input, "[A-Z]{3}\\d{3}",
    "Code1", "Code2", "Code3");
// Results: ["Code1:ABC123", "Code2:XYZ456", "Code3:DEF789"]
```

### Example 5: Extract with Optional Spaces
```java
String input = "PartNo.   :   123 SerialNo : 45 ItemId: 999";

// The methods handle optional spaces before and after colons automatically
List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
    "PartNo.", "SerialNo", "ItemId");
// Results: ["PartNo.:123", "SerialNo:45", "ItemId:999"]
```

### Example 6: Extract with Special Characters in Fixed String
```java
String input = "Sr. No. : 5 Part-No : 10 Serial_Id: 999";

List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
    "Sr. No.", "Part-No", "Serial_Id");
// Results: ["Sr. No.:5", "Part-No:10", "Serial_Id:999"]
```

### Example 7: Extract Real Voter Registration Format
```java
String input = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456";

List<String> results = StringUtils.extractAllStringsWithPattern(input, "\\d{1,3}",
    "PartNo.", "SerialNo");
// Results: ["PartNo.:1", "SerialNo:456"]
```

---

## Common Regex Patterns

| Pattern | Description | Example |
|---------|-------------|---------|
| `\\d{1,3}` | 1-3 digits | 1, 45, 999 |
| `\\d{7}` | Exactly 7 digits | 1234567 |
| `\\d{8}` | Exactly 8 digits | 12345678 |
| `[a-zA-Z]{3}\\d{7}` | 3 letters + 7 digits | ABC1234567 |
| `ZU\\d{8}` | ZU + 8 digits | ZU12345678 |
| `[A-Z]{3}\\d{3}` | 3 uppercase letters + 3 digits | ABC123 |
| `[A-Z0-9]{8}` | 8 alphanumeric chars | ABC12XYZ |
| `\\d{2}-\\d{4}` | 2 digits, dash, 4 digits | 12-3456 |
| `[A-Z]{2}\\d{4}[A-Z]{2}` | Complex pattern | AB1234CD |

---

## Convenience Methods (Maintained for Backward Compatibility)

The following convenience methods still work and internally use the generic methods:

```java
// Extracts 1-3 digits (uses "\\d{1,3}" pattern internally)
String result = StringUtils.extractStringWithNumbers(input, fixedString);

// Extracts multiple values with 1-3 digits pattern
List<String> results = StringUtils.extractAllStringsWithNumbers(input, fixedString1, fixedString2);
```

---

## Key Features

✅ **Generic Pattern Support** - Pass any valid regex pattern
✅ **Handles Optional Spaces** - Works with colons that have spaces before/after
✅ **Special Character Handling** - Properly escapes regex special characters in fixed strings
✅ **Null-Safe** - Returns null or empty list for invalid inputs
✅ **Case-Sensitive** - Matches are case-sensitive by default
✅ **Multiple Delimiters** - Easy to extract from lines with multiple values

---

## Error Handling

All methods follow these error handling rules:

- **Null input** → Returns `null` or empty `List`
- **Empty input** → Returns `null` or empty `List`
- **Pattern not found** → Returns `null` or empty `List`
- **Pattern mismatch** → Returns `null` or empty `List`
- **Invalid regex** → May throw `PatternSyntaxException`

---

## Integration with PDFSearchService

You can use these methods in `PDFSearchService` to extract voter information:

```java
String voterLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456";

// Extract assembly number
String assemblyInfo = StringUtils.extractStringWithPattern(voterLine, 
    "Assembly Constituency No and Name", "[0-9]+-[A-Z]+");

// Extract part and serial numbers
List<String> results = StringUtils.extractAllStringsWithPattern(voterLine, "\\d{1,3}",
    "PartNo.", "SerialNo");
```

---

## Best Practices

1. **Use specific patterns** - More specific patterns are more reliable than broad ones
2. **Test your regex** - Always test your regex patterns beforehand
3. **Handle nulls** - Always check for null returns in production code
4. **Use convenience methods** - For common patterns (1-3 digits), use the convenience methods
5. **Document patterns** - Add comments explaining complex regex patterns


