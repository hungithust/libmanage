package st.librarymanagement.Service;

import st.librarymanagement.Entity.Member;
import st.librarymanagement.Entity.Status.MemberStatus;
import st.librarymanagement.Repository.MemberRepository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class MemberService {
    private final static MemberRepository memberRepository = new MemberRepository();
    private final static LoanService loanService = new LoanService();
    public final static FineService fineService = new FineService();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{1,11}$");

    public MemberService() {}

    public static Member save(Member member) throws SQLException {
        validateMember(member);
        return memberRepository.save(member);
    }

    public static void delete(int id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    public static List<Member> getAllMembers() throws SQLException {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(int id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> getMemberByName(String name) throws SQLException {
        return memberRepository.findByName(name);
    }

    public Optional<Member> getMemberByPhone(String phone) throws SQLException {
        return memberRepository.findByPhone(phone);
    }

    public List<Member> getMembersByJoinDate(Date start, Date end) {
        return memberRepository.findByJoinDate(start, end);
    }

    public List<Member> getMembersByStatus(MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    private static void validateMember(Member member) {
        if (member.getName() == null || member.getName().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty");
        }
        if ((member.getEmail() == null || !EMAIL_PATTERN.matcher(member.getEmail()).matches())) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (member.getPhone() == null || !PHONE_PATTERN.matcher(member.getPhone()).matches()) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (member.getJoinDate() == null) {
            throw new IllegalArgumentException("Join date cannot be null");
        }
        if (member.getStatus() == null) {
            throw new IllegalArgumentException("Member status cannot be null");
        }
    }
}