package leibooks.domain.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.naming.OperationNotSupportedException;

import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.IShelvesController;
import leibooks.domain.facade.events.LBEvent;
import leibooks.domain.facade.events.ShelfEvent;
import leibooks.domain.shelves.IShelf;
import leibooks.domain.shelves.IShelves;
import leibooks.utils.AbsSubject;
import leibooks.utils.Listener;

/**
 * Controller responsible for managing shelf-related events.
 * 
 * Acts as a subject emitting LBEvent instances to external listeners whenever
 * shelf-related changes occur.
 * 
 * @author André Marques, fc61782
 */
public class ShelvesController extends AbsSubject<LBEvent> implements IShelvesController {

    private IShelves shelves;

    /**
     * Constructs a ShelvesController with a given shelves list.
     *
     * @param shelves The list of shelves to be managed by this controller.
     */
    public ShelvesController(IShelves shelves) {
        this.shelves = shelves;
        this.shelves.registerListener(this);
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
    public void processEvent(ShelfEvent e) {
        emitEvent(e);
    }

    @Override
    public boolean addNormalShelf(String name) {
        return shelves.addNormalShelf(name);
    }

    @Override
    public boolean addSmartShelf(String name, Predicate<IDocument> criteria) {
        return shelves.addSmartShelf(name, criteria);
    }

    @Override
    public Iterable<String> getShelves() {
        List<String> names = new ArrayList<>();

        for (IShelf shelf : shelves) {
            names.add(shelf.getName());
        }

        return names;
    }

    @Override
    public void remove(String name) throws OperationNotSupportedException {
        shelves.removeShelf(name);
    }

    @Override
    public void removeDocument(String shelfName, IDocument document) throws OperationNotSupportedException {
        shelves.removeDocument(shelfName, document);
    }

    @Override
    public boolean addDocument(String shelfName, IDocument document) throws OperationNotSupportedException {
        return shelves.addDocument(shelfName, document);
    }

    @Override
    public Iterable<IDocument> getDocuments(String shelfName) {
        return shelves.getDocuments(shelfName);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Shelves=\n");
        for (IShelf s : shelves) {
            result.append(s + "= [");
            StringBuilder shelfInfo = new StringBuilder("");
            for (IDocument d : s) {
                shelfInfo.append(d.getFile() + ", ");
            }
            if (shelfInfo.length() == 0) {
                result.append("] \n");
            }
            else {
                result.append(shelfInfo.substring(0, shelfInfo.length() - 2) + "]\n");
            }
        }
        return result + "\n";
    }
}
