package leibooks.domain.core;

import java.io.File;
import java.io.FileNotFoundException;

import leibooks.domain.facade.IDocument;
import leibooks.domain.metadatareader.IMetadataReader;
import leibooks.domain.metadatareader.MetadataReaderFactory;

/**
 * Enum implementing the factory design pattern for documents.
 * 
 * @author Simão da Luz, fc61816
 */
public enum DocumentFactory {
    INSTANCE;

    /**
     * Empty constructor - No initialization required.
     */
    private DocumentFactory() {}

    /**
     * Creates a document through the metadata reading of the document file with
     * path, {@param pathToPhotoFile}.
     * 
     * @param title           The title of the document to be created.
     * @param pathToPhotoFile The path to the document file.
     * @return An IDocument instance represented by its authors, last modified date, MIME type, path & number of pages.
     * @throws java.io.FileNotFoundException If the file does not exists or is unreadable.
     */
    public IDocument createDocument(String title, String pathToPhotoFile) throws java.io.FileNotFoundException {
        File file = new File(pathToPhotoFile);

        // Checks if the path corresponds to a valid and readable file before proceeding.
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(pathToPhotoFile);
        }

        // The MetadataReaderFactory creates a IMetadataReader;
        IMetadataReader reader = MetadataReaderFactory.INSTANCE.createMetadataReader(pathToPhotoFile);

        return new Document(title, reader.getAuthors(), reader.getModifiedDate(), reader.getMimeType(), pathToPhotoFile,
                reader.getNumPages());
    }
}
