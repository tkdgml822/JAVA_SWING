package kr.co.ync.project.controller.member;

import kr.co.ync.project.controller.member.enums.MemberEventType;
import kr.co.ync.project.controller.member.listeners.MemberEvent;
import kr.co.ync.project.controller.member.listeners.MemberListener;
import kr.co.ync.project.model.Member;
import kr.co.ync.project.model.MemberModel;
import kr.co.ync.project.view.member.MemberView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberController {
    private List<MemberListener> memberListeners = new ArrayList<>();
    private MemberView memberView;
    private static final MemberController instance = new MemberController();

    public static MemberController getInstance() {
        return instance;
    }

    public synchronized void addMemberListener(MemberListener memberListener) {
        if (!memberListeners.contains(memberListener)) {
            memberListeners.add(memberListener);
        }
    }

    public List<Member> allMember() throws SQLException {
        return MemberModel.getInstance().allMembers();
    }
    public Member save(Member member) {
        if (member != null) {
            try {
                MemberModel.getInstance().register(member);
                notifyListeners(new MemberEvent<>(member, MemberEventType.REGISTER));
            } catch (SQLException e) {

            }
        }
        return member;
    }

    public Member modify(Member member) {
        if (member != null) {
            if (member.getId() == null) {
                throw new IllegalArgumentException("Member ID cannot be null");
            }
            try {
                MemberModel.getInstance().update(member);
                notifyListeners(new MemberEvent<>(member, MemberEventType.MODIFY));

            } catch (SQLException e) {
            }
        }
        return member;
    }

    public void delete(Member member) {
        if (member != null) {
            if (member.getId() == null) {
                throw new IllegalArgumentException("Member ID cannot be null");
            }
            try {
                MemberModel.getInstance().delete(member);
                notifyListeners(new MemberEvent<>(member, MemberEventType.REMOVE));
            } catch (SQLException e) {
                // 에러 처리
            }
        }
    }

    public Member findByEmail(String email) {
        Member member = null;
        if (email != null) {
            try {
                member = MemberModel.getInstance().findByEmail(email);
            } catch (SQLException e) {
                // 에러 처리
            }
        }
        return member;
    }

    private void notifyListeners(MemberEvent<Member> memberEvent) {
        memberListeners.forEach(listener -> {
            switch (memberEvent.getMemberEventType()) {
                case REGISTER -> listener.register(memberEvent);
                case MODIFY -> listener.modify(memberEvent);
                case REMOVE -> listener.delete(memberEvent);
            }
        });
    }



}
