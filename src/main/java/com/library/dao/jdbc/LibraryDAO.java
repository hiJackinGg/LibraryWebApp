package com.library.dao.jdbc;


import com.library.model.Book;
import com.library.model.BooksWithGridParams;
import com.library.model.DAOQueries;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import com.library.dao.jdbc.BooksCreator.*;
import org.springframework.stereotype.Repository;

import java.util.AbstractMap.SimpleEntry;

@Repository()
public class LibraryDAO {

    @Autowired
    private DataSource dataSource;

    public BooksWithGridParams getAllBooks(int pageNumber, int pageSize, String fieldSortBy, boolean asc) throws SQLException {

        if (pageNumber < 0)
            throw new IllegalArgumentException(String.format("PageNumber argument is %d. It must be greater or equal \"0\".", pageNumber));
        if (pageSize <= 0)
            throw new IllegalArgumentException(String.format("PageSize argument is %d. It must be greater \"0\".", pageSize));

        String query = null;
        String sortDirection = asc == true ? "asc" : "desc";
        IBooksCreator booksCreator = null;

        if (fieldSortBy.equals("title")) {
            query = String.format(DAOQueries.GET_ALL_BOOKS_SORTED_BY_TITLE, sortDirection);
            booksCreator = new BookListCreator();
        } else if (fieldSortBy.equals("author")) {
            query = String.format(DAOQueries.GET_ALL_BOOKS_SORTED_BY_AUTHOR, sortDirection);
            booksCreator = new BookPerAuthorListCreator();
        }
        else {
            throw new IllegalArgumentException(String.format("FieldSortBy argument is %d. It must have \"title\" or \"author\" values.", fieldSortBy));
        }

        BooksWithGridParams booksWithGridParams = new BooksWithGridParams();

        SimpleEntry<Integer, List<Book>> res = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prst = connection.prepareStatement(query);
            prst.setInt(1, pageNumber * pageSize);
            prst.setInt(2, pageSize);

            ResultSet resultSet = prst.executeQuery();

            res = booksCreator.buildBooks(resultSet);

        }

        booksWithGridParams.setTotalRows(res.getKey());
        booksWithGridParams.setBooks(res.getValue());
        booksWithGridParams.setPageSize(pageSize);
        booksWithGridParams.setPageNumber(pageNumber);

