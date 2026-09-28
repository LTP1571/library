package com.ltp.library;

import dto.AuthorResponse;
import dto.CreateAuthorRequest;
import dto.UpdateAuthorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {
    @Mock
    private AuthorRepository repository;
    @InjectMocks
    private AuthorService service;

    // ===== findAll =====

    @Test
    public void findAll_shouldReturnListOfAuthorResponse() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("Wasinton");
        Author author2 = new Author();
        author2.setId(2L);
        author2.setName("Plato");

        when(repository.findAll()).thenReturn(List.of(author1, author2));

        List<AuthorResponse> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Wasinton", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Plato", result.get(1).getName());
    }

    // ===== findById =====

    @Test
    public void findById_whenIdExists_shouldReturnAuthorResponse() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Wasinton");

        when(repository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponse result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Wasinton", result.getName());
    }

    @Test
    public void findById_whenIdNotExists_shouldThrowAuthorNotFoundException() {
        when(repository.findById(80L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> service.findById(80L));
    }

    // ===== create =====

    @Test
    public void create_shouldReturnAuthorResponse() {
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setName("Wasinton");
        Author saved = new Author();
        saved.setId(1L);
        saved.setName("Wasinton");

        // AuthorService.create dùng giá trị save() trả về nên BẮT BUỘC stub save
        when(repository.save(any(Author.class))).thenReturn(saved);

        AuthorResponse result = service.create(request);

        assertEquals(1L, result.getId());
        assertEquals("Wasinton", result.getName());
        verify(repository).save(any(Author.class));
    }

    // ===== update =====

    @Test
    public void update_whenIdExists_shouldReturnAuthorResponse() {
        Author exist = new Author();
        exist.setId(1L);
        exist.setName("Wasinton");
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("Plato");

        when(repository.findById(1L)).thenReturn(Optional.of(exist));
        when(repository.save(exist)).thenReturn(exist);

        AuthorResponse result = service.update(1L, request);

        assertEquals(1L, result.getId());
        assertEquals("Plato", result.getName());
        verify(repository).save(exist);
    }

    @Test
    public void update_whenIdNotExists_shouldThrowAuthorNotFoundException() {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setName("Plato");

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> service.update(1L, request));
        verify(repository, never()).save(any());
    }

    // ===== delete =====

    @Test
    public void delete_whenAuthorHasNoBooks_shouldCallDeleteById() {
        Author exist = new Author();
        exist.setId(1L);
        exist.setName("Wasinton");
        exist.setBooks(new ArrayList<>()); // để null thì getBooks().isEmpty() ném NullPointerException

        when(repository.findById(1L)).thenReturn(Optional.of(exist));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    public void delete_whenAuthorHasBooks_shouldThrowAuthorHasBooksException() {
        Author exist = new Author();
        exist.setId(1L);
        exist.setName("Wasinton");
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Killing a bird");
        book.setAuthor(exist);
        exist.setBooks(List.of(book));

        when(repository.findById(1L)).thenReturn(Optional.of(exist));

        assertThrows(AuthorHasBooksException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    public void delete_whenIdNotExists_shouldThrowAuthorNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(any());
    }
}