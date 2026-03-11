package leibooks.domain.core;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import leibooks.domain.facade.DocumentProperties;
import leibooks.domain.facade.IDocument;

class LibraryTest {

	private Library library;

	@BeforeEach
	void setUp() {
		library = new Library();
	}

	@Test
	void testAddNewDocument() {
		MockDocument doc = new MockDocument("Test.jpg");
		assertTrue(library.addDocument(doc));
		Stream<IDocument> s = StreamSupport.stream(library.spliterator(), false);
		assertTrue(s.anyMatch(d -> d.equals(doc)));
		assertEquals(1, library.getNumberOfDocuments());
	}

	@Test
	void testAddDuplicatedDocument() {
		MockDocument doc = new MockDocument("Test.jpg");

		assertTrue(library.addDocument(doc));
		assertFalse(library.addDocument(doc));
		Stream<IDocument> s = StreamSupport.stream(library.spliterator(), false);
		assertTrue(s.anyMatch(d -> d.equals(doc)));
		assertEquals(1, library.getNumberOfDocuments());
	}


	@Test
	void testDeleteExistingDocument() {
		MockDocument doc = new MockDocument("Test.jpg");
		library.addDocument(doc);

		library.removeDocument(doc);
		Stream<IDocument> s = StreamSupport.stream(library.spliterator(), false);
		assertFalse(s.anyMatch(d -> d.equals(doc)));
		assertEquals(0, library.getNumberOfDocuments());
	}

	@Test
	void testDeleteNotExistingDocument() {
		MockDocument doc1 = new MockDocument("One.jpg");
		MockDocument doc2 = new MockDocument("Two.jpg");

		library.addDocument(doc1);
		library.removeDocument(doc2);
		Stream<IDocument> s = StreamSupport.stream(library.spliterator(), false);
		assertFalse(s.anyMatch(d -> d.equals(doc2)));
		assertEquals(1, library.getNumberOfDocuments());
	}


	@Test
	void testUpdateDocument() {
		MockDocument doc = new MockDocument("Test.jpg");
		DocumentProperties props = new DocumentProperties(doc);
		props.setTitle("New Title");
		props.setAuthor("New Author");
		
		library.addDocument(doc);
		library.updateDocument(doc, props);
		Stream<IDocument> s = StreamSupport.stream(library.spliterator(), false);
		assertTrue(s.anyMatch(d -> d.equals(doc)));
		//since we are using mocks that are imutable, the effect on properties is not visible
		assertEquals(1, library.getNumberOfDocuments());
	}

	@Test
	void testGetMatchesEmpty() {
		Collection<IDocument> matches = library.getMatches(".*");
		assertNotNull(matches);
		assertTrue(matches.isEmpty());
	}

	@Test
	void testGetMatchesNotEmpty() {
		MockDocument docY = new MockDocument("Y.jpg",true);
		MockDocument docN = new MockDocument("N.jpg",false);

		library.addDocument(docY);
		library.addDocument(docN);
		Collection<IDocument> matches = library.getMatches(".*");

		assertNotNull(matches);
		assertEquals(1, matches.size());
	}
	
	//---------------------------------------------------------------------------------------------------
	//                                      TESTES CRIADOS POR NÓS
	//---------------------------------------------------------------------------------------------------
	 @Test
	    void testAddNullDocument() {
	        assertFalse(library.addDocument(null));
	        assertEquals(0, library.getNumberOfDocuments());
	    }
	 
	 @Test
	    void testRemoveNullDocument() {
	        MockDocument doc = new MockDocument("Existing.jpg");
	        library.addDocument(doc);
	        library.removeDocument(null);
	        assertEquals(1, library.getNumberOfDocuments());
	    }
	 @Test
	    void testToStringIncludesDocuments() {
	        MockDocument doc1 = new MockDocument("Alpha.jpg");
	        MockDocument doc2 = new MockDocument("Beta.jpg");
	        library.addDocument(doc1);
	        library.addDocument(doc2);
	        String libStr = library.toString();
	        assertNotNull(libStr, "A string retornada por toString não deve ser nula.");
	        assertTrue(libStr.contains("Alpha.jpg"), "O toString deve conter a representação de Alpha.jpg.");
	        assertTrue(libStr.contains("Beta.jpg"), "O toString deve conter a representação de Beta.jpg.");
	    }

}