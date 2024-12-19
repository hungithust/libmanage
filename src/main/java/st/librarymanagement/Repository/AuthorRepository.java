package st.librarymanagement.Repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Entity.Author;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class AuthorRepository {
    private static final Logger logger = LoggerFactory.getLogger(AuthorRepository.class);
    private final DatabaseConnection connection = new DatabaseConnection();

    public Author save(Author author) {
        String sqlInsert = "INSERT INTO Authors (name, email, phone, address, description) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE Authors SET name = ?, email = ?, phone = ?, address = ?, description = ? WHERE id = ?";
        try {
            if (author.getId() == 0) {
                logger.info("Inserting new author: {}", author);
                try (PreparedStatement stmt = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, author.getName());
                    stmt.setString(2, author.getEmail());
                    stmt.setString(3, author.getPhone());
                    stmt.setString(4, author.getAddress());
                    stmt.setString(5, author.getDescription());
                    stmt.executeUpdate();
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            author.setId(generatedKeys.getInt(1));
                        }
                    }
                    logger.info("Author inserted successfully with ID: {}", author.getId());
                }
            } else {
                logger.info("Updating author with id: {}", author.getId());
                try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, author.getName());
                    stmt.setString(2, author.getEmail());
                    stmt.setString(3, author.getPhone());
                    stmt.setString(4, author.getAddress());
                    stmt.setString(5, author.getDescription());
                    stmt.setInt(6, author.getId());
                    stmt.executeUpdate();
                    logger.info("Author updated successfully with ID: {}", author.getId());
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving author: {}", author, e);
            throw new RuntimeException("Error saving author", e);
        }
        return author;
    }

    public void delete(int id) {
        String sql = "DELETE FROM Authors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            logger.info("Author deleted successfully with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting author by ID: {} because {}", id, e.getMessage());
        }
    }

    public List<Author> findAll() {
        ObservableList<Author> authors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Authors";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                authors.add(MapResultSetToAuthor(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error fetching authors: {}", e.getMessage());
        }
        return authors;
    }

    public Optional<Author> findById(int id) {
        Author author = null;
        String sql = "SELECT * FROM Authors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                author = MapResultSetToAuthor(resultSet);
                return Optional.of(author);
            }
        } catch (SQLException e) {
            logger.error("Error fetching author by ID: {} \n message {}", id, e.getMessage());
        }
        return Optional.empty();
    }

    public ObservableList<Author> findByName(String name) {
        String sql = "SELECT * FROM Authors WHERE LOWER(name) LIKE LOWER(?)";
        logger.info("Fetching author by name: {}", name);
        ObservableList<Author> authors = FXCollections.observableArrayList();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                authors.add(MapResultSetToAuthor(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error fetching author by name: {}", name, e);
        }
        return authors;
    }


    public Optional<Author> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Authors WHERE email = ?";
        logger.info("Fetching author by email: {}", email);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Author author = MapResultSetToAuthor(resultSet);
                return Optional.of(author);
            }
        } catch (SQLException e) {
            logger.error("Error fetching author by email: {}", email, e);
        }
        return Optional.empty();
    }

    private Author MapResultSetToAuthor(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        String address = resultSet.getString("address");
        String description = resultSet.getString("description");
        return new Author(id, name, email, phone, address, description);
    }

    private List<Author> MapResultSetToList(ResultSet resultSet) throws SQLException {
        List<Author> authors = new ArrayList<>();
        while (resultSet.next()) {
            authors.add(MapResultSetToAuthor(resultSet));
        }
        return authors;
    }
}