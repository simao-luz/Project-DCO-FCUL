package leibooks.domain.shelves;

import leibooks.domain.core.ILibrary;
import leibooks.domain.facade.IDocument;

import leibooks.domain.facade.events.DocumentEvent;
import leibooks.domain.facade.events.RemoveDocumentEvent;

/**
 * A regular shelf that allows manual addition and removal of documents.
 * 
 * It is a listener to a Library (more specifically it listens to removals in
 * the Library).
 * 
 * @author Simão da Luz, fc61816
 */
public class NormalShelf extends AShelf {

    private final ILibrary lib;

    /**
     * Creates a normal shelf associated with the given library.
     *
     * @param name The name of the shelf.
     * @param lib  The library this shelf belongs to.
     */
    public NormalShelf(String name, ILibrary lib) {
        super(name);

        this.lib = lib;
        this.lib.registerListener(this); // A new shelf automatically becomes a listener of a library;
    }

    @Override
    public void processEvent(DocumentEvent e) {
        IDocument eventDoc = e.getDocument();

        if (e instanceof RemoveDocumentEvent) {
            for (IDocument shelfDoc : documents) {

                // If the shelf contains the document that emitted the event;
                if (eventDoc.equals(shelfDoc)) {
                    documents.remove(shelfDoc); // Removes the document from the shelf.
                    break;
                }
            }
        }
    }
}
