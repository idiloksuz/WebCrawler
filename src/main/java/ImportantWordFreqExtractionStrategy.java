import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportantWordFreqExtractionStrategy implements ExtractionStrategy {

    // List of stop words to ignore in word count
    private static final List<String> unnecessaryWords = List.of("the", "is", "in", "on", "and", "to", "a",
            "of", "for", "with", "at", "by", "from", "it", "as", "that",
            "this", "an", "be", "was", "were", "can", "will", "if","about","from");
    private final Map<String, Integer> wordCount = new HashMap<>();

    @Override
    public void extract(Document doc) {
        String[] words = doc.text().toLowerCase().split("[^a-zA-Z]+");
        for (String word : words) {
            if (word.length() >= 4 && !unnecessaryWords.contains(word)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        displayMostFrequentWords();
    }

    private void displayMostFrequentWords() {
        System.out.println("\nMost Frequent Words:");
        wordCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))  // Sort by frequency (desc)
                .limit(20)  // Display top 20 words
                .forEach(entry -> System.out.println(entry.getKey()));
    }
}