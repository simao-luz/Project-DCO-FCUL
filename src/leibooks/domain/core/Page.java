package leibooks.domain.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a document page, identified by its page number.
 * 
 * A page can contain multiple annotations and may be bookmarked.
 * 
 * @author André Marques, fc61782
 */
public class Page {

    private final int pageNum;
    private int annotationID;
    private boolean bookmarked;
    private Map<Integer, Annotation> annotations;

    /**
     * Creates a page, identified by its page number.
     * 
     * @param pageNum The page number.
     */
    public Page(int pageNum) {
        this.pageNum = pageNum;
        annotationID = -1;
        bookmarked = false;
        annotations = new HashMap<>();
    }

    /**
     * Adds an annotation to the page. The annotations are numbered sequencially.
     * 
     * @param annotationText The text to be noted.
     */
    public void addAnnotation(String annotationText) {
        annotations.put(++annotationID, new Annotation(annotationText)); // Starts with zero.
    }

    /**
     * Gets the number of annotations the page has.
     * 
     * @return The number of annotations of the page.
     * @ensures \result >= 0
     */
    public int getAnnotationCount() {
        return annotationID;
    }

    /**
     * Returns an Iterable over the page's annotations.
     *
     * @return An {@code Iterable} that can be used to traverse the annotations.
     * @ensures \result != null
     */
    public Iterable<Annotation> getAnnotations() {
        return annotations.values();
    }

    /**
     * Gets an annotation text based on its sequencial number.
     * 
     * @param annotationID The sequencial number.
     * @return The annotation with that ID.
     * @requires {@code annotationID} >= 0
     */
    public String getAnnotationText(int annotationID) {
        Annotation annotation = annotations.get(annotationID);
        return annotation.getAnnotationText();
    }

    /**
     * Gets the page number.
     * 
     * @return The page number.
     * @ensures \result >= 1
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * Checks if the page has any annotations.
     * 
     * @return {@code true} if the page contains at least one annotation,
     *         {@code false} otherwise.
     */
    public boolean hasAnnotations() {
        return annotationID >= 0;
    }

    /**
     * Checks if the page is bookmarked.
     * 
     * @return {@code true} if the page is bookmarked, {@code false} otherwise.
     */
    public boolean isBookmarked() {
        return bookmarked;
    }

    /**
     * Removes the annotation with the specified ID from the page.
     * 
     * @param annotationID The ID of the annotation to be removed.
     * @requires annotationID >= 0
     */
    public void removeAnnotation(int annotationID) {
        annotations.remove(annotationID);
        reorganizeAnnotations();
    }

    /**
     * Auxiliar method to removeAnnotation(int annotationID). Reorganizes all
     * annotation IDs to reflect the current sequential order after a removal.
     */
    private void reorganizeAnnotations() {
        Map<Integer, Annotation> newAnnotations = new HashMap<>();
        int count = -1;

        for (Annotation annotation : getAnnotations()) {
            newAnnotations.put(++count, annotation);
        }

        annotations = newAnnotations; // Replaces the current annotations with the newAnnotations HashMap.
        annotationID = count;
    }

    /**
     * Toggles the bookmarked status of the page. Activates or removes the bookmark
     * depending on the current state.
     */
    public void toggleBookmark() {
        bookmarked = !bookmarked;
    }

    @Override
    public String toString() {
        return "Page{bookmark=" + bookmarked + ", annotations=" + getAnnotations().toString() + ", pageNum=" + pageNum
                + "}";
    }
}