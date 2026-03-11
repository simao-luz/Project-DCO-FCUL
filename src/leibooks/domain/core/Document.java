package leibooks.domain.core;

import java.io.File;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.events.AddAnnotationEvent;
import leibooks.domain.facade.events.DocumentEvent;
import leibooks.domain.facade.events.RemoveAnnotationEvent;
import leibooks.domain.facade.events.ToggleBookmarkEvent;

import leibooks.utils.AbsSubject;
import leibooks.utils.Listener;
import leibooks.utils.RegExpMatchable;

/**
 * Class that represents a document. Implements the {@code IDocument} interface.
 * A document stores metadata such as title, authors, last modified date, MIME
 * type, path, and number of pages.
 * 
 * This class extends {@code AbsSubject<DocumentEvent>} to notify its observers
 * (shelves & controller) about {@code DocumentEvent}s related to it, such as
 * the addition and removal of annotations, as well as the bookmarking of any of
 * its pages.
 * 
 * It also implements {@code RegExpMatchable} to support pattern-based searches.
 * 
 * @author André Marques, fc61782
 * @author Simão da Luz, fc61816
 * 
 * @see DocumentFactory
 */
public class Document extends AbsSubject<DocumentEvent> implements IDocument, RegExpMatchable {

    private String expectedTitle;
    private String expectedAuthor;
    private LocalDate expectedModifiedDate;

    private final String expectedMimeType;
    private final String expectedPath;
    private final Optional<Integer> expectedNumPages;
    private final Map<Integer, Page> pages;

    private int lastPageVisited;

    /**
     * Constructs an instance of IDocument with the given metadata.
     *
     * @param expectedTitle        The title of the document
     * @param expectedAuthor       The author(s) of the document
     * @param expectedModifiedDate The last modified date of the document
     * @param expectedMimeType     The MIME type of the document
     * @param expectedPath         The path to the document file
     * @param expectedNumPages     The number of pages in the document
     */
    public Document(String expectedTitle, String expectedAuthor, LocalDate expectedModifiedDate,
            String expectedMimeType, String expectedPath, Optional<Integer> expectedNumPages) {

        this.expectedTitle = expectedTitle;
        this.expectedAuthor = expectedAuthor;
        lastPageVisited = 0;

        this.expectedModifiedDate = expectedModifiedDate;
        this.expectedMimeType = expectedMimeType;
        this.expectedPath = expectedPath;
        this.expectedNumPages = expectedNumPages;
        pages = new TreeMap<>();

        if (expectedNumPages.isPresent()) {
            for (int i = 1; i <= expectedNumPages.get(); i++) {
                pages.put(i, new Page(i));
            }
        }
    }

    @Override
    public void emitEvent(DocumentEvent e) {
        super.emitEvent(e);
    }

    @Override
    public void registerListener(Listener<DocumentEvent> obs) {
        super.registerListener(obs);
    }

    @Override
    public void unregisterListener(Listener<DocumentEvent> obs) {
        super.unregisterListener(obs);
    }

    @Override
    public File getFile() {
        // The file is objectively valid because the path's integrity was verified
        // beforehand by the DocumentFactory.
        return new File(expectedPath);
    }

    @Override
    public String getTitle() {
        return expectedTitle;
    }

    @Override
    public String getAuthor() {
        return expectedAuthor;
    }

    @Override
    public String getMimeType() {
        return expectedMimeType;
    }

    @Override
    public LocalDate getLastModifiedDate() {
        return expectedModifiedDate;
    }

    @Override
    public Optional<Integer> getNumberOfPages() {
        return expectedNumPages;
    }

    @Override
    public int getLastPageVisited() {
        return lastPageVisited;
    }

    @Override
    public List<Integer> getBookmarks() {
        List<Integer> bookmarks = new LinkedList<>();

        for (Map.Entry<Integer, Page> entry : pages.entrySet()) {
            Page page = entry.getValue();

            if (page.isBookmarked()) {
                bookmarks.add(page.getPageNum());
            }
        }

        return bookmarks;
    }

    @Override
    public void toggleBookmark(int pageNum) {
        Page page = getOrCreatePage(pageNum);
        page.toggleBookmark();

        expectedModifiedDate = LocalDate.now(); // Update the last modified date BEFORE the emission of the event.
        emitEvent(new ToggleBookmarkEvent(this, pageNum, isBookmarked(pageNum)));
    }

