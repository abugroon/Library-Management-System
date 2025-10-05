package com.example.library.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for library members.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int id;
    private String name;
    private String email;
    private final List<Integer> borrowedBookIds = new ArrayList<>();

    protected User(int id, String name, String email) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.email = Objects.requireNonNull(email, "email");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "email");
    }

    public List<Integer> getBorrowedBookIds() {
        return Collections.unmodifiableList(borrowedBookIds);
    }

    public int getBorrowedCount() {
        return borrowedBookIds.size();
    }

    public void borrowBook(int bookId) {
        if (borrowedBookIds.size() >= getMaxBooksAllowed()) {
            throw new IllegalStateException("Borrow limit reached");
        }
        borrowedBookIds.add(bookId);
    }

    public void returnBook(int bookId) {
        if (!borrowedBookIds.remove(Integer.valueOf(bookId))) {
            throw new IllegalArgumentException("Book not recorded as borrowed by user");
        }
    }

    public abstract String getType();

    public abstract int getMaxBooksAllowed();

    @Override
    public String toString() {
        return String.format("%s: %s (Email: %s) Borrowed: %d/%d", getType(), name, email, borrowedBookIds.size(), getMaxBooksAllowed());
    }
}
