package kr.co.ync.project.model;

import kr.co.ync.project.dao.MemberDao;
import kr.co.ync.project.dao.factory.DaoFactory;

import java.sql.SQLException;
import java.util.List;

public class MemberModel {
    private static final MemberModel instance = new MemberModel();

    private MemberModel() {
    }

    public static MemberModel getInstance() {
        return instance;
    }

    public List<Member> allMembers() throws SQLException {
        return memberDao().all();
    }

    public void register(Member member) throws SQLException {
        memberDao().insert(member);
    }

    public void update(Member member) throws SQLException {
        memberDao().modify(member);
    }

    public void delete(Member member) throws SQLException {
        memberDao().delete(member);
    }

    public Member findByEmail(String email) throws SQLException {
        return memberDao().findByEmail(email);
    }

    private static MemberDao memberDao() {
        return DaoFactory.getDatabase().getMemberDao();
    }

}
