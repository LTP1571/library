package com.ltp.library;

import dto.BookResponse;
import dto.CreateBookRequest;
import dto.UpdateBookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository repository;
    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private BookService service;

    // ===== findAll =====

    @Test
    public void findAll_shouldReturnListOfBookResponse() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Wasinton");
        Book book1 = new Book();
        book1.setId(10L);
        book1.setTitle("Killing a bird");
        book1.setAuthor(author);
        Book book2 = new Book();
        book2.setId(11L);
        book2.setTitle("Kill");
        book2.setAuthor(author);

        when(repository.findAll()).thenReturn(List.of(book1, book2));

        List<BookResponse> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals("Killing a bird", result.get(0).getTitle());
        assertEquals(11L, result.get(1).getId());
        assertEquals("Wasinton", result.get(1).getAuthor().getName());
    }

    // ===== findById =====

    @Test
    public void findById_whenIdExists_shouldReturnBookResponse() {
        Author author = new Author();
        author.setId(1L);
        author.setName("wasinton");
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Killing a bird");
        book.setAuthor(author);

        when(repository.findById(2L)).thenReturn(Optional.of(book));

        BookResponse result = service.findById(2L);

        assertEquals(2L, result.getId());
        assertEquals("wasinton", result.getAuthor().getName());
        assertEquals("Killing a bird", result.getTitle());
    }

    @Test
    public void findById_whenIdNotExists_shouldThrowBookNotFoundException() {
        when(repository.findById(80L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> service.findById(80L));
    }

    // ===== create =====

    @Test
    public void create_whenHasAuthor_shouldReturnBookResponse() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Killing a bird");
        request.setAuthorId(1L);
        Author author = new Author();
        author.setName("Wasinton");
        author.setId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        BookResponse response = service.create(request);

        assertEquals(1L, response.getAuthor().getId());
        assertEquals("Wasinton", response.getAuthor().getName());
        assertEquals("Killing a bird", response.getTitle());
        verify(repository).save(any(Book.class));
    }

    @Test
    public void create_whenHasNoAuthor_shouldThrowAuthorNotFoundException() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Killing a bird");
        request.setAuthorId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    // ===== update =====

    @Test
    public void update_whenBookAndAuthorExist_shouldReturnBookResponse() {
        Author author = new Author();
        author.setId(2L);
        author.setName("Wasinton");
        Author newAuthor = new Author();
        newAuthor.setId(5L);
        newAuthor.setName("Plato");
        Book exist = new Book();
        exist.setId(1L);
        exist.setTitle("Killing a bird");
        exist.setAuthor(author);
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Kill");
        request.setAuthorId(5L);

        when(repository.findById(1L)).thenReturn(Optional.of(exist));
        when(authorRepository.findById(5L)).thenReturn(Optional.of(newAuthor));

        BookResponse response = service.update(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("Kill", response.getTitle());
        assertEquals(5L, response.getAuthor().getId());
        verify(repository).save(exist);
    }

    @Test
    public void update_whenBookNotExists_shouldThrowBookNotFoundException() {
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Kill");
        request.setAuthorId(5L);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> service.update(1L, request));
        verify(authorRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    public void update_whenAuthorNotExists_shouldThrowAuthorNotFoundException() {
        Book exist = new Book();
        exist.setId(1L);
        exist.setTitle("Killing a bird");
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Kill");
        request.setAuthorId(99L);

        when(repository.findById(1L)).thenReturn(Optional.of(exist));
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> service.update(1L, request));
        verify(repository, never()).save(any());
    }

    // ===== delete =====

    @Test
    public void delete_whenBookExists_shouldCallDeleteById() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    public void delete_whenBookNotExists_shouldThrowBookNotFoundException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(any());
    }
}