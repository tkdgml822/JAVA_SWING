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
    private CardLayout cardLayout;
    private JPanel contentPanel;
    public static final Dimension SIZE = new Dimension(1100, 500);


    public MemberView(String title) {
        super(title);
        setLayout(new GridBagLayout());

        // CardLayout을 사용하여 createLeftPanel과 createModifyPanel을 하나의 패널에 추가
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createLeftPanel(), "Register");
        contentPanel.add(createModifyPanel(), "Modify");

        // DefaultTableModel 초기화
        defaultTableModel = new DefaultTableModel(new String[]{
                "NO", "이메일", "이름", "전화번호", "생년원일", "가입일"
        }, 0);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;  // 가로 세로 모두 채우기

        // 왼쪽 메뉴바
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.01;  // 가로 비율 설정
        gbc.weighty = 1;
        add(createLeftMenuBar(), gbc);

        // 왼쪽 패널
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;  // 가로 비율 설정
        gbc.weighty = 1;
        add(createLeftPanel(), gbc);

        // 오른쪽 패널
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1;  // 가로 비율 설정
        gbc.weighty = 1;
        add(createRightPanel(), gbc);

        // 왼쪽 패널
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;  // 가로 비율 설정
        gbc.weighty = 1;
        add(contentPanel, gbc);  // contentPanel을 추가

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


        jTable = new JTable(defaultTableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable.getSelectedRow() != -1) {
                // 회원을 선택하면 해당 회원의 정보를 createModifyPanel의 필드에 채웁니다.
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText((String) jTable.getValueAt(jTable.getSelectedRow(), i + 1));
                }
                cardLayout.show(contentPanel, "Modify");
            }
        });

        jPanel.setLayout(new BorderLayout());
        JScrollPane jScrollPane = new JScrollPane();

        jScrollPane.setViewportView(jTable);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jTable.setFillsViewportHeight(true);

        // JScrollPane의 크기를 조절
        jScrollPane.setPreferredSize(new Dimension(20, 300));

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

    public JPanel createLeftMenuBar() {
        JPanel menuBar = new JPanel();
        menuBar.setLayout(new GridLayout(4, 1));  // 4개의 버튼을 수직으로 배치

        // 회원 등록 버튼
        JButton registerButton = new JButton("회원 등록");
        registerButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Register");
        });
        menuBar.add(registerButton);

        // 회원 수정 버튼
        JButton modifyButton = new JButton("회원 수정");
        modifyButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Modify");
        });
        menuBar.add(modifyButton);

        // 회원 삭제 버튼
        JButton deleteButton = new JButton("회원 삭제");
        deleteButton.addActionListener(e -> {
            // TODO: 회원 삭제 기능을 수행하는 코드를 작성하세요.
        });
        menuBar.add(deleteButton);

        // 회원 조회 버튼
        JButton searchButton = new JButton("회원 조회");
        searchButton.addActionListener(e -> {
            // TODO: 회원 조회 기능을 수행하는 코드를 작성하세요.
        });
        menuBar.add(searchButton);

        return menuBar;
    }

    private JPanel createModifyPanel() {
        fields = new JTextField[labelTexts.length];
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setBounds(15, 6, 450, 185);
        fieldPanel.setLayout(new GridLayout(4, 2, 5, 5));
        fieldPanel.setBorder(
            BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("회원수정"), BorderFactory.createEmptyBorder(5, 5, 5, 5)
            )
        );

        // init
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new JTextField();
            JLabel jLabel = new JLabel(labelTexts[i], SwingConstants.LEFT);
            fieldPanel.add(jLabel);
            fieldPanel.add(fields[i]);
        }

        JButton modifyButton = new JButton("수정");
        modifyButton.setBounds(15, 186, 450, 40);
        modifyButton.addActionListener(e -> {
            // TODO: 회원 정보를 수정하는 코드를 작성하세요.
        });

        jPanel.add(fieldPanel);
        jPanel.add(modifyButton);

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
}