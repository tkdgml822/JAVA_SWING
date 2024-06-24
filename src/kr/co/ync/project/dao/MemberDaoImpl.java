package kr.co.ync.project.dao;

import kr.co.ync.project.dao.factory.DaoFactory;
import kr.co.ync.project.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MemberDaoImpl implements MemberDao {
    private static final String ALL = "SELECT * FROM tb_members";
    private static final String INSERT = "INSERT INTO tb_members (email, name, phone, birth, reg_date) VALUES (?, ?, ?, ?, ?)";
    private static final String MODIFY = "UPDATE tb_members SET  name = ?, birth = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM tb_members WHERE email = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM tb_members WHERE email = ?";

    @Override
    public List<Member> all() throws SQLException {
        ArrayList<Member> members = new ArrayList<>();

        Connection connection = DaoFactory.getDatabase().openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(ALL); // sql 문을 실행하기 위해
        ResultSet resultSet = preparedStatement.executeQuery(); // SELECT 문과 같은 쿼리문을 실행할 때 사용

        while (resultSet.next()) {
            members.add(createMember(resultSet));
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return members;
    }

    @Override
    public Member insert(Member member) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        try (
            Connection c = DaoFactory.getDatabase().openConnection();
            PreparedStatement pstmt = c.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getPhone());
            pstmt.setDate(4, Date.valueOf(member.getBirth()));
            pstmt.setTimestamp(5, Timestamp.valueOf(now));

            pstmt.executeUpdate();

            try (ResultSet rset = pstmt.getGeneratedKeys()) {
                rset.next();
                Long idGenerated = rset.getLong(1);
                member.setId(idGenerated);
                member.setRegDate(now);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

    @Override
    public Member modify(Member member) throws SQLException {
        try (
            Connection c = DaoFactory.getDatabase().openConnection();
            PreparedStatement pstmt = c.prepareStatement(MODIFY)
        ) {
            pstmt.setString(1, member.getName());
            pstmt.setDate(2, Date.valueOf(member.getBirth()));
            pstmt.setLong(3, member.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }
    @Override
    public Member delete(Member member) throws SQLException {
        try (
                Connection c = DaoFactory.getDatabase().openConnection();
                PreparedStatement pstmt = c.prepareStatement(DELETE)
        ) {
            pstmt.setString(1, member.getEmail());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

    @Override
    public Member findByEmail(String email) throws SQLException {
        Member member = null;

        try (
                Connection c = DaoFactory.getDatabase().openConnection();
                PreparedStatement pstmt = c.prepareStatement(FIND_BY_EMAIL)
        ) {
            pstmt.setString(1, email);

            try (ResultSet rset = pstmt.executeQuery()) {
                if (rset.next()) {
                    member = createMember(rset);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

    private Member createMember(ResultSet resultSet) throws SQLException {
        Member member = new Member();
        member.setId(resultSet.getLong("id"));
        member.setEmail(resultSet.getString("email"));
        member.setName(resultSet.getString("name"));
        member.setPhone(resultSet.getString("phone"));
        member.setBirth(resultSet.getObject("birth", LocalDate.class));
        member.setRegDate(resultSet.getObject("reg_date", LocalDateTime.class));
        return member;
    }
}