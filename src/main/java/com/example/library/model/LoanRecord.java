package com.example.library.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Tracks a borrowing transaction.
 */
public class LoanRecord implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int bookId;
    private final int userId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;

    public LoanRecord(int bookId, int userId, LocalDate loanDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public int getBookId() {
        return bookId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return String.format("Loan[user=%d, book=%d, loanDate=%s, dueDate=%s]", userId, bookId, loanDate, dueDate);
    }
}
