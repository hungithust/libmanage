package st.librarymanagement.Repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Dto.FineDto;
import st.librarymanagement.Entity.Fine;
import st.librarymanagement.Entity.Status.FineStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class FineRepository {
    public static final Logger logger = LoggerFactory.getLogger(FineRepository.class);
    DatabaseConnection connection = new DatabaseConnection();

    private FineDto mapFineToFineResponse(Fine fine) {
        // Assuming you have methods to get member name by their IDs
        String memberName = getMemberNameById(fine.getMemberId());
        String bookTitle = getBookTitleById(fine.getLoanId());

        return new FineDto(
            fine.getId(),
            fine.getLoanId(),
            fine.getMemberId(),
            fine.getAmount(),
            fine.getReason(),
            fine.getFineDate(),
            fine.getStatus(),
            memberName,
            bookTitle
        );
    }

    private String getBookTitleById(int loanId) {
        String selectSql = "SELECT title FROM Books WHERE id = (SELECT bookId FROM Loans WHERE id = ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setInt(1, loanId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("title");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private String getMemberNameById(int memberId) {
        String selectSql = "SELECT name FROM Members WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setInt(1, memberId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public FineDto save(Fine fine) {
        String insertSql = "INSERT INTO Fines (loanId, memberId, amount, reason, fineDate, status) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE Fines SET loanId = ?, memberId = ?, amount = ?, reason = ?, fineDate = ?, status = ? WHERE id = ?";
        try {
            if (fine.getId() == 0) {
                logger.info("Inserting new fine");
                try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, fine.getLoanId());
                    stmt.setInt(2, fine.getMemberId());
                    stmt.setDouble(3, fine.getAmount());
                    stmt.setString(4, fine.getReason());
                    stmt.setDate(5, new java.sql.Date(fine.getFineDate().getTime()));
                    stmt.setString(6, fine.getStatus().toString());
                    stmt.executeUpdate();
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            fine.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            } else {
                logger.info("Updating fine with id: {}", fine.getId());
                try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(updateSql)) {
                    stmt.setInt(1, fine.getLoanId());
                    stmt.setInt(2, fine.getMemberId());
                    stmt.setDouble(3, fine.getAmount());
                    stmt.setString(4, fine.getReason());
                    stmt.setDate(5, new java.sql.Date(fine.getFineDate().getTime()));
                    stmt.setString(6, fine.getStatus().toString());
                    stmt.setInt(7, fine.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mapFineToFineResponse(fine);
    }

    public void delete(int id) {
        String deleteSql = "DELETE FROM Fines WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(deleteSql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Fine deleted successfully with ID: {}", id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<FineDto> findAll() {
        String selectSql = "SELECT * FROM Fines";
        ObservableList<Fine> fines = FXCollections.observableArrayList();
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                fines.add(mapResultSetToFine(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fines.stream().map(this::mapFineToFineResponse).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public Optional<FineDto> findById(int id) {
        String selectSql = "SELECT * FROM Fines WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapFineToFineResponse(mapResultSetToFine(resultSet)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<FineDto> findByLoanId(int loanId) {
        String selectSql = "SELECT * FROM Fines WHERE loanId = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setInt(1, loanId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapFineToFineResponse(mapResultSetToFine(resultSet)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<FineDto> findByMemberId(int memberId) {
        String selectSql = "SELECT * FROM Fines WHERE memberId = ?";
        List<Fine> fines = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setInt(1, memberId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                fines.add(mapResultSetToFine(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fines.stream().map(this::mapFineToFineResponse).collect(Collectors.toList());
    }

    public List<FineDto> findByStatus(FineStatus status) {
        String selectSql = "SELECT * FROM Fines WHERE status = ?";
        List<Fine> fines = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setString(1, status.toString());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                fines.add(mapResultSetToFine(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fines.stream().map(this::mapFineToFineResponse).collect(Collectors.toList());
    }

    public List<FineDto> findByAmount(double start, double end) {
        String selectSql = "SELECT * FROM Fines WHERE amount BETWEEN ? AND ?";
        List<Fine> fines = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            stmt.setDouble(1, start);
            stmt.setDouble(2, end);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                fines.add(mapResultSetToFine(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fines.stream().map(this::mapFineToFineResponse).collect(Collectors.toList());
    }

    private Fine mapResultSetToFine(ResultSet resultSet) {
        try {
            return new Fine(
                resultSet.getInt("id"),
                resultSet.getInt("loanId"),
                resultSet.getInt("memberId"),
                resultSet.getDouble("amount"),
                resultSet.getString("reason"),
                resultSet.getDate("fineDate"),
                FineStatus.valueOf(resultSet.getString("status"))
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void pay(int id) {
        String updateSql = "UPDATE Fines SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(updateSql)) {
            stmt.setString(1, FineStatus.Paid.toString());
            stmt.setInt(2, id);
            stmt.executeUpdate();
            logger.info("Fine paid successfully with ID: {}", id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}