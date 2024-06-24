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
import java.util.List;

public class MemberView extends JFrame implements MemberListener {
    private final String[] labelTexts = {"이메일", "이름", "전화번호", "생년원일"};
    private JTextField[] registerFields, modifyFields;
    private JTextField emailField;
    private JButton regButton, modifyButton, deleteButton, searchButton;
    private JTable jTable, searchResultTable;
    private DefaultTableModel defaultTableModel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    public static final Dimension SIZE = new Dimension(1100, 500);


    public MemberView(String title) {
        super(title);
        setLayout(new GridBagLayout());

        // CardLayout을 사용하여 createLeftPanel, createModifyPanel, createDeletePanel, createSearchPanel을 하나의 패널에 추가
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createLeftPanel(), "Register");  // 회원 가입 패널 추가
        contentPanel.add(createModifyPanel(), "Modify");  // 회원 수정 패널 추가
        contentPanel.add(createDeletePanel(), "Delete");  // 회원 삭제 패널 추가
        contentPanel.add(createSearchPanel(), "Search");  // 회원 조회 패널 추가

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
        add(contentPanel, gbc);  // contentPanel을 추가

        // 오른쪽 패널
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1;  // 가로 비율 설정
        gbc.weighty = 1;
        add(createRightPanel(), gbc);

