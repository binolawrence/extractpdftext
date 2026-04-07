package com.extract.pdf.extractpdftext.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class to manage file and directory paths
 * Priority order (highest to lowest):
 * 1. Environment variables (PDF_DOCS_DIR, LUCENE_INDEX_DIR)
 * 2. application.properties or application.yml
 * 3. Default hardcoded values
 */
@Component
public class PathConfig {

    // Default directories (fallback if not configured anywhere)
    private static final String DEFAULT_PDF_DOCS_DIR = "C:\\Users\\bfrancis\\projects\\pdfdocs";
    private static final String DEFAULT_LUCENE_INDEX_DIR = "C:\\Users\\bfrancis\\projects\\lucene-index";

    // Environment variable names
    private static final String PDF_DOCS_DIR_ENV = "PDF_DOCS_DIR";
    private static final String LUCENE_INDEX_DIR_ENV = "LUCENE_INDEX_DIR";

    // Read from application.properties/yml with defaults
    @Value("${app.pdf.docs-dir:}")
    private String configuredPdfDocsDir;

    @Value("${app.lucene.index-dir:}")
    private String configuredLuceneIndexDir;

    /**
     * Get the PDF documents directory
     * Priority: Environment Variable > application.properties > Default
     *
     * @return Path to PDF documents directory
     */
    public String getPdfDocsDir() {
        // Check environment variable first (highest priority)
        String envPath = System.getenv(PDF_DOCS_DIR_ENV);
        if (envPath != null && !envPath.trim().isEmpty()) {
            return envPath;
        }

        // Check application.properties/yml
        if (configuredPdfDocsDir != null && !configuredPdfDocsDir.trim().isEmpty()) {
            return configuredPdfDocsDir;
        }

        // Return default
        return DEFAULT_PDF_DOCS_DIR;
    }

    /**
     * Get the Lucene index directory
     * Priority: Environment Variable > application.properties > Default
     *
     * @return Path to Lucene index directory
     */
    public String getLuceneIndexDir() {
        // Check environment variable first (highest priority)
        String envPath = System.getenv(LUCENE_INDEX_DIR_ENV);
        if (envPath != null && !envPath.trim().isEmpty()) {
            return envPath;
        }

        // Check application.properties/yml
        if (configuredLuceneIndexDir != null && !configuredLuceneIndexDir.trim().isEmpty()) {
            return configuredLuceneIndexDir;
        }

        // Return default
        return DEFAULT_LUCENE_INDEX_DIR;
    }

    /**
     * Log the configuration on startup (for debugging)
     */
    public void logConfiguration() {
        System.out.println("\n===== PATH CONFIGURATION =====");
        System.out.println("PDF Docs Directory: " + getPdfDocsDir());
        if (System.getenv(PDF_DOCS_DIR_ENV) != null) {
            System.out.println("  Source: Environment variable (PDF_DOCS_DIR)");
        } else if (configuredPdfDocsDir != null && !configuredPdfDocsDir.trim().isEmpty()) {
            System.out.println("  Source: application.properties (app.pdf.docs-dir)");
        } else {
            System.out.println("  Source: Default hardcoded value");
        }

        System.out.println("Lucene Index Directory: " + getLuceneIndexDir());
        if (System.getenv(LUCENE_INDEX_DIR_ENV) != null) {
            System.out.println("  Source: Environment variable (LUCENE_INDEX_DIR)");
        } else if (configuredLuceneIndexDir != null && !configuredLuceneIndexDir.trim().isEmpty()) {
            System.out.println("  Source: application.properties (app.lucene.index-dir)");
        } else {
            System.out.println("  Source: Default hardcoded value");
        }
        System.out.println("==============================\n");
    }
}
