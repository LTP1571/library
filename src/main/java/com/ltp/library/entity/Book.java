package com.ltp.library.entity;

import jakarta.persistence.*;

@Entity
public class Book {
    @GeneratedValue
    @Id
    private Long id;
    private String title;
    @JoinColumn(name = "author_id")//bảng book sẽ có một cột tên author_id
    @ManyToOne //nhiều Book trỏ về một Author
    private Author author;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
