package st.librarymanagement.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import st.librarymanagement.Entity.Status.FineStatus;
import st.librarymanagement.Entity.Status.MemberStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    int id;
    String name;
    String email;
    String phone;
    String address;
    Date joinDate;
    MemberStatus status; // Active, Inactive
}
