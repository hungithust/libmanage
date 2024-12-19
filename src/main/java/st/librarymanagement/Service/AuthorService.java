package st.librarymanagement.Service;

import com.google.protobuf.ServiceException;
import javafx.collections.ObservableList;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Entity.Author;
import st.librarymanagement.Repository.AuthorRepository;
import st.librarymanagement.Repository.BookRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AuthorService {
    private static final AuthorRepository repository = new AuthorRepository();
    private static final BookRepository bookRepository = new BookRepository();

    public AuthorService() {}

    public static Author save(Author author) throws ServiceException {
        return repository.save(author);
    }

    public static void delete(int id) throws ServiceException {
        repository.delete(id);
    }

    public static List<Author> getAll() throws ServiceException {
        return repository.findAll();
    }

    public Optional<Author> getById(int id) throws ServiceException {
        return repository.findById(id);
    }



    public static ObservableList<Author> getByName(String name) throws ServiceException, SQLException {
        return repository.findByName(name);
    }

    public Optional<Author> getByEmail(String email) throws ServiceException, SQLException {
        return repository.findByEmail(email);
    }

    public List<BookDto> getBooksByAuthor(int authorId) throws SQLException {
        return bookRepository.findByAuthor(authorId);
    }

    private void validateAuthor(Author author) {
        if (author.getName() == null || author.getName().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }
        if (author.getEmail() == null || author.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Author email cannot be null or empty");
        }
    }
}

