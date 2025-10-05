package com.example.library.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a book in the library catalogue.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int id;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, String author, String isbn, int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.id = id;
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = Objects.requireNonNull(author, "author");
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = Objects.requireNonNull(isbn, "isbn");
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        int borrowedCopies = getBorrowedCopies();
        if (totalCopies < borrowedCopies) {
            throw new IllegalArgumentException("Total copies cannot be less than borrowed copies");
        }
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies - borrowedCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies");
        }
        this.availableCopies = availableCopies;
    }

    public int getBorrowedCopies() {
        return totalCopies - availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrowCopy() {
        if (!isAvailable()) {
            throw new IllegalStateException("No copies available to borrow");
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new IllegalStateException("All copies are already accounted for");
        }
        availableCopies++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%s by %s (ISBN: %s) - Available: %d/%d", title, author, isbn, availableCopies, totalCopies);
    }
}
