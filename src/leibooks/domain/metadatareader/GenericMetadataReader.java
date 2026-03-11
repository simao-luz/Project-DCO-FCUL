package leibooks.domain.metadatareader;

import java.io.FileNotFoundException;

/**
 * Class representing a generic metadata reader used for unsupported document types.
 * 
 * Provides default metadata values such as "n/a" and an empty page value.
 * 
 * @author André Marques, fc61782
 * @author Simão da Luz, fc61816
 */
public class GenericMetadataReader extends AMetadataReader {

    /**
     * Constructs a generic metadata reader for a specified document path.
     * 
     * @param pathToDocFile String representing the path to the document file.
     * @throws FileNotFoundException If the file does not exists or it's unreadable.
     */
    public GenericMetadataReader(String pathToDocFile) throws FileNotFoundException {
        super(pathToDocFile);
    }
}
