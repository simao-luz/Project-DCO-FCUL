package leibooks.domain.shelves;

import java.util.Iterator;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.naming.OperationNotSupportedException;

import leibooks.domain.core.ILibrary;
import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.events.AddShelfEvent;
import leibooks.domain.facade.events.RemoveDocumentShelfEvent;
import leibooks.domain.facade.events.RemoveShelfEvent;
import leibooks.domain.facade.events.ShelfEvent;
import leibooks.utils.AbsSubject;
import leibooks.utils.Listener;
import leibooks.utils.UnRemovable;

/**
 * Manages a collection of shelves within a library.
 * 
 * Also acts as a subject that can emit shelf-related events to its
 * ShelvesController.
 * 
 * @author Simão da Luz, fc61816
 */
public class Shelves extends AbsSubject<ShelfEvent> implements IShelves {

    private final ILibrary lib;
    private Map<String, IShelf> shelves;

    /**
     * Constructs a Shelves manager for the given library. Initializes as an empty
     * shelf map.
     *
     * @param lib The library associated with the list of shelves.
     */
    public Shelves(ILibrary lib) {
        this.lib = lib;
        shelves = new HashMap<>();

        // Creating a "Recent" smart shelf that includes documents modified within the last 5 years;
        SmartShelf recent = new UnremovableSmartShelf("Recent", lib,
                doc -> !doc.getLastModifiedDate().isBefore(LocalDate.now().minusYears(5)));
        shelves.put("Recent", recent);
        emitEvent(new AddShelfEvent("Recent"));

        // Creating a "Bookmarked" smart shelf that includes documents marked as bookmarked;
        SmartShelf bookmarked = new UnremovableSmartShelf("Bookmarked", lib, doc -> doc.isBookmarked());
        shelves.put("Bookmarked", bookmarked);
        emitEvent(new AddShelfEvent("Bookmarked"));
    }

    @Override
    public Iterator<IShelf> iterator() {
        return shelves.values().iterator();
    }

    @Override
    public void emitEvent(ShelfEvent e) {
        super.emitEvent(e);
    }

    @Override
    public void registerListener(Listener<ShelfEvent> obs) {
        super.registerListener(obs);
    }

    @Override
    public void unregisterListener(Listener<ShelfEvent> obs) {
        super.unregisterListener(obs);
    }

    @Override
    public boolean addNormalShelf(String shelfName) {

        // A shelf with this name already exists!
        if (shelves.containsKey(shelfName)) return false;

        IShelf shelf = new NormalShelf(shelfName, lib);
        shelves.put(shelfName, shelf);

        emitEvent(new AddShelfEvent(shelfName));
        return true;
    }

    @Override
    public boolean addSmartShelf(String shelfName, Predicate<IDocument> criteria) {

        if (shelves.containsKey(shelfName)) return false;

        IShelf shelf = new SmartShelf(shelfName, lib, criteria);
        shelves.put(shelfName, shelf);

        emitEvent(new AddShelfEvent(shelfName));
        return true;
    }

    @Override
    public void removeShelf(String shelfName) throws OperationNotSupportedException {
        IShelf shelf = shelves.get(shelfName);

        if (shelf instanceof UnRemovable) {
            throw new OperationNotSupportedException(shelfName + " it's Unremovable.");
        }

        // If the shelf is valid, we remove it from the shelf map
        shelves.remove(shelfName);

        // Unregisters from all documents it was listening to (it no longer exists)
        for (IDocument doc : shelf) {
            doc.unregisterListener(shelf);
        }

        emitEvent(new RemoveShelfEvent(shelfName));
    }

    @Override
    public void removeDocument(String shelfName, IDocument document) throws OperationNotSupportedException {
        IShelf shelf = shelves.get(shelfName);

        // The shelf attempts to remove the document!
        if (shelf.removeDocument(document)) {

            // If successfully removed, an event is emitted to the ShelvesController;
            emitEvent(new RemoveDocumentShelfEvent(shelfName, document));
        }
    }

    @Override
    public boolean addDocument(String shelfName, IDocument document) throws OperationNotSupportedException {
        IShelf shelf = shelves.get(shelfName);
        return shelf.addDocument(document);
    }

    @Override
    public Iterable<IDocument> getDocuments(String shelfName) {
        return shelves.get(shelfName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shelves=\n");

        for (IShelf shelf : this) {
            sb.append(shelf.getName() + "=" + shelf + "\n");
        }

        return sb.toString();
    }
}
