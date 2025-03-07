import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Crawler {
    private static final Set<String> visitedUrls = new HashSet<>();
    private static final int maxStep = 3;
    private static final long timeFrame = 300 * 1000;  // 5 minutes in milliseconds
    private static final long requestDelay = 1000;  // 1 second delay between requests
    private static long startTime;

    // Store the extraction strategy globally to maintain IDF across documents
    private static TFIDFWordExtractionStrategy strategy = new TFIDFWordExtractionStrategy();

    public static void main(String[] args) {
        String seedUrl = "https://en.wikipedia.org/wiki/Open-source_intelligence";
        startTime = System.currentTimeMillis();  // Record start time

        // Start crawling
        crawl(seedUrl, 0);
    }

    /**
     * Fetches the HTML document from the given URL with error handling and User-Agent.
     *
     * @param url The URL to fetch.
     * @return The fetched Document or null if an error occurs.
     */
    private static Document retrieveHTML(String url) {
        try {
            // Add a User-Agent to prevent getting blocked
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            System.err.println("Unable to fetch HTML of: " + url + " (" + e.getMessage() + ")");
            return null;
        }
    }

    /**
     * Normalizes URLs by removing fragments (#something) and trailing slashes.
     *
     * @param url The URL to normalize.
     * @return The normalized URL.
     */
    private static String normalizeUrl(String url) {
        return url.split("#")[0].replaceAll("/+$", "");
    }

    /**
     * Recursively crawls web pages starting from a given URL.
     *
     * @param url  The URL to crawl.
     * @param step Current step of crawling.
     */
    private static void crawl(String url, int step) {
        if (System.currentTimeMillis() - startTime >= timeFrame) {
            System.out.println("Time limit reached. Stopping crawl.");
            return;
        }

        if (step >= maxStep) {
            return;
        }

        url = normalizeUrl(url);
        if (visitedUrls.contains(url)) {
            return;
        }

        // Print crawling status before adding to visitedUrls
        System.out.println("Crawling: " + url);
        visitedUrls.add(url);

        Document doc = retrieveHTML(url);
        if (doc != null) {
            strategy.extract(doc);  // Execute the extraction strategy
            strategy.computeTFIDFForCurrentDocument(url);  // Compute and print TF-IDF immediately

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = normalizeUrl(link.absUrl("href"));
                if (!nextUrl.isEmpty() && !visitedUrls.contains(nextUrl) && nextUrl.startsWith("https://en.wikipedia.org")) {
                    // Delay to avoid overwhelming the server
                    try {
                        Thread.sleep(requestDelay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    crawl(nextUrl, step + 1);  // Recursive call for the next URL
                }
            }
        }
    }
}
