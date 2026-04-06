# Practical Code Examples - Use Cases

## Real-World Scenarios

### Scenario 1: Extract Voter Information from PDF Line

**Input:**
```
"Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456"
```

**Goal:** Extract all three pieces of information

**Code:**
```java
String voterLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456";

// Method 1: Extract each individually
String assemblyResult = StringUtils.extractStringWithPattern(
    voterLine,
    "Assembly Constituency No and Name",
    "\\d+-[A-Z]+"
);
String assembly = extractValue(assemblyResult);  // "31-TAMBARAM"

String partNoResult = StringUtils.extractStringWithNumbers(voterLine, "PartNo.");
String partNo = extractValue(partNoResult);  // "1"

// Method 2: Extract multiple numeric values at once
List<String> numResults = StringUtils.extractAllStringsWithNumbers(
    voterLine,
    "PartNo.",
    "SerialNo"
);

Map<String, String> extracted = new HashMap<>();
for (String result : numResults) {
    String key = extractKey(result);
    String value = extractValue(result);
    extracted.put(key, value);
}
// Result: {PartNo.=1, SerialNo=456}
```

---

### Scenario 2: Extract Voter ID Formats

**Input:**
```
"Voter Details: EPIC ABC1234567 ZuId ZU98765432 Reference XYZ789"
```

**Goal:** Extract EPIC, ZU, and reference numbers with different patterns

**Code:**
```java
String voterLine = "Voter Details: EPIC ABC1234567 ZuId ZU98765432 Reference XYZ789";

// Extract EPIC (3 letters + 7 digits)
String epicResult = StringUtils.extractStringWithPattern(
    voterLine,
    "EPIC",
    "[a-zA-Z]{3}\\d{7}"
);
String epicNumber = extractValue(epicResult);  // "ABC1234567"

// Extract ZU ID (ZU + 8 digits)
String zuResult = StringUtils.extractStringWithPattern(
    voterLine,
    "ZuId",
    "ZU\\d{8}"
);
String zuId = extractValue(zuResult);  // "ZU98765432"

// Extract Reference (3 letters + 3 digits)
String refResult = StringUtils.extractStringWithPattern(
    voterLine,
    "Reference",
    "[A-Z]{3}\\d{3}"
);
String reference = extractValue(refResult);  // "XYZ789"

// Verify formats
boolean validEpic = IdPattern.EPIC.matches(epicNumber);
boolean validZu = IdPattern.ZU.matches(zuId);
```

---

### Scenario 3: Batch Extract Multiple Fields

**Input:**
```java
List<String> voterLines = Arrays.asList(
    "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456",
    "Assembly Constituency No and Name : 32-MYLAPORE PartNo.: 5 SerialNo : 123",
    "Assembly Constituency No and Name : 33-ADYAR PartNo.: 3 SerialNo : 789"
);
```

**Goal:** Extract all part and serial numbers efficiently

**Code:**
```java
public class VoterBatchExtractor {
    
    public List<VoterInfo> extractVoterBatch(List<String> voterLines) {
        List<VoterInfo> results = new ArrayList<>();
        
        for (String line : voterLines) {
            VoterInfo info = new VoterInfo();
            
            // Extract assembly
            String assemblyResult = StringUtils.extractStringWithPattern(
                line,
                "Assembly Constituency No and Name",
                "\\d+-[A-Z]+"
            );
            if (assemblyResult != null) {
                info.setAssembly(extractValue(assemblyResult));
            }
            
            // Extract all numeric identifiers
            List<String> numResults = StringUtils.extractAllStringsWithNumbers(
                line,
                "PartNo.",
                "SerialNo"
            );
            
            for (String result : numResults) {
                String key = extractKey(result);
                String value = extractValue(result);
                
                if ("PartNo.".equals(key)) {
                    info.setPartNo(value);
                } else if ("SerialNo".equals(key)) {
                    info.setSerialNo(value);
                }
            }
            
            results.add(info);
        }
        
        return results;
    }
}

// Usage:
VoterBatchExtractor extractor = new VoterBatchExtractor();
List<VoterInfo> voters = extractor.extractVoterBatch(voterLines);
// Results: 3 voters with extracted data
```

---

### Scenario 4: Handle Missing or Partial Data

**Input:**
```java
String[] voterLines = {
    "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1",  // Missing SerialNo
    "Assembly Constituency No and Name : 32-MYLAPORE",              // Missing PartNo and SerialNo
    "PartNo.: 5 SerialNo : 789",                                    // Missing Assembly
};
```

**Goal:** Safely extract available data without errors

