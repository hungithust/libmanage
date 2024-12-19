package st.librarymanagement.Controller.Right;

import com.google.protobuf.ServiceException;
import javafx.scene.control.TextField;
import lombok.Setter;
import st.librarymanagement.Controller.Center.AuthorController;
import st.librarymanagement.Controller.Center.BookController;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Entity.Book;
import st.librarymanagement.Entity.Status.BookCategory;
import st.librarymanagement.Entity.Status.BookStatus;
import st.librarymanagement.Service.BookService;

import java.sql.SQLException;



@Setter
public class BookDetailSideController extends RightSideController<BookDto> {
    public TextField id, title, authorId, publisherId,
            isbn, category, publishYear, quantity, quantityAvailable, status;

    // Method to update UI with selected book details
    @Override
    public void setSelectedItem(BookDto item) {
        this.selectedItem = item;
    }

    @Override
    public void fillFields() {
        id.setText(String.valueOf(selectedItem.getId()));
        title.setText(selectedItem.getTitle());
        authorId.setText(String.valueOf(selectedItem.getAuthorId()));
        publisherId.setText(String.valueOf(selectedItem.getPublisherId()));
        isbn.setText(selectedItem.getIsbn());
        category.setText(selectedItem.getCategory().toString());
        publishYear.setText(String.valueOf(selectedItem.getPublishYear()));
        quantity.setText(String.valueOf(selectedItem.getQuantity()));
        quantityAvailable.setText(String.valueOf(selectedItem.getQuantityAvailable()));
        status.setText(selectedItem.getStatus().toString());
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void handleSave() throws SQLException, ServiceException {
        selectedItem = BookService.save(new Book(
                Integer.parseInt(id.getText()),
                title.getText(),
                Integer.parseInt(authorId.getText()),
                Integer.parseInt(publisherId.getText()),
                BookCategory.valueOf(category.getText()),
                isbn.getText(),
                Integer.parseInt(publishYear.getText()),
                Integer.parseInt(quantity.getText()),
                Integer.parseInt(quantityAvailable.getText()),
                BookStatus.valueOf(status.getText())
        ));
        managementController.refresh();
    }

    @Override
    public void handleDelete() throws ServiceException, SQLException {
        BookService.delete(selectedItem.getId());
        clearFields();
        managementController.refresh();
    }
    @Override
    public void handleEdit() {
        title.setEditable(true);
        authorId.setEditable(true);
        publisherId.setEditable(true);
        isbn.setEditable(true);
        category.setEditable(true);
        publishYear.setEditable(true);
        quantity.setEditable(true);
        quantityAvailable.setEditable(true);
        status.setEditable(true);
    }

    @Override
    public void clearFields() {
        id.clear();
        title.clear();
        authorId.clear();
        publisherId.clear();
        isbn.clear();
        category.clear();
        publishYear.clear();
        quantity.clear();
        quantityAvailable.clear();
        status.clear();
    }

    @Override
    public void setManagementController(ManagementController<BookDto> managementController) throws ServiceException {
        this.managementController = managementController;
    }

}
