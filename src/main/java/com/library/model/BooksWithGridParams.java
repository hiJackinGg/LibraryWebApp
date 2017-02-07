package com.library.model;


import java.util.LinkedList;
import java.util.List;

/**
 * Object to be converted to JSON and return to the client for ajax table
 */
public class BooksWithGridParams {

    private List<Book> books;
    //count of rows to be displayed without paging
    private int totalRows;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private boolean isAuthorized;

    public BooksWithGridParams()
    {
        books = new LinkedList<>();
    }

    public int getTotalPages() {
        return  (int) Math.ceil((double)totalRows / pageSize);
    }

//    public void setTotalPages(int totalPages) {
//        this.totalPages = totalPages;
//    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;

        if (pageSize != 0)
            totalPages = (int) Math.ceil((double)totalRows / pageSize);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;

        if (totalRows != 0)
            totalPages = (int) Math.ceil((double)totalRows / pageSize);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    @Override
    public String toString() {
        return "BooksWithGridParams{" +
                "books=" + books +
                ", totalRows=" + totalRows +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", isAuthorized=" + isAuthorized +
                '}';
    }
}
