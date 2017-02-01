package ui;

import java.util.*;

/**
 * Class that represents data stored in table row of the application.
 * Table row describes information about book and has several option button to handle rows.
 */
public class BookRow {

    private String title;

    private Collection<String> authors = new ArrayList<>();

    private int quantity;

    enum ActionWithRow{

        REMOVE("button-remove"),

        BOOK_DETAILS("button-details"),

        TAKE_BOOK("button-take"),

        CHANGE_QUANTITY("button-edit"),

        SAVE_CHANGES("button-saveChanges");

        private final String value;

        private ActionWithRow(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

    }

    public BookRow(){
    }

    public BookRow(String title, List<String> authors, int quantity) {
        this.title = title;
        this.authors = authors;
        this.quantity = quantity;
    }

    public void reduceQuantityByOne(){
        if (this.quantity <= 0)
            throw new RuntimeException("Quantity is less or equal to '0'");

        this.quantity = this.quantity - 1;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<String> authors) {
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
        return "ui.BookRow{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookRow row = (BookRow) o;

        if (quantity != row.quantity) return false;
        if (title != null ? !title.equals(row.title) : row.title != null) return false;
        return authors != null ? authors.equals(row.authors) : row.authors == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        result = 31 * result + quantity;
        return result;
    }
}
