import java.util.HashMap;
import java.util.Map;

class ExtractionStrategyFactory {
    private static final Map<String, ExtractionStrategy> strategies = new HashMap<>();

    static {
        strategies.put("important words", new ImportantWordFreqExtractionStrategy());
        strategies.put("tfidf", new TFIDFWordExtractionStrategy());
    }

    public static ExtractionStrategy getStrategy(String type) {
        ExtractionStrategy strategy = strategies.get(type.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("This strategy does not exist. " + type);
        }
        return strategy;
    }
}

