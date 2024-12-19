package st.librarymanagement.Repository;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Entity.Publisher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class PublisherRepository {
    public static final Logger logger = LoggerFactory.getLogger(PublisherRepository.class);
    private final DatabaseConnection connection = new DatabaseConnection();

    public Publisher save(Publisher publisher) {
        String sqlInsert = "INSERT INTO Publishers (name, email, address, contact) VALUES (?, ?, ?, ?)";
        String sqlUpdate = "UPDATE Publishers SET name = ?, email = ?, address = ?, contact = ? WHERE id = ?";
        try {
            if (publisher.getId() == 0) {
                logger.info("Inserting new publisher: {}", publisher);
                try (PreparedStatement stmt = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, publisher.getName());
                    stmt.setString(2, publisher.getEmail());
                    stmt.setString(3, publisher.getAddress());
                    stmt.setString(4, publisher.getContact());
                    stmt.executeUpdate();
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            publisher.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            } else {
                logger.info("Updating publisher with id: {}", publisher.getId());
                try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, publisher.getName());
                    stmt.setString(2, publisher.getEmail());
                    stmt.setString(3, publisher.getAddress());
                    stmt.setString(4, publisher.getContact());
                    stmt.setInt(5, publisher.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving publisher: {}", publisher, e);
            throw new RuntimeException("Error saving publisher", e);
        }
        return publisher;
    }

    public void delete(int publisherId) {
        String sql = "DELETE FROM Publishers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publisherId);
            stmt.executeUpdate();
            logger.info("Publisher deleted successfully with ID: {}", publisherId);
        } catch (SQLException e) {
            logger.error("Error deleting publisher by ID: {}", publisherId, e);
            throw new RuntimeException("Error deleting publisher", e);
        }
    }

    public List<Publisher> findAll() throws SQLException {
        String sql = "SELECT * FROM Publishers";
        logger.info("Fetching all publishers");
        List<Publisher> publishers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                publishers.add(mapResultSetToPublisher(resultSet));
            }
        }
        return publishers;
    }

    public Optional<Publisher> findById(int publisherId) throws SQLException {
        String sql = "SELECT * FROM Publishers WHERE id = ?";
        logger.info("Fetching publisher by ID: {}", publisherId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, publisherId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToPublisher(resultSet));
            }
        }
        return Optional.empty();
    }

    public Optional<Publisher> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM Publishers WHERE name = ?";
        logger.info("Fetching publisher by name: {}", name);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToPublisher(resultSet));
            }
        }
        return Optional.empty();
    }

    public Optional<Publisher> findByEmail(String email) {
        String sql = "SELECT * FROM Publishers WHERE email = ?";
        logger.info("Fetching publisher by email: {}", email);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToPublisher(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error fetching publisher by email: {}", email, e);
            throw new RuntimeException(e);
        }
    }

    private Publisher mapResultSetToPublisher(ResultSet resultSet) throws SQLException {
        Publisher publisher = new Publisher();
        publisher.setId(resultSet.getInt("id"));
        publisher.setName(resultSet.getString("name"));
        publisher.setEmail(resultSet.getString("email"));
        publisher.setAddress(resultSet.getString("address"));
        publisher.setContact(resultSet.getString("contact"));
        return publisher;
    }

    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM Publishers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            logger.error("Error checking if publisher exists by ID: {}", id, e);
            throw new RuntimeException("Error checking if publisher exists", e);
        }
    }
}