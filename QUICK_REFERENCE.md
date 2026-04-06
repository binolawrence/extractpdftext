# Quick Reference - Generic Pattern Extraction

## New Methods Added to StringUtils

### 1. extractStringWithPattern() - Single Extraction
```java
String extractStringWithPattern(String input, String fixedString, String numberPattern)
```

**Quick Examples:**
```java
// 1-3 digits
StringUtils.extractStringWithPattern("PartNo. : 123", "PartNo.", "\\d{1,3}")
→ "PartNo.:123"

// EPIC format (3 letters + 7 digits)
StringUtils.extractStringWithPattern("EPIC: ABC1234567", "EPIC", "[a-zA-Z]{3}\\d{7}")
→ "EPIC:ABC1234567"

// ZU format (ZU + 8 digits)
StringUtils.extractStringWithPattern("ID: ZU12345678", "ID", "ZU\\d{8}")
→ "ID:ZU12345678"
```

---

### 2. extractAllStringsWithPattern() - Multiple Extractions
```java
List<String> extractAllStringsWithPattern(String input, String numberPattern, String... fixedStrings)
```

**Quick Examples:**
```java
// Extract multiple numeric values
StringUtils.extractAllStringsWithPattern(
    "PartNo. : 1 SerialNo : 456 ItemId: 99",
    "\\d{1,3}",
    "PartNo.", "SerialNo", "ItemId"
)
→ ["PartNo.:1", "SerialNo:456", "ItemId:99"]

// Extract multiple EPIC numbers
StringUtils.extractAllStringsWithPattern(
    "Voter1: ABC1234567 Voter2: XYZ7654321",
    "[a-zA-Z]{3}\\d{7}",
    "Voter1", "Voter2"
)
→ ["Voter1:ABC1234567", "Voter2:XYZ7654321"]
```

---

## Backward Compatible Methods

These still work and internally use the generic methods:

```java
// Single extraction (uses "\\d{1,3}" pattern)
StringUtils.extractStringWithNumbers(input, fixedString)

// Multiple extraction (uses "\\d{1,3}" pattern)
StringUtils.extractAllStringsWithNumbers(input, fixedString1, fixedString2, ...)
```

---

## Common Regex Patterns Reference

| Use Case | Pattern | Example |
|----------|---------|---------|
| 1-3 digits | `\\d{1,3}` | 1, 45, 999 |
| Exactly 7 digits | `\\d{7}` | 1234567 |
| Exactly 8 digits | `\\d{8}` | 12345678 |
| EPIC (3L+7D) | `[a-zA-Z]{3}\\d{7}` | ABC1234567 |
| ZU ID (ZU+8D) | `ZU\\d{8}` | ZU12345678 |
| Alphanumeric (3+3) | `[A-Z]{3}\\d{3}` | ABC123 |
| Constituency | `\\d+-[A-Z]+` | 31-TAMBARAM |

---

## Features

✅ Handles optional spaces before and after colons  
✅ Properly escapes regex special characters  
✅ Case-sensitive matching  
✅ Null-safe (returns null or empty list)  
✅ Supports any valid regex pattern  

---

## Error Handling

- **Null input** → Returns `null` or `[]`
- **Pattern not found** → Returns `null` or `[]`
- **Pattern mismatch** → Returns `null` or `[]`

---

## Usage Pattern

**Three simple steps:**

1. **Prepare input string**
   ```java
   String input = "PartNo. : 123 SerialNo: 456";
   ```

2. **Define regex pattern**
   ```java
   String pattern = "\\d{1,3}";  // or any custom pattern
   ```

3. **Extract values**
   ```java
   // Single
   String result = StringUtils.extractStringWithPattern(input, "PartNo.", pattern);
   
   // Multiple
   List<String> results = StringUtils.extractAllStringsWithPattern(input, pattern, 
       "PartNo.", "SerialNo");
   ```

---

## Extract Value Helper

To extract just the value from "key:value" result:
```java
String result = "PartNo.:123";
String value = result.split(":")[1];  // "123"
```

Or use a helper method:
```java
private static String extractValue(String keyValueString) {
    if (keyValueString == null || !keyValueString.contains(":")) {
        return null;
    }
    String[] parts = keyValueString.split(":", 2);
    return parts.length > 1 ? parts[1].trim() : null;
}
```

---

## PDFSearchService Integration

```java
// Extract voter details
String voterLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";

List<String> results = StringUtils.extractAllStringsWithNumbers(voterLine, "PartNo.");
if (!results.isEmpty()) {
    String partNo = extractValue(results.get(0));  // "1"
    voter.setPartNumber(partNo);
}
```

---

## Testing

All 66+ test cases are included in `StringUtilsTest.java`:
- ✅ Single pattern extraction tests
- ✅ Multiple pattern extraction tests
- ✅ Edge cases (null, empty, not found)
- ✅ Special characters handling
- ✅ Multiple spaces handling
- ✅ Different regex patterns (EPIC, ZU, numeric)

Run tests:
```bash
mvn test -Dtest=StringUtilsTest
```

---

## Files Modified/Created

1. **Modified:**
   - `StringUtils.java` - Added generic methods
   - `StringUtilsTest.java` - Added 40+ test cases

2. **Created:**
   - `GENERIC_PATTERN_USAGE.md` - Complete usage guide
   - `PDFSERVICE_INTEGRATION.md` - Integration examples
   - `QUICK_REFERENCE.md` - This file


