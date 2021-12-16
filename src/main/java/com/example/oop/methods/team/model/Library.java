package com.example.oop.methods.team.model;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private Long id;
    private User owner;
    private List<Author> authors;
    private List<Book> books;

    public Library() {
        this.authors = new ArrayList<>();
        this.books = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}