**Code:**
```java
public class RobustVoterExtractor {
    
    public VoterInfo safeExtractVoterInfo(String voterLine) {
        VoterInfo info = new VoterInfo();
        
        if (voterLine == null || voterLine.isEmpty()) {
            return info;  // Return empty info, not null
        }
        
        // Try to extract assembly (optional)
        String assemblyResult = StringUtils.extractStringWithPattern(
            voterLine,
            "Assembly Constituency No and Name",
            "\\d+-[A-Z]+"
        );
        if (assemblyResult != null) {
            info.setAssembly(extractValue(assemblyResult));
        } else {
            info.setAssembly("N/A");
        }
        
        // Try to extract part number (optional)
        String partNoResult = StringUtils.extractStringWithNumbers(voterLine, "PartNo.");
        if (partNoResult != null) {
            info.setPartNo(extractValue(partNoResult));
        } else {
            info.setPartNo("N/A");
        }
        
        // Try to extract serial number (optional)
        String serialNoResult = StringUtils.extractStringWithNumbers(voterLine, "SerialNo");
        if (serialNoResult != null) {
            info.setSerialNo(extractValue(serialNoResult));
        } else {
            info.setSerialNo("N/A");
        }
        
        return info;
    }
}

// Usage:
RobustVoterExtractor extractor = new RobustVoterExtractor();
for (String line : voterLines) {
    VoterInfo info = extractor.safeExtractVoterInfo(line);
    System.out.println(info);  // All have data, some fields are "N/A"
}
```

---

### Scenario 5: Validate Format While Extracting

**Input:**
```
"EPIC: ABC1234567 Valid: Yes"
"EPIC: INVALID Pattern"
```

**Goal:** Extract only if format is valid

**Code:**
```java
public class ValidatedVoterExtractor {
    
    public Optional<String> extractValidEpic(String line) {
        String epicResult = StringUtils.extractStringWithPattern(
            line,
            "EPIC",
            "[a-zA-Z]{3}\\d{7}"
        );
        
        if (epicResult == null) {
            return Optional.empty();
        }
        
        String epicNumber = extractValue(epicResult);
        
        // Validate using IdPattern enum
        if (IdPattern.EPIC.matches(epicNumber)) {
            return Optional.of(epicNumber);
        }
        
        return Optional.empty();
    }
    
    public Optional<String> extractValidZuId(String line) {
        String zuResult = StringUtils.extractStringWithPattern(
            line,
            "ZU",
            "ZU\\d{8}"
        );
        
        if (zuResult == null) {
            return Optional.empty();
        }
        
        String zuId = extractValue(zuResult);
        
        // Validate using IdPattern enum
        if (IdPattern.ZU.matches(zuId)) {
            return Optional.of(zuId);
        }
        
        return Optional.empty();
    }
}

// Usage:
ValidatedVoterExtractor extractor = new ValidatedVoterExtractor();

String line1 = "EPIC: ABC1234567 Valid: Yes";
Optional<String> epic1 = extractor.extractValidEpic(line1);
// Result: Optional[ABC1234567]

String line2 = "EPIC: INVALID Pattern";
Optional<String> epic2 = extractor.extractValidEpic(line2);
// Result: Optional.empty
```

---

### Scenario 6: Cache Patterns for Performance

**Goal:** Improve performance for repeated extractions

**Code:**
```java
public class CachedVoterExtractor {
    
    private static final Map<String, String> PATTERN_CACHE = new HashMap<>();
    
    static {
        PATTERN_CACHE.put("EPIC", "[a-zA-Z]{3}\\d{7}");
        PATTERN_CACHE.put("ZU", "ZU\\d{8}");
        PATTERN_CACHE.put("Assembly", "\\d+-[A-Z]+");
        PATTERN_CACHE.put("Reference", "[A-Z]{3}\\d{3}");
    }
    
    public String extractWithCache(String input, String key, String fixedString) {
        String pattern = PATTERN_CACHE.getOrDefault(key, null);
        
        if (pattern == null) {
            return null;  // Pattern not found in cache
        }
        
        String result = StringUtils.extractStringWithPattern(
            input,
            fixedString,
            pattern
        );
        
        return result != null ? extractValue(result) : null;
    }
}

// Usage:
CachedVoterExtractor extractor = new CachedVoterExtractor();
String line = "Voter: ABC1234567 ID: ZU12345678";

String epic = extractor.extractWithCache(line, "EPIC", "Voter");      // "ABC1234567"
String zuId = extractor.extractWithCache(line, "ZU", "ID");           // "ZU12345678"
```

---

### Scenario 7: Stream Processing Multiple Lines

**Goal:** Process large number of voter lines efficiently

**Code:**
```java
public class StreamVoterProcessor {
    
    public List<VoterInfo> processVoterStream(List<String> voterLines) {
        return voterLines.stream()
            .filter(line -> line != null && !line.isEmpty())
            .map(this::extractVoterInfo)
            .filter(info -> info.isValid())  // Filter out invalid entries
            .collect(Collectors.toList());
    }
    
    private VoterInfo extractVoterInfo(String line) {
        VoterInfo info = new VoterInfo();
        
        // Extract assembly
        String assemblyResult = StringUtils.extractStringWithPattern(
            line,
            "Assembly Constituency No and Name",
            "\\d+-[A-Z]+"
        );
        if (assemblyResult != null) {
            info.setAssembly(extractValue(assemblyResult));
        }
        
        // Extract numeric values
        List<String> numResults = StringUtils.extractAllStringsWithNumbers(
            line,
            "PartNo.",
            "SerialNo"
        );
        
        numResults.forEach(result -> {
            String key = extractKey(result);
            String value = extractValue(result);
            if ("PartNo.".equals(key)) {
                info.setPartNo(value);
            } else if ("SerialNo".equals(key)) {
                info.setSerialNo(value);
            }
        });
        
        return info;
    }
}

// Usage:
StreamVoterProcessor processor = new StreamVoterProcessor();
List<VoterInfo> validVoters = processor.processVoterStream(allVoterLines);

// With parallel processing
List<VoterInfo> parallelVoters = voterLines.parallelStream()
    .filter(line -> line != null && !line.isEmpty())
    .map(this::extractVoterInfo)
    .filter(VoterInfo::isValid)
    .collect(Collectors.toList());
```

