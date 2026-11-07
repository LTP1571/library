package com.ltp.library.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Author {
    @GeneratedValue
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "author")
    private List<Book> books = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {

        return books;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }


}
