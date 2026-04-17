package com.extract.pdf.extractpdftext.service;

import com.extract.pdf.extractpdftext.config.PathConfig;
import com.extract.pdf.extractpdftext.util.OcrProcessor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class PDFIndexService {

    private static final Logger logger = LoggerFactory.getLogger(PDFIndexService.class);

    @Autowired
    private PathConfig pathConfig;

    public void indexPDF(File file, String content) throws Exception {
        logger.info("Indexing PDF file: {}", file.getName());
        try {
            Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));
            logger.debug("Opened Lucene index directory: {}", pathConfig.getLuceneIndexDir());
            
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter writer = new IndexWriter(dir, config);
            logger.debug("Created IndexWriter for file: {}", file.getName());

            Document doc = new Document();
            doc.add(new TextField("content", content, Field.Store.NO));
            doc.add(new StringField("filename", file.getName(), Field.Store.YES));

            writer.addDocument(doc);
            logger.debug("Document added to index: {}", file.getName());

            writer.close();
            logger.info("Successfully indexed PDF file: {}", file.getName());
        } catch (Exception e) {
            logger.error("Error indexing PDF file: {}", file.getName(), e);
            throw e;
        }
    }

    /* public void indexPDF(String text, String fileName, String filePath, int page) throws Exception {
        logger.info("Indexing PDF content - File: {}, Page: {}", fileName, page);
        try {
            if (text == null || text.trim().isEmpty()) {
                logger.debug("Skipping indexing - Empty text content for file: {}, page: {}", fileName, page);
                return;
            }

            Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));
            logger.debug("Opened Lucene index directory: {}", pathConfig.getLuceneIndexDir());
            
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter writer = new IndexWriter(dir, config);
            logger.debug("Created IndexWriter for file: {} at page: {}", fileName, page);

            Document doc = new Document();
            doc.add(new TextField("content", text, Field.Store.YES));
            doc.add(new StringField("fileName", fileName, Field.Store.YES));
            doc.add(new StoredField("filePath", filePath));
            doc.add(new IntPoint("pageNumber", page));
            doc.add(new StoredField("pageNumberStored", page));

            writer.addDocument(doc);
            logger.debug("Document added to index - File: {}, Page: {}", fileName, page);

            writer.close();
            logger.info("Successfully indexed PDF content - File: {}, Page: {}, Text length: {}", fileName, page, text.length());
        } catch (Exception e) {
            logger.error("Error indexing PDF content - File: {}, Page: {}", fileName, page, e);
            throw e;
        }
    }*/

    public void indexPDF(String text, String fileName, String filePath, int page) throws Exception {
        logger.info("Indexing PDF content - File: {}, Page: {}", fileName, page);

        try {
            if (text == null || text.trim().isEmpty()) {
                logger.debug("Skipping indexing - Empty text content for file: {}, page: {}", fileName, page);
                return;
            }

            Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));
            logger.debug("Opened Lucene index directory: {}", pathConfig.getLuceneIndexDir());

            // 🔥 Custom analyzer: Standard + NGram per field
            /*Analyzer analyzer = new PerFieldAnalyzerWrapper(
                    new StandardAnalyzer(), // default
                    Map.of(
                            "content_ngram", new Analyzer() {
                                @Override
                                protected TokenStreamComponents createComponents(String fieldName) {
                                    Tokenizer tokenizer = new StandardTokenizer();
                                    TokenStream tokenStream = new LowerCaseFilter(tokenizer);
                                    tokenStream = new NGramTokenFilter(tokenStream, 3, 6,false);
                                    return new TokenStreamComponents(tokenizer, tokenStream);
                                }
                            }
                    )
            );*/


            IndexWriterConfig config = new IndexWriterConfig(PDFSearchService.buildAnalyzerv1());
            IndexWriter writer = new IndexWriter(dir, config);

            logger.debug("Created IndexWriter for file: {} at page: {}", fileName, page);

            Document doc = new Document();

            // ✅ Normal full-word search
            doc.add(new TextField("content", text, Field.Store.YES));

            // ✅ NGram partial search (not stored to reduce index size)
            doc.add(new TextField("content_ngram", text, Field.Store.NO));

            doc.add(new StringField("fileName", fileName, Field.Store.YES));
            doc.add(new StoredField("filePath", filePath));
            doc.add(new IntPoint("pageNumber", page));
            doc.add(new StoredField("pageNumberStored", page));

            writer.addDocument(doc);
            logger.debug("Document added to index - File: {}, Page: {}", fileName, page);

            writer.close();

            logger.info("Successfully indexed PDF content - File: {}, Page: {}, Text length: {}", fileName, page, text.length());

        } catch (Exception e) {
            logger.error("Error indexing PDF content - File: {}, Page: {}", fileName, page, e);
            throw e;
        }
    }
}