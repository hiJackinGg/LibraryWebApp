package dao;

import com.library.dao.jdbc.LibraryDAO;
import com.library.model.Book;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-spring-context.xml")
public class LibraryDaoTest {

    @Autowired
    LibraryDAO libraryDAO;

    static List<Book> expectedBooksAllSortedByTitle = new LinkedList<>();
    static List<Book> expectedBooksAllSortedByAuthor = new LinkedList<>();
    static List<Book> expectedBooksAvailableSortedByTitle = new LinkedList<>();
    static List<Book> expectedBooksAvailableSortedByAuthor = new LinkedList<>();
    static List<Book> expectedBooksUserSortedByTitle = new LinkedList<>();
    static List<Book> expectedBooksUserSortedByAuthor = new LinkedList<>();

    @BeforeClass
    public static void init(){
        prepareData();
    }


    @Test
    public  void getContactsTest() throws SQLException {

        libraryDAO.getAllBooks(1, 3, "title", true);

    }

    /**
     * Retrieves all books sorted by "title" ascending.
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks1() throws SQLException {
        List<Book> actualBooks = libraryDAO.getAllBooks(0, expectedBooksAllSortedByTitle.size(), "title", true).getBooks();
        expectedBooksAllSortedByTitle.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));

        assertEquals(expectedBooksAllSortedByTitle, actualBooks);
    }

    /**
     * Retrieves all books sorted by "title" descending.
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks2() throws SQLException
    {
        List<Book> actualBooks = libraryDAO.getAllBooks(0, expectedBooksAllSortedByTitle.size(), "title", false).getBooks();
        expectedBooksAllSortedByTitle.sort((b1, b2) -> b2.getTitle().compareTo(b1.getTitle()));

        assertEquals(expectedBooksAllSortedByTitle, actualBooks);
    }

    /**
     * Retrieves books empty.
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks21() throws SQLException {
        List<Book> actualBooks = libraryDAO.getAllBooks(10, 3, "title", true).getBooks();

        assertTrue(actualBooks.isEmpty());
    }

    /**
     * Incorrect page number (must be >=0), throw exception
     * @throws SQLException
     */
    @Test(expected = IllegalArgumentException.class)
    public  void testFindAllBooks22() throws SQLException {
        libraryDAO.getAllBooks(-1, 3, "title", true).getBooks();
    }

    /**
     * page size must be greater then 0, throw exception
     * @throws SQLException
     */
    @Test(expected = IllegalArgumentException.class)
    public  void testFindAllBooks23() throws SQLException {
        libraryDAO.getAllBooks(0, 0, "title", true).getBooks();
    }

    /**
     * incorrect sorting field - "price", throw exception
     * @throws SQLException
     */
    @Test(expected = IllegalArgumentException.class)
    public  void testFindAllBooks24() throws SQLException {
        List<Book> actualBooks1 = libraryDAO.getAllBooks(0, 3, "price", true).getBooks();
    }

    /**
     * Retrieves all books from second page.
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks25() throws SQLException {
        List<Book> actualBooks = libraryDAO.getAllBooks(1, 3, "title", false).getBooks();
        int expectedRowCountInPage = 1;
        assertEquals(expectedRowCountInPage, actualBooks.size());
    }

    /**
     * Retrieves all books from second page with page size = 1
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks26() throws SQLException {
        List<Book> actualBooks = libraryDAO.getAllBooks(2, 1, "title", false).getBooks();
        int expectedRowCountInPage = 1;
        assertEquals(expectedRowCountInPage, actualBooks.size());
    }

    /**
     * Retrieves all books sorted by "author" ascending.
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks4() throws SQLException {
        List<Book> actualBooks = libraryDAO.getAllBooks(0, expectedBooksAllSortedByAuthor.size(), "author", true).getBooks();
        expectedBooksAllSortedByAuthor.sort((b1, b2) -> {
            int res = b1.getAuthors().get(0).compareTo(b2.getAuthors().get(0));
            if ( res == 0 ) {
                return b1.getId() - b2.getId();
            } else {
                return res;
            }
        });

        assertEquals(expectedBooksAllSortedByAuthor, actualBooks);
    }

    /**
     * Retrieves all books sorted by "author" descending.
     * @throws SQLException
     */
    @Test
    public  void testFindAllBooks5() throws SQLException {
        List<Book> actualBooks = libraryDAO.getAllBooks(0, expectedBooksAllSortedByAuthor.size(), "author", false).getBooks();
        expectedBooksAllSortedByAuthor.sort((b1, b2) -> {
            int res = b2.getAuthors().get(0).compareTo(b1.getAuthors().get(0));
            if ( res == 0 ) {
                return b2.getId() - b1.getId();
            } else {
                return res;
            }
        });
        assertEquals(expectedBooksAllSortedByAuthor, actualBooks);
    }

