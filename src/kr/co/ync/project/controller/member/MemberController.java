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
    public void save(Member member) {
        if (member != null) {
            try {
                // 이메일과 전화번호가 이미 데이터베이스에 존재하는지 확인
                if (MemberModel.getInstance().findByEmail(member.getEmail()) != null) {
                    System.out.println("이미 존재하는 이메일입니다.");
                } else if (MemberModel.getInstance().findByPhoneNumber(member.getPhone()) != null) {
                    System.out.println("이미 존재하는 전화번호입니다.");
                } else {
                    // 이메일과 전화번호가 중복되지 않으면 회원 가입 진행
                    MemberModel.getInstance().register(member);
                    notifyListeners(new MemberEvent(member, MemberEventType.REGISTER));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Member modify(Member member) {
        if (member != null) {
            try {
                System.out.println("MemberController.modify");
                MemberModel.getInstance().update(member);
                notifyListeners(new MemberEvent<>(member, MemberEventType.MODIFY));

            } catch (SQLException e) {
            }
        }
        return member;
    }

    public void delete(Member member) {
        if (member != null) {
            try {
                MemberModel.getInstance().delete(member);
                System.out.println("시작");
//                notifyListeners(new MemberEvent<>(member, MemberEventType.REMOVE));
                System.out.println("중간");
            } catch (SQLException e) {
                // 에러 처리
            }
        }
        System.out.println("끝");
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

    public Member findByPhone(String phoneStr) {
        Member member = null;
        if (phoneStr  != null) {
            try {
                member = MemberModel.getInstance().findByPhoneNumber(phoneStr);
            } catch (SQLException e) {
                // 에러 처리
            }
        }
        return member;
    }

    public void notifyListeners(MemberEvent<Member> memberEvent) {
        memberListeners.forEach(listener -> {
            switch (memberEvent.getMemberEventType()) {
                case REGISTER -> listener.register(memberEvent);
                case REMOVE -> listener.delete(memberEvent);
                case MODIFY -> listener.modify(memberEvent);
            }
        });
    }
}
