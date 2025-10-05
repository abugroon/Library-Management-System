package com.example.library.model;

/**
 * Represents a student library member.
 */
public class Student extends User {
    private static final long serialVersionUID = 2L;
    private static final int MAX_BOOKS_ALLOWED = 3;

    public Student(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getType() {
        return "Student";
    }

    @Override
    public int getMaxBooksAllowed() {
        return MAX_BOOKS_ALLOWED;
    }
}
