import java.util.HashMap;
import java.util.Map;

/**
 * Factory class that creates new strategies and stores them in a map.
 * Map is used instead of if-else statements or cases so that it is easier to expand the application.
 */
class CrawlingStrategyFactory {
    private static final Map<String, CrawlingStrategy> strategies = new HashMap<>();

    static {
        strategies.put("by frequency", new WordFreqCrawlingStrategy());
        strategies.put("tfidf", new TFIDFCrawlingStrategy());
    }

    /**
     * Gets the desired strategy.
     * @param type of the strategy. Like either td-idf or counting.
     * @return the strategy desired.
     */
    public static CrawlingStrategy getStrategy(String type) {
        CrawlingStrategy strategy = strategies.get(type.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("This strategy does not exist. " + type);
        }
        return strategy;
    }
}

