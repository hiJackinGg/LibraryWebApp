package com.library.controller;

import com.library.dao.jdbc.LibraryDAO;
import com.library.model.Book;
import com.library.model.BookDetails;
import com.library.model.BooksWithGridParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

@Controller
public class BaseController {

	@Autowired
	LibraryDAO libraryDAO;

	@Autowired
	MailSender mailSender;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {

		return "frontPage";

	}

	@RequestMapping(value = "/getBooks",produces="application/json", method = RequestMethod.GET)
	public @ResponseBody
	BooksWithGridParams getBooks(HttpServletRequest request) throws SQLException {

		int pageSize = (request.getParameter("pageSize") == null) ? 3 : Integer.parseInt(request.getParameter("pageSize"));
		int pageNumber = (request.getParameter("pageNumber") == null) ? 0 : Integer.parseInt(request.getParameter("pageNumber"));
		int filter = (request.getParameter("filter") == null) ? 1 : Integer.parseInt(request.getParameter("filter"));
		String fieldSortBy = (request.getParameter("fieldSortBy") == null) ? "title" : request.getParameter("fieldSortBy");
		boolean asc = (request.getParameter("sortASC") == null) ? true : Boolean.parseBoolean(request.getParameter("sortASC"));

		String userEmail = (String)request.getSession().getAttribute("user_email");

		BooksWithGridParams booksWithGridParams = null;

		if (filter == 1) {
			booksWithGridParams = libraryDAO.getAllBooks(pageNumber, pageSize, fieldSortBy, asc);
		}
		else if (filter == 2)
		{
			booksWithGridParams = libraryDAO.getBooksAvailable(pageNumber, pageSize, fieldSortBy, asc);
		}
		else if (filter == 3)
		{
			if (userEmail != null)
			{
				booksWithGridParams = libraryDAO.getBooksTakenByUser(pageNumber, pageSize, userEmail, fieldSortBy, asc);
			}
			else
			{
				throw new ResourceException(HttpStatus.FORBIDDEN, "Authorization error. Input your email");
			}
		}
		else
		{
			throw new ResourceException(HttpStatus.BAD_REQUEST, "BAD REQUEST");
		}


		booksWithGridParams.setAuthorized(userEmail != null);
		return booksWithGridParams;
	}


	@RequestMapping(value="/rememberUser", method = RequestMethod.GET )
	@ResponseStatus(value = HttpStatus.OK)
	public void rememberUser(@RequestParam(value = "email") String email,
									 HttpServletRequest request)
	{
		if (email == null)//regex
			throw new ResourceException(HttpStatus.BAD_REQUEST, "The email you've entered is wrong: " + email);

		request.getSession().setAttribute("user_email", email);
	}

	@RequestMapping(value="/addBook", method = RequestMethod.POST)
	public String addBook(@ModelAttribute Book book) throws IOException, SQLException {

		if (book.getTitle().isEmpty()) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, "Book title can not be empty.");
		}
		else if (book.getQuantity() < 0){
			throw new ResourceException(HttpStatus.BAD_REQUEST, "Incorrect quantity value: " + book.getQuantity());
		}

		libraryDAO.addBook(book);

		return "redirect:getBooks";
	}

	@RequestMapping(value="/changeQuantity", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void changeQuantity(int quantity, int bookId) throws IOException, SQLException {

		if (quantity < 0) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, "Incorrect quantity value: " + quantity);
		}

		libraryDAO.changeBookQuantity(bookId, quantity);

	}

	@RequestMapping(value="/takeBook", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void takeBook(int bookId,
						 HttpServletRequest request) throws SQLException {

			String userEmail = (String) request.getSession().getAttribute("user_email");

			if (userEmail == null)
				throw new ResourceException(HttpStatus.FORBIDDEN, "Log in, please. Input your email.");

			if (libraryDAO.getBookQuantity(bookId) <= 0)
				throw new ResourceException(HttpStatus.BAD_REQUEST, "There are no books left.");

			libraryDAO.takeBook(bookId, userEmail);

			this.sendNotification(bookId, userEmail);

	}

    @RequestMapping(value="/bookDetails", method = RequestMethod.GET)
	public @ResponseBody
	BookDetails bookDetails(int bookId) throws SQLException {

		List<SimpleEntry<String, String>> details = libraryDAO.getBookDetails(bookId);

		BookDetails bookDetails = new BookDetails(details);

		return bookDetails;
	}

	@RequestMapping(value="/deleteBook", method = RequestMethod.POST)
	public String deleteBook(int bookId) throws SQLException {

		libraryDAO.removeBook(bookId);

		return "redirect:getBooks";
	}

	/**
	 * send mail to the user
	 * @param bookId
	 * @param userEmail
     */
	public void sendNotification(int bookId, String userEmail)
	{
		String body = "You took a book in our library. (Id of the book is - " + bookId + ")";
		String subject = "Library";

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(userEmail);
		email.setSubject(subject);
		email.setText(body);

		mailSender.send(email);
	}

}