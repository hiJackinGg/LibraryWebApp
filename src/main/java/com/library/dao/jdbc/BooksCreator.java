package com.library.dao.jdbc;



import com.library.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Class to build Books entities from ResultSet.
 * Contains nested classes which implement IBooksCreator interface to use different algorithms for Books building.
 */
public class BooksCreator {

    /**
     * Build list of books with its authors (group by each book) and total number.
     */
    public static class BookListCreator implements IBooksCreator {

        @Override
        public SimpleEntry<Integer, List<Book>> buildBooks(ResultSet rs) throws SQLException {
            Map<Integer, Book> books = new LinkedHashMap<>();
            int totalRows = 0;

            Book book = null;

            while (rs.next()) {

                Integer id = rs.getInt(1);
                book = books.get(id);

                if (book == null){
                    book = new Book();
                    book.setId(id);
                    book.setTitle(rs.getString(2));
                    book.setQuantity(rs.getInt(3));
                    books.put(id, book);
                }

                String authorName = rs.getString(4);
                if (rs.wasNull())
                    authorName = "";
                book.getAuthors().add(authorName);

                totalRows = rs.getInt(5);
            }


            return new SimpleEntry<>(totalRows, new LinkedList<>(books.values()));
        }
    }

    /**
     * Build list of books (corresponds one book to only one author in a row) and total number.
     */
    public static class BookPerAuthorListCreator implements IBooksCreator{

        @Override
        public SimpleEntry<Integer, List<Book>> buildBooks(ResultSet rs) throws SQLException {
            List<Book> books = new LinkedList<>();
            int totalRows = 0;

            Book book;

            while (rs.next())
            {
                book = new Book();
                book.setId(rs.getInt(1));
                book.setTitle(rs.getString(2));
                book.setQuantity(rs.getInt(3));

                String authorName = rs.getString(4);
                if (rs.wasNull())
                    authorName = "";
                book.getAuthors().add(authorName);

                totalRows = rs.getInt(5);

                books.add(book);
            }

            return new SimpleEntry<>(totalRows, books);
        }
    }

}
