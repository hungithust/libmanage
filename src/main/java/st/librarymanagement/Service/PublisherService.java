package st.librarymanagement.Service;

import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Entity.Publisher;
import st.librarymanagement.Repository.BookRepository;
import st.librarymanagement.Repository.PublisherRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PublisherService {
    private final static PublisherRepository publisherRepository = new PublisherRepository();
    private final static BookRepository bookRepository = new BookRepository();

    public PublisherService() {}

    public static Publisher save(Publisher publisher) throws SQLException {
        validatePublisher(publisher);
        return publisherRepository.save(publisher);
    }

    public static void delete(int id) {
        if (publisherRepository.existsById(id)) {
            throw new IllegalArgumentException("Publisher not found with id: " + id);
        }
        publisherRepository.delete(id);
    }

    public static List<Publisher> getAll() throws SQLException {
        return publisherRepository.findAll();
    }

    public Optional<Publisher> getById(int id) throws SQLException {
        return publisherRepository.findById(id);
    }

    public Optional<Publisher> getByName(String name) throws SQLException {
        return publisherRepository.findByName(name);
    }

    public Optional<Publisher> getByEmail(String email) {
        return publisherRepository.findByEmail(email);
    }

    public List<BookDto> getBooksByPublisher(int publisherId) throws SQLException {
        if (publisherRepository.existsById(publisherId)) {
            throw new IllegalArgumentException("Publisher not found with id: " + publisherId);
        }
        return bookRepository.findByPublisher(publisherId);
    }

    private static void validatePublisher(Publisher publisher) {
        if (publisher.getName() == null || publisher.getName().isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be null or empty");
        }
        if (publisher.getEmail() == null || publisher.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Publisher email cannot be null or empty");
        }
        if (publisher.getAddress() == null || publisher.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Publisher address cannot be null or empty");
        }
        if (publisher.getContact() == null || publisher.getContact().isEmpty()) {
            throw new IllegalArgumentException("Publisher contact cannot be null or empty");
        }
    }
}