---

### Scenario 8: Custom Pattern for Special Cases

**Goal:** Extract data with unique patterns not in standard list

**Code:**
```java
public class CustomPatternExtractor {
    
    // Pattern for "State-District-Region" format
    private static final String LOCATION_PATTERN = "[A-Z]{2}-\\d{2}-[A-Z]{3}";
    
    // Pattern for "YYYY-MM-DD" dates
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
    
    // Pattern for 10-digit phone numbers
    private static final String PHONE_PATTERN = "\\d{10}";
    
    public String extractLocation(String input, String locationName) {
        String result = StringUtils.extractStringWithPattern(
            input,
            locationName,
            LOCATION_PATTERN
        );
        return result != null ? extractValue(result) : null;
    }
    
    public String extractDate(String input, String dateLabel) {
        String result = StringUtils.extractStringWithPattern(
            input,
            dateLabel,
            DATE_PATTERN
        );
        return result != null ? extractValue(result) : null;
    }
    
    public String extractPhone(String input, String phoneLabel) {
        String result = StringUtils.extractStringWithPattern(
            input,
            phoneLabel,
            PHONE_PATTERN
        );
        return result != null ? extractValue(result) : null;
    }
}

// Usage:
CustomPatternExtractor extractor = new CustomPatternExtractor();

String line = "Location: UP-27-CHI DOB: 1990-05-15 Phone: 9876543210";

String location = extractor.extractLocation(line, "Location:");       // "UP-27-CHI"
String dob = extractor.extractDate(line, "DOB:");                     // "1990-05-15"
String phone = extractor.extractPhone(line, "Phone:");                // "9876543210"
```

---

## Helper Methods

### Extract Value
```java
private static String extractValue(String keyValueString) {
    if (keyValueString == null || !keyValueString.contains(":")) {
        return null;
    }
    String[] parts = keyValueString.split(":", 2);
    return parts.length > 1 ? parts[1].trim() : null;
}
```

### Extract Key
```java
private static String extractKey(String keyValueString) {
    if (keyValueString == null || !keyValueString.contains(":")) {
        return null;
    }
    String[] parts = keyValueString.split(":", 2);
    return parts.length > 0 ? parts[0].trim() : null;
}
```

### Validate ID Pattern
```java
private static boolean isValidId(String id, String pattern) {
    if (id == null || pattern == null) {
        return false;
    }
    return Pattern.compile(pattern).matcher(id).matches();
}
```

---

## Testing Code Examples

```java
// Test single extraction
@Test
public void testExtractVoterAssembly() {
    String line = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
    String result = StringUtils.extractStringWithPattern(
        line,
        "Assembly Constituency No and Name",
        "\\d+-[A-Z]+"
    );
    assertEquals("Assembly Constituency No and Name:31-TAMBARAM", result);
}

// Test multiple extraction
@Test
public void testExtractMultipleValues() {
    String line = "PartNo.: 1 SerialNo : 456 ItemId: 99";
    List<String> results = StringUtils.extractAllStringsWithNumbers(
        line,
        "PartNo.",
        "SerialNo",
        "ItemId"
    );
    assertEquals(3, results.size());
    assertTrue(results.contains("PartNo.:1"));
}

// Test with missing data
@Test
public void testExtractWithMissingData() {
    String line = "PartNo.: 1";  // Missing SerialNo
    List<String> results = StringUtils.extractAllStringsWithNumbers(
        line,
        "PartNo.",
        "SerialNo"
    );
    assertEquals(1, results.size());  // Only PartNo found
}

// Test with invalid pattern
@Test
public void testExtractWithInvalidPattern() {
    String line = "Serial: ABC123";  // Doesn't match numeric pattern
    String result = StringUtils.extractStringWithNumbers(line, "Serial");
    assertNull(result);  // Pattern doesn't match
}
```

---

## Performance Tips

1. **Cache Patterns**
   ```java
   private static final Pattern EPIC_PATTERN = Pattern.compile("[a-zA-Z]{3}\\d{7}");
   ```

2. **Batch Operations**
   ```java
   List<String> results = StringUtils.extractAllStringsWithNumbers(
       line,
       "PartNo.", "SerialNo", "ItemId"  // All at once
   );
   ```

3. **Early Exit**
   ```java
   if (result == null) {
       return defaultValue;  // Don't continue processing
   }
   ```

4. **Reuse Strings**
   ```java
   String key = extractKey(result);  // Extract once, use multiple times
   ```


