package leibooks.domain.controllers;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import leibooks.domain.core.DocumentFactory;
import leibooks.domain.core.ILibrary;
import leibooks.domain.facade.DocumentProperties;
import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.ILibraryController;
import leibooks.domain.facade.events.DocumentEvent;
import leibooks.domain.facade.events.LBEvent;
import leibooks.utils.Listener;
import leibooks.utils.AbsSubject;

/**
 * Controller responsible for managing document-related events & library events.
 * 
 * Acts as a subject emitting LBEvent instances to external listeners whenever
 * document-related & library-related events occur.
 * 
 * @author André Marques, fc61782
 */
public class LibraryController extends AbsSubject<LBEvent> implements ILibraryController {

    private ILibrary library;

    /**
     * Constructs a LibraryController with the given library.
     * 
     * Registers itself as a listener to the library and to each document it
     * contains, allowing it to handle and emit LBEvents based on library and
     * document events.
     *
     * @param library The library instance to be managed by this controller.
     */
    public LibraryController(ILibrary library) {
        this.library = library;

        for (IDocument doc : library) {
            doc.registerListener(this);
        }

        this.library.registerListener(this);
    }

    @Override
    public void emitEvent(LBEvent e) {
        super.emitEvent(e);

    }

    @Override
    public void registerListener(Listener<LBEvent> obs) {
        super.registerListener(obs);

    }

    @Override
    public void unregisterListener(Listener<LBEvent> obs) {
        super.unregisterListener(obs);

    }

    @Override
    public void processEvent(DocumentEvent e) {
        emitEvent(e);
    }

    @Override
    public Iterable<IDocument> getDocuments() {
        return library;
    }

    @Override
    public Optional<IDocument> importDocument(String title, String pathTofile) {
        try {
            IDocument doc = DocumentFactory.INSTANCE.createDocument(title, pathTofile);
            library.addDocument(doc);
            
            return Optional.of(doc);
        }

        catch (FileNotFoundException e) {
            System.out.println("File " + e.getMessage() + " not found or could not be open");
            
            return Optional.empty();
        }
    }

    @Override
    public void removeDocument(IDocument document) {
        library.removeDocument(document);

    }

    @Override
    public void updateDocument(IDocument document, DocumentProperties documentProperties) {
        library.updateDocument(document, documentProperties);

    }

    @Override
    public List<IDocument> getMatches(String regex) {
        return library.getMatches(regex);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Library = \n");
        for (IDocument doc : library) {
            result.append(doc + "\n");
        }
        return result + "\n";
    }
}
