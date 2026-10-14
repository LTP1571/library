package com.ltp.library.controller;

import com.ltp.library.dto.BookResponse;
import com.ltp.library.dto.CreateBookRequest;
import com.ltp.library.dto.UpdateBookRequest;
import com.ltp.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }
    @GetMapping
    public List<BookResponse> findAll() {
        return service.findAll();
    }
    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody CreateBookRequest request) {
        BookResponse response= service.create(request);
        URI location=URI.create("/api/books/"+response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }


    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
