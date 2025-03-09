import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * This class implements a web crawler that crawls pages recursively up to a specified depth (step).
 * It retrieves HTML documents, extracts text using a specified strategy, and computes TF-IDF scores.
 * FURTHER DEVELOPMENT: Prioritization can be added for efficiency. Determining which pages to crawl first
 * based on criteria like freshness. This can be done with a priority queue.
 * Multiple threads can be used instead of one thread. Therefore, multiple URLs can be processed at the same time.
 * robots.txt file can be checked before, which tells crawlers which parts of the site they can and cannot access.
 *
 */
public class Crawler {
    private static final Set<String> visitedUrls = new HashSet<>();
    private static final int maxStep = 2;
    private static final long timeFrame = 300 * 1000;  // 5 minutes in milliseconds
    private static final long requestDelay = 1000;  // 1 second delay between requests
    private static long startTime;

    /**
     * Main method that crawls websites.
     * @param args arguments.
     */
    public static void main(String[] args) {
        String startPage = "https://en.wikipedia.org/wiki/Open-source_intelligence";
        startTime = System.currentTimeMillis();
        crawl(startPage, 1, "tfidf");
//        crawl(startPage,1,"by frequency");
    }

    /**
     * Fetches the HTML document from the given URL with error handling and User-Agent.
     * @param url The URL to fetch.
     * @return The fetched Document or null if an error occurs.
     */
    private static Document retrieveHTML(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            System.err.println("Unable to fetch HTML of: " + url + " (" + e.getMessage() + ")");
            return null;
        }
    }

    /**
     * Normalizes URLs by removing fragments (#something) and trailing slashes.
     * @param url The URL to normalize.
     * @return The normalized URL.
     */
    private static String normalizeUrl(String url) {
        return url.split("#")[0].replaceAll("/+$", "");
    }

    /**
     * Recursively crawls web pages starting from a given URL.
     * @param url  The URL to crawl.
     * @param step Current step of crawling.
     */
    public static void crawl(String url, int step, String strategyType) {
        if (System.currentTimeMillis() - startTime >= timeFrame) {
            System.out.println("Time limit reached. Stopping crawl.");
            System.exit(0);
        }

        if (step > maxStep) {
            return;
        }

        url = normalizeUrl(url);
        if (visitedUrls.contains(url)) {
            return;
        }

        System.out.println("\nCrawling: " + url);
        visitedUrls.add(url);

        // Select the extraction strategy based on strategyType
        CrawlingStrategy strategy = CrawlingStrategyFactory.getStrategy(strategyType);

        Document doc = retrieveHTML(url);
        if (doc != null) {
            strategy.crawlingStrategy(doc);  // Execute the extraction strategy
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = normalizeUrl(link.absUrl("href"));
                if (!nextUrl.isEmpty() && !visitedUrls.contains(nextUrl)) {
                    // Delay to avoid overwhelming the server
                    try {
                        Thread.sleep(requestDelay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    crawl(nextUrl, step++, strategyType);  // Recursive call for the next URL
                }
            }
        }
    }

}
