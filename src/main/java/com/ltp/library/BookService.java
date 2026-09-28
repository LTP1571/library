package com.ltp.library;

import dto.AuthorResponse;
import dto.BookResponse;
import dto.CreateBookRequest;
import dto.UpdateBookRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository repository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository repository, AuthorRepository authorRepository) {

        this.repository = repository;
        this.authorRepository = authorRepository;
    }


    public List<BookResponse> findAll() {
        List<Book> books = repository.findAll();
        List<BookResponse> result = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Book t = books.get(i);
            result.add(toResponse(t));
        }
        return result;
    }

    public BookResponse findById(Long id) {
        Book exist = repository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return toResponse(exist);
    }

    public BookResponse create(CreateBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        Author exist = authorRepository.findById(request.getAuthorId()).orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + request.getAuthorId()));
        book.setAuthor(exist);
        repository.save(book);
        return toResponse(book);
    }

    public BookResponse update(Long id, UpdateBookRequest request){
        Book exist1 = repository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        exist1.setTitle(request.getTitle());
        Author exist = authorRepository.findById(request.getAuthorId()).orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + request.getAuthorId()));
        exist1.setAuthor(exist);
        repository.save(exist1);
        return toResponse(exist1);

    }

    public void delete(Long id){
        boolean result=repository.existsById(id);

        if (result){
            repository.deleteById(id);
        }
        else {
            throw new BookNotFoundException("Book not found with id: " + id);
        }

    }


    private AuthorResponse toAuthorResponse(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }


    private BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), toAuthorResponse(book.getAuthor()));
    }


}
