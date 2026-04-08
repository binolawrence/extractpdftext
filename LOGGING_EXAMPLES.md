# Logging Examples and Output Samples

## 🎯 Real-World Log Examples

### Example 1: Application Startup with Development Profile
```
2026-04-08 14:23:45.123 [main] INFO  c.e.p.e.ExtractpdftextApplication - Starting ExtractPDF Text Application...
2026-04-08 14:23:45.234 [main] INFO  c.e.p.e.config.PathConfig - ===== PATH CONFIGURATION =====
2026-04-08 14:23:45.235 [main] INFO  c.e.p.e.config.PathConfig - PDF Docs Directory: C:\Users\bfrancis\projects\pdfdocs
2026-04-08 14:23:45.236 [main] INFO  c.e.p.e.config.PathConfig - Source: application.properties (app.pdf.docs-dir)
2026-04-08 14:23:45.237 [main] INFO  c.e.p.e.config.PathConfig - Lucene Index Directory: C:\Users\bfrancis\projects\lucene-index
2026-04-08 14:23:45.238 [main] INFO  c.e.p.e.config.PathConfig - Source: application.properties (app.lucene.index-dir)
2026-04-08 14:23:45.239 [main] INFO  c.e.p.e.config.PathConfig - ==============================
2026-04-08 14:23:45.456 [main] DEBUG c.e.p.e.configs.CorsConfig - Adding CORS mappings for localhost:5173
2026-04-08 14:23:45.457 [main] INFO  c.e.p.e.configs.CorsConfig - CORS configuration completed
2026-04-08 14:23:45.678 [main] INFO  c.e.p.e.listener.ApplicationStartupListener - Application startup event triggered
2026-04-08 14:23:45.679 [main] INFO  c.e.p.e.listener.ApplicationStartupListener - Application is ready and fully initialized
2026-04-08 14:23:45.890 [main] INFO  c.e.p.e.ExtractpdftextApplication - ExtractPDF Text Application started successfully
```

---

### Example 2: PDF Upload and Indexing Operation
```
2026-04-08 14:25:12.123 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFUploadController - Upload endpoint called
2026-04-08 14:25:12.124 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFUploadController - Processing PDFs from folder: C:\Users\bfrancis\projects\pdfdocs
2026-04-08 14:25:12.125 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFUploadController - Found 3 files to process
2026-04-08 14:25:12.126 [http-nio-8080-exec-1] DEBUG c.e.p.e.controller.PDFUploadController - Processing file: voter_list_2024.pdf
2026-04-08 14:25:12.234 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFProcessingService - Starting PDF processing for file: voter_list_2024.pdf
2026-04-08 14:25:12.235 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFProcessingService - PDF document loaded: C:\Users\bfrancis\projects\pdfdocs\voter_list_2024.pdf
2026-04-08 14:25:12.345 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFProcessingService - PDF has 45 pages
2026-04-08 14:25:12.346 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFProcessingService - Processing page 1 of 45
2026-04-08 14:25:12.456 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFProcessingService - Text extracted from page 1. Length: 1245
2026-04-08 14:25:12.567 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFIndexService - Indexing PDF content - File: voter_list_2024.pdf, Page: 1
2026-04-08 14:25:12.568 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFIndexService - Opened Lucene index directory: C:\Users\bfrancis\projects\lucene-index
2026-04-08 14:25:12.569 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFIndexService - Created IndexWriter for file: voter_list_2024.pdf at page: 1
2026-04-08 14:25:12.678 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFIndexService - Document added to index - File: voter_list_2024.pdf, Page: 1
2026-04-08 14:25:12.789 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFIndexService - Successfully indexed PDF content - File: voter_list_2024.pdf, Page: 1, Text length: 1245
...
2026-04-08 14:27:45.123 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFProcessingService - PDF processing completed successfully for file: voter_list_2024.pdf
2026-04-08 14:27:45.234 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFUploadController - PDF indexing completed successfully
```

---

