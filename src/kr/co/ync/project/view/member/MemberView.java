package kr.co.ync.project.view.member;

import kr.co.ync.project.controller.member.MemberController;
import kr.co.ync.project.controller.member.listeners.MemberEvent;
import kr.co.ync.project.controller.member.listeners.MemberListener;
import kr.co.ync.project.model.Member;
import kr.co.ync.project.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class MemberView extends JFrame implements MemberListener {

    private final String[] labelTexts = {"이메일", "이름", "전화번호", "생년원일"};
    private JTextField[] fields;
    private JButton regButton;
    private DefaultTableModel defaultTableModel;
    private JTable jTable;
    public static final Dimension SIZE = new Dimension(1000, 500);

    public MemberView(String title) {
        super(title);
        setLayout(new BorderLayout());  // 레이아웃을 BorderLayout으로 변경

        add(createSearchPanel(), BorderLayout.NORTH);  // 검색 패널을 상단에 추가
        JPanel jPanel = new JPanel(new GridLayout(1, 2));
        jPanel.add(createLeftPanel());
        jPanel.add(createRightPanel());
        add(jPanel, BorderLayout.CENTER);  // jPanel을 중앙에 추가

        MemberController.getInstance().addMemberListener(this);
        loadMembers();
        registerListeners();
    }

    private void registerListeners() {
        regButton.addActionListener(e -> {
            Member member = new Member(
                    fields[0].getText(),
                    fields[1].getText(),
                    fields[2].getText(),
                    Util.strToLocalDate(fields[3].getText())
            );
            MemberController.getInstance().save(member);
        });
    }

    private void loadMembers() {
        try {
            for (Member member : MemberController.getInstance().allMember()) {
                defaultTableModel.insertRow(0, member.toArray());
            }
        } catch (SQLException e) {

        }
    }

    private JPanel createRightPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("회원목록"), BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );

        jPanel.setLayout(new BorderLayout());
        JScrollPane jScrollPane = new JScrollPane();

        defaultTableModel = new DefaultTableModel(new String[]{
                "NO", "이메일", "이름", "전화번호", "생년원일", "가입일"
        }, 0);

        jTable = new JTable(defaultTableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        jScrollPane.setViewportView(jTable);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jTable.setFillsViewportHeight(true);

        jPanel.add(jScrollPane, BorderLayout.CENTER);

        return jPanel;
    }

    private JPanel createLeftPanel() {
        fields = new JTextField[labelTexts.length];
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setBounds(15, 6, 450, 185);
        fieldPanel.setLayout(new GridLayout(4, 2, 5, 5));
        fieldPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("회원등록"), BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );

        // init
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new JTextField();
            JLabel jLabel = new JLabel(labelTexts[i], SwingConstants.LEFT);
            fieldPanel.add(jLabel);
            fieldPanel.add(fields[i]);
        }

        regButton = new JButton("등록");
        regButton.setBounds(15, 186, 450, 40);

        jPanel.add(fieldPanel);
        jPanel.add(regButton);

        return jPanel;
    }

    public static void createShowGUI() {
        JFrame frame = new MemberView("2205052 회원관리 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 응용 프로그램도 같이 종료
        frame.setMinimumSize(SIZE); // 사이즈 조절
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void register(MemberEvent memberEvent) {
        Member member = (Member) memberEvent.getSource();
        defaultTableModel.insertRow(0, member.toArray());
    }

    private void clearMemberFields() {
        Arrays.stream(fields).forEach(
                field -> field.setText("")
        );
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());  // 레이아웃을 BorderLayout으로 변경
        JTextField searchField = new JTextField();  // 검색 필드의 크기를 지정하지 않음
        JButton searchButton = new JButton("검색");

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText();
            // TODO: 입력된 검색어로 회원을 검색하고 결과를 테이블에 표시하는 코드를 작성하세요.
        });

        searchPanel.add(searchField, BorderLayout.CENTER);  // 검색 필드를 패널의 중앙에 추가
        searchPanel.add(searchButton, BorderLayout.EAST);  // 검색 버튼을 패널의 동쪽에 추가

        return searchPanel;
    }
}
