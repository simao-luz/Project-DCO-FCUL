package leibooks.domain.core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.naming.OperationNotSupportedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import leibooks.domain.core.Library;
import leibooks.domain.facade.IDocument;
import leibooks.domain.facade.events.ShelfEvent;
import leibooks.domain.shelves.IShelf;
import leibooks.domain.shelves.NormalShelf;
import leibooks.domain.shelves.Shelves;
import leibooks.utils.UnRemovable;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShelvesTest {

    private Library library;
    private Shelves shelvesManager;


    @BeforeEach
    void setUp() {
        library = new Library();
        shelvesManager = new Shelves(library);
    }
    
    @Test
    void testAddNormalShelfSuccess() {
        boolean added = shelvesManager.addNormalShelf("Shelf1");
        assertTrue(added, "addNormalShelf deve retornar true para nova shelf.");        
        boolean found = false;
        for (IShelf shelf : shelvesManager) {
            if (shelf.getName().equals("Shelf1")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "A shelf 'Shelf1' deve estar presente.");
    }

    @Test
    void testAddNormalShelfDuplicate() {
        assertTrue(shelvesManager.addNormalShelf("Shelf1"));
        boolean secondAttempt = shelvesManager.addNormalShelf("Shelf1");
        assertFalse(secondAttempt, "Adicionar uma shelf duplicada deve retornar false.");
    }
    
    @Test
    void testAddSmartShelfSuccess() {
        Predicate<IDocument> criteria = d -> d.getTitle().contains("Test");
        boolean added = shelvesManager.addSmartShelf("SmartShelf1", criteria);
        assertTrue(added, "addSmartShelf deve retornar true para nova smart shelf.");
        boolean found = false;
        for (IShelf shelf : shelvesManager) {
            if (shelf.getName().equals("SmartShelf1")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "A smart shelf 'SmartShelf1' deve estar presente.");
    }
    
    @Test
    void testRemoveShelfSuccess() throws OperationNotSupportedException {
        shelvesManager.addNormalShelf("ShelfToRemove");
        shelvesManager.removeShelf("ShelfToRemove");
        
        boolean found = false;
        for (IShelf shelf : shelvesManager) {
            if (shelf.getName().equals("ShelfToRemove")) {
                found = true;
                break;
            }
        }
        assertFalse(found, "A shelf 'ShelfToRemove' não deve estar presente após remoção.");
    }
    
    @Test
    void testAddDocumentToShelf() throws OperationNotSupportedException {
        shelvesManager.addNormalShelf("ShelfDocs");
        MockDocument doc = new MockDocument("Doc1.jpg");
        boolean added = shelvesManager.addDocument("ShelfDocs", doc);
        assertTrue(added, "O documento deve ser adicionado com sucesso à shelf.");
        Iterable<IDocument> docs = shelvesManager.getDocuments("ShelfDocs");
        boolean found = false;
        for (IDocument d : docs) {
            if (d.equals(doc)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "O documento 'Doc1.jpg' deve estar presente na shelf 'ShelfDocs'.");
    }
    
    @Test
    void testRemoveDocumentFromShelf() throws OperationNotSupportedException {
        shelvesManager.addNormalShelf("ShelfDocs");
        MockDocument doc = new MockDocument("Doc1.jpg");
        shelvesManager.addDocument("ShelfDocs", doc);
        shelvesManager.removeDocument("ShelfDocs", doc);
        Iterable<IDocument> docs = shelvesManager.getDocuments("ShelfDocs");
        boolean found = false;
        for (IDocument d : docs) {
            if (d.equals(doc)) {
                found = true;
                break;
            }
        }
        assertFalse(found, "O documento deve ter sido removido da shelf 'ShelfDocs'.");
    }
    
    @Test
    void testShelvesToString() {
        shelvesManager.addNormalShelf("ShelfA");
        shelvesManager.addSmartShelf("ShelfB", d -> d.getTitle().contains("Test"));
        String result = shelvesManager.toString();
        assertNotNull(result, "toString não deve retornar null.");
        assertTrue(result.contains("ShelfA"), "toString deve conter o nome 'ShelfA'.");
        assertTrue(result.contains("ShelfB"), "toString deve conter o nome 'ShelfB'.");
    }
}