### Example 3: Search Operation
```
2026-04-08 14:30:20.123 [http-nio-8080-exec-2] INFO  c.e.p.e.controller.PDFController - Search endpoint called - Name: Ramesh, FatherName: Kumar, HusbandName: null
2026-04-08 14:30:20.124 [http-nio-8080-exec-2] DEBUG c.e.p.e.controller.PDFController - Invoking search service
2026-04-08 14:30:20.234 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Starting search with parameters - Name: Ramesh, FatherName: Kumar, StreetName: null
2026-04-08 14:30:20.235 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Normalizing 2 terms
2026-04-08 14:30:20.236 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Added normalized term: Ramesh
2026-04-08 14:30:20.237 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Added normalized term: Kumar
2026-04-08 14:30:20.238 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Normalized to 2 valid terms
2026-04-08 14:30:20.345 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Opened Lucene index directory: C:\Users\bfrancis\projects\lucene-index
2026-04-08 14:30:20.346 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Opened IndexReader
2026-04-08 14:30:20.456 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Executing search query: content:ramesh content:kumar
2026-04-08 14:30:20.678 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Found 5 search results
2026-04-08 14:30:20.679 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Processing result - File: voter_list_2024.pdf, Page: 12
2026-04-08 14:30:20.789 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Extracting matching lines from file: voter_list_2024.pdf with 2 search terms
2026-04-08 14:30:20.890 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Found name match: Ramesh
2026-04-08 14:30:20.891 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Found relative name match: Kumar
2026-04-08 14:30:20.892 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Voter Details - Name: Ramesh, Relative: Kumar, Address: Main Street
2026-04-08 14:30:20.893 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Voter record added. Total records: 1
2026-04-08 14:30:20.994 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Full match found. Returning 1 voter records
2026-04-08 14:30:21.045 [http-nio-8080-exec-2] DEBUG c.e.p.e.service.PDFSearchService - Found 1 matching voters in voter_list_2024.pdf at page 12
2026-04-08 14:30:21.145 [http-nio-8080-exec-2] INFO  c.e.p.e.service.PDFSearchService - Search completed. Total results: 1
2026-04-08 14:30:21.246 [http-nio-8080-exec-2] INFO  c.e.p.e.controller.PDFController - Search completed. Found 1 results
```

---

### Example 4: Load PDF Image
```
2026-04-08 14:32:15.123 [http-nio-8080-exec-3] INFO  c.e.p.e.controller.PDFController - LoadPDF endpoint called - FileName: voter_list_2024.pdf, PageNo: 12
2026-04-08 14:32:15.234 [http-nio-8080-exec-3] INFO  c.e.p.e.service.PDFSearchService - Loading PDF from C:\Users\bfrancis\projects\pdfdocs\voter_list_2024.pdf at page 12
2026-04-08 14:32:15.345 [http-nio-8080-exec-3] DEBUG c.e.p.e.service.PDFSearchService - PDF document loaded successfully
2026-04-08 14:32:15.346 [http-nio-8080-exec-3] DEBUG c.e.p.e.service.PDFSearchService - Rendering page 12 with 100 DPI
2026-04-08 14:32:15.567 [http-nio-8080-exec-3] INFO  c.e.p.e.service.PDFSearchService - PDF page rendered successfully. Size: 245678 bytes
2026-04-08 14:32:15.668 [http-nio-8080-exec-3] INFO  c.e.p.e.controller.PDFController - PDF loaded successfully from voter_list_2024.pdf at page 12
```

---

### Example 5: Error Handling
```
2026-04-08 14:35:42.123 [http-nio-8080-exec-4] INFO  c.e.p.e.controller.PDFUploadController - Upload endpoint called
2026-04-08 14:35:42.124 [http-nio-8080-exec-4] INFO  c.e.p.e.controller.PDFUploadController - Processing PDFs from folder: C:\Users\bfrancis\projects\pdfdocs
2026-04-08 14:35:42.125 [http-nio-8080-exec-4] WARN  c.e.p.e.controller.PDFUploadController - No PDF files found in folder: C:\Users\bfrancis\projects\pdfdocs
2026-04-08 14:35:43.123 [http-nio-8080-exec-5] INFO  c.e.p.e.service.PDFProcessingService - Starting PDF processing for file: corrupted.pdf
2026-04-08 14:35:43.234 [http-nio-8080-exec-5] ERROR c.e.p.e.service.PDFProcessingService - Error processing PDF file: corrupted.pdf
java.io.IOException: Invalid PDF structure
    at org.apache.pdfbox.pdmodel.PDDocument.load(PDDocument.java:123)
    at com.extract.pdf.extractpdftext.service.PDFProcessingService.processPDF(PDFProcessingService.java:45)
    ...
```

---

