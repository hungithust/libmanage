package st.librarymanagement.Dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.FineStatus;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class FineDto {
    int id;
    int loanId;
    int memberId;
    double amount;
    String reason;
    Date fineDate;
    FineStatus status; // Paid, Unpaid
    String memberName;
    String bookTitle;
}