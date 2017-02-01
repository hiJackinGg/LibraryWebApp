package com.library.dao.jdbc;



import com.library.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;


public interface IBooksCreator {

    /**
     * Build books from ResultSet.
     * @param rs of books
     * @return pair of books collection and its total number (in database table)
     * @throws SQLException
     */
    public SimpleEntry<Integer, List<Book>> buildBooks(ResultSet rs) throws SQLException;

}
