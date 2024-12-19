package st.librarymanagement.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.FineStatus;

import java.time.LocalDate;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Fine {
    int id;
    int loanId;
    int memberId;
    double amount;
    String reason;
    Date fineDate;
    FineStatus status; // Paid, Unpaid
}
