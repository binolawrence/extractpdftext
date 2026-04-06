# PDFSearchService Integration Guide

## Overview
This guide shows how to integrate the new generic pattern extraction methods into `PDFSearchService.java` for more flexible voter data extraction.

---

## Current Implementation

In `PDFSearchService.getMatchingLines()`, voter information is extracted like this:

```java
java.util.Map<String, String> splitResult = StringUtils.splitStringByKey(lines[0], "PartNo.:");
String first = splitResult.get("first");
String second = splitResult.get("second");
voter.setAssembly(StringUtils.splitStringByKey(first, ":").get("second"));
voter.setPartSerialNo(StringUtils.splitStringByKey(second, ":").get("second"));
```

---

## Improved Implementation Using New Methods

### Option 1: Using Generic Pattern for 1-3 Digits

```java
// Extract voter information from a line like:
// "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456"

String voterLine = lines[0];

// Extract part number using the convenience method (1-3 digits)
String partNoResult = StringUtils.extractStringWithNumbers(voterLine, "PartNo.");
// Result: "PartNo.:1"

// Extract serial number
String serialNoResult = StringUtils.extractStringWithNumbers(voterLine, "SerialNo");
// Result: "SerialNo:456"

if (partNoResult != null) {
    String partNo = partNoResult.split(":")[1];
    voter.setPartSerialNo(partNo);  // "1"
}

if (serialNoResult != null) {
    String serialNo = serialNoResult.split(":")[1];
    voter.setSerialNo(serialNo);  // "456"
}
```

### Option 2: Using Custom Pattern for Complex Formats

```java
// For custom patterns like EPIC numbers or ZU IDs
String voterLine = lines[0];

// If the line contains EPIC numbers
String epicResult = StringUtils.extractStringWithPattern(
    voterLine, 
    "EPIC", 
    "[a-zA-Z]{3}\\d{7}"
);
// Result: "EPIC:ABC1234567" or null if not found

if (epicResult != null) {
    String epicNumber = epicResult.split(":")[1];
    voter.setEpicNumber(epicNumber);  // "ABC1234567"
}
```

### Option 3: Batch Extract Multiple Values

```java
// Extract multiple voter details from a line in one call
String voterLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456 VoterId: 999";

List<String> results = StringUtils.extractAllStringsWithNumbers(
    voterLine,
    "PartNo.",
    "SerialNo",
    "VoterId"
);

// Results: ["PartNo.:1", "SerialNo:456", "VoterId:999"]

for (String result : results) {
    String[] parts = result.split(":");
    String key = parts[0];
    String value = parts[1];
    
    switch(key) {
        case "PartNo.":
            voter.setPartNumber(value);
            break;
        case "SerialNo":
            voter.setSerialNumber(value);
            break;
        case "VoterId":
            voter.setVoterId(value);
            break;
    }
}
```

---

## Refactored getMatchingLines() Method

Here's how to refactor the `getMatchingLines()` method with the new generic methods:

```java
private List<Voter> getMatchingLines(String content, String[] queryText, String fileName) throws Exception {
    List<Voter> voterMatches = new ArrayList<>();
    String[] lines = content.split("\\r?\\n");
    List<String> terms = normalizeTerms(queryText);
    
    if (terms.isEmpty()) {
        return Collections.emptyList();
    }

    boolean[] termMatched = new boolean[terms.size()];

    for (int lineCount = 0; lineCount < lines.length; lineCount++) {
        Voter voter = new Voter();
        String lowerLine = lines[lineCount].toLowerCase();
        
        for (int i = 0; i < terms.size(); i++) {
            String term = terms.get(i);

            if (i == 0) {
                // Check if term matches Name label
                if (matchesLabel(lowerLine, term, Label.NAME)) {
                    termMatched[i] = true;
                    voter.setName(term);
                    
                    // Get next line for relative name
                    if (lineCount + 1 < lines.length) {
                        lowerLine = lines[++lineCount].toLowerCase();
                        i++;
                        
                        if ((matchesRelativeLabel(lowerLine, terms.get(i), Label.FATHER_NAME)) || 
                            (matchesRelativeLabel(lowerLine, terms.get(i), Label.HUSBAND_NAME))) {
                            
                            termMatched[i] = true;
                            voter.setRelativeName(terms.get(i));
                            
                            // Extract voter details using new generic methods
                            if (lineCount >= 2) {
                                String firstLine = lines[lineCount - 2];
                                String addressLine = lines[lineCount - 1];
                                
                                // Extract Assembly Constituency No and Part/Serial Nos
                                extractVoterDetailsGeneric(firstLine, voter);
                                
                                voter.setAddress(addressLine);
                                voter = getStreetAndPollingStationDetails(fileName, voter);
                                voterMatches.add(voter);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    return voterMatches;
}

/**
 * Extract voter details using new generic pattern methods
 */
private void extractVoterDetailsGeneric(String detailsLine, Voter voter) {
    // Extract assembly info
    String assemblyPattern = "\\d+-[A-Z]+";  // Pattern for "31-TAMBARAM"
    String assemblyResult = StringUtils.extractStringWithPattern(
        detailsLine,
        "Assembly Constituency No and Name",
        assemblyPattern
    );
    
    if (assemblyResult != null) {
        String assembly = assemblyResult.split(":")[1];
        voter.setAssembly(assembly);
    }
    
    // Extract part number and serial number
    List<String> results = StringUtils.extractAllStringsWithNumbers(
        detailsLine,
        "PartNo.",
        "SerialNo"
    );
    
    for (String result : results) {
        String[] parts = result.split(":");
        if (parts.length == 2) {
            String key = parts[0];
            String value = parts[1];
            
            if ("PartNo.".equals(key)) {
                voter.setPartNumber(value);
            } else if ("SerialNo".equals(key)) {
                voter.setSerialNumber(value);
            }
        }
    }
}
```

