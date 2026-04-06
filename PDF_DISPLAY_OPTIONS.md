# PDF Display Options - Highlighted vs Subsection

## Overview

You now have TWO enhanced methods in PDFSearchService for handling PDF display with search results:

---

## Option 1: Highlight Matching Text (FULL PAGE)

### Method: `loadPDF(String fileLocation, int pageNo, String[] searchText)`

**What it does:**
- Returns the **full page** rendered as PNG
- Highlights matching search terms with **visual indicators**
- Shows complete context around matches
- Useful when users need to see surrounding information

**Usage:**
```java
ByteArrayResource pdfImage = pdfSearchService.loadPDF(
    "path/to/voter.pdf",
    1,  // Page number
    new String[]{"Ramesh", "Chennai"}  // Search terms to highlight
);
```

**Output:**
```
┌─────────────────────────────────┐
│   [Full PDF Page Image]         │
│                                 │
│  Matches: Ramesh, Chennai       │ ← Indicator at top
│                                 │
│  [Name: Ramesh]  ← Highlighted  │
│  [Address: Chennai] ← Highlighted│
│  [Other content...]             │
│                                 │
└─────────────────────────────────┘
```

**Advantages:**
- ✅ Full context visible
- ✅ User can see surrounding information
- ✅ All page content available
- ✅ Better for verification

**Disadvantages:**
- ❌ Larger file size
- ❌ User needs to locate matches manually
- ❌ Full page rendered even if only small section matches

---

## Option 2: Display Only Matching Subsection (CROPPED)

### Method: `loadPDFSubsection(String fileLocation, int pageNo, String[] searchText)`

**What it does:**
- Returns only a **cropped section** of the page containing relevant data
- Focuses on the central area where voter data typically appears
- Skips header/footer areas
- Smaller, more focused output

**Usage:**
```java
ByteArrayResource subsectionImage = pdfSearchService.loadPDFSubsection(
    "path/to/voter.pdf",
    1,  // Page number
    new String[]{"Ramesh", "Chennai"}  // Search terms
);
```

**Output:**
```
┌─────────────────────────────────┐
│ Showing results for: Ramesh,... │ ← Header
├─────────────────────────────────┤
│                                 │
│ [Cropped section with matches]  │
│                                 │
│ [Name: Ramesh]                  │
│ [Address: Chennai]              │
│ [Relevant voter data...]        │
│                                 │
└─────────────────────────────────┘
```

**Advantages:**
- ✅ Smaller file size
- ✅ Focused on relevant content
- ✅ Faster transmission
- ✅ Less clutter
- ✅ Mobile-friendly

**Disadvantages:**
- ❌ Less context
- ❌ May miss important surrounding info
- ❌ Fixed crop area may not fit all data

---

## Comparison Table

| Feature | Highlight (Full) | Subsection (Cropped) |
|---------|------------------|----------------------|
| Page Coverage | Full page | Central 90% × 75% |
| File Size | Large | Small |
| Context | Complete | Limited |
| Processing | Fast | Fast |
| Visual Clarity | Moderate | High |
| Mobile-Friendly | No | Yes |
| Use Case | Verification | Quick Preview |

---

## Implementation Details

### Crop Area Definition (Subsection)

The cropped area uses these percentages:
```
Start X:     5% from left edge
Start Y:    15% from top edge (skips header)
Width:      90% of total width
Height:     75% of total height (skips footer)
```

**Why these percentages?**
- Voter forms typically have margins and headers
- Important data is in the middle sections
- Footer areas contain page numbers, etc.
- 90% × 75% captures most relevant content

---

## Code Examples

### Example 1: Display Full Page with Highlights

```java
@GetMapping("/pdf/view")
public ResponseEntity<ByteArrayResource> viewPDF(
        @RequestParam String fileLocation,
        @RequestParam int pageNo,
        @RequestParam String[] searchText) throws Exception {
    
    ByteArrayResource resource = pdfSearchService.loadPDF(
        fileLocation,
        pageNo,
        searchText
    );
    
    return ResponseEntity.ok()
        .header("Content-Type", "image/png")
        .body(resource);
}
```

### Example 2: Display Cropped Subsection

