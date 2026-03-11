package leibooks.domain.metadatareader;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Interface representing a metadata reader for a document.
 * 
 * Implementations of this interface must extract relevant metadata such as
 * author, MIME type, last modified data & number of pages.
 * 
 * @author André Marques, fc61782
 * @author Simão da Luz, fc61816
 */
public interface IMetadataReader {

    /**
     * Returns the author(s) of a document (default: "n/a").
     * 
     * @return String representing the documents author(s).
     */
    String getAuthors();

    /**
     * Returns the MIME type of a document (default: "application/octet-stream").
     * 
     * @return String representing the documents MIME type.
     */
    String getMimeType();

    /**
     * Returns the last modified data of a document.
     * 
     * @return LocalDate as the last date modified date.
     */
    LocalDate getModifiedDate();

    /**
     * Returns the number of pages of a document.
     * 
     * @return Optional containing the number of pages, or Optional.empty() if
     *         unknown.
     */
    Optional<Integer> getNumPages();
}