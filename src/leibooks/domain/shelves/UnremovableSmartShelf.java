package leibooks.domain.shelves;

import java.util.function.Predicate;

import leibooks.domain.core.ILibrary;
import leibooks.domain.facade.IDocument;
import leibooks.utils.UnRemovable;

/**
 * Class representing an unremovable smart shelf.
 * 
 * Extends {@code SmartShelf} and implements the {@code UnRemovable} marker
 * interface, indicating that this shelf cannot be removed once created.
 * 
 * @author Simão da Luz, fc61816
 */
public class UnremovableSmartShelf extends SmartShelf implements UnRemovable {

    /**
     * Creates an unremovable smart shelf. The implementation is identical to that
     * of a smart shelf.
     * 
     * @param name     The Name of the shelf.
     * @param lib      The Library in which the shelf is located.
     * @param criteria The Predicate used to determine if a document belongs to the
     *                 shelf.
     */
    public UnremovableSmartShelf(String name, ILibrary lib, Predicate<IDocument> criteria) {
        super(name, lib, criteria);
    }
}