```java
@GetMapping("/pdf/subsection")
public ResponseEntity<ByteArrayResource> viewPDFSubsection(
        @RequestParam String fileLocation,
        @RequestParam int pageNo,
        @RequestParam String[] searchText) throws Exception {
    
    ByteArrayResource resource = pdfSearchService.loadPDFSubsection(
        fileLocation,
        pageNo,
        searchText
    );
    
    return ResponseEntity.ok()
        .header("Content-Type", "image/png")
        .body(resource);
}
```

---

## Method Signatures

### loadPDF() - Full Page with Highlights
```java
@SuppressWarnings("deprecation")
public ByteArrayResource loadPDF(
    String fileLocation,      // Path to PDF file
    int pageNo,               // Page number to extract
    String[] searchText       // Terms to highlight
) throws Exception;
```

### loadPDFSubsection() - Cropped Section Only
```java
@SuppressWarnings("deprecation")
public ByteArrayResource loadPDFSubsection(
    String fileLocation,      // Path to PDF file
    int pageNo,               // Page number to extract
    String[] searchText       // Terms to locate subsection
) throws Exception;
```

### Helper Methods
```java
// Adds visual indicators for matching text
private BufferedImage highlightMatchingText(
    BufferedImage image,
    String[] searchText
);

// Extracts relevant subsection from full page
private BufferedImage extractSubsection(
    BufferedImage image,
    String[] searchText
);
```

---

## Current Highlighting Implementation

The current highlighting method:
1. **Creates a Graphics2D context** on the BufferedImage
2. **Adds a red text indicator** at the top showing matches found
3. **Format**: "Matches: [search term 1], [search term 2], ..."

**Example Output Text:**
```
Matches: Ramesh, Chennai, Father Name
```

### For Enhanced Highlighting (Optional Future Enhancement):

To highlight actual text locations on the page, you would:
1. Use Apache PDFBox's `PDFTextStripper` to extract text positions
2. Map text coordinates to image coordinates
3. Draw colored rectangles around matching text
4. Example: Yellow rectangles around "Ramesh", "Chennai", etc.

---

## Decision Matrix

**Choose `loadPDF()` if:**
- You need complete page context
- Users need to verify surrounding information
- Data accuracy is critical
- Network bandwidth is not a concern

**Choose `loadPDFSubsection()` if:**
- You want fast, focused preview
- Mobile/Web optimization is priority
- File size matters
- Quick scanning is the goal
- You only need voter data (not full document)

---

## Usage Recommendations

### For Search Results Display
```
Search Results → Show Subsection (focused view)
                → If user clicks "View Full Page" → Show Highlights (context)
```

### For Data Verification
```
Data Entry → Show Highlights (verify complete info)
```

### For Mobile Apps
```
Mobile View → Subsection (smaller, faster)
Desktop View → Highlights (full context)
```

---

## Current Behavior

| Method | Returns | Uses searchText |
|--------|---------|-----------------|
| `loadPDF()` | Full page PNG with highlight indicator | ✅ Yes - shows matches at top |
| `loadPDFSubsection()` | Cropped PNG focused on data area | ✅ Yes - adds search info header |

---

## Future Enhancements

### Enhancement 1: Advanced Highlighting
```java
// Draw colored boxes around actual matching text locations
private BufferedImage highlightWithBoxes(BufferedImage image, String[] searchText) {
    // Use PDFTextStripper to get text coordinates
    // Draw colored rectangles around matches
    // Return highlighted image
}
```

### Enhancement 2: Adjustable Crop Area
```java
// Allow callers to specify crop percentages
public ByteArrayResource loadPDFCustomCrop(
    String fileLocation,
    int pageNo,
    String[] searchText,
    int startPercentX,    // Adjustable start
    int cropPercentWidth  // Adjustable width
);
```

### Enhancement 3: Smart Cropping
```java
// Automatically detect relevant content area
// Crop only to areas containing search terms
private BufferedImage smartCrop(BufferedImage image, String[] searchText);
```

---

## Summary

| Aspect | Current Implementation |
|--------|----------------------|
| Full Page Display | ✅ `loadPDF()` with indicator |
| Subsection Display | ✅ `loadPDFSubsection()` with crop |
| Highlighting | ✅ Text indicator at top |
| Advanced Highlighting | ⏳ Future enhancement |
| Responsive Design | ✅ Both methods available |

---

**Recommendation:** 
- Use **`loadPDF()`** for general PDF viewing (default option)
- Use **`loadPDFSubsection()`** for mobile/web optimization
- Both methods are now available in PDFSearchService


