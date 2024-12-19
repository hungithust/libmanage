package st.librarymanagement.Dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.LoanStatus;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    int id;
    int bookId;
    int memberId;
    Date loanDate;
    Date dueDate;
    Date returnDate;
    LoanStatus status; // Active, Returned, Overdue
    String BookTitle;
    String MemberName;
}