### Example 6: String Utility Operations
```
2026-04-08 14:40:15.123 [http-nio-8080-exec-1] DEBUG c.e.p.e.util.StringUtils - Searching for value 'Ramesh' in input
2026-04-08 14:40:15.124 [http-nio-8080-exec-1] DEBUG c.e.p.e.util.StringUtils - Found matching key-value pair: Name = Ramesh
2026-04-08 14:40:15.234 [http-nio-8080-exec-1] DEBUG c.e.p.e.util.StringUtils - Fetching key-value pairs from input
2026-04-08 14:40:15.235 [http-nio-8080-exec-1] TRACE c.e.p.e.util.StringUtils - Key: Assembly Constituency No and Name, Value: 31-TAMBARAM
2026-04-08 14:40:15.236 [http-nio-8080-exec-1] TRACE c.e.p.e.util.StringUtils - Key: PartNo, Value: 1
2026-04-08 14:40:15.237 [http-nio-8080-exec-1] DEBUG c.e.p.e.util.StringUtils - Extracted 2 key-value pairs
```

---

### Example 7: Production Profile (Minimal Logging)
```
2026-04-08 14:45:20.456 [main] INFO  c.e.p.e.ExtractpdftextApplication - Starting ExtractPDF Text Application...
2026-04-08 14:45:20.567 [main] INFO  c.e.p.e.ExtractpdftextApplication - ExtractPDF Text Application started successfully
2026-04-08 14:45:25.123 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFController - Search endpoint called - Name: Ramesh, FatherName: Kumar, HusbandName: null
2026-04-08 14:45:25.234 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFSearchService - Starting search with parameters - Name: Ramesh, FatherName: Kumar, StreetName: null
2026-04-08 14:45:25.456 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFSearchService - Found 5 search results
2026-04-08 14:45:25.567 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFSearchService - Search completed. Total results: 1
2026-04-08 14:45:25.678 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFController - Search completed. Found 1 results
```
**Note**: No DEBUG messages, only INFO level events

---

## 📊 Log File Sample Content

### File: logs/application.log (Default)
```
2026-04-08 14:23:45.123 [main] INFO  c.e.p.e.ExtractpdftextApplication - Starting ExtractPDF Text Application...
2026-04-08 14:23:45.234 [main] INFO  c.e.p.e.config.PathConfig - ===== PATH CONFIGURATION =====
2026-04-08 14:23:45.235 [main] INFO  c.e.p.e.config.PathConfig - PDF Docs Directory: C:\Users\bfrancis\projects\pdfdocs
2026-04-08 14:25:12.123 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFUploadController - Upload endpoint called
2026-04-08 14:25:12.234 [http-nio-8080-exec-1] INFO  c.e.p.e.service.PDFProcessingService - Starting PDF processing for file: voter_list_2024.pdf
2026-04-08 14:30:20.123 [http-nio-8080-exec-2] INFO  c.e.p.e.controller.PDFController - Search endpoint called - Name: Ramesh, FatherName: Kumar, HusbandName: null
```

---

## 🔍 Analyzing Logs

### Count total requests
```bash
grep "endpoint called" logs/application.log | wc -l
```

### Find errors
```bash
grep "ERROR" logs/application.log
```

### Track specific operation
```bash
grep "Search endpoint called" logs/application.log | tail -20
```

### Monitor performance (time between events)
```bash
grep "INFO  c.e.p.e.service.PDFSearchService" logs/application.log
```

---

## 📈 Log File Growth Example

**Default Configuration**: 10MB max, 10 backup files
```
application.log        → Current file (growing)
application.log.1      → Previous file (10MB)
application.log.2      → 2 files back (10MB)
...
application.log.10     → Oldest kept file (10MB)
Total storage: ~110MB
```

**Production Configuration**: 50MB max, 30 backup files
```
application.log        → Current file
application.log.1      → Previous (50MB)
application.log.2      → 2 files back (50MB)
...
application.log.30     → Oldest (50MB)
Total storage: ~1.5GB (capped at 1GB)
```

---

## 💡 Tips for Reading Logs

1. **Follow Thread**: Look for `[thread-name]` to follow single request
2. **Check Timestamps**: Identify performance bottlenecks by timing
3. **Search Keywords**: Use "ERROR", "WARN", "Search endpoint", "Processing file"
4. **Count Occurrences**: Use `grep | wc -l` to find patterns
5. **Monitor Growth**: Check log file sizes to predict disk usage


