package leibooks.domain.core;

/**
 * Class representing an annotation, identified by its text content.
 * 
 * @author André Marques, fc61782
 */
public class Annotation {

    private String annotationText;

    /**
     * Creates an annotation with its text content.
     * 
     * @param annotationText The text content.
     */
    public Annotation(String annotationText) {
        this.annotationText = annotationText;
    }

    /**
     * Gets the annotation text.
     * 
     * @return The annotation text.
     */
    public String getAnnotationText() {
        return annotationText;
    }

    /**
     * Sets a new annotation text.
     * 
     * @param newText The new annotation text.
     */
    public void setAnnotationText(String newText) {
        this.annotationText = newText;
    }

    @Override
    public String toString() {
        return "Annotation [text=" + annotationText + "]";
    }
}