        return booksWithGridParams;

    }

    public BooksWithGridParams getBooksAvailable(int pageNumber, int pageSize, String fieldSortBy, boolean asc) throws SQLException {

        if (pageNumber < 0)
            throw new IllegalArgumentException(String.format("PageNumber argument is %d. It must be greater or equal \"0\".", pageNumber));
        if (pageSize <= 0)
            throw new IllegalArgumentException(String.format("PageSize argument is %d. It must be greater \"0\".", pageSize));

        String query = null;
        String sortDirection = asc == true ? "asc" : "desc";
        IBooksCreator booksCreator = null;

        if (fieldSortBy.equals("title")) {
            query = String.format(DAOQueries.GET_AVAILABLE_BOOKS_SORTED_BY_TITLE, sortDirection);
            booksCreator = new BookListCreator();
        } else if (fieldSortBy.equals("author")) {
            query = String.format(DAOQueries.GET_AVAILABLE_BOOKS_SORTED_BY_AUTHOR, sortDirection);
            booksCreator = new BookPerAuthorListCreator();
        }
        else {
            throw new IllegalArgumentException(String.format("FieldSortBy argument is %d. It must have \"title\" or \"author\" values.", fieldSortBy));
        }

        BooksWithGridParams booksWithGridParams = new BooksWithGridParams();

        SimpleEntry<Integer, List<Book>> res = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prst = connection.prepareStatement(query);
            prst.setInt(1, pageNumber * pageSize);
            prst.setInt(2, pageSize);

            ResultSet resultSet = prst.executeQuery();

            res = booksCreator.buildBooks(resultSet);

        }

        booksWithGridParams.setTotalRows(res.getKey());
        booksWithGridParams.setBooks(res.getValue());
        booksWithGridParams.setPageSize(pageSize);
        booksWithGridParams.setPageNumber(pageNumber);

        return booksWithGridParams;
    }

    public BooksWithGridParams getBooksTakenByUser(int pageNumber, int pageSize, String userEmail, String fieldSortBy, boolean asc) throws SQLException {

        if (pageNumber < 0)
            throw new IllegalArgumentException(String.format("PageNumber argument is %d. It must be greater or equal \"0\".", pageNumber));
        if (pageSize <= 0)
            throw new IllegalArgumentException(String.format("PageSize argument is %d. It must be greater \"0\".", pageSize));

        String query = null;
        String sortDirection = asc == true ? "asc" : "desc";
        IBooksCreator booksCreator = null;

        if (fieldSortBy.equals("title")) {
            query = String.format(DAOQueries.GET_TAKEN_BY_USER_BOOKS_SORTED_BY_TITLE, sortDirection, sortDirection);
            booksCreator = new BookListCreator();
        } else if (fieldSortBy.equals("author")) {
            query = String.format(DAOQueries.GET_TAKEN_BY_USER_BOOKS_SORTED_BY_AUTHOR, sortDirection, sortDirection);
            booksCreator = new BookPerAuthorListCreator();
        }
        else {
            throw new IllegalArgumentException(String.format("FieldSortBy argument is %d. It must have \"title\" or \"author\" values.", fieldSortBy));
        }

        BooksWithGridParams booksWithGridParams = new BooksWithGridParams();

        SimpleEntry<Integer, List<Book>> res = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prst = connection.prepareStatement(query);
            prst.setString(1, userEmail);
            prst.setInt(2, pageNumber * pageSize);
            prst.setInt(3, pageSize);
            prst.setString(4, userEmail);

            ResultSet resultSet = prst.executeQuery();

            res = booksCreator.buildBooks(resultSet);

        }

        booksWithGridParams.setTotalRows(res.getKey());
        booksWithGridParams.setBooks(res.getValue());
        booksWithGridParams.setPageSize(pageSize);
        booksWithGridParams.setPageNumber(pageNumber);

        return booksWithGridParams;

    }

    public List<SimpleEntry<String, String>> getBookDetails(int bookId) throws SQLException {

        StringBuilder query = new StringBuilder();
        query.append("select l.date, l.email from Books b ");
        query.append("inner join Lib_log l ");
        query.append("on l.bookID = b.id ");
        query.append("where b.id = ? ");

        List<SimpleEntry<String, String>> details = new LinkedList<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prst = connection.prepareStatement(query.toString());
            prst.setInt(1, bookId);

            try (ResultSet resultSet = prst.executeQuery()) {

                SimpleEntry<String, String> pair;
                String date, user;

                while (resultSet.next()) {
                    date = resultSet.getDate(1).toString();
                    user = resultSet.getString(2);
                    pair = new SimpleEntry<String, String>(date, user);
                    details.add(pair);
                }

            }
        }

        return details;
    }

    public int addBook(Book book) throws SQLException {

        if (book == null)
            throw new IllegalArgumentException(String.format("Book entity is null."));

        String query = "insert into Books (title, quantity) values (?,?)";

        int bookId = 0;
        Collection<String> authors = book.getAuthors();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement prst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prst.setString(1, book.getTitle());
            prst.setInt(2, book.getQuantity());
            prst.executeUpdate();

            ResultSet resultSet = prst.getGeneratedKeys();
            if (resultSet.next()) {
                bookId = resultSet.getInt(1);
            }


            attachAuthors(bookId, authors, connection);

            connection.commit();


        }

        return bookId;
    }

    private void attachAuthors(int bookId, Collection<String> authors, Connection connection) throws SQLException {
        String sp_attachAuthorToBook = "{call sp_attachAuthorToBook(?,?)}";
        CallableStatement callableStatement = null;
        callableStatement = connection.prepareCall(sp_attachAuthorToBook);

        for (String name : authors) {
            if (name.length() == 0)
                continue;

            callableStatement.setInt(1, bookId);
            callableStatement.setString(2, name);

            callableStatement.addBatch();
        }

        callableStatement.executeBatch();

    }


    public void removeBook(int bookId) throws SQLException {

        StringBuilder query = new StringBuilder();
        query.append("delete from Books_Authors where bookId = ?;");
        query.append("delete from Lib_log where bookId = ?;");
        query.append("delete from Books where id = ?;");

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement prst = connection.prepareStatement(query.toString());
            prst.setInt(1, bookId);
            prst.setInt(2, bookId);
            prst.setInt(3, bookId);
            prst.executeUpdate();


        }
    }

    public void takeBook(int bookId, String email) throws SQLException {


        StringBuilder query = new StringBuilder();
        query.append("insert into Lib_log (email, bookId) values (?, ?)");
        query.append("update Books set quantity = quantity-1 where id = ?");

        try(Connection connection = dataSource.getConnection())
        {
            PreparedStatement prst = connection.prepareStatement(query.toString());
            prst.setString(1, email);
            prst.setInt(2, bookId);
            prst.setInt(3, bookId);
            prst.executeUpdate();

        }
    }

    public void changeBookQuantity(int bookId, int quantity) throws SQLException {

        if (quantity < 0)
            throw new IllegalArgumentException(String.format("Quantity argument is %d. It must be greater or equal \"0\".", quantity));

        String query = "update Books set quantity = ? where id = ?";

        try(Connection connection = dataSource.getConnection())
        {
            PreparedStatement prst = connection.prepareStatement(query);
            prst.setInt(1, quantity);
            prst.setInt(2, bookId);
            prst.executeUpdate();

        }

    }

    public int getBookQuantity(int bookId) throws SQLException {

        String query = "select quantity from Books where id = ?";
        int qty = 0;

        try(Connection connection = dataSource.getConnection())
        {
            PreparedStatement prst = connection.prepareStatement(query);
            prst.setInt(1, bookId);
            prst.executeQuery();

            ResultSet resultSet = prst.getResultSet();
            if (resultSet.next()) {
                qty = resultSet.getInt(1);
            }

        }

        return qty;
    }

}