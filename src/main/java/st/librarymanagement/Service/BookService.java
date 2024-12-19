package st.librarymanagement.Service;




import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Entity.Book;
import st.librarymanagement.Entity.Status.BookStatus;
import st.librarymanagement.Repository.BookRepository;
import java.util.List;
import java.util.Optional;

public class BookService{
    private static final BookRepository bookRepository = new BookRepository();

    public BookService() {}

    public static BookDto save(Book book) {
        validateBook(book);
        if (book.getId() == 0) {book.setQuantityAvailable(book.getQuantity());}
        return bookRepository.save(book);
    }

    public static void delete(int id) {
        if (!bookRepository.existsById(id)) {throw new IllegalArgumentException("Book not found with id: " + id);}
        bookRepository.delete(id);
    }

    public static List<BookDto> getAllBooks() { return bookRepository.findAll(); }

    public Optional<BookDto> getBookById(int id) {
        return bookRepository.findById(id);
    }

    public Optional<BookDto> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public static List<BookDto> getBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<BookDto> getBooksByAuthor(int authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    public List<BookDto> getBooksByCategory(String categoryId) {
        return bookRepository.findByCategory(categoryId);
    }

    public List<BookDto> getBooksByPublisher(int publisherId) {
        return bookRepository.findByPublisher(publisherId);
    }

    public boolean isBookAvailable(int id) {
        return bookRepository.IsAvailable(id);
    }

    public static boolean checkIsbnExists(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    public void updateBookQuantity(int id, int quantity) {
        Book book = bookRepository.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
        int difference = quantity - book.getQuantity();
        book.setQuantity(quantity);
        book.setQuantityAvailable(book.getQuantityAvailable() + difference);
        // Update status based on availability
        if (book.getQuantityAvailable() > 0) {book.setStatus(BookStatus.Available);}
        else {book.setStatus(BookStatus.NotAvailable);}
        bookRepository.save(book);
    }
    private static void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {throw new IllegalArgumentException("Book title cannot be empty");}
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {throw new IllegalArgumentException("Book ISBN cannot be empty");}
        if (book.getAuthorId() <= 0) {throw new IllegalArgumentException("Invalid author ID");}
        if (book.getPublishYear() <= 0) {throw new IllegalArgumentException("Invalid publish year");}
        if (book.getQuantity() < 0) {throw new IllegalArgumentException("Quantity cannot be negative");}
        // Check ISBN uniqueness for new books
        if (book.getId() == 0 && checkIsbnExists(book.getIsbn())) { throw new IllegalArgumentException("ISBN already exists"); }
    }
}
