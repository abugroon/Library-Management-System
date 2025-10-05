# Java Library Management System

A console-based library management system built with core Java and OOP principles. It supports book and member management, borrowing/return tracking, and simple data persistence using Java object serialization.

## Features
- Manage books: add, edit, delete, list, and search by keyword.
- Capture optional descriptions for each book and include them in search results.
- Manage members: add, edit, delete, and list students or professors.
- Borrowing workflow: enforce copy availability and per-role loan limits (students up to 3, professors up to 5).
- Track active loans with automatic due dates (14 days for students, 28 days for professors).
- Persist all data to `data/library.dat` so the catalogue and members survive restarts.
- Auto-incrementing numeric IDs for books and users to keep references simple.

## Project Structure
```
src/main/java/com/example/library/
+-- LibraryApplication.java        # Console UI entry point
+-- model/                         # Domain entities (Book, User, etc.)
+-- persistence/LibraryStorage.java# File persistence helper
+-- service/Library.java           # Core business logic
```

## Build & Run
```
# Compile
javac -d out @sources.txt

# Run
java -cp out com.example.library.LibraryApplication
```

From the menu you can perform all library operations. Choose `13` to save and exit.

## Data Files
- `data/library.dat`: binary serialized snapshot of the entire library state.

If you previously ran a version that used an older serialization format, the program will automatically reset the store and start fresh the next time you run it.
