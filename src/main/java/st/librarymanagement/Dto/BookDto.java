package st.librarymanagement.Dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.BookCategory;
import st.librarymanagement.Entity.Status.BookStatus;



@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
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
    String authorName;
    String publisherName;
}
