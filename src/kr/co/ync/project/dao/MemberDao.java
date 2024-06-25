package kr.co.ync.project.dao;

import kr.co.ync.project.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberDao {
    List<Member> all() throws SQLException;
    Member insert(Member member) throws SQLException;
    Member modify(Member member) throws SQLException;
    Member delete(Member member) throws SQLException;
    Member findByEmail(String email) throws SQLException;
    Member findByPhoneNumber(String phoneNumber) throws SQLException;
}
