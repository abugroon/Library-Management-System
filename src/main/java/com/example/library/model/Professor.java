package com.example.library.model;

/**
 * Represents a professor library member.
 */
public class Professor extends User {
    private static final long serialVersionUID = 2L;
    private static final int MAX_BOOKS_ALLOWED = 5;

    public Professor(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getType() {
        return "Professor";
    }

    @Override
    public int getMaxBooksAllowed() {
        return MAX_BOOKS_ALLOWED;
    }
}
