package com.library.model;

import java.util.LinkedList;
import java.util.List;

public class Book {

    private int id;

    private String title;

    private List<String> authors;

    private int quantity;

    public Book()
    {
        authors = new LinkedList<>();
    }

    public Book(String title, List<String> authors, int quantity) {
        this.title = title;
        this.authors = authors;
        this.quantity = quantity;
    }

    public Book(int id, String title, List<String> authors, int quantity) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return id == book.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
