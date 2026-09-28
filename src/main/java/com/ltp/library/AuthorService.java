package com.ltp.library;

import dto.AuthorResponse;
import dto.CreateAuthorRequest;
import dto.UpdateAuthorRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }


    public List<AuthorResponse> findAll(){
        List<Author> authors=repository.findAll();
        List<AuthorResponse> result=new ArrayList<>();
        for (int i=0;i<authors.size();i++){
            Author t=authors.get(i);
            result.add(toResponse(t));
        }
        return result;
    }

    public AuthorResponse findById(Long id){
        Author exist=repository.findById(id).orElseThrow(()->new AuthorNotFoundException("Author not found with id: " + id));
        return toResponse(exist);
    }

    public AuthorResponse create(CreateAuthorRequest AuthorRequest){
        Author author = new Author();
        author.setName(AuthorRequest.getName());
        Author saved=repository.save(author);
        return toResponse(saved);
    }

    public AuthorResponse update(Long id, UpdateAuthorRequest AuthorRequest){
        Author exist=repository.findById(id).orElseThrow(()->new AuthorNotFoundException("Author not found with id: " + id));
        exist.setName(AuthorRequest.getName());
        Author saved=repository.save(exist);
        return toResponse(saved);
    }

    public void delete(Long id) {

        Author exist=repository.findById(id).orElseThrow(()->new AuthorNotFoundException("Author not found with id: " + id));

        if (exist.getBooks().isEmpty()) {
            repository.deleteById(id);
        } else {
            throw new AuthorHasBooksException("Author with the id "+id+" still has books");
        }

    }

    private AuthorResponse toResponse (Author author){
        return new AuthorResponse(author.getId(),author.getName());
    }
}


