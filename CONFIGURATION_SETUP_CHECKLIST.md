# Configuration Implementation - Setup Checklist

## ✅ Verification & Setup Checklist

Use this checklist to verify the configuration is working correctly.

---

## Phase 1: Verify Files Exist

- [ ] `src/main/java/com/extract/pdf/extractpdftext/config/PathConfig.java` exists
- [ ] `src/main/resources/application.properties` updated with config properties
- [ ] `src/main/resources/application-dev.properties` exists
- [ ] `src/main/resources/application-prod.properties` exists
- [ ] `src/main/resources/application-test.properties` exists
- [ ] `src/main/resources/application.yml.example` exists
- [ ] Documentation files created (4+ markdown files)

---

## Phase 2: Code Updates

Verify these files were updated:

- [ ] `PDFUploadController.java` has `@Autowired private PathConfig pathConfig;`
- [ ] `PDFIndexService.java` has `@Autowired private PathConfig pathConfig;`
- [ ] `PDFSearchService.java` has `@Autowired private PathConfig pathConfig;`
- [ ] All three services use `pathConfig.getLuceneIndexDir()` or `pathConfig.getPdfDocsDir()`

---

## Phase 3: Build & Test

Run these tests:

- [ ] `mvn clean compile` completes without errors
- [ ] `mvn clean install` builds successfully
- [ ] Tests pass (104 tests)
- [ ] JAR file created in target directory

---

## Phase 4: Configuration Setup

Choose your configuration method:

### Option A: Properties File (Recommended for Most Users)

- [ ] Open `src/main/resources/application.properties`
- [ ] Find lines starting with `app.pdf.docs-dir` and `app.lucene.index-dir`
- [ ] Update paths to your actual directories
- [ ] Ensure directories exist and are accessible
- [ ] Save file

Example:
```properties
app.pdf.docs-dir=C:\\Users\\YourName\\pdfdocs
app.lucene.index-dir=C:\\Users\\YourName\\lucene-index
```

### Option B: Environment Variables (For Deployment)

**Windows:**
- [ ] Open Command Prompt as Administrator
- [ ] Run: `setx PDF_DOCS_DIR "C:\path\to\pdfdocs"`
- [ ] Run: `setx LUCENE_INDEX_DIR "C:\path\to\lucene-index"`
- [ ] Close and reopen Command Prompt
- [ ] Verify with: `echo %PDF_DOCS_DIR%`

**Linux/Mac:**
- [ ] Run: `export PDF_DOCS_DIR="/path/to/pdfdocs"`
- [ ] Run: `export LUCENE_INDEX_DIR="/path/to/lucene-index"`
- [ ] Add to shell profile (~/.bashrc, ~/.zshrc) for persistence
- [ ] Verify with: `echo $PDF_DOCS_DIR`

### Option C: YAML Format (Alternative)

- [ ] Copy `application.yml.example` to `application.yml`
- [ ] (Optional) Rename `application.properties` to `application.properties.bak`
- [ ] Update paths in `application.yml`
- [ ] Delete or disable `application.properties`

---

## Phase 5: Runtime Verification

### Run Application

- [ ] `mvn clean install` (or `mvn spring-boot:run`)

### Check Startup Output

Look for this in console:
```
===== PATH CONFIGURATION =====
PDF Docs Directory: [YOUR_PATH]
  Source: [Source indicator]
Lucene Index Directory: [YOUR_PATH]
  Source: [Source indicator]
==============================
```

- [ ] Paths match your configuration
- [ ] Sources are correctly identified
- [ ] No errors in console output

### Verify Source Priority

**Test 1: Properties file source**
- [ ] Keep properties file configured
- [ ] Run application
- [ ] Verify output shows "application.properties" as source

**Test 2: Environment variable priority**
- [ ] Set environment variable
- [ ] Run application
- [ ] Verify output shows "Environment variable" as source

**Test 3: Default fallback**
- [ ] Remove environment variable
- [ ] Clear properties file
- [ ] Run application
- [ ] Verify output shows "Default hardcoded value"

---

## Phase 6: Directory Verification

Ensure required directories exist:

- [ ] PDF docs directory exists: `dir C:\Users\bfrancis\projects\pdfdocs`
- [ ] Lucene index directory exists: `dir C:\Users\bfrancis\projects\lucene-index`
- [ ] Application has read/write permissions to both directories
- [ ] Directories are not empty (or will be populated by application)

