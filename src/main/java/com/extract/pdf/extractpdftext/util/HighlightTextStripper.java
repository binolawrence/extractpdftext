package com.extract.pdf.extractpdftext.util;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HighlightTextStripper extends PDFTextStripper {

    public List<Rectangle2D.Float> matches = new ArrayList<>();
    String searchText;

    public HighlightTextStripper(String searchText) throws IOException, IOException {
        this.searchText = searchText.toLowerCase();
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) {
        String lower = text.toLowerCase();

        if (lower.contains(searchText)) {
            for (TextPosition tp : textPositions) {
                matches.add(new Rectangle2D.Float(
                        tp.getXDirAdj(),
                        tp.getYDirAdj(),
                        tp.getWidthDirAdj(),
                        tp.getHeightDir()
                ));
            }
        }
    }
}