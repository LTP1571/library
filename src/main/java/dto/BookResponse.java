package dto;



public class BookResponse {
    private Long id;
    private String title;
    private AuthorResponse author;

    public BookResponse(Long id, String title, AuthorResponse author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public AuthorResponse getAuthor() {
        return author;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(AuthorResponse author) {
        this.author = author;
    }
}
