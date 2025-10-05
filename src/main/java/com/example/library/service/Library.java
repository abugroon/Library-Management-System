package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.LoanRecord;
import com.example.library.model.Professor;
import com.example.library.model.Student;
import com.example.library.model.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Core library domain logic.
 */
public class Library implements Serializable {
    private static final long serialVersionUID = 2L;

    private final Map<Integer, Book> booksById = new LinkedHashMap<>();
    private final Map<String, Book> booksByIsbn = new LinkedHashMap<>();
    private final Map<Integer, User> usersById = new LinkedHashMap<>();
    private final List<LoanRecord> loanRecords = new ArrayList<>();
    private int nextBookId = 1;
    private int nextUserId = 1;

    public Book addBook(String title, String author, String isbn, int totalCopies) {
        String normalizedIsbn = normalizeIsbn(isbn);
        if (booksByIsbn.containsKey(normalizedIsbn)) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }
        Book book = new Book(nextBookId++, title, author, normalizedIsbn, totalCopies);
        booksById.put(book.getId(), book);
        booksByIsbn.put(normalizedIsbn, book);
        return book;
    }

    public Optional<Book> updateBook(int bookId, String title, String author, String isbn, int totalCopies) {
        Book book = booksById.get(bookId);
        if (book == null) {
            return Optional.empty();
        }
        String normalizedIsbn = normalizeIsbn(isbn);
        Book existing = booksByIsbn.get(normalizedIsbn);
        if (existing != null && existing.getId() != bookId) {
            throw new IllegalArgumentException("Another book already uses this ISBN");
        }
        booksByIsbn.remove(book.getIsbn());
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(normalizedIsbn);
        book.setTotalCopies(totalCopies);
        booksByIsbn.put(normalizedIsbn, book);
        return Optional.of(book);
    }

    public boolean removeBook(int bookId) {
        Book book = booksById.get(bookId);
        if (book == null) {
            return false;
        }
        if (book.getBorrowedCopies() > 0) {
            throw new IllegalStateException("Cannot remove a book that is currently borrowed");
        }
        booksById.remove(bookId);
        booksByIsbn.remove(book.getIsbn());
        return true;
    }

    public List<Book> listBooks() {
        return booksById.values().stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());
    }

    public Optional<Book> findBookById(int bookId) {
        return Optional.ofNullable(booksById.get(bookId));
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(booksByIsbn.get(normalizeIsbn(isbn)));
    }

    public List<Book> searchBooks(String query) {
        String lower = query.toLowerCase();
        return booksById.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lower)
                        || book.getAuthor().toLowerCase().contains(lower)
                        || book.getIsbn().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());
    }

    public User addStudent(String name, String email) {
        Student student = new Student(nextUserId++, name, email);
        usersById.put(student.getId(), student);
        return student;
    }

    public User addProfessor(String name, String email) {
        Professor professor = new Professor(nextUserId++, name, email);
        usersById.put(professor.getId(), professor);
        return professor;
    }

    public Optional<User> updateUser(int userId, String name, String email) {
        User user = usersById.get(userId);
        if (user == null) {
            return Optional.empty();
        }
        user.setName(name);
        user.setEmail(email);
        return Optional.of(user);
    }

    public boolean removeUser(int userId) {
        User user = usersById.get(userId);
        if (user == null) {
            return false;
        }
        if (user.getBorrowedCount() > 0) {
            throw new IllegalStateException("User must return all books before removal");
        }
        usersById.remove(userId);
        return true;
    }

    public List<User> listUsers() {
        return usersById.values().stream()
                .sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList());
    }

    public Optional<User> findUser(int userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    public boolean borrowBookByIsbn(int userId, String isbn) {
        User user = usersById.get(userId);
        Book book = booksByIsbn.get(normalizeIsbn(isbn));
        if (user == null || book == null) {
            return false;
        }
        borrowBook(user, book);
        return true;
    }

    public boolean borrowBookById(int userId, int bookId) {
        User user = usersById.get(userId);
        Book book = booksById.get(bookId);
        if (user == null || book == null) {
            return false;
        }
        borrowBook(user, book);
        return true;
    }

    private void borrowBook(User user, Book book) {
        if (!book.isAvailable()) {
            throw new IllegalStateException("No copies available");
        }
        user.borrowBook(book.getId());
        book.borrowCopy();
        loanRecords.add(new LoanRecord(book.getId(), user.getId(), LocalDate.now(), calculateDueDate(user)));
    }

    public boolean returnBookByIsbn(int userId, String isbn) {
        Book book = booksByIsbn.get(normalizeIsbn(isbn));
        return returnBookById(userId, book == null ? -1 : book.getId());
    }

    public boolean returnBookById(int userId, int bookId) {
        if (bookId < 0) {
            return false;
        }
        User user = usersById.get(userId);
        Book book = booksById.get(bookId);
        if (user == null || book == null) {
            return false;
        }
        user.returnBook(book.getId());
        book.returnCopy();
        loanRecords.removeIf(record -> record.getBookId() == bookId && record.getUserId() == userId);
        return true;
    }

    public List<LoanRecord> getLoanRecords() {
        return Collections.unmodifiableList(loanRecords);
    }

    public void syncCounters() {
        nextBookId = booksById.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        nextUserId = usersById.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    private LocalDate calculateDueDate(User user) {
        int loanDuration = user instanceof Professor ? 28 : 14;
        return LocalDate.now().plusDays(loanDuration);
    }

    private String normalizeIsbn(String isbn) {
        return Objects.requireNonNull(isbn, "isbn").replaceAll("[^0-9Xx]", "").toUpperCase();
    }
}
