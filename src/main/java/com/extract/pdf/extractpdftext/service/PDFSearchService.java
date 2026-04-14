package com.extract.pdf.extractpdftext.service;

import com.extract.pdf.extractpdftext.config.PathConfig;
import com.extract.pdf.extractpdftext.pojo.SearchResult;
import com.extract.pdf.extractpdftext.pojo.Voter;
import com.extract.pdf.extractpdftext.util.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PDFSearchService {

    private static final Logger logger = LoggerFactory.getLogger(PDFSearchService.class);

    @Autowired
    private PathConfig pathConfig;

    // Enum for predefined label constants
    public enum Label {
        NAME("Name:"),
        HUSBAND_NAME("Husband Name:"),
        FATHER_NAME("Father Name:"),
        MOTHER_NAME("Mother Name:");
        private final String value;

        Label(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    private String fileName;

    // Regex patterns for common ID formats

    // EPIC_PATTERN - Kept for reference, validation uses IdPattern.EPIC enum
    // private static final String EPIC_PATTERN = "^[a-zA-Z]{3}\\d{7}$";

    private List<String> normalizeTerms(String... rawTerms) {
        List<String> terms = new ArrayList<>();
        for (String raw : rawTerms) {
            if (raw != null) {
                String trimmed = raw.trim();
                if (!trimmed.isEmpty()) {
                    terms.add(trimmed);
                }
            }
        }
        return terms;
    }


    @SuppressWarnings("deprecation")
    public List<SearchResult> search(String name, String relativeName, String streetName) throws Exception {

        Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));
        boolean relativeNamePresent = relativeName != null && !relativeName.isBlank();

        boolean streetNamePresent = streetName != null && !streetName.isBlank();

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        QueryParser parser = new QueryParser("content", new StandardAnalyzer());
        parser.setDefaultOperator(QueryParser.Operator.AND);

        List<String> normalizedTerms = normalizeTerms(name, relativeName, streetName);
        if (normalizedTerms.isEmpty()) {
            reader.close();
            return Collections.emptyList();
        }
        String[] searchText = normalizedTerms.toArray(new String[0]);

        /*for (String search : searchText) {
            if (OCRFixer.isAlphanumeric(search)) {
                text = text.replaceFirst(search.toUpperCase(), OCRFixer.fixOCR(search.toUpperCase()));
            }
        }*/
        String queryString = String.join(" ", normalizedTerms);
        Query query = parser.parse(String.join(" ", normalizedTerms));
        List<SearchResult> searchResults = new ArrayList<>();
        TopDocs results = searcher.search(query, 20);
        for (ScoreDoc sd : results.scoreDocs) {
            Document d = searcher.doc(sd.doc);

            fileName = d.get("fileName");
            String content = d.get("content");
            System.out.println("content: " + content);
            int page = Integer.parseInt(d.get("pageNumberStored"));
            String fileLocation = d.get("filePath");
            //String.setFileLocation(fileLocation);
            //searchResult.setPageNo(page);
            //searchResult.setFileName(fileName);
            //searchResult.setName(name);
            //searchResult.setRelativeName(relativeName);
            //searchResult = getStreetPollingStationDetails(fileName, searchResult);
            //searchResult = getAddressDetails(searchResult, fileName, content);
            //System.out.println(fileName + " | Page: " + page);
            //System.out.println("fileLocation: " + fileLocation);
            //searchResults.add(searchResult);


            List<Voter> voters = getMatchingLines(content, searchText, fileName, relativeNamePresent, streetNamePresent);
            voters.forEach(v -> {
                SearchResult searchResult = new SearchResult();
                searchResult.setFileLocation(fileLocation);
                searchResult.setPageNo(page);
                searchResult.setFileName(fileName);
                searchResult.setName(v.getName());
                searchResult.setRelativeName(v.getRelativeName());
                searchResult.setStreetName(v.getAddress());
                searchResult.setPollingStation(v.getGetPollingStation());
                searchResults.add(searchResult);
            });
        }
        //searchResults.add(fileName);

            /*searchResults.addAll(voters);
            System.out.println("File: " + d.get("fileName"));
            System.out.println("Page: " + d.get("pageNumberStored"));

            if (!voters.isEmpty()) {
                voters.forEach(v -> {
                    String voterName = v.getName() != null ? v.getName() : "N/A";
                    String relative = v.getRelativeName() != null ? v.getRelativeName() : "N/A";
                    String address = v.getAddress() != null ? v.getAddress() : "N/A";
                    System.out.printf("Name: %s, Relative Name: %s, Address: %s%n", voterName, relative, address);
                });
            } else {
                System.out.println("No matching voters found for the given search criteria.");
            }

        }
        reader.close();
        Display Street and Polling Station Details
        searchResults.addAll(getStreetAndPollingStationDetails(fileName));*/
        if (searchResults.isEmpty()) {
            return Collections.emptyList();
        }
        return searchResults;
    }


    public ByteArrayResource loadPDF(String fileLocation, int pageNo) throws Exception {
        // Construct full file path: if fileLocation is just a filename, prepend the PDF docs directory
        String fullFilePath = fileLocation;
        if (!new File(fileLocation).isAbsolute()) {
            fullFilePath = new File(pathConfig.getPdfDocsDir(), fileLocation).getAbsolutePath();
        }

        logger.debug("Loading PDF from: {}", fullFilePath);

        try (PDDocument document = PDDocument.load(new File(fullFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int dpi = 100;  // Reduced from 150 to fit single view - no horizontal scroll needed

            // Extract the requested page as an image (pageNumber - 1 because PDF pages are 0-indexed)
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNo - 1, dpi);

            // Convert BufferedImage to ByteArray with proper resource management
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                logger.debug("PDF page {} rendered successfully as PNG image", pageNo);
                return new ByteArrayResource(imageBytes);
            }
        } catch (Exception e) {
            logger.error("Error loading PDF from {}", fullFilePath, e);
            throw e;
        }
    }


    // ...existing code...


    // ...existing code...


    // ...existing code...


    @SuppressWarnings("deprecation")
    private Voter getStreetAndPollingStationDetails(String fileName, Voter voter) throws Exception {

        Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        //Get the first page contents

        Query fileQuery = new TermQuery(new Term("fileName", fileName));
        Query pageQuery = IntPoint.newExactQuery("pageNumber", 1);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(fileQuery, BooleanClause.Occur.MUST);
        builder.add(pageQuery, BooleanClause.Occur.MUST);

        Query finalQuery = builder.build();


        TopDocs firstPageResult = searcher.search(finalQuery, 10);

        String content = "";
        for (ScoreDoc sd : firstPageResult.scoreDocs) {
            Document d = searcher.doc(sd.doc);

            System.out.println("File: " + d.get("fileName"));
            System.out.println("Page: " + d.get("page"));
            content = d.get("content");
            break;
        }

        //End getting the first page contents
        return fetchPollingStationDetails(content, voter);

    }


    private SearchResult getStreetPollingStationDetails(String fileName, SearchResult searchResult) throws Exception {

        Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        //Get the first page contents

        Query fileQuery = new TermQuery(new Term("fileName", fileName));
        Query pageQuery = IntPoint.newExactQuery("pageNumber", 1);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(fileQuery, BooleanClause.Occur.MUST);
        builder.add(pageQuery, BooleanClause.Occur.MUST);

        Query finalQuery = builder.build();


        TopDocs firstPageResult = searcher.search(finalQuery, 10);

        String content = "";
        for (ScoreDoc sd : firstPageResult.scoreDocs) {
            Document d = searcher.doc(sd.doc);

            System.out.println("File: " + d.get("fileName"));
            System.out.println("Page: " + d.get("page"));
            content = d.get("content");
            break;
        }

        //End getting the first page contents
        return searchPollingStationDetails(content, searchResult);

    }

    private Boolean getAllMatch(String content, List<String> terms) {

        String[] lines = content.split("\\r?\\n");
        boolean[] termMatched = new boolean[terms.size()];

        for (String line : lines) {
            String lowerLine = line.toLowerCase();

            for (int i = 0; i < terms.size(); i++) {
                String term = terms.get(i);
                if (lowerLine.contains(term.toLowerCase())) {
                    termMatched[i] = true;
                }
            }

        }

        // Check if all terms are matched
        for (boolean matched : termMatched) {
            if (!matched) {
                return false;
            }
        }

        return true;
    }


    /**
     * Generic method to check if a term matches a specific label type in a line
     * Excludes matches from other label types to avoid conflicts
     * Pattern accepts optional spaces before and after the colon (e.g., "name : term", "name: term", "name :term")
     */
    @SuppressWarnings("all")
    private boolean matchesLabel(String lowerLine, String term, Label targetLabel) {
        String labelPrefix = targetLabel.getValue().toLowerCase().replace(":", "").trim();
        // Pattern: labelPrefix + optional spaces + : + optional spaces + term
        String searchPattern = labelPrefix + "\\s*:\\s*" + term.toLowerCase();

        return lowerLine.matches(".*" + searchPattern + ".*");
    }

    private boolean matchesRelativeLabel(String lowerLine, String term, Label targetLabel) {
        String labelPrefix = targetLabel.getValue().toLowerCase().replace(":", "").trim();
        // Pattern: labelPrefix + optional spaces + : + optional spaces + term
        String searchPattern = labelPrefix + "\\s*:\\s*" + term.toLowerCase();

        if (!lowerLine.matches(".*" + searchPattern + ".*")) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("all")
    private List<Voter> getMatchingLines(String content, String[] queryText, String fileName, boolean relativeNamePresent, boolean streetNamePresent) throws Exception {
        List<Voter> voterMatches = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        List<String> terms = normalizeTerms(queryText);
        if (terms.isEmpty()) {
            return Collections.emptyList();
        }

        boolean nameMatch = false;
        boolean relativeMatch = false;
        boolean streetMatch = false;

        Voter voter = null;


        for (int lineCount = 0; lineCount < lines.length; lineCount++) {

            String lowerLine = lines[lineCount].toLowerCase();

            for (int i = 0; i < terms.size(); i++) {
                String term = terms.get(i).toLowerCase();

                if (i == 0) {
                    // Check if term matches Name label (and not HusbandName or FatherName)
                    if (matchesLabel(lowerLine, term, Label.NAME)) {
                        voter = new Voter();
                        //Extract the name
                        Pattern pattern = Pattern.compile("Nam[a-z]*\\s*:\\s*(.*?)(?=Nam[a-z]*\\s*:|$)", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(lowerLine);

                        List<String> names = new ArrayList<>();

                        while (matcher.find()) {
                            names.add(matcher.group(1).trim());
                            System.out.println("Extracted name: " + matcher.group(1).trim());
                        }
                        for (String name : names) {
                            if (name.toLowerCase().contains(term.toLowerCase())) {
                                voter.setName(name);
                            }
                        }
                        nameMatch = true;


                        if (nameMatch && relativeNamePresent) {
                            lowerLine = lines[++lineCount].toLowerCase();
                            System.out.println(lowerLine);
                            i = i + 1;
                            if ((matchesRelativeLabel(lowerLine, terms.get(i), Label.FATHER_NAME)) || (matchesRelativeLabel(lowerLine, terms.get(i), Label.HUSBAND_NAME) ||(matchesRelativeLabel(lowerLine, terms.get(i), Label.MOTHER_NAME)))) {

                                //Extract and set the relative name

                                List<String> relativeNames = new ArrayList<>();

                                Pattern relativePattern = Pattern.compile(
                                        "(?:Father|Husband|Mother)\\s*Nam[a-z]*\\s*:\\s*(.*?)(?=\\s*-|(?:Father|Husband|Mother)\\s*Nam[a-z]*\\s*:|$)",
                                        Pattern.CASE_INSENSITIVE
                                );

                                Matcher relativeMatcher = relativePattern.matcher(lowerLine);

                                while (relativeMatcher.find()) {
                                    String value = relativeMatcher.group(1).trim();
                                    if (!value.isEmpty()) {
                                        relativeNames.add(value);
                                        System.out.println("Extracted relative name: " + value);
                                    }
                                }
                                for (int namesno = 0; namesno <names.size(); namesno ++) {
                                    if (names.get(namesno).toLowerCase().contains(terms.get(0).toLowerCase())) {
                                        System.out.println("Matching name for relative: " + names.get(namesno));
                                        System.out.println("No: " + namesno);
                                        voter.setRelativeName(relativeNames.get(namesno));
                                    }
                                }
                                relativeMatch = true;
                                voter.setAddress(StringUtils.extractAfterMatch(lines[1], "Section No and Name"));
                                //voter.setAddress(lines[1]);
                                Map<String, String> mapResultConstituency = StringUtils.splitStringByKeyIgnoreCase(
                                        lines[0],
                                        ":");
                                String assembly = mapResultConstituency.get("second");
                                voter.setAssembly(assembly);
                                voter.setWardNo(StringUtils.extractStringWithPattern(lines[1], "Ward", "\\s*(\\d+)\\b"));
                                // Extract voter details using new generic pattern methods
                                extractVoterDetailsUsingPatterns(lines[0], voter);
                                voter = getStreetAndPollingStationDetails(fileName, voter);
                                voter.setDistrict(StringUtils.extractAfterMatch(voter.getPart(), "Distriot :"));
                                names.clear();
                                relativeNames.clear();
                                 voterMatches.add(voter);
                                //break;
                            }
                        }else if (!relativeNamePresent && nameMatch) {
                            {
                                List<String> relativeNames = new ArrayList<>();
                                lowerLine = lines[++lineCount].toLowerCase();
                                Pattern relativePattern = Pattern.compile(
                                        "(?:Father|Husband|Mother)\\s*Nam[a-z]*\\s*:\\s*(.*?)(?=\\s*-|(?:Father|Husband|Mother)\\s*Nam[a-z]*\\s*:|$)",
                                        Pattern.CASE_INSENSITIVE
                                );

                                Matcher relativeMatcher = relativePattern.matcher(lowerLine);
                                System.out.println("printing relative line");
                                System.out.println(lowerLine);
                                while (relativeMatcher.find()) {
                                    String value = relativeMatcher.group(1).trim();
                                    if (!value.isEmpty()) {
                                        relativeNames.add(value);
                                    }
                                }
                                System.out.println("Names count:"+names.size());
                                System.out.println("RElatives count:"+relativeNames.size());

                                for (int n = 0; n < names.size(); n++) {
                                    if (voter.getName().contains(names.get(n))) {
                                        voter.setRelativeName(relativeNames.get(n));
                                        break;
                                    }
                                }

                                voter.setAddress(StringUtils.extractAfterMatch(lines[1], "Section No and Name"));
                                //voter.setAddress(lines[1]);
                                Map<String, String> mapResultConstituency = StringUtils.splitStringByKeyIgnoreCase(
                                        lines[0],
                                        ":");
                                String assembly = mapResultConstituency.get("second");
                                voter.setAssembly(assembly);
                                voter.setWardNo(StringUtils.extractStringWithPattern(lines[1], "Ward", "\\s*(\\d+)\\b"));
                                // Extract voter details using new generic pattern methods
                                extractVoterDetailsUsingPatterns(lines[0], voter);
                                voter = getStreetAndPollingStationDetails(fileName, voter);
                                voter.setDistrict(StringUtils.extractAfterMatch(voter.getPart(), "Distriot :"));

                                names.clear();
                                relativeNames.clear();
                                voterMatches.add(voter);
                            }
                        }
                    }
                }
                if (streetNamePresent) {
                    // Check if street matches
                    /*if (relativeNamePresent) {
                        term = terms.get(2);
                    } else {
                        term = terms.get(1);
                    }
                    if (lines[1].contains(term.toLowerCase())) {
                        Map<String, String> mapResultConstituency = StringUtils.splitStringByKeyIgnoreCase(
                                lines[0],
                                ":");
                        String assembly = mapResultConstituency.get("second");
                        voter.setAssembly(assembly);
                        voter.setWardNo(StringUtils.extractStringWithPattern(lines[1], "Ward", "\\s*(\\d+)\\b"));
                        // Extract voter details using new generic pattern methods
                        extractVoterDetailsUsingPatterns(lines[0], voter);
                        voter = getStreetAndPollingStationDetails(fileName, voter);
                        voter.setDistrict(StringUtils.extractAfterMatch(voter.getPart(), "Distriot :"));
                        voterMatches.add(voter);
                    }
                   nameMatch=false;*/
                }else if (nameMatch) {
                   /* Map<String, String> mapResultConstituency = StringUtils.splitStringByKeyIgnoreCase(
                            lines[0],
                            ":");
                    String assembly = mapResultConstituency.get("second");
                    voter.setAssembly(assembly);
                    voter.setWardNo(StringUtils.extractStringWithPattern(lines[1], "Ward", "\\s*(\\d+)\\b"));
                    // Extract voter details using new generic pattern methods
                    extractVoterDetailsUsingPatterns(lines[0], voter);
                    voter = getStreetAndPollingStationDetails(fileName, voter);
                    voter.setDistrict(StringUtils.extractAfterMatch(voter.getPart(), "Distriot :"));
                    voterMatches.add(voter);
                    names.clear();;
                   nameMatch=false;
                   relativeMatch=false;
*/
                }
            }
        }

        // Check if all terms are matched


        if (voterMatches != null) {
            return voterMatches;
        }
        return Collections.emptyList();
    }

    private List<String> setPollingStationDetails(String content) {
        List<String> partAndPollingArea = new ArrayList<>();
        List<String> pollingStation = new ArrayList<>();
        List<String> pollingStationAddress = new ArrayList<>();
        List<String> pollingLocationDetails = new ArrayList<>();
        boolean partAndPollingAreaFlagFetching = false;
        boolean pollingStationFlagFetching = false;
        boolean pollingStationAddressFlagFetching = false;

        String[] lines = content.split("\\r?\\n");
        System.out.println("lines content:");
        Arrays.stream(lines).forEach(System.out::println);
        String[] terms = new String[]{"Details of part and polling area", "Polling station details", "Address of Polling Station :", "NUMBER OF ELECTORS"};
        for (String line : lines) {
            if (line.contains(terms[0])) {
                partAndPollingAreaFlagFetching = true;
                pollingStationAddressFlagFetching = false;
                pollingStationFlagFetching = false;
            }

            if (line.contains(terms[1])) {
                pollingStationFlagFetching = true;
                partAndPollingAreaFlagFetching = false;
                pollingStationAddressFlagFetching = false;

            }

            if (line.contains(terms[2])) {
                pollingStationAddressFlagFetching = true;
                partAndPollingAreaFlagFetching = false;
                pollingStationFlagFetching = false;
            }

            if (line.contains(terms[3])) {
                break;
            }

            if (partAndPollingAreaFlagFetching) {
                partAndPollingArea.add(line);
            }
            if (pollingStationAddressFlagFetching) {
                pollingStationAddress.add(line);

            }
            if (pollingStationFlagFetching) {
                pollingStation.add(line);
            }

        }
        pollingLocationDetails.addAll(partAndPollingArea);
        pollingLocationDetails.addAll(pollingStation);
        pollingLocationDetails.addAll(pollingStationAddress);
        return pollingLocationDetails;
    }


    private Voter fetchPollingStationDetails(String content, Voter voter) {
        List<String> partAndPollingArea = new ArrayList<>();
        List<String> pollingStation = new ArrayList<>();
        List<String> pollingStationAddress = new ArrayList<>();
        List<String> pollingLocationDetails = new ArrayList<>();
        boolean partAndPollingAreaFlagFetching = false;
        boolean pollingStationFlagFetching = false;
        boolean pollingStationAddressFlagFetching = false;

        String[] lines = content.split("\\r?\\n");
        System.out.println("lines content:");
        Arrays.stream(lines).forEach(System.out::println);
        String[] terms = new String[]{"Details of part and polling area", "Polling station details", "Address of Polling Station :", "NUMBER OF ELECTORS"};
        for (String line : lines) {
            if (line.contains(terms[0])) {
                partAndPollingAreaFlagFetching = true;
                pollingStationAddressFlagFetching = false;
                pollingStationFlagFetching = false;
            }

            if (line.contains(terms[1])) {
                pollingStationFlagFetching = true;
                partAndPollingAreaFlagFetching = false;
                pollingStationAddressFlagFetching = false;

            }

            if (line.contains(terms[2])) {
                pollingStationAddressFlagFetching = true;
                partAndPollingAreaFlagFetching = false;
                pollingStationFlagFetching = false;
            }

            if (line.contains(terms[3])) {
                break;
            }

            if (partAndPollingAreaFlagFetching) {
                partAndPollingArea.add(line);
            }
            if (pollingStationAddressFlagFetching) {
                pollingStationAddress.add(line);

            }
            if (pollingStationFlagFetching) {
                pollingStation.add(line);
            }

        }
        pollingLocationDetails.addAll(partAndPollingArea);
        pollingLocationDetails.addAll(pollingStation);
        pollingLocationDetails.addAll(pollingStationAddress);
        voter.setGetPollingStation(StringUtils.extractStringBetween(String.join(", ", pollingStation), "(Male/Female/General)", "Number of Auxiliary"));
        voter.setPart(StringUtils.extractStringBetween(String.join(", ", partAndPollingArea), "Details of part and polling area , No. and name of sections in the part", "Pin code"));
        return voter;
    }


    private SearchResult searchPollingStationDetails(String content, SearchResult searchResult) {
        List<String> partAndPollingArea = new ArrayList<>();
        List<String> pollingStation = new ArrayList<>();
        List<String> pollingStationAddress = new ArrayList<>();
        List<String> pollingLocationDetails = new ArrayList<>();
        boolean partAndPollingAreaFlagFetching = false;
        boolean pollingStationFlagFetching = false;
        boolean pollingStationAddressFlagFetching = false;

        String[] lines = content.split("\\r?\\n");
        System.out.println("lines content:");
        Arrays.stream(lines).forEach(System.out::println);
        String[] terms = new String[]{"Details of part and polling area", "Polling station details", "Address of Polling Station :", "NUMBER OF ELECTORS"};
        for (String line : lines) {
            if (line.contains(terms[0])) {
                partAndPollingAreaFlagFetching = true;
                pollingStationAddressFlagFetching = false;
                pollingStationFlagFetching = false;
            }

            if (line.contains(terms[1])) {
                pollingStationFlagFetching = true;
                partAndPollingAreaFlagFetching = false;
                pollingStationAddressFlagFetching = false;

            }

            if (line.contains(terms[2])) {
                pollingStationAddressFlagFetching = true;
                partAndPollingAreaFlagFetching = false;
                pollingStationFlagFetching = false;
            }

            if (line.contains(terms[3])) {
                break;
            }

            if (partAndPollingAreaFlagFetching) {
                partAndPollingArea.add(line);
            }
            if (pollingStationAddressFlagFetching) {
                pollingStationAddress.add(line);

            }
            if (pollingStationFlagFetching) {
                pollingStation.add(line);
            }

        }
        pollingLocationDetails.addAll(partAndPollingArea);
        pollingLocationDetails.addAll(pollingStation);
        pollingLocationDetails.addAll(pollingStationAddress);
        searchResult.setPollingStation(StringUtils.extractStringBetween(String.join(", ", pollingStation), "(Male/Female/General)", "Number of Auxiliary"));
        return searchResult;
    }


    // Enum for predefined ID patterns
    public enum IdPattern {
        ZU("^ZU\\d{8}$"),
        EPIC("^[a-zA-Z]{3}\\d{7}$");

        private final String regex;
        private final Pattern pattern;

        IdPattern(String regex) {
            this.regex = regex;
            this.pattern = Pattern.compile(regex);
        }

        public String getRegex() {
            return regex;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public boolean matches(String input) {
            return pattern.matcher(input).matches();
        }
    }

    /**
     * Extract voter details using new generic pattern extraction methods
     * Handles lines like: "Assembly Constituency No and Name : 31-TAMBARAM PartNo.: 1 SerialNo : 456"
     *
     * @param detailsLine The line containing voter details
     * @param voter       The voter object to populate
     */
    @SuppressWarnings("all")
    private void extractVoterDetailsUsingPatterns(String detailsLine, Voter voter) {
        if (detailsLine == null || detailsLine.isEmpty()) {
            return;
        }

        // Extract assembly info - Pattern: digits + dash + letters (e.g., "31-TAMBARAM")
        String assemblyResult = StringUtils.extractStringWithPattern(
                detailsLine,
                "Assembly Constituency No and Name",
                "\\d+-[A-Z]+"
        );

        if (assemblyResult != null) {
            String assembly = extractValueFromResult(assemblyResult);
            voter.setAssembly(assembly);
        }

        // Extract part number and serial number using the new generic pattern method (1-3 digits)
        List<String> numericResults = StringUtils.extractAllStringsWithPattern(
                detailsLine,
                "\\d{1,3}",
                "PartNo.",
                "SerialNo"
        );

        for (String result : numericResults) {
            String key = extractKeyFromResult(result);
            String value = extractValueFromResult(result);

            if ("PartNo.".equals(key)) {
                voter.setPartSerialNo(value);
            }
            // SerialNo handled if needed in future
        }

        // Extract EPIC numbers if present - Pattern: 3 letters + 7 digits (e.g., "ABC1234567")
        String epicResult = StringUtils.extractStringWithPattern(
                detailsLine,
                "EPIC",
                "[a-zA-Z]{3}\\d{7}"
        );

        if (epicResult != null) {
            String epicValue = extractValueFromResult(epicResult);
            if (IdPattern.EPIC.matches(epicValue)) {
                // voter.setEpicNumber(epicValue);
                // Future enhancement: uncomment when Voter class has setEpicNumber() method
            }
        }

        // Get ward no if present - Pattern: "Ward No" followed by 1-3 digits (e.g., "Ward No: 12"))
        String wardNo = StringUtils.extractStringWithPattern(
                detailsLine,
                "Ward",
                "\\\\d{1,3}"
        );

        if (wardNo != null) {
            String wardValue = extractValueFromResult(wardNo);
            if (wardValue != null) {
                voter.setWardNo(wardValue);
            }
        }
    }

    /**
     * Extract value from "key:value" format result
     * Example: "PartNo.:123" returns "123"
     */
    private static String extractValueFromResult(String keyValueString) {
        if (keyValueString == null || !keyValueString.contains(":")) {
            return null;
        }
        String[] parts = keyValueString.split(":", 2);
        return parts.length > 1 ? parts[1].trim() : null;
    }

    /**
     * Extract key from "key:value" format result
     * Example: "PartNo.:123" returns "PartNo."
     */
    private static String extractKeyFromResult(String keyValueString) {
        if (keyValueString == null || !keyValueString.contains(":")) {
            return null;
        }
        String[] parts = keyValueString.split(":", 2);
        return parts.length > 0 ? parts[0].trim() : null;
    }

    private SearchResult getAddressDetails(SearchResult searchResult, String fileName, String content) throws Exception {

        String[] lines = content.split("\\r?\\n");
        String addressLine = lines[1];
        String wardLine = lines[1];

        searchResult.setStreetName(StringUtils.extractAfterMatch(wardLine, "Section No and Name"));
        searchResult = getStreetPollingStationDetails(fileName, searchResult);
        return searchResult;
    }

}