package leibooks.domain.metadatareader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import leibooks.services.reader.PDFReader;

/**
 * Class responsible for reading metadata from PDF documents.
 * 
 * Extends {@code AMetadataReader} and uses PDFReader services to extract
 * information such as author and number of pages.
 * 
 * @author André Marques, fc61782
 */
public class PDFMetadataReaderAdapter extends AMetadataReader {

    /**
     * Constructs a PDF metadata reader for a specified document path.
     * 
     * @param pathToDocFile String representing the path to the document file.
     * @throws FileNotFoundException If the file does not exists or it's unreadable.
     */
    public PDFMetadataReaderAdapter(String pathToDocFile) throws FileNotFoundException {
        super(pathToDocFile);
        
        File pdfFile = new File(pathToDocFile);

        try {
            PDFReader pdfReader = new PDFReader(pdfFile);

            numPages = Optional.of(pdfReader.getPages()); // The number of pages is defined.

            String pdfAuthors = pdfReader.getAuthors();
            
            // If the authors are null or empty, the default value ("n/a") is returned.
            if (pdfAuthors == null || pdfAuthors.trim().isEmpty()) {
                pdfAuthors = "n/a";
            }

            authors = pdfAuthors; // The author(s) is/are defined.
        }

        catch (IOException e) {
            System.err.println("Error reading the PDF metadata: " + e.getMessage());
        }
    }
}