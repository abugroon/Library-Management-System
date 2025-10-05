package com.example.library;

import com.example.library.model.Book;
import com.example.library.model.LoanRecord;
import com.example.library.model.User;
import com.example.library.persistence.LibraryStorage;
import com.example.library.service.Library;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Simple console interface for the library management system.
 */
public class LibraryApplication {
    private final Library library;
    private final LibraryStorage storage;
    private final Scanner scanner;

    public static void main(String[] args) {
        new LibraryApplication().run();
    }

    public LibraryApplication() {
        this.storage = LibraryStorage.defaultStorage();
        this.library = storage.loadOrCreate();
        this.scanner = new Scanner(System.in);
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        handleAddBook();
                        break;
                    case "2":
                        handleEditBook();
                        break;
                    case "3":
                        handleRemoveBook();
                        break;
                    case "4":
                        handleListBooks();
                        break;
                    case "5":
                        handleSearchBooks();
                        break;
                    case "6":
                        handleAddUser();
                        break;
                    case "7":
                        handleEditUser();
                        break;
                    case "8":
                        handleRemoveUser();
                        break;
                    case "9":
                        handleListUsers();
                        break;
                    case "10":
                        handleBorrowBook();
                        break;
                    case "11":
                        handleReturnBook();
                        break;
                    case "12":
                        handleListLoans();
                        break;
                    case "13":
                        persist();
                        System.out.println("Data saved. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown option. Please try again.");
                }
            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Library Management ===");
        System.out.println("1. Add book");
        System.out.println("2. Edit book");
        System.out.println("3. Delete book");
        System.out.println("4. List books");
        System.out.println("5. Search books");
        System.out.println("6. Add user");
        System.out.println("7. Edit user");
        System.out.println("8. Delete user");
        System.out.println("9. List users");
        System.out.println("10. Borrow book");
        System.out.println("11. Return book");
        System.out.println("12. View active loans");
        System.out.println("13. Save & exit");
        System.out.print("Choose an option: ");
    }

    private void handleAddBook() {
        System.out.println("-- Add Book --");
        String title = prompt("Title");
        String author = prompt("Author");
        String isbn = prompt("ISBN");
        int totalCopies = promptInt("Total copies", 1);
        String description = promptOptional("Description (optional)");
        Book book = library.addBook(title, author, isbn, totalCopies, description);
        persist();
        System.out.println("Book added with ID: " + book.getId());
    }

    private void handleEditBook() {
        System.out.println("-- Edit Book --");
        int bookId = promptInt("Book ID", 1);
        Optional<Book> existing = library.findBookById(bookId);
        if (existing.isEmpty()) {
            System.out.println("Book not found.");
            return;
        }
        Book book = existing.get();
        String title = promptDefault("Title", book.getTitle());
        String author = promptDefault("Author", book.getAuthor());
        String isbn = promptDefault("ISBN", book.getIsbn());
        int totalCopies = promptIntDefault("Total copies", book.getTotalCopies(), 1);
        String description = promptDefault("Description", book.getDescription());
        library.updateBook(bookId, title, author, isbn, totalCopies, description);
        persist();
        System.out.println("Book updated.");
    }

    private void handleRemoveBook() {
        System.out.println("-- Delete Book --");
        int bookId = promptInt("Book ID", 1);
        if (library.removeBook(bookId)) {
            persist();
            System.out.println("Book removed.");
        } else {
            System.out.println("Book not found.");
        }
    }

    private void handleListBooks() {
        System.out.println("-- All Books --");
        List<Book> books = library.listBooks();
        if (books.isEmpty()) {
            System.out.println("No books registered.");
            return;
        }
        books.forEach(book -> System.out.printf("ID: %d | %s%n", book.getId(), book));
    }

    private void handleSearchBooks() {
        System.out.println("-- Search Books --");
        String query = prompt("Keyword");
        List<Book> results = library.searchBooks(query);
        if (results.isEmpty()) {
            System.out.println("No books match your search.");
            return;
        }
        results.forEach(book -> System.out.printf("ID: %d | %s%n", book.getId(), book));
    }

    private void handleAddUser() {
        System.out.println("-- Add User --");
        String type = prompt("Type (student/professor)").toLowerCase();
        String name = prompt("Name");
        String email = prompt("Email");
        User user;
        switch (type) {
            case "student":
                user = library.addStudent(name, email);
                break;
            case "professor":
                user = library.addProfessor(name, email);
                break;
            default:
                System.out.println("Unsupported user type.");
                return;
        }
        persist();
        System.out.println("User added with ID: " + user.getId());
    }

    private void handleEditUser() {
        System.out.println("-- Edit User --");
        int userId = promptInt("User ID", 1);
        Optional<User> existing = library.findUser(userId);
        if (existing.isEmpty()) {
            System.out.println("User not found.");
            return;
        }
        User user = existing.get();
        String name = promptDefault("Name", user.getName());
        String email = promptDefault("Email", user.getEmail());
        library.updateUser(userId, name, email);
        persist();
        System.out.println("User updated.");
    }

    private void handleRemoveUser() {
        System.out.println("-- Delete User --");
        int userId = promptInt("User ID", 1);
        if (library.removeUser(userId)) {
            persist();
            System.out.println("User removed.");
        } else {
            System.out.println("User not found.");
        }
    }

    private void handleListUsers() {
        System.out.println("-- All Users --");
        List<User> users = library.listUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        users.forEach(user -> System.out.printf("ID: %d | %s%n", user.getId(), user));
    }

    private void handleBorrowBook() {
        System.out.println("-- Borrow Book --");
        int userId = promptInt("User ID", 1);
        String isbn = prompt("Book ISBN");
        if (library.borrowBookByIsbn(userId, isbn)) {
            persist();
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Unable to process borrowing. Check IDs.");
        }
    }

    private void handleReturnBook() {
        System.out.println("-- Return Book --");
        int userId = promptInt("User ID", 1);
        String isbn = prompt("Book ISBN");
        if (library.returnBookByIsbn(userId, isbn)) {
            persist();
            System.out.println("Book returned. Thank you!");
        } else {
            System.out.println("Unable to process return. Check IDs.");
        }
    }

    private void handleListLoans() {
        System.out.println("-- Active Loans --");
        List<LoanRecord> loans = library.getLoanRecords();
        if (loans.isEmpty()) {
            System.out.println("No active loans.");
            return;
        }
        loans.forEach(loan -> System.out.printf("User: %d | Book: %d | Loaned: %s | Due: %s%n",
                loan.getUserId(), loan.getBookId(), loan.getLoanDate(), loan.getDueDate()));
    }

    private String prompt(String label) {
        System.out.print(label + ": ");
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print("Value required. " + label + ": ");
            input = scanner.nextLine().trim();
        }
        return input;
    }

    private String promptDefault(String label, String current) {
        System.out.printf("%s (%s): ", label, current);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? current : input;
    }

    private String promptOptional(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private int promptInt(String label, int minInclusive) {
        while (true) {
            System.out.print(label + ": ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < minInclusive) {
                    System.out.println("Value must be at least " + minInclusive + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private int promptIntDefault(String label, int current, int minInclusive) {
        while (true) {
            System.out.printf("%s (%d): ", label, current);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return current;
            }
            try {
                int value = Integer.parseInt(input);
                if (value < minInclusive) {
                    System.out.println("Value must be at least " + minInclusive + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void persist() {
        storage.save(library);
    }
}
