package leibooks.domain.metadatareader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstract class responsible for reading metadata from documents.
 * 
 * This class provides default behavior used by generic metadata readers when no
 * specific implementation is available.
 * 
 * It also serves as the base class from which all metadata readers are built.
 * 
 * @author André Marques, fc61782
 */
public abstract class AMetadataReader implements IMetadataReader {

    protected String authors;
    protected Optional<Integer> numPages;
    protected String mimeType;

    protected final LocalDate modifiedDate;
    protected final String pathToDocFile;

    /**
     * Constructor for abstract metadata readers.
     * 
     * @param pathToDocFile String representing the path to the document file
     * @throws FileNotFoundException If the file does not exists or it's unreadable.
     */
    protected AMetadataReader(String pathToDocFile) throws FileNotFoundException {
        this.pathToDocFile = pathToDocFile;
        authors = "n/a";
        numPages = Optional.empty();

        File file = new File(pathToDocFile);

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("The file was not found or is not a file: " + pathToDocFile);
        }

        try {
            mimeType = Files.probeContentType(Path.of(pathToDocFile));
        }

        catch (IOException e) {
            mimeType = "application/octet-stream"; // Standart defined by IANA for binary documents with an unspecified type!
        }

        // The File class from java.io only provides lastModified() method, which
        // returns a long representing the number of milliseconds since Unix epoch time.
        long lastModifiedMillis = file.lastModified();

        // It's necessary to convert the milliseconds into an actual date.
        this.modifiedDate = Instant.ofEpochMilli(lastModifiedMillis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public String getAuthors() {
        return authors;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public LocalDate getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public Optional<Integer> getNumPages() {
        return numPages;
    }
}
