package st.librarymanagement.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.LoanStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    int id;
    int bookId;
    int memberId;
    Date borrowDate;
    Date dueDate;
    Date returnDate;
    LoanStatus status; // Active, Returned, Overdue
}
