package kr.co.ync.project.controller.member;

import kr.co.ync.project.controller.member.enums.MemberEventType;
import kr.co.ync.project.controller.member.listeners.MemberEvent;
import kr.co.ync.project.controller.member.listeners.MemberListener;
import kr.co.ync.project.model.Member;
import kr.co.ync.project.model.MemberModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberController {
    private List<MemberListener> memberListeners = new ArrayList<>();

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
                notifyListeners(new MemberEvent<Member>(member, MemberEventType.REGISTER));
            } catch (SQLException e) {

            }
        }
        return member;
    }

    private void notifyListeners(MemberEvent<Member> memberEvent) {
        memberListeners.forEach(listener -> {
            switch (memberEvent.getMemberEventType()) {
                case REGISTER -> listener.register(memberEvent);
            }
        });
    }

}
