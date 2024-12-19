package st.librarymanagement.Repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Entity.Member;
import st.librarymanagement.Entity.Status.MemberStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class MemberRepository {
    private static final Logger logger = LoggerFactory.getLogger(MemberRepository.class);
    private final DatabaseConnection connection = new DatabaseConnection();

    public Member save(Member member) throws RuntimeException {
        String sqlInsert = "INSERT INTO Members (name, email, phone, address, joinDate, status) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE Members SET name = ?, email = ?, phone = ?, " +
                           "address = ?, status = ? WHERE id = ?";

        try {
            if (member.getId() == 0) {
                logger.info("Inserting new member: {}", member);
                try (PreparedStatement stmt = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, member.getName());
                    stmt.setString(2, member.getEmail());
                    stmt.setString(3, member.getPhone());
                    stmt.setString(4, member.getAddress());
                    stmt.setDate(5, new java.sql.Date(member.getJoinDate().getTime()));
                    stmt.setString(6, member.getStatus().toString());
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        member.setId(rs.getInt(1));
                    }
                }
            } else {
                logger.info("Updating member with id: {}", member.getId());
                try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, member.getName());
                    stmt.setString(2, member.getEmail());
                    stmt.setString(3, member.getPhone());
                    stmt.setString(4, member.getAddress());
                    stmt.setString(5, member.getStatus().toString());
                    stmt.setInt(6, member.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving member: {}", member, e);
            throw new RuntimeException("Error saving member", e);
        }
        return member;
    }

    public void deleteById(int id) throws RuntimeException {
        String sql = "DELETE FROM Members WHERE id = ?";
        logger.info("Deleting member with id: {}", id);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting member by ID: {}", id, e);
            throw new RuntimeException("Error deleting member by ID", e);
        }
    }

    public List<Member> findAll() throws RuntimeException {
        ObservableList<Member> members = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Members ORDER BY name";
        logger.info("Retrieving all members");

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all members", e);
            throw new RuntimeException("Error retrieving all members", e);
        }
        return members;
    }

    public Optional<Member> findById(int id) throws RuntimeException {
        String sql = "SELECT * FROM Members WHERE id = ?";
        logger.info("Retrieving member with id: {}", id);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding member by ID: {}", id, e);
            throw new RuntimeException("Error finding member by ID", e);
        }
        return Optional.empty();
    }

    public Optional<Member> findByEmail(String email) throws RuntimeException {
        String sql = "SELECT * FROM Members WHERE email = ?";
        logger.info("Retrieving member with email: {}", email);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding member by email: {}", email, e);
            throw new RuntimeException("Error finding member by email", e);
        }
        return Optional.empty();
    }

    public Optional<Member> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM Members WHERE LOWER(name) = LOWER(?)";
        logger.info("Retrieving member with name: {}", name);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding member by name: {}", name, e);
            throw new RuntimeException("Error finding member by name", e);
        }
        return Optional.empty();
    }

    public Optional<Member> findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM Members WHERE phone = ?";
        logger.info("Retrieving member with phone: {}", phone);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding member by phone: {}", phone, e);
            throw new RuntimeException("Error finding member by phone", e);
        }
        return Optional.empty();
    }

    public List<Member> findByJoinDate(Date start, Date end) {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM Members WHERE joinDate BETWEEN ? AND ?";
        logger.info("Retrieving members by join date between {} and {}", start, end);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(start.getTime()));
            stmt.setDate(2, new java.sql.Date(end.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding members by join date", e);
            throw new RuntimeException("Error finding members by join date", e);
        }
        return members;
    }

    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM Members WHERE id = ?";
        logger.info("Checking existence of member with id: {}", id);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            logger.error("Error checking existence of member with id: {}", id, e);
            throw new RuntimeException("Error checking existence of member with id", e);
        }
    }

    public List<Member> findByStatus(MemberStatus status) throws RuntimeException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Members WHERE status = ?";
        logger.info("Retrieving members by status: {}", status);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding members by status", e);
            throw new RuntimeException("Error finding members by status", e);
        }
        return members;
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAddress(rs.getString("address"));
        member.setJoinDate(rs.getDate("joinDate"));
        member.setStatus(MemberStatus.valueOf(rs.getString("status")));
        return member;
    }
}