package com.ltp.library.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAuthorRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
