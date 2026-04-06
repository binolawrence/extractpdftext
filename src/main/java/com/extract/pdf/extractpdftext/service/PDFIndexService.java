package com.extract.pdf.extractpdftext.service;

import com.extract.pdf.extractpdftext.util.OcrProcessor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

@Service
public class PDFIndexService {
    private final String INDEX_DIR = "C:\\Users\\bfrancis\\projects\\lucene-index";


    public void indexPDF(File file, String content) throws Exception {

        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config =
                new IndexWriterConfig(new StandardAnalyzer());

        IndexWriter writer = new IndexWriter(dir, config);

        Document doc = new Document();

        doc.add(new TextField("content", content, Field.Store.NO));
        doc.add(new StringField("filename", file.getName(), Field.Store.YES));

        writer.addDocument(doc);

        writer.close();
    }


    public void indexPDF(String text,String fileName,String filePath,int page) throws Exception {

        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config =
                new IndexWriterConfig(new StandardAnalyzer());

        IndexWriter writer = new IndexWriter(dir, config);

        Document doc = new Document();

        doc.add(new TextField("content", text, Field.Store.YES));
        doc.add(new StringField("fileName", fileName, Field.Store.YES));
        doc.add(new StoredField("filePath", filePath));
        doc.add(new IntPoint("pageNumber", page));
        doc.add(new StoredField("pageNumberStored", page));
        if (text != null && !text.trim().isEmpty()) {
            writer.addDocument(doc);
        }
        writer.close();
    }
}