    @Override
    public void setTitle(String title) {
        expectedTitle = title;
        expectedModifiedDate = LocalDate.now();
    }

    @Override
    public void setAuthor(String author) {
        expectedAuthor = author;
        expectedModifiedDate = LocalDate.now();
    }

    @Override
    public void setLastPageVisited(int lastPageVisited) {
        this.lastPageVisited = lastPageVisited;
    }

    @Override
    public void addAnnotation(int pageNum, String text) {
        getOrCreatePage(pageNum).addAnnotation(text);

        expectedModifiedDate = LocalDate.now();
        emitEvent(new AddAnnotationEvent(this, pageNum, numberOfAnnotations(pageNum), text, hasAnnotations(pageNum)));
    }

    @Override
    public void removeAnnotation(int pageNum, int annotNum) {
        getOrCreatePage(pageNum).removeAnnotation(annotNum);

        expectedModifiedDate = LocalDate.now();
        emitEvent(new RemoveAnnotationEvent(this, pageNum, annotNum, hasAnnotations(pageNum)));
    }

    @Override
    public int numberOfAnnotations(int pageNum) {
        return getOrCreatePage(pageNum).getAnnotationCount();
    }

    @Override
    public Iterable<String> getAnnotations(int pageNum) {
        List<String> annotations = new LinkedList<>();

        for (Annotation annotation : getOrCreatePage(pageNum).getAnnotations()) {
            annotations.add(annotation.getAnnotationText());
        }

        return annotations;
    }

    @Override
    public String getAnnotationText(int pageNum, int annotNum) {
        return getOrCreatePage(pageNum).getAnnotationText(annotNum);
    }

    @Override
    public boolean hasAnnotations(int pageNum) {
        return getOrCreatePage(pageNum).hasAnnotations();
    }

    @Override
    public boolean isBookmarked(int pageNum) {
        return getOrCreatePage(pageNum).isBookmarked();
    }

    @Override
    public boolean isBookmarked() {
        for (Map.Entry<Integer, Page> entry : pages.entrySet()) {

            if (entry.getValue().isBookmarked()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean matches(String regexp) {
        return expectedTitle.matches(regexp) || expectedAuthor.matches(regexp);
    }

    @Override
    public int compareTo(IDocument other) {
        return getFile().compareTo(other.getFile()); // Solely based on the document file.
    }

    @Override
    public String toString() {
        return "Document{title=" + expectedTitle + ", author=" + expectedAuthor + ", file=" + expectedPath + ", date="
                + expectedModifiedDate + ", mimeType=" + expectedMimeType + ", numPages=" + expectedNumPages
                + ", lastPageVisited=" + lastPageVisited + ", pages=" + getRelevantPages().toString() + "}";
    }

    /**
     * Auxiliary function to obtain the bookmarked and/or annotated pages. This
     * method iterates through the pages and collects the ones that are either
     * bookmarked or have annotations.
     * 
     * @return A map of pages where the key is the page number and the value is the
     *         Page object.
     */
    private Map<Integer, Page> getRelevantPages() {
        Map<Integer, Page> bookmarkedPages = new TreeMap<>();

        for (Map.Entry<Integer, Page> entry : pages.entrySet()) {
            Page page = entry.getValue();

            if (page.isBookmarked() || page.hasAnnotations()) {
                bookmarkedPages.put(entry.getKey(), page);
            }
        }

        return bookmarkedPages;
    }

    /**
     * Retrieves the page with the given page number. If the page does not exist, it
     * is created and added to the document. This method is used throughout the
     * program to manage and create pages dynamically as needed.
     * 
     * @param pageNum The number of the page to retrieve or create.
     * @return The existing or newly created page.
     */
    private Page getOrCreatePage(int pageNum) {
        return pages.computeIfAbsent(pageNum, Page::new);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expectedAuthor, expectedMimeType, expectedModifiedDate, expectedNumPages, expectedPath,
                expectedTitle);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Document other = (Document) obj;
        return Objects.equals(expectedPath, other.expectedPath); // The path is used as the unique identifier for the
                                                                 // document.
    }
}