        MemberController.getInstance().addMemberListener(this);
        loadMembers();
        registerListeners();
        modifyMember();
        deleteMember();
    }

    // 회원 등록 누르면 발생되는 이벤트
    private void registerListeners() {
        regButton.addActionListener(e -> {
            Member member = new Member(
                    registerFields[0].getText(), // 이메일
                    registerFields[1].getText(), // 이름
                    registerFields[2].getText(), // 전화번호
                    Util.strToLocalDate(registerFields[3].getText()) // 생년원일
            );
            MemberController.getInstance().save(member);
        });
    }

    // 회원 수정하기 버튼을 누르면 발생하는 이벤트
    private void modifyMember() {
        modifyButton.addActionListener(e -> {
            int selectedRow = jTable.getSelectedRow();
            if (selectedRow != -1) {
                // 테이블에서 선택된 행의 ID를 가져옵니다.
                Long id = Long.valueOf((String) jTable.getValueAt(selectedRow, 0));
                Member member = new Member(
                        id, // ID
                        modifyFields[1].getText(), // 이름
                        Util.strToLocalDate(modifyFields[3].getText()) // 생년원일
                );
                MemberController.getInstance().modify(member);
                refreshMemberList();
            } else {
                JOptionPane.showMessageDialog(this, "수정할 회원을 선택해주세요.");
            }
            clearMemberFields();
        });
    }
    private void deleteMember() {
        deleteButton.addActionListener(e -> {
            new Thread(() -> {
                String email = emailField.getText();
                if (email.isEmpty()) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "이메일을 입력해주세요."));
                } else {
                    Member member = MemberController.getInstance().findByEmail(email);
                    if (member != null) {
                        MemberController.getInstance().delete(member);
                        refreshMemberList();  // 회원 목록 새로고침
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "회원이 삭제되었습니다."));
                    } else {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "해당 이메일을 가진 회원이 없습니다."));
                    }
                }
            }).start();
        });
    }

    private void searchMember() {
        searchButton.addActionListener(e -> {
            String email = emailField.getText();
            if (email.isEmpty()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "이메일을 입력해주세요."));
            } else {
                Member member = MemberController.getInstance().findByEmail(email);
                if (member != null) {
                    MemberController.getInstance().delete(member);
                    refreshMemberList();  // 회원 목록 새로고침
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "회원이 삭제되었습니다."));
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "해당 이메일을 가진 회원이 없습니다."));
                }
            }
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
        jTable.setRowSelectionAllowed(true); // 변경된 부분: 행 선택 활성화

        jTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable.getSelectedRow() != -1) {
                int selectedRow = jTable.getSelectedRow();
                for (int i = 0; i < modifyFields.length; i++) {
                    modifyFields[i].setText((String) jTable.getValueAt(selectedRow, i + 1));
                }
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
        registerFields = new JTextField[labelTexts.length];
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
        for (int i = 0; i < registerFields.length; i++) {
            registerFields[i] = new JTextField();
            JLabel jLabel = new JLabel(labelTexts[i], SwingConstants.LEFT);
            fieldPanel.add(jLabel);
            fieldPanel.add(registerFields[i]);
        }

        regButton = new JButton("등록");
        regButton.setBounds(15, 186, 450, 40);

        jPanel.add(fieldPanel);
        jPanel.add(regButton);

        return jPanel;
    }


    private JPanel createModifyPanel() {
        modifyFields = new JTextField[labelTexts.length];
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
        for (int i = 0; i < modifyFields.length; i++) {
            modifyFields[i] = new JTextField();
            JLabel jLabel = new JLabel(labelTexts[i], SwingConstants.LEFT);
            fieldPanel.add(jLabel);
            fieldPanel.add(modifyFields[i]);

            if (i == 0 || i == 2) {
                modifyFields[i].setEnabled(false);
            }
        }

        modifyButton = new JButton("수정");
        modifyButton.setBounds(15, 186, 450, 40);

        jPanel.add(fieldPanel);
        jPanel.add(modifyButton);

        return jPanel;
    }

    private JPanel createDeletePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);

        JLabel emailLabel = new JLabel("이메일", SwingConstants.LEFT);
        emailLabel.setBounds(15, 6, 450, 25);
        jPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(15, 31, 450, 25);
        jPanel.add(emailField);

        deleteButton = new JButton("삭제");
        deleteButton.setBounds(15, 56, 450, 40);

        jPanel.add(deleteButton);

        return jPanel;
    }

    private JPanel createSearchPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);

        JLabel emailLabel = new JLabel("이메일", SwingConstants.LEFT);
        emailLabel.setBounds(15, 6, 450, 25);
        jPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(15, 31, 450, 25);
        jPanel.add(emailField);

        searchButton = new JButton("조회");
        searchButton.setBounds(15, 56, 450, 40);

        jPanel.add(searchButton);

        // 조회 결과를 보여주는 테이블
        searchResultTable = new JTable();
        searchResultTable.setBounds(15, 96, 450, 200);
        jPanel.add(searchResultTable);

        return jPanel;
    }

    public JPanel createLeftMenuBar() {
        JPanel menuBar = new JPanel();
        menuBar.setLayout(new GridLayout(4, 1));  // 4개의 버튼을 수직으로 배치

        // 회원 등록 버튼
        JButton registerButton = new JButton("회원 등록");
        registerButton.addActionListener(e -> {
            clearMemberFields();
            cardLayout.show(contentPanel, "Register");
        });
        menuBar.add(registerButton);

        // 회원 수정 버튼
        JButton modifyButton = new JButton("회원 수정");
        modifyButton.addActionListener(e -> {
            clearMemberFields();
            cardLayout.show(contentPanel, "Modify");
        });
        menuBar.add(modifyButton);

        // 회원 삭제 버튼
        JButton deleteButton = new JButton("회원 삭제");
        deleteButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Delete");  // 회원 삭제 패널을 보이도록 함
        });
        menuBar.add(deleteButton);

        // 회원 조회 버튼
        JButton searchButton = new JButton("회원 조회");
        searchButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Search");  // 회원 조회 패널을 보이도록 함
        });
        menuBar.add(searchButton);

        return menuBar;
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

    @Override
    public void modify(MemberEvent memberEvent) {
        Member member = (Member) memberEvent.getSource();
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            if (defaultTableModel.getValueAt(i, 1).equals(member.getEmail())) {
                defaultTableModel.setValueAt(member.getName(), i, 2);
                defaultTableModel.setValueAt(member.getBirth().toString(), i, 4);
                break;
            }
        }
    }

    @Override
    public void delete(MemberEvent memberEvent) {
        Member member = (Member) memberEvent.getSource();
        MemberController.getInstance().delete(member);
        refreshMemberList();
    }

    private void clearMemberFields() {
        // 현재 보이는 패널의 이름을 가져옴
        String currentPanel = null;
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                currentPanel = ((JPanel) comp).getName();
                break;
            }
        }

        // 각 패널의 필드 초기화
        if ("Register".equals(currentPanel)) {
            Arrays.stream(registerFields).forEach(field -> field.setText(""));
        } else if ("Modify".equals(currentPanel)) {
            Arrays.stream(modifyFields).forEach(field -> field.setText(""));
        } else if ("Delete".equals(currentPanel)) {
            emailField.setText("");
        }
    }

    public void refreshMemberList() {
        try {
            // 데이터베이스에서 모든 회원 정보를 다시 가져옵니다.
            List<Member> members = MemberController.getInstance().allMember();

            // 테이블의 모든 행을 제거합니다.
            defaultTableModel.setRowCount(0);

            // 새로운 회원 정보를 테이블에 추가합니다.
            for (Member member : members) {
                defaultTableModel.insertRow(0, member.toArray());
            }

            // 테이블의 레이아웃과 그래픽을 갱신합니다.
            jTable.revalidate();
            jTable.repaint();
        } catch (SQLException e) {
            // 데이터베이스 에러 처리
        }
    }
}