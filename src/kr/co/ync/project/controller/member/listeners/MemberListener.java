package kr.co.ync.project.controller.member.listeners;

import java.util.ArrayList;
import java.util.List;

public interface MemberListener {
    void register(MemberEvent memberEvent); // C
    void modify(MemberEvent memberEvent); // U
    void delete(MemberEvent memberEvent); // D
}
