package st.librarymanagement.Service;

import com.google.protobuf.ServiceException;
import lombok.NoArgsConstructor;
import st.librarymanagement.Dto.LoanDto;
import st.librarymanagement.Entity.Loan;
import st.librarymanagement.Repository.BookRepository;
import st.librarymanagement.Repository.LoanRepository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class LoanService {
    private static final LoanRepository loanRepository = new LoanRepository();
    private static final BookRepository bookRepository = new BookRepository();
    private static final FineService fineService = new FineService();

    public static LoanDto save(Loan loan) throws SQLException, ServiceException {
        validateLoan(loan);
        return loanRepository.save(loan);
    }

    public static void delete(int id) {
        if (loanRepository.existsById(id)) {
            throw new IllegalArgumentException("Loan not found with id: " + id);
        }
        loanRepository.deleteById(id);
    }

    public static List<LoanDto> getAllLoans() {
        return loanRepository.findAll();
    }

    public static void returnLoan(int id) {
        if (!loanRepository.existsById(id)) {
            throw new IllegalArgumentException("Loan not found with id: " + id);
        }
        loanRepository.returnLoan(id);
    }

    public Optional<LoanDto> getLoanById(int id) {
        return loanRepository.findById(id);
    }

    public List<LoanDto> getByMemberId(int memberId) throws SQLException {
        return loanRepository.findByMemberId(memberId);
    }

    public List<LoanDto> getByBookId(int bookId) throws SQLException {
        return loanRepository.findByBookId(bookId);
    }

    public static List<LoanDto> getOverdueLoans() throws SQLException {
        return loanRepository.findOverdueLoans();
    }

    public List<LoanDto> getLoansByDate(Date start, Date end) throws SQLException {
        return loanRepository.findByDate(start, end);
    }

    public static void renewLoan(int loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new IllegalArgumentException("Loan not found with id: " + loanId);
        }
        loanRepository.renewLoan(loanId);
    }

    private static void validateLoan(Loan loan) throws ServiceException {
        if (loan.getBookId() <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }
        if (loan.getMemberId() <= 0) {
            throw new IllegalArgumentException("Invalid member ID");
        }
        if (loan.getBorrowDate() == null) {
            throw new IllegalArgumentException("Borrow date cannot be null");
        }
        if (loan.getDueDate() == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (loan.getStatus() == null) {
            throw new IllegalArgumentException("Loan status cannot be null");
        }
        if(bookRepository.getBookById(loan.getBookId()).stream().findAny().isEmpty()) {
            throw new IllegalArgumentException("Book is not available");
        }
        if(fineService.getFinesByMemberId(loan.getMemberId()).stream().findAny().isPresent()) {
            throw new IllegalArgumentException("Member has fine");
        }
    }
}