package leibooks.domain.shelves;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import leibooks.domain.facade.IDocument;

/**
 * Abstract base class for all types of shelves.
 * 
 * Provides common functionality such as storing the shelf's name, its
 * associated library, and the list of documents it contains.
 * 
 * @author Simão da Luz, fc61816
 */
public abstract class AShelf implements IShelf {

    protected final String name;
    protected final List<IDocument> documents = new LinkedList<>();

    /**
     * Constructs a shelf with the given name and associated library. Automatically
     * registers this shelf as a listener of the library.
     *
     * @param name The name of the shelf.
     * @param lib  The library to which this shelf is associated.
     */
    protected AShelf(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addDocument(IDocument document) throws OperationNotSupportedException {
        if (document == null) {
            throw new IllegalArgumentException("Cannot add null document to shelf " + name);
        }

        if (documents.contains(document)) {
            throw new OperationNotSupportedException(
                    "The document " + document.getTitle() + " already exists in shelf " + name);
        }

        return documents.add(document);
    }

    @Override
    public boolean removeDocument(IDocument document) throws OperationNotSupportedException {
        if (document == null) {
            throw new IllegalArgumentException("Cannot remove null document from the shelf " + name);
        }

        if (!documents.contains(document)) {
            throw new OperationNotSupportedException(
                    "The document " + document.getTitle() + " does not belong to shelf " + name);
        }

        return documents.remove(document);
    }

    @Override
    public Iterator<IDocument> iterator() {
        return documents.iterator();
    }

    @Override
    public String toString() {
        return getName();
    }
}
