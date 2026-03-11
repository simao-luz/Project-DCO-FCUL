package leibooks.domain.shelves;

import java.util.function.Predicate;
import javax.naming.OperationNotSupportedException;
import leibooks.domain.core.ILibrary;
import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.events.*;

/**
 * Class representing a smart shelf. It is considered smart because it does not
 * support manual addition or removal of documents. Documents are automatically
 * added or removed based on whether they meet the defined criteria.
 * 
 * @author Simão da Luz, fc61816
 */
public class SmartShelf extends AShelf {

    private final ILibrary lib;
    private final Predicate<IDocument> criteria;

    /**
     * Creates a smart shelf. Documents will be automatically added or removed based
     * on whether they meet the defined criteria.
     *
     * @param name     The name of the shelf.
     * @param lib      The library in which the shelf is located.
     * @param criteria The predicate used to determine if a document belongs to the
     *                 shelf.
     */
    public SmartShelf(String name, ILibrary lib, Predicate<IDocument> criteria) {
        super(name);

        this.lib = lib;
        this.criteria = criteria;
        this.lib.registerListener(this);

        // Initial Synchronization - Add documents from the Library that already meet
        // the criteria;
        for (IDocument doc : this.lib) {
            if (criteria.test(doc)) {
                documents.add(doc);
            }

            doc.registerListener(this); // Listens to all documents in the library to track future changes.
        }
    }

    @Override
    public void processEvent(DocumentEvent e) {
        IDocument eventDoc = e.getDocument();

        // If a document was added to the Library;
        if (e instanceof AddDocumentEvent) {

            // Check if that new document meets the criteria;
            if (criteria.test(eventDoc)) {
                documents.add(eventDoc);
            }

            eventDoc.registerListener(this);
        }

        // If a document was removed from the Library;
        else if (e instanceof RemoveDocumentEvent) {

            // The shelf has a reference to the document removed?;
            if (documents.contains(eventDoc)) {
                documents.remove(eventDoc);
                eventDoc.unregisterListener(this);
            }
        }

        // For all other events (update events) — just recheck the criteria!
        else {

            // If the document meets the criteria and isn't yet in the smartshelf...
            if (criteria.test(eventDoc) && !documents.contains(eventDoc)) {

                documents.add(eventDoc); // ...then, it'll be added.
            }

            // If the document does not meet the criteria and was present in the shelf -> is
            // no longer valid...
            else if (!criteria.test(eventDoc) && documents.contains(eventDoc)) {

                documents.remove(eventDoc); // ...then, it'll be removed.
            }

            else {
                // The event does not meet the criteria, so it is ignored as it serves no
                // purpose for the shelf.
            }
        }
    }

    @Override
    public boolean addDocument(IDocument document) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("A SmartShelf does not allow direct addition of documents");
    }

    @Override
    public boolean removeDocument(IDocument document) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("A SmartShelf does not allow direct removal of documents");
    }
}
