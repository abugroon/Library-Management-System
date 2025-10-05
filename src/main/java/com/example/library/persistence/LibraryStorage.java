package com.example.library.persistence;

import com.example.library.service.Library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles saving and loading library data from disk.
 */
public class LibraryStorage {
    private final Path storagePath;

    public LibraryStorage(Path storagePath) {
        this.storagePath = storagePath;
    }

    public static LibraryStorage defaultStorage() {
        return new LibraryStorage(Paths.get("data", "library.dat"));
    }

    public Library loadOrCreate() {
        if (!Files.exists(storagePath)) {
            return new Library();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(storagePath))) {
            Object obj = ois.readObject();
            if (obj instanceof Library) {
                return (Library) obj;
            }
            throw new IOException("Unexpected data format inside " + storagePath);
        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to load library data", ex);
        }
    }

    public void save(Library library) {
        try {
            Files.createDirectories(storagePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(storagePath))) {
                oos.writeObject(library);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to save library data", ex);
        }
    }
}