    /**
     * Retrieves available books sorted by "title" ascending.
     * @throws SQLException
     */
    @Test
    public  void testFindAvailableBooks6() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksAvailable(0, expectedBooksAvailableSortedByTitle.size(), "title", true).getBooks();
        expectedBooksAvailableSortedByTitle.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));

        assertEquals(expectedBooksAvailableSortedByTitle, actualBooks);
    }

    /**
     * Retrieves available books sorted by "title" descending.
     * @throws SQLException
     */
    @Test
    public  void testFindAvailableBooks7() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksAvailable(0, expectedBooksAvailableSortedByTitle.size(), "title", false).getBooks();
        expectedBooksAvailableSortedByTitle.sort((b1, b2) -> b2.getTitle().compareTo(b1.getTitle()));

        assertEquals(expectedBooksAvailableSortedByTitle, actualBooks);
    }

    /**
     * Retrieves available books sorted by "author" ascending.
     * @throws SQLException
     */
    @Test
    public  void testFindAvailableBooks8() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksAvailable(0, expectedBooksAvailableSortedByAuthor.size(), "author", true).getBooks();
        expectedBooksAvailableSortedByAuthor.sort((b1, b2) -> {
            int res = b1.getAuthors().get(0).compareTo(b2.getAuthors().get(0));
            if ( res == 0 ) {
                return b1.getId() - b2.getId();
            } else {
                return res;
            }
        });
        assertEquals(expectedBooksAvailableSortedByAuthor, actualBooks);
    }

    /**
     * Retrieves available books sorted by "author" descending.
     * @throws SQLException
     */
    @Test
    public  void testFindAvailableBooks9() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksAvailable(0, expectedBooksAvailableSortedByAuthor.size(), "author", false).getBooks();
        expectedBooksAvailableSortedByAuthor.sort((b1, b2) -> {
            int res = b2.getAuthors().get(0).compareTo(b1.getAuthors().get(0));
            if ( res == 0 ) {
                return b1.getId() - b2.getId();
            } else {
                return res;
            }
        });
        assertEquals(expectedBooksAvailableSortedByAuthor, actualBooks);
    }

    /**
     * Retrieves books taken by user sorted by "title" ascending.
     * @throws SQLException
     */
    @Test
    public  void testFindBooksTakenByUser10() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksTakenByUser(0, expectedBooksUserSortedByTitle.size(), "address2@gmail.com", "title", true).getBooks();
