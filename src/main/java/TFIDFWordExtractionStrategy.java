import org.jsoup.nodes.Document;

import java.util.*;

public class TFIDFWordExtractionStrategy implements ExtractionStrategy {
    private final List<Map<String, Integer>> allDocuments = new ArrayList<>();
    private final Map<String, Integer> dfMap = new HashMap<>();  // Document frequency map
    private int totalDocs = 0;  // Track total documents processed

    // Define a set of stop words to exclude from TF-IDF computation
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "is", "in", "at", "of", "and", "a", "to", "it", "for", "on", "with", "as", "by", "an", "be"
    ));

    // Extract words and compute TF for each document
    @Override
    public void extract(Document doc) {
        String text = doc.body().text();
        String[] tokens = text.split("\\W+");
        Map<String, Integer> tfMap = new HashMap<>();
        Set<String> uniqueWords = new HashSet<>();

        for (String token : tokens) {
            String word = token.toLowerCase();
            if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                tfMap.put(word, tfMap.getOrDefault(word, 0) + 1);
                uniqueWords.add(word);
            }
        }

        if (!tfMap.isEmpty()) {
            allDocuments.add(tfMap);
            totalDocs++;

            // Update Document Frequency (DF) for each unique word in the document
            for (String word : uniqueWords) {
                dfMap.put(word, dfMap.getOrDefault(word, 0) + 1);
            }
        }
    }

    // Compute and print TF-IDF for the current document
    public void computeTFIDFForCurrentDocument(String url) {
        if (allDocuments.isEmpty()) return;  // No documents to process

        // Get the last processed document
        Map<String, Integer> tfMap = allDocuments.get(allDocuments.size() - 1);
        Map<String, Double> tfidfMap = new HashMap<>();

        // Compute TF-IDF for each word
        for (String word : tfMap.keySet()) {
            int tf = tfMap.get(word);  // Term Frequency in the document
            int df = dfMap.getOrDefault(word, 1);  // Document Frequency for the word
            double idf = Math.log((double) totalDocs / df);  // Inverse Document Frequency
            double tfidf = tf * idf;  // TF-IDF score
            tfidfMap.put(word, tfidf);
        }

        // Sort words by TF-IDF score in descending order
        List<Map.Entry<String, Double>> sortedWords = new ArrayList<>(tfidfMap.entrySet());
        sortedWords.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Print the URL and the top 20 words with the highest TF-IDF scores
        System.out.println("\nTop TF-IDF words for: " + url);
        sortedWords.stream().limit(20).forEach(entry ->
                System.out.println(entry.getKey() + ": " + String.format("%.4f", entry.getValue()))
        );
    }
}