---

## Phase 7: Documentation Review

- [ ] Read `CONFIGURATION_QUICK_REFERENCE.md` (5 min)
- [ ] Read `CONFIGURATION_GUIDE.md` (15 min)
- [ ] Review `CONFIGURATION_IMPLEMENTATION_COMPLETE.md` (10 min)
- [ ] Bookmark these files for future reference

---

## Phase 8: Test with Different Profiles

### Development Profile
- [ ] Run: `mvn clean install -Dspring.profiles.active=dev`
- [ ] Verify dev properties are used

### Production Profile
- [ ] Run: `mvn clean install -Dspring.profiles.active=prod`
- [ ] Verify prod properties are used

### Test Profile
- [ ] Run: `mvn test -Dspring.profiles.active=test`
- [ ] Verify test properties are used

---

## Phase 9: API Testing

Test that services use correct paths:

### Test PDF Upload
- [ ] Call `/pdf/upload` endpoint
- [ ] Verify it reads from configured PDF docs directory
- [ ] Check for errors related to path

### Test PDF Search
- [ ] Call `/pdf/search` endpoint
- [ ] Verify it uses configured Lucene index directory
- [ ] Check for errors related to path

---

## Troubleshooting Checklist

If something doesn't work:

### Configuration Not Loading
- [ ] Check file syntax (escaping backslashes in .properties)
- [ ] Verify property names are correct (`app.pdf.docs-dir`, not `app.pdfdocsdir`)
- [ ] Check for typos in values
- [ ] Verify @Value annotation is present in PathConfig

### Environment Variables Not Working
- [ ] Restart terminal/IDE after setting environment variables
- [ ] Verify variable is set: `echo %PDF_DOCS_DIR%`
- [ ] Check for typos in variable names
- [ ] Ensure variables are set BEFORE running application

### Paths Not Found
- [ ] Verify directories exist
- [ ] Check path formatting (backslashes in Windows, forward slashes in Linux)
- [ ] Verify application has read/write permissions
- [ ] Check for extra spaces in path values

### Wrong Source Showing
- [ ] Review configuration priority order
- [ ] Check for conflicting settings
- [ ] Clear all configurations and set one at a time
- [ ] Review startup output carefully

---

## Success Indicators ✅

You're done when you see:

1. ✅ Application builds without errors
2. ✅ Startup output shows correct paths and sources
3. ✅ Directories exist and are accessible
4. ✅ API endpoints can read from configured directories
5. ✅ No file not found errors related to paths
6. ✅ Configuration changes take effect without code rebuild

---

## Quick Troubleshooting

| Issue | Solution |
|-------|----------|
| Properties not loading | Check property names and escaping |
| Env vars not working | Restart terminal after setting |
| Wrong path used | Check priority - env var > props > default |
| Path not found error | Verify directory exists and is accessible |
| Changes not taking effect | Rebuild with `mvn clean install` |
| Multiple profiles loaded | Only one profile should be active at a time |

---

## Support Resources

- **CONFIGURATION_QUICK_REFERENCE.md** - Quick answers
- **CONFIGURATION_GUIDE.md** - Complete guide with examples
- **CONFIGURATION_IMPLEMENTATION_COMPLETE.md** - Technical details
- **ENVIRONMENT_VARIABLES_GUIDE.md** - Environment variables details
- **PathConfig.java** - Source code with documentation

---

## Final Steps

Once everything passes the checklist:

1. ✅ Commit changes to git (if using version control)
2. ✅ Document your configuration choices for your team
3. ✅ Set up environment-specific configurations (dev, prod)
4. ✅ Create deployment guide for your infrastructure
5. ✅ Train team members on configuration management

---

## Notes Section

Use this space to document your specific configuration:

```
Project-Specific Configuration Notes:
=====================================

PDF Docs Directory: ________________________
Lucene Index Directory: ___________________
Configuration Method Used: ________________
Environment: ______________________________
Last Updated: ______________________________

Additional Notes:
__________________________________________________
__________________________________________________
__________________________________________________
```

---

## Completion Status

- [ ] All phases completed
- [ ] All tests passing
- [ ] Documentation reviewed
- [ ] Team notified
- [ ] Ready for production

**Completion Date:** _________________

**Completed By:** _________________

