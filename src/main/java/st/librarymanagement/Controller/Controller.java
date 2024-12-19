package st.librarymanagement.Controller;

import com.google.protobuf.ServiceException;

import java.io.IOException;
import java.sql.SQLException;

public abstract class Controller {
    public abstract void initialize() throws ServiceException, SQLException, IOException;
}
