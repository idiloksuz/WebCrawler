import org.jsoup.nodes.Document;
import java.util.*;

/**
 * Strategy using TF-IDF algorithm for determining the most important words.
 */
public class TFIDFCrawlingStrategy implements CrawlingStrategy {
    final List<Map<String, Double>> allDocuments = new ArrayList<>();
    private final Map<String, Integer> dfMap = new HashMap<>();
    private int totalDocs = 0;

    /**
     * Unnecessary words are written in a list so that they do not affect the scores.
     */
    private static final List<String> unnecessaryWords = List.of(
            "the", "is", "in", "at", "of", "and", "a", "to", "it", "for", "on", "with", "as", "by", "an", "be",
            "this", "that", "or", "s", "was", "from", "about", "are", "other", "were", "can", "will", "if","t"
    );

    /**
     * Extracts words and computes TF for each document
     * @param doc that is being crawled. Words are extracted from this object.
     */
    @Override
    public void crawlingStrategy(Document doc) {
        String text = doc.body().text();
        String[] tokens = text.split("\\W+");
        Map<String, Double> tfMap = new HashMap<>();
        Set<String> uniqueWords = new HashSet<>();
        int totalWords = 0;

        for (String token : tokens) {
            String word = token.toLowerCase();
            if (!word.isEmpty() && !unnecessaryWords.contains(word) && !word.matches("\\d+")) {
                tfMap.put(word, tfMap.getOrDefault(word, 0.0) + 1);
                uniqueWords.add(word);
                totalWords++;
            }
        }

        if (!tfMap.isEmpty()) {
            for (String word : tfMap.keySet()) {
                double normalizedTF = tfMap.get(word) / totalWords;
                tfMap.put(word, (double) Math.round(normalizedTF * 1000));
            }

            allDocuments.add(tfMap);
            totalDocs++;

            for (String word : uniqueWords) {
                dfMap.put(word, dfMap.getOrDefault(word, 0) + 1);
            }
        }
        computeTFIDF();
    }

    /**
     *  Compute and print TF-IDF.
     */
    public void computeTFIDF() {
        if (allDocuments.isEmpty()) return;  // Base case: No documents to process

        // Get the last processed document
        Map<String, Double> tfMap = allDocuments.get(allDocuments.size() - 1);
        Map<String, Double> tfidfMap = new HashMap<>();

        // Compute TF-IDF for each word
        for (String word : tfMap.keySet()) {
            double tf = tfMap.get(word) / 1000;  // Retrieve normalized TF
            int df = dfMap.getOrDefault(word, 1);  // Document Frequency for the word

            double idf = Math.log10((double) (totalDocs + 1) / (df + 1)) + 1;
            double tfidf = tf * idf;  // TFIDF score
            tfidfMap.put(word, tfidf);
        }

        // Sort words by TF-IDF score in decreasing order.
        List<Map.Entry<String, Double>> sortedWords = new ArrayList<>(tfidfMap.entrySet());
        sortedWords.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Print the URL and the top 20 words with the highest TF-IDF scores.
        System.out.println("Most important words: ");
        sortedWords.stream().limit(20).forEach(entry ->
                System.out.println(entry.getKey())
        );
    }
}