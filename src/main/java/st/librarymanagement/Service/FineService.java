package st.librarymanagement.Service;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import st.librarymanagement.Dto.FineDto;
import st.librarymanagement.Entity.Fine;
import st.librarymanagement.Entity.Status.FineStatus;
import st.librarymanagement.Repository.FineRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FineService {
    public final static Logger logger = LoggerFactory.getLogger(FineService.class);
    private final static FineRepository fineRepository = new FineRepository();

    public FineService() {}

    public static FineDto save(Fine fine) throws SQLException {
        validateFine(fine);
        return fineRepository.save(fine);
    }

    public static void delete(int id) throws SQLException {
        if (fineRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Fine not found with id: " + id);
        }
        fineRepository.delete(id);
    }

    public static ObservableList<FineDto> getAllFines() {
        return (ObservableList<FineDto>) fineRepository.findAll();
    }

    public static void pay(int id) {
        if (fineRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Fine not found with id: " + id);
        }
        fineRepository.pay(id);
    }

    public Optional<FineDto> getFineById(int id) throws SQLException {
        return fineRepository.findById(id);
    }

    public Optional<FineDto> getFineByLoanId(int loanId) throws SQLException {
        return fineRepository.findByLoanId(loanId);
    }

    public List<FineDto> getFinesByMemberId(int memberId) {
        return fineRepository.findByMemberId(memberId);
    }

    public List<FineDto> getFinesByStatus(FineStatus status) {
        return fineRepository.findByStatus(status);
    }

    public List<FineDto> getFinesByAmount(int start, int end) {
        return fineRepository.findByAmount(start, end);
    }

    private static void validateFine(Fine fine) {
        if (fine.getLoanId() <= 0) {
            throw new IllegalArgumentException("Invalid loan ID");
        }
        if (fine.getMemberId() <= 0) {
            throw new IllegalArgumentException("Invalid member ID");
        }
        if (fine.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid fine amount");
        }
        if (fine.getReason() == null || fine.getReason().isEmpty()) {
            throw new IllegalArgumentException("Fine reason cannot be null or empty");
        }
        if (fine.getFineDate() == null) {
            throw new IllegalArgumentException("Fine date cannot be null");
        }
        if (fine.getStatus() == null) {
            throw new IllegalArgumentException("Fine status cannot be null");
        }
    }

    public FineDto getFinesByBookId(int i) {
        return null;
    }
}