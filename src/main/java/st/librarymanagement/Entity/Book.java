package st.librarymanagement.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.BookCategory;
import st.librarymanagement.Entity.Status.BookStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    int id;
    String title;
    int authorId;
    int publisherId;
    BookCategory category;
    String isbn;
    int publishYear;
    int quantity;
    int quantityAvailable;
    BookStatus status; // Available, NotAvailable
}
