package leibooks.domain.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import leibooks.domain.facade.DocumentProperties;
import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.events.AddDocumentEvent;
import leibooks.domain.facade.events.DocumentEvent;
import leibooks.domain.facade.events.RemoveDocumentEvent;
import leibooks.utils.AbsSubject;
import leibooks.utils.Listener;

/**
 * Class that represents a library that stores documents and manages
 * document-related events.
 * 
 * This class extends {@code AbsSubject<DocumentEvent>} to notify its observers
 * (shelves & controller) of document-related events, such as when a document is
 * added or removed.
 * 
 * It implements the {@code ILibrary} interface to fulfill the contract for
 * managing a list of documents.
 * 
 * @author André Marques, fc61782
 * @author Simão da Luz, fc61816
 */
public class Library extends AbsSubject<DocumentEvent> implements ILibrary {

    private List<IDocument> documents; // List of documents in the Library;

    public Library() {
        documents = new LinkedList<>(); // A Library is initially an empty list.
    }

    @Override
    public Iterator<IDocument> iterator() {
        return documents.iterator();
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
    public int getNumberOfDocuments() {
        return documents.size();
    }

    @Override
    public boolean addDocument(IDocument document) {
        if (document == null || documents.contains(document)) {
            return false;
        }

        documents.add(document);
        emitEvent(new AddDocumentEvent(document));
        return true;
    }

    @Override
    public void removeDocument(IDocument document) {
        if (document == null) {
            return;
        }

        // If the document exists, it will be removed and an event will be emitted to
        // notify the change.
        if (documents.remove(document)) {
            emitEvent(new RemoveDocumentEvent(document));
        }
    }

    @Override
    public void updateDocument(IDocument document, DocumentProperties documentProperties) {
        if (document == null || documentProperties == null) {
            return;
        }

        // Author and title are the only mutable properties of a document (as well as
        // the last modified date);
        document.setAuthor(documentProperties.author());
        document.setTitle(documentProperties.title());
    }

    @Override
    public List<IDocument> getMatches(String regex) {
        List<IDocument> matchesList = new LinkedList<>();

        for (IDocument document : documents) {
            if (document.matches(regex)) {
                matchesList.add(document);
            }
        }

        return matchesList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Library =\n");

        for (IDocument doc : documents) {
            sb.append(doc).append("\n");
        }

        return sb.toString();
    }
}