---

## Extract Method from String Utility

Here's a utility method to simplify extraction:

```java
/**
 * Helper method to extract value from "key:value" format result
 */
private static String extractValue(String keyValueString) {
    if (keyValueString == null || !keyValueString.contains(":")) {
        return null;
    }
    String[] parts = keyValueString.split(":", 2);
    return parts.length > 1 ? parts[1].trim() : null;
}

/**
 * Helper method to extract key from "key:value" format result
 */
private static String extractKey(String keyValueString) {
    if (keyValueString == null || !keyValueString.contains(":")) {
        return null;
    }
    String[] parts = keyValueString.split(":", 2);
    return parts.length > 0 ? parts[0].trim() : null;
}
```

---

## Usage in Different Scenarios

### Scenario 1: Standard Voter List Format
```
Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1
Name : Ramachandran Husband Name : Vijay
22/5, Main Street, Chennai
```

**Extraction Code:**
```java
// Line 0: Assembly info
String firstLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1";
List<String> results = StringUtils.extractAllStringsWithNumbers(
    firstLine, 
    "PartNo."
);
// Results: ["PartNo.:1"]

// Line 1: Voter info (handled by matchesLabel)
// Line 2: Address (direct assignment)
```

### Scenario 2: Extended Voter Format with Serial Number
```
Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456
Name : Ramachandran Husband Name : Vijay
22/5, Main Street, Chennai
```

**Extraction Code:**
```java
String firstLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456";
List<String> results = StringUtils.extractAllStringsWithNumbers(
    firstLine,
    "PartNo.",
    "SerialNo"
);
// Results: ["PartNo.:1", "SerialNo:456"]
```

### Scenario 3: Complex Format with Custom Patterns
```
Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 EPIC: ABC1234567 ZU: ZU12345678
Name : Ramachandran Husband Name : Vijay
22/5, Main Street, Chennai
```

**Extraction Code:**
```java
String firstLine = "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 EPIC: ABC1234567 ZU: ZU12345678";

// Extract EPIC numbers
String epicResult = StringUtils.extractStringWithPattern(
    firstLine,
    "EPIC",
    "[a-zA-Z]{3}\\d{7}"
);

// Extract ZU IDs
String zuResult = StringUtils.extractStringWithPattern(
    firstLine,
    "ZU",
    "ZU\\d{8}"
);

// Extract part number
String partNoResult = StringUtils.extractStringWithNumbers(firstLine, "PartNo.");
```

---

## Benefits of New Generic Methods

| Aspect | Old Method | New Method |
|--------|-----------|-----------|
| Flexibility | Limited to specific patterns | Supports any regex pattern |
| Readability | Multiple string manipulations | Clear single method call |
| Maintainability | Hard to modify patterns | Easy to add new patterns |
| Performance | Multiple regex operations | Optimized single regex |
| Error Handling | Manual null checks | Built-in null handling |

---

## Migration Checklist

- [ ] Replace `splitStringByKey()` calls with `extractStringWithNumbers()` or `extractStringWithPattern()`
- [ ] Update `getMatchingLines()` to use new generic methods
- [ ] Add helper methods `extractValue()` and `extractKey()`
- [ ] Create `extractVoterDetailsGeneric()` method
- [ ] Update voter model to include new fields (EPIC, ZU ID, etc.)
- [ ] Test with sample voter data
- [ ] Update error handling for null results
- [ ] Add logging for debugging

---

## Example Complete Implementation

```java
public Voter extractVoterDetails(String detailsLine, Voter voter) {
    if (detailsLine == null || detailsLine.isEmpty()) {
        return voter;
    }
    
    // Extract assembly info
    String assemblyResult = StringUtils.extractStringWithPattern(
        detailsLine,
        "Assembly Constituency No and Name",
        "[0-9]+-[A-Z]+"
    );
    if (assemblyResult != null) {
        voter.setAssembly(extractValue(assemblyResult));
    }
    
    // Extract all numeric identifiers
    List<String> numericResults = StringUtils.extractAllStringsWithNumbers(
        detailsLine,
        "PartNo.",
        "SerialNo",
        "VoterId"
    );
    
    for (String result : numericResults) {
        String key = extractKey(result);
        String value = extractValue(result);
        
        if ("PartNo.".equals(key)) {
            voter.setPartNumber(value);
        } else if ("SerialNo".equals(key)) {
            voter.setSerialNumber(value);
        } else if ("VoterId".equals(key)) {
            voter.setVoterId(value);
        }
    }
    
    // Extract EPIC if present
    String epicResult = StringUtils.extractStringWithPattern(
        detailsLine,
        "EPIC",
        "[a-zA-Z]{3}\\d{7}"
    );
    if (epicResult != null) {
        voter.setEpicNumber(extractValue(epicResult));
    }
    
    // Extract ZU ID if present
    String zuResult = StringUtils.extractStringWithPattern(
        detailsLine,
        "ZU",
        "ZU\\d{8}"
    );
    if (zuResult != null) {
        voter.setZuId(extractValue(zuResult));
    }
    
    return voter;
}
```


