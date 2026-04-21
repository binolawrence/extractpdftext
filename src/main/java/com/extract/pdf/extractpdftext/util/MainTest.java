package com.extract.pdf.extractpdftext.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainTest {
    public static void main(String[] args) throws Exception {
        String exactTokens="nithinjaasiel";
        System.out.println("Exact tokens: " + exactTokens);
        System.out.println("NGram tokens for 'nithinjaasiel': " + analyze("content_ngram", "nithinjaasiel", buildAnalyzerv1()));
        System.out.println("NGram tokens for 'jaasiel': " + analyze("content_ngram", "jaasiel", buildAnalyzerv1()));

        System.out.println(analyze("content", "bino lawrence", buildAnalyzerv1()));

        //Query q = new TermQuery(new Term("content", "lawrence"));
        //System.out.println(searcher.search(q, 10).totalHits);
    }

    private static List<String> analyze(String field, String text, Analyzer analyzer) throws Exception {
        List<String> result = new ArrayList<>();
        TokenStream ts = analyzer.tokenStream(field, text);
        CharTermAttribute attr = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            result.add(attr.toString());
        }
        ts.end();
        ts.close();
        return result;
    }


    public static Analyzer buildAnalyzerv1() {
        return new PerFieldAnalyzerWrapper(
                new StandardAnalyzer(),
                Map.of(
                        "content_ngram", new Analyzer() {
                            @Override
                            protected TokenStreamComponents createComponents(String fieldName) {
                                Tokenizer tokenizer = new StandardTokenizer();
                                TokenStream tokenStream = new LowerCaseFilter(tokenizer);
                                tokenStream = new NGramTokenFilter(tokenStream, 3, 10, false);
                                return new TokenStreamComponents(tokenizer, tokenStream);
                            }
                        }
                )
        );
    }
}
