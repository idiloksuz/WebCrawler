import org.jsoup.nodes.Document;

/**
 * Interface that every strategy implements. Different ways of extracting words and analyzing them.
 */
public interface CrawlingStrategy {
     void crawlingStrategy(Document document);
}
