package com.ltp.library.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateBookRequest {
    @NotBlank
    @Size(max = 255)
    private String title;
    @NotNull
    private Long authorId;

    public String getTitle() {
        return title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
