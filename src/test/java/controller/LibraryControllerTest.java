package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.controller.BaseController;
import com.library.controller.MyControllerAdvice;
import com.library.dao.jdbc.LibraryDAO;
import com.library.model.Book;
import com.library.model.BookDetails;
import com.library.model.BooksWithGridParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerTest {

    private BooksWithGridParams booksWithGridParams;
    private BookDetails bookDetails;

    @Mock
    private LibraryDAO libraryDAO;

    @Mock
    private MailSender mailSender;

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private BaseController baseController;

    @Before
    public void initData() {
        booksWithGridParams = new BooksWithGridParams();

        List<Book> booksAllSortedByTitle = new LinkedList<>();
        booksAllSortedByTitle.add( new Book(1, "The Golden Calf", Arrays.asList("I.Ilf", "Y.Petrov"), 0) );
        booksAllSortedByTitle.add( new Book(2, "The Old Man and the Sea", Arrays.asList("E.Hemingway"), 0) );
        booksAllSortedByTitle.add( new Book(3, "The Metamorphosis", Arrays.asList("F.Kafka"), 2) );

        booksWithGridParams.setBooks(booksAllSortedByTitle);
        booksWithGridParams.setPageNumber(0);
        booksWithGridParams.setPageSize(3);
        booksWithGridParams.setTotalRows(4);
        booksWithGridParams.setAuthorized(false);

        bookDetails = new BookDetails(Arrays.asList(new SimpleEntry<>("2017-01-25", "address1@gmail.com")));


        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(baseController)
                .setControllerAdvice(new MyControllerAdvice())
                .build();

    }

    @Test
    public void test200okGetAllBooks1() throws Exception {

        when(libraryDAO.getAllBooks(0, 3, "title", true)).thenReturn(booksWithGridParams);

        String jsonResponse = mapper.writeValueAsString(booksWithGridParams);

        mockMvc.perform(get("/getBooks")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(jsonResponse));
    }

    @Test
    public void test200okGetAllBooks2() throws Exception {

        when(libraryDAO.getAllBooks(1, 2, "title", true)).thenReturn(booksWithGridParams);

        String jsonResponse = mapper.writeValueAsString(booksWithGridParams);

        mockMvc.perform(get("/getBooks?pageSize=2&pageNumber=1&filter=1&fieldSortBy=title&sortASC=true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(jsonResponse));
    }

    @Test
    public void test200okGetAvailableBooks() throws Exception {

        when(libraryDAO.getBooksAvailable(0, 3, "title", true)).thenReturn(booksWithGridParams);

        String jsonResponse = mapper.writeValueAsString(booksWithGridParams);

        mockMvc.perform(get("/getBooks?filter=2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(jsonResponse));
    }

    @Test
    public void test200okGetUserBooks() throws Exception {

        booksWithGridParams.setAuthorized(true);

        when(libraryDAO.getBooksTakenByUser(0, 3, "unname@gmail.com", "title", true)).thenReturn(booksWithGridParams);

        String jsonResponse = mapper.writeValueAsString(booksWithGridParams);

        mockMvc.perform(get("/getBooks?filter=3")
                .sessionAttr("user_email", "unname@gmail.com")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(jsonResponse));
    }

    /**
     * Authorisation error (user request his books without log in)
     * @throws Exception
     */
    @Test
    public void test403GetUserBooks() throws Exception {

        mockMvc.perform(get("/getBooks?filter=3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test400GetUserBooks() throws Exception {

        mockMvc.perform(get("/getBooks?filter=4")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test200okRememberUser() throws Exception {

     HttpSession session = mockMvc.perform(get("/rememberUser?email=unname@gmail.com")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession();

        assertEquals("unname@gmail.com", session.getAttribute("user_email"));
    }

    @Test
    public void testIndex() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("frontPage"));

    }

    @Test
    public void test200okAddBook() throws Exception {

        mockMvc.perform(post("/addBook")
                .param("title", "newTestBook")
                .param("authors", "newA1", "newA2")
                .param("quantity", "3"))
                .andExpect(status().isFound()).andExpect(redirectedUrl("getBooks"));

    }

    @Test
    public void test400AddBook() throws Exception {

        mockMvc.perform(post("/addBook")
                .param("title", "")
                .param("authors", "newA1", "newA2")
                .param("quantity", "3"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/addBook")
                .param("title", "newTestBook")
                .param("authors", "newA1", "newA2")
                .param("quantity", "-1"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void test200okChangeQuantity() throws Exception {

        mockMvc.perform(post("/changeQuantity")
                .param("quantity", "5")
                .param("bookId", "1"))
                .andExpect(status().isOk());

    }

    @Test
    public void test400ChangeQuantity() throws Exception {

        mockMvc.perform(post("/changeQuantity")
                .param("quantity", "-1")
                .param("bookId", "1"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void test200okTakeBook() throws Exception {

        when(libraryDAO.getBookQuantity(1)).thenReturn(3);

        mockMvc.perform(post("/takeBook")
                .sessionAttr("user_email", "unname@gmail.com")
                .param("bookId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void test403TakeBook() throws Exception {

        mockMvc.perform(post("/takeBook")
                .param("bookId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test400TakeBook() throws Exception {

        mockMvc.perform(post("/takeBook")
                .sessionAttr("user_email", "unname@gmail.com")
                .param("bookId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test200okGetBookDetails() throws Exception {

        when(libraryDAO.getBookDetails(1)).thenReturn(bookDetails.getDetails());

        String jsonResponse = mapper.writeValueAsString(bookDetails);

        mockMvc.perform(get("/bookDetails?bookId=1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(jsonResponse));
    }

    @Test
    public void test200oDeleteBook() throws Exception {

        mockMvc.perform(post("/deleteBook")
                .param("bookId", "1"))
                .andExpect(status().isFound()).andExpect(redirectedUrl("getBooks"));

    }

}
