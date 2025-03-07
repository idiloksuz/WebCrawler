import org.jsoup.nodes.Document;
import java.util.*;

public class TFIDFWordExtractionStrategy implements ExtractionStrategy {
    private final List<Map<String, Integer>> allDocuments = new ArrayList<>();
    private final Map<String, Integer> dfMap = new HashMap<>();  // Document frequency map
    private int totalDocs = 0;  // Track total documents processed

    // Define a set of stop words to exclude from TF-IDF computation
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "is", "in", "at", "of", "and", "a", "to", "it", "for", "on", "with", "as", "by", "an", "be",
            "this","that","or","s","was","from","about"
    ));

    // Extract words and compute TF for each document
    @Override
    public void extract(Document doc) {
        String text = doc.body().text();
        String[] tokens = text.split("\\W+");
        Map<String, Integer> tfMap = new HashMap<>();
        Set<String> uniqueWords = new HashSet<>();
        int totalWords = 0;

        // Compute raw TF and total word count
        for (String token : tokens) {
            String word = token.toLowerCase();
            if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                tfMap.put(word, tfMap.getOrDefault(word, 0) + 1);
                uniqueWords.add(word);
                totalWords++;
            }
        }

        if (!tfMap.isEmpty()) {
            // Normalize TF by total words in the document
            for (String word : tfMap.keySet()) {
                double normalizedTF = (double) tfMap.get(word) / totalWords;
                tfMap.put(word, (int) Math.round(normalizedTF * 1000));  // Store normalized TF scaled by 1000
            }

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
            double tf = (double) tfMap.get(word) / 1000;  // Retrieve normalized TF
            int df = dfMap.getOrDefault(word, 1);  // Document Frequency for the word

            // Smoothed IDF to handle zero DF cases and avoid log(0)
            double idf = Math.log10((double) (totalDocs + 1) / (df + 1)) + 1;
            double tfidf = tf * idf;  // TF-IDF score
            tfidfMap.put(word, tfidf);
        }

        // Sort words by TF-IDF score in descending order
        List<Map.Entry<String, Double>> sortedWords = new ArrayList<>(tfidfMap.entrySet());
        sortedWords.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Print the URL and the top 20 words with the highest TF-IDF scores
        System.out.println("\nTop TF-IDF words for: " + url);
        sortedWords.stream().limit(20).forEach(entry ->
                System.out.println(entry.getKey())
        );
    }
}