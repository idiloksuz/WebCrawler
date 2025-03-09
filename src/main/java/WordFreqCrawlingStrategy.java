import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finds the most frequent words in an HTML document.
 */
public class WordFreqCrawlingStrategy implements CrawlingStrategy {
    /**
     * Unnecessary words are written in a list so that they do not affect the counts.
     */
    private static final List<String> unnecessaryWords = List.of(
            "the", "is", "in", "at", "of", "and", "a", "to", "it", "for", "on", "with", "as", "by", "an", "be",
            "this", "that", "or", "s", "was", "from", "about", "are", "other", "were", "can", "will", "if"
    );
    private final Map<String, Integer> wordCount = new HashMap<>();

    /**
     * Extracts the words in the document and counts them.
     * @param doc that is being crawled. Words are extracted from this object.
     */
    @Override
    public void crawlingStrategy(Document doc) {
        String[] words = doc.text().toLowerCase().split("\\W+");
        for (String word : words) {
            if (word.length() >= 4 && !unnecessaryWords.contains(word)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        displayMostFrequentWords();
    }

    /**
     * Sorts and displays the most frequent words on the log.
     * FURTHER DEVELOPMENT: the words can be visualized in a better way like a data visualisation tool (Ex: graph) or
     * a panel. Can be stored in a CSV or JSON.
     */
    private void displayMostFrequentWords() {
        System.out.println("\nMost Frequent Words:");
        wordCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(20)
                .forEach(entry -> System.out.println(entry.getKey()));
    }
}