//        expectedBooksUserSortedByTitle.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
        expectedBooksUserSortedByTitle.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
        assertEquals(expectedBooksUserSortedByTitle, actualBooks);
    }

    /**
     * Retrieves books taken by user sorted by "title" descending.
     * @throws SQLException
     */
    @Test
    public  void testFindBooksTakenByUser11() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksTakenByUser(0, expectedBooksUserSortedByTitle.size(), "address2@gmail.com", "title", false).getBooks();
        expectedBooksUserSortedByTitle.sort((b1, b2) -> b2.getTitle().compareTo(b1.getTitle()));

        assertEquals(expectedBooksUserSortedByTitle, actualBooks);
    }

    /**
     * Retrieves books taken by user sorted by "author" ascending.
     * @throws SQLException
     */
    @Test
    public  void testFindBooksTakenByUser12() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksTakenByUser(0, expectedBooksUserSortedByAuthor.size(), "address2@gmail.com", "author", true).getBooks();
        expectedBooksUserSortedByAuthor.sort((b1, b2) -> {
            int res = b1.getAuthors().get(0).compareTo(b2.getAuthors().get(0));
            if ( res == 0 ) {
                return b1.getId() - b2.getId();
            } else {
                return res;
            }
        });
        assertEquals(expectedBooksUserSortedByAuthor, actualBooks);
    }

    /**
     * Retrieves books taken by user sorted by "author" descending.
     * @throws SQLException
     */
    @Test
    public  void testFindBooksTakenByUser13() throws SQLException {
        List<Book> actualBooks = libraryDAO.getBooksTakenByUser(0, expectedBooksUserSortedByAuthor.size(), "address2@gmail.com", "author", false).getBooks();
        expectedBooksUserSortedByAuthor.sort((b1, b2) -> {
            int res = b2.getAuthors().get(0).compareTo(b1.getAuthors().get(0));
            if ( res == 0 ) {
                return b1.getId() - b2.getId();
            } else {
                return res;
            }
        });
        assertEquals(expectedBooksUserSortedByAuthor, actualBooks);
    }

    /**
     * Retrieves book details.
     * @throws SQLException
     */
    @Test
    public  void testFindBookDetails14() throws SQLException {
        List<SimpleEntry<String, String>> expectedDetails = new LinkedList<>();
        expectedDetails.add(new SimpleEntry<>("2017-01-25", "address1@gmail.com"));
        expectedDetails.add(new SimpleEntry<>("2017-01-25", "address3@yandex.com"));

        int bookId = 1;
        List<AbstractMap.SimpleEntry<String, String>> actualDetails = libraryDAO.getBookDetails(bookId);
        assertEquals(expectedDetails, actualDetails);
    }

    /**
     * Retrieves book quantity.
     * @throws SQLException
     */
    @Test
    public  void testBookQuantity15() throws SQLException {
        int bookId1 = 1;
        int bookId2 = 3;
        int expectedQty1 = 0;
        int expectedQty2 = 2;

        int actualQty1 = libraryDAO.getBookQuantity(bookId1);
        int actualQty2 = libraryDAO.getBookQuantity(bookId2);

        assertEquals(expectedQty1, actualQty1);
        assertEquals(expectedQty2, actualQty2);
    }

    /**
     * Add new book.
     * @throws SQLException
     */
    @Test
    public  void testAddNewBook16() throws SQLException {

        Book newTestBook = new Book("testBook", Arrays.asList("", "testAuthor", ""), 1);
        int id = libraryDAO.addBook(newTestBook);

        assertTrue(id != 0);//check that new book added (persistent)

        newTestBook.setId(id);
        newTestBook.setAuthors(Arrays.asList("testAuthor"));//empty authors won't be saved

        List<Book> allBooksWithNewInserted = libraryDAO.getAllBooks(0, expectedBooksAllSortedByTitle.size() + 1, "title", true).getBooks();

        assertTrue(allBooksWithNewInserted.contains(newTestBook));//check that new book really exists
        assertEquals(expectedBooksAllSortedByTitle.size() + 1, allBooksWithNewInserted.size());//check that retrieved books size changed by one

        libraryDAO.removeBook(id);//undo changes
    }

    /**
     * Remove book.
     * @throws SQLException
     */
    @Test
    public  void testRemoveBook17() throws SQLException {

        Book newTestBook = new Book("testBook", Arrays.asList("", "testAuthor", ""), 1);
        int id = libraryDAO.addBook(newTestBook);//add new book before delete it

        assertTrue(id != 0);//check that new book added (persistent)

        libraryDAO.removeBook(id);

        List<Book> allBooksWithNewInserted = libraryDAO.getAllBooks(0, expectedBooksAllSortedByTitle.size(), "title", true).getBooks();

        assertFalse(allBooksWithNewInserted.contains(newTestBook));//check that the added book hasn't existed any more
        assertEquals(expectedBooksAllSortedByTitle.size(), allBooksWithNewInserted.size());//check that retrieved books size has initial size

    }

    /**
     * Change book quantity.
     * @throws SQLException
     */
    @Test
    public  void testChangeQty18() throws SQLException {

        int newQty = 5;
        int oldQty = 0;
        int bookId = 1;

        libraryDAO.changeBookQuantity(bookId, newQty);

        int actualQty = libraryDAO.getBookQuantity(bookId);

        assertEquals(newQty, actualQty);

        libraryDAO.changeBookQuantity(bookId, oldQty);

    }

    /**
     * Change book quantity with incorrect value.
     * @throws SQLException
     */
    @Test(expected = IllegalArgumentException.class)
    public  void testBookQtyError19() throws SQLException {

        int newQty = -1;
        int oldQty = 0;
        int bookId = 1;

        libraryDAO.changeBookQuantity(bookId, newQty);

        ////////
        int actualQty = libraryDAO.getBookQuantity(bookId);
        assertEquals(newQty, actualQty);
        libraryDAO.changeBookQuantity(bookId, oldQty);

    }

    /**
     * Take book.
     * @throws SQLException
     */
    @Test
    public  void testTakeBook20() throws SQLException {

        int bookId = 3;
        String userEmail = "unname@gmail.com";
        int oldQty = 2;

        libraryDAO.takeBook(bookId, userEmail);

        int qty = libraryDAO.getBookQuantity(bookId);

        assertEquals(oldQty - 1, qty);//check that book quantity really decreased by one

        List<SimpleEntry<String, String>> bookDetails = libraryDAO.getBookDetails(bookId);

        assertEquals(userEmail, bookDetails.get(0).getValue());//check that user really took the book

    }

    /**
     * Take book which is not available (quantity = 0).
     * @throws SQLException
     */
    @Test(expected = SQLException.class)
    public  void testTakeBookError21() throws SQLException {

        libraryDAO.takeBook(1, "unname@gmail.com");

    }


    /**
     * Prepare test data before testing.
     * All this data must be in test database (testLibrary).
     */
    private static void prepareData(){
        expectedBooksAllSortedByTitle.add( new Book(1, "The Golden Calf", Arrays.asList("I.Ilf", "Y.Petrov"), 0) );
        expectedBooksAllSortedByTitle.add( new Book(2, "The Old Man and the Sea", Arrays.asList("E.Hemingway"), 0) );
        expectedBooksAllSortedByTitle.add( new Book(3, "The Metamorphosis", Arrays.asList("F.Kafka"), 2) );
        expectedBooksAllSortedByTitle.add( new Book(4, "For Whom the Bell Tolls", Arrays.asList("E.Hemingway"), 3) );

        expectedBooksAllSortedByAuthor.add( new Book(1, "The Golden Calf", Arrays.asList("I.Ilf"), 0) );
        expectedBooksAllSortedByAuthor.add( new Book(1, "The Golden Calf", Arrays.asList("Y.Petrov"), 0) );
        expectedBooksAllSortedByAuthor.add( new Book(3, "The Metamorphosis", Arrays.asList("F.Kafka"), 2) );
        expectedBooksAllSortedByAuthor.add( new Book(4, "For Whom the Bell Tolls", Arrays.asList("E.Hemingway"), 3) );
        expectedBooksAllSortedByAuthor.add( new Book(2, "The Old Man and the Sea", Arrays.asList("E.Hemingway"), 0) );

        expectedBooksAvailableSortedByTitle.add( new Book(3, "The Metamorphosis", Arrays.asList("F.Kafka"), 2) );
        expectedBooksAvailableSortedByTitle.add( new Book(4, "For Whom the Bell Tolls", Arrays.asList("E.Hemingway"), 3) );

        expectedBooksAvailableSortedByAuthor.add( new Book(3, "The Metamorphosis", Arrays.asList("F.Kafka"), 2) );
        expectedBooksAvailableSortedByAuthor.add( new Book(4, "For Whom the Bell Tolls", Arrays.asList("E.Hemingway"), 3) );

        expectedBooksUserSortedByTitle.add( new Book(4, "For Whom the Bell Tolls", Arrays.asList("E.Hemingway"), 3) );
        expectedBooksUserSortedByTitle.add( new Book(2, "The Old Man and the Sea", Arrays.asList("E.Hemingway"), 0) );

        expectedBooksUserSortedByAuthor.add( new Book(4, "For Whom the Bell Tolls", Arrays.asList("E.Hemingway"), 3) );
        expectedBooksUserSortedByAuthor.add( new Book(2, "The Old Man and the Sea", Arrays.asList("E.Hemingway"), 0) );
    }

}
