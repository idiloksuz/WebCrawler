import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TFIDFCrawlingStrategyTest {
    /**
     * FURTHER DEVELOPMENT: More tests can be written. For example to check the TD-IDF scores. Or checking if the
     * unnecessary words are being checked correctly.
     */
    private TFIDFCrawlingStrategy tfidfStrategy;

    @BeforeEach // Ensures the method below runs before all the tests.
    public void setUpStrategy() {
        tfidfStrategy = new TFIDFCrawlingStrategy();
    }

    @Test
    public void testExtractMethod() {
        // Create a mock document to stimulate a document object instead of needing an HTML doc.
        Document mockDoc = mock(Document.class); // Behaves like a document.

        // Mock the body() method to return a mock Element.
        Element mockBody = mock(Element.class);
        when(mockDoc.body()).thenReturn(mockBody);

        // Mock the text() method of the body element to return some sample text.
        when(mockBody.text()).thenReturn("Open source intelligence (OSINT) is the collection and analysis " +
                "of data gathered from open sources (overt sources and publicly available information) to produce " +
                        "actionable intelligence. OSINT is primarily used in national security, law enforcement, and business" +
                        " intelligence functions and is of value to analysts who use non-sensitive intelligence in answering" +
                        " classified, unclassified, or proprietary intelligence requirements across the previous intelligence " +
                        "disciplines");

        // Simulate extraction process.
        tfidfStrategy.crawlingStrategy(mockDoc);

        // Verify that the document was processed.
        assertEquals(1, tfidfStrategy.allDocuments.size(), "In the private sector, companies like IBM define" +
                " OSINT as the process of gathering and analyzing publicly available information" +
                " to assess threats, inform decisions, or answer specific questions. Similarly, " +
                "cyber security firms such as CrowdStrike describe OSINT as the act of collecting " +
                "and analyzing publicly available data for intelligence purposes.");
    }

    @Test
    public void testComputeTFIDFForCurrentDocument() {
        // Create a mock document
        Document mockDoc1 = mock(Document.class);
        Element mockBody1 = mock(Element.class);
        when(mockDoc1.body()).thenReturn(mockBody1);
        when(mockBody1.text()).thenReturn("Open source intelligence (OSINT) is the collection and analysis " +
                "of data gathered from open sources (overt sources and publicly available information) to produce " +
                "actionable intelligence. OSINT is primarily used in national security, law enforcement, and business" +
                " intelligence functions and is of value to analysts who use non-sensitive intelligence in answering" +
                " classified, unclassified, or proprietary intelligence requirements across the previous intelligence " +
                "disciplines");
        tfidfStrategy.crawlingStrategy(mockDoc1);

        // Another mock document
        Document mockDoc2 = mock(Document.class);
        Element mockBody2 = mock(Element.class);
        when(mockDoc2.body()).thenReturn(mockBody2);
        when(mockBody2.text()).thenReturn("In the private sector, companies like IBM define" +
                " OSINT as the process of gathering and analyzing publicly available information" +
                " to assess threats, inform decisions, or answer specific questions. Similarly, " +
                "cyber security firms such as CrowdStrike describe OSINT as the act of collecting " +
                "and analyzing publicly available data for intelligence purposes.");
        tfidfStrategy.crawlingStrategy(mockDoc2);

        // Compute the TF-IDF for the last document (the second one)
        tfidfStrategy.computeTFIDF();

        assertTrue(tfidfStrategy.allDocuments.size() > 0, "Documents are processed.");
    }
}
