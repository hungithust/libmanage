package st.librarymanagement.Repository;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Entity.Book;
import st.librarymanagement.Entity.Status.BookCategory;
import st.librarymanagement.Entity.Status.BookStatus;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class BookRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookRepository.class);
    private final DatabaseConnection connection = new DatabaseConnection();

    private BookDto mapBookToBookResponse(Book book) {
        // Assuming you have methods to get authorName and publisherName by their IDs
        String authorName = getAuthorNameById(book.getAuthorId());
        String publisherName = getPublisherNameById(book.getPublisherId());

        return new BookDto(
            book.getId(),
            book.getTitle(),
            book.getAuthorId(),
            book.getPublisherId(),
            book.getCategory(),
            book.getIsbn(),
            book.getPublishYear(),
            book.getQuantity(),
            book.getQuantityAvailable(),
            book.getStatus(),
            authorName,
            publisherName
        );
    }

    public BookDto save(Book book) {
        String sqlInsert = "INSERT INTO Books (title, isbn, authorId, publisherId, category, " +
                           "publishYear, quantity, quantityAvailable, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE Books SET title = ?, isbn = ?, authorId = ?, publisherId = ?, " +
                           "category = ?, publishYear = ?, quantity = ?, quantityAvailable = ?, " +
                           "status = ? WHERE id = ?";
        try {
            if (book.getId() == 0) {
                logger.info("Inserting new book: {}", book);
                try (PreparedStatement stmt = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, book.getTitle());
                    stmt.setString(2, book.getIsbn());
                    stmt.setInt(3, book.getAuthorId());
                    stmt.setInt(4, book.getPublisherId());
                    stmt.setString(5, book.getCategory().name());
                    stmt.setInt(6, book.getPublishYear());
                    stmt.setInt(7, book.getQuantity());
                    stmt.setInt(8, book.getQuantityAvailable());
                    stmt.setString(9, book.getStatus().name());
                    stmt.executeUpdate();
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            book.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            } else {
                logger.info("Updating book with id: {}", book.getId());
                try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, book.getTitle());
                    stmt.setString(2, book.getIsbn());
                    stmt.setInt(3, book.getAuthorId());
                    stmt.setInt(4, book.getPublisherId());
                    stmt.setString(5, book.getCategory().name());
                    stmt.setInt(6, book.getPublishYear());
                    stmt.setInt(7, book.getQuantity());
                    stmt.setInt(8, book.getQuantityAvailable());
                    stmt.setString(9, book.getStatus().name());
                    stmt.setInt(10, book.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving book: {}", book, e);
            throw new RuntimeException("Error saving book", e);
        }
        return mapBookToBookResponse(book);
    }

    public void delete(int id) {
        String sql = "DELETE FROM Books WHERE id = ?";
        logger.info("Deleting book with id: {}", id);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting book with id: {}", id, e);
            throw new RuntimeException("Error deleting book", e);
        }
    }

    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM Books WHERE id = ?";
        logger.info("Checking existence of book with id: {}", id);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of book with id: {}", id, e);
            throw new RuntimeException("Error checking book existence by id", e);
        }
    }

    public boolean existsByIsbn(String isbn) {
        String sql = "SELECT COUNT(*) FROM Books WHERE isbn = ?";
        logger.info("Checking existence of book with ISBN: {}", isbn);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of book with ISBN: {}", isbn, e);
            throw new RuntimeException("Error checking book existence by ISBN", e);
        }
    }

    public ObservableList<BookDto> findAll() {
        String sql = "SELECT * FROM Books; ";
        logger.info("Fetching all books");
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all books", e);
            throw new RuntimeException("Error fetching all books", e);
        }
        return books.stream().map(this::mapBookToBookResponse).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<BookDto> findByTitle(String title) {
        String sql = "SELECT * FROM Books WHERE LOWER(title) LIKE LOWER(?)";
        logger.info("Fetching books with title containing: {}", title);
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + title.replace("%", "\\%").replace("_", "\\_") + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching books by title: {}", title, e);
            throw new RuntimeException("Error fetching books by title", e);
        }
        return books.stream().map(this::mapBookToBookResponse).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public Optional<BookDto> findById(int id) {
        String sql = "SELECT * FROM Books WHERE id = ?";
        logger.info("Fetching book with id: {}", id);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBookToBookResponse(mapResultSetToBook(rs)));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching book by id: {}", id, e);
            throw new RuntimeException("Error fetching book by id", e);
        }
        return Optional.empty();
    }

    public Optional<Book> getBookById(int id) {
        String sql = "SELECT * FROM Books WHERE id = ?";
        logger.info("Fetching book with id: {}", id);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching book by id: {}", id, e);
            throw new RuntimeException("Error fetching book by id", e);
        }
        return Optional.empty();
    }

    public Optional<BookDto> findByIsbn(String isbn) {
        String sql = "SELECT * FROM Books WHERE isbn = ?";
        logger.info("Fetching book with ISBN: {}", isbn);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBookToBookResponse(mapResultSetToBook(rs)));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching book by ISBN: {}", isbn, e);
            throw new RuntimeException("Error fetching book by ISBN", e);
        }
        return Optional.empty();
    }

    public List<BookDto> findByAuthor(int authorId){
        String sql = "SELECT * FROM Books WHERE authorId =?";
        logger.info("Fetching books by author with id: {}", authorId);
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching Books by author: {}", authorId, e);
            throw new RuntimeException("Error fetching books by author", e);
        }
        return books.stream().map(this::mapBookToBookResponse).collect(Collectors.toList());
    }

    public List<BookDto> findByPublisher(int publisherId){
        String sql = "SELECT * FROM Books WHERE publisherId =?";
        logger.info("Fetching books by publisher with id: {}", publisherId);
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publisherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching Books by publisher: {}", publisherId, e);
            throw new RuntimeException("Error fetching books by publisher", e);
        }
        return books.stream().map(this::mapBookToBookResponse).collect(Collectors.toList());
    }

    public List<BookDto> findByCategory(String category){
        String sql = "SELECT * FROM Books WHERE category =?";
        logger.info("Fetching books by category: {}", category);
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching books by category: {}", category, e);
            throw new RuntimeException("Error fetching books by category", e);
        }
        return books.stream().map(this::mapBookToBookResponse).collect(Collectors.toList());
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setIsbn(rs.getString("isbn"));
        book.setAuthorId(rs.getInt("authorId"));
        book.setPublisherId(rs.getInt("publisherId"));
        book.setCategory(BookCategory.valueOf(rs.getString("category")));
        book.setPublishYear(rs.getInt("publishYear"));
        book.setQuantity(rs.getInt("quantity"));
        book.setQuantityAvailable(rs.getInt("quantityAvailable"));
        book.setStatus(BookStatus.valueOf(rs.getString("status")));
        return book;
    }

    public boolean IsAvailable(int id) {
        String sql = "SELECT quantityAvailable FROM Books WHERE id =?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantityAvailable") > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking book availability by id: {}", id, e);
            throw new RuntimeException("Error checking book availability by id", e);
        }
        return false;
    }

    private String getPublisherNameById(int publisherId) {
        String sql = "SELECT name FROM Publishers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publisherId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching publisher name by id: {}", publisherId, e);
            throw new RuntimeException("Error fetching publisher name by id", e);
        }
    }

    private String getAuthorNameById(int authorId) {
        String sql = "SELECT name FROM Authors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching author name by id: {}", authorId, e);
            throw new RuntimeException("Error fetching author name by id", e);
        }
    }
}