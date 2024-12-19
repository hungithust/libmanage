package st.librarymanagement.Repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Dto.LoanDto;
import st.librarymanagement.Entity.Loan;
import st.librarymanagement.Entity.Status.LoanStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class LoanRepository {
    private final DatabaseConnection connection = new DatabaseConnection();
    private static final Logger logger = LoggerFactory.getLogger(LoanRepository.class);

    private LoanDto mapLoanToLoanResponse(Loan loan) {
        // Assuming you have methods to get bookTitle and memberName by their IDs
        String bookTitle = getBookTitleById(loan.getBookId());
        String memberName = getMemberNameById(loan.getMemberId());

        return new LoanDto(
            loan.getId(),
            loan.getBookId(),
            loan.getMemberId(),
            loan.getBorrowDate(),
            loan.getDueDate(),
            loan.getReturnDate(),
            loan.getStatus(),
            bookTitle,
            memberName
        );
    }

    private String getMemberNameById(int memberId) {
        String sql = "SELECT name FROM Members WHERE id =?";
        logger.info("Retrieving member name by ID: {}", memberId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("name") : null;
        } catch (SQLException e) {
            logger.error("Error retrieving member name by ID: {}", memberId, e);
            throw new RuntimeException("Error retrieving member name by id", e);
        }
    }

    private String getBookTitleById(int bookId) {
        String sql = "SELECT title FROM Books WHERE id =?";
        logger.info("Retrieving book title by ID: {}", bookId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("title") : null;
        } catch (SQLException e) {
            logger.error("Error retrieving book title by ID: {}", bookId, e);
            throw new RuntimeException("Error retrieving book title by id", e);
        }
    }

    public LoanDto save(Loan loan) throws SQLException {
        String sql;
        if (loan.getId() == 0) {
            sql = "INSERT INTO Loans (bookId, memberId, borrowDate, dueDate, status) " +
                  "VALUES (?, ?, ?, ?, ?, ?)";
            logger.info("Inserting new loan: {}", loan);
        } else {
            sql = "UPDATE Loans SET status = ?, returnDate = ?, bookId = ?, memberId = ?, librarianId = ?, borrowDate = ?, dueDate = ? WHERE id = ?";
            logger.info("Updating loan with ID: {}", loan.getId());
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setDate(3, new java.sql.Date(loan.getBorrowDate().getTime()));
            stmt.setDate(4, new java.sql.Date(loan.getDueDate().getTime()));
            stmt.setString(5, loan.getStatus().toString());

            if (loan.getId() != 0) {
                stmt.setDate(6, loan.getReturnDate() != null ? new java.sql.Date(loan.getReturnDate().getTime()) : null);
                stmt.setInt(7, loan.getId());
            }

            stmt.executeUpdate();

            if (loan.getId() == 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    loan.setId(rs.getInt(1));
                    logger.info("Loan inserted successfully with ID: {}", loan.getId());
                }
            } else {
                logger.info("Loan updated successfully with ID: {}", loan.getId());
            }
        } catch (SQLException e) {
            logger.error("Error saving loan: {}", loan, e);
            throw e;
        }
        return mapLoanToLoanResponse(loan);
    }

    public void deleteById(int loanId) {
        String sql = "DELETE FROM Loans WHERE id =?";
        logger.info("Deleting loan with ID: {}", loanId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, loanId);
            stmt.executeUpdate();
            logger.info("Loan deleted successfully with ID: {}", loanId);
        } catch (SQLException e) {
            logger.error("Error deleting loan by ID: {}", loanId, e);
            throw new RuntimeException("Error deleting loan by id", e);
        }
    }

    public List<LoanDto> findAll(){
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Loans";
        logger.info("Retrieving all loans");
        try (Statement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all loans", e);
            throw new RuntimeException("Error retrieving all loans", e);
        }
        return loans.stream().map(this::mapLoanToLoanResponse).collect(Collectors.toList());
    }

    public List<LoanDto> findByMemberId(int memberId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE memberId = ?";
        logger.info("Retrieving loans for member ID: {}", memberId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving loans for member ID: {}", memberId, e);
            throw e;
        }
        return loans.stream().map(this::mapLoanToLoanResponse).collect(Collectors.toList());
    }

    public Optional<LoanDto> findById(int loanId) {
        try {
            String sql = "SELECT * FROM Loans WHERE id =?";
            logger.info("Retrieving loan with ID: {}", loanId);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapLoanToLoanResponse(mapResultSetToLoan(rs)));
            }
        } catch (SQLException e) {
            logger.error("Error finding loan by ID: {}", loanId, e);
            throw new RuntimeException("Error finding loan by id", e);
        }
        return Optional.empty();
    }

    public List<LoanDto> findByBookId(int bookId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE bookId = ?";
        logger.info("Retrieving loans for book ID: {}", bookId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving loans for book ID: {}", bookId, e);
            throw e;
        }
        return loans.stream().map(this::mapLoanToLoanResponse).collect(Collectors.toList());
    }

    public List<LoanDto> findOverdueLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE status = 'ACTIVE' AND dueDate < CURRENT_DATE";
        logger.info("Retrieving overdue loans");
        try (Statement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving overdue loans", e);
            throw e;
        }
        return loans.stream().map(this::mapLoanToLoanResponse).collect(Collectors.toList());
    }

    public List<LoanDto> findByDate(Date start, Date end) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE borrowDate BETWEEN ? AND ?";
        logger.info("Retrieving loans between dates: {} and {}", start, end);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(start.getTime()));
            stmt.setDate(2, new java.sql.Date(end.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving loans between dates: {} and {}", start, end, e);
            throw e;
        }
        return loans.stream().map(this::mapLoanToLoanResponse).collect(Collectors.toList());
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("bookId"));
        loan.setMemberId(rs.getInt("memberId"));
        loan.setBorrowDate(rs.getDate("borrowDate"));
        loan.setDueDate(rs.getDate("dueDate"));
        loan.setReturnDate(rs.getDate("returnDate"));
        loan.setStatus(LoanStatus.valueOf(rs.getString("status")));
        return loan;
    }

    public boolean existsById(int loanId) {
        String sql = "SELECT COUNT(*) FROM Loans WHERE id =?";
        logger.info("Checking existence of loan with ID: {}", loanId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            logger.error("Error checking existence of loan by ID: {}", loanId, e);
            throw new RuntimeException("Error checking existence of loan by id", e);
        }
    }

    public boolean isOverdue(Loan loan) {
        boolean overdue = loan.getDueDate().before(new Date());
        logger.info("Loan with ID: {} is overdue: {}", loan.getId(), overdue);
        return overdue;
    }

    public void renewLoan(int loanId) {
        String sql = "UPDATE Loans SET dueDate = DATE_ADD(dueDate, INTERVAL 1 MONTH) WHERE id =?";
        logger.info("Renewing loan with ID: {}", loanId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, loanId);
            stmt.executeUpdate();
            logger.info("Loan renewed successfully with ID: {}", loanId);
        } catch (SQLException e) {
            logger.error("Error renewing loan with ID: {}", loanId, e);
            throw new RuntimeException("Error renewing loan", e);
        }
    }

    public void returnLoan(int id) {
        String sql = "UPDATE Loans SET status = 'Returned', returnDate = CURRENT_DATE WHERE id =?";
        logger.info("Returning loan with ID: {}", id);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Loan returned successfully with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error returning loan with ID: {}", id, e);
            throw new RuntimeException("Error returning loan", e);
        }
    }
}