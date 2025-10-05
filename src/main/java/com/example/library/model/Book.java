package com.example.library.model;

import java.io.Serializable;

/**
 * Represents a book in the library catalogue.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 3L;

    private final int id;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
    private String description;

    public Book(int id, String title, String author, String isbn, int totalCopies, String description) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.id = id;
        this.title = requireNonBlank(title, "title");
        this.author = requireNonBlank(author, "author");
        this.isbn = requireNonBlank(isbn, "isbn");
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.description = sanitizeDescription(description);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = requireNonBlank(title, "title");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = requireNonBlank(author, "author");
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = requireNonBlank(isbn, "isbn");
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = sanitizeDescription(description);
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
        String base = String.format("%s by %s (ISBN: %s) - Available: %d/%d", title, author, isbn, availableCopies, totalCopies);
        if (description.isBlank()) {
            return base;
        }
        return base + " | " + description;
    }

    private String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return value.trim();
    }

    private String sanitizeDescription(String value) {
        return value == null ? "" : value.trim();
    }
}
