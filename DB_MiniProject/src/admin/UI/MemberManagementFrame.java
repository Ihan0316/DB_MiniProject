package admin.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MemberManagementFrame extends JFrame {
    private JTextField userIdField, userNameField, telField, regDateField;
    private JPasswordField passwordField;
    private JRadioButton yesRadioButton, noRadioButton;
    private ButtonGroup rentalYN;
    private JSpinner delayCountSpinner;
    private Map<String, Member> memberMap;

    public MemberManagementFrame() {
        setTitle("회원 정보 관리");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        memberMap = new HashMap<>();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 필드 초기화
        userIdField = new JTextField(15);
        passwordField = new JPasswordField(15);
        userNameField = new JTextField(15);
        telField = new JTextField(15);
        regDateField = new JTextField(15);

        SpinnerModel model = new SpinnerNumberModel(0, 0, 50, 1);
        delayCountSpinner = new JSpinner(model);

        yesRadioButton = new JRadioButton("Yes");
        noRadioButton = new JRadioButton("No");
        rentalYN = new ButtonGroup();
        rentalYN.add(yesRadioButton);
        rentalYN.add(noRadioButton);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 필드 추가
        addLabelAndField(panel, gbc, "회원 ID:", userIdField, 0, null);
        addLabelAndField(panel, gbc, "비밀번호:", passwordField, 1, null);
        addLabelAndField(panel, gbc, "이름:", userNameField, 2, null);
        addLabelAndField(panel, gbc, "연락처:", telField, 3, null);
        addLabelAndField(panel, gbc, "가입일:", regDateField, 4, "(yyyy-mm-dd)");

        JPanel loanEligibilityPanel = new JPanel();
        loanEligibilityPanel.add(yesRadioButton);
        loanEligibilityPanel.add(noRadioButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        panel.add(new JLabel("대여 가능 여부:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(loanEligibilityPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("연체 횟수:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(delayCountSpinner, gbc);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton deleteButton = new JButton("삭제");
        JButton listButton = new JButton("회원 목록");

        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);

        // 버튼 이벤트 리스너
        deleteButton.addActionListener(e -> handleDelete());
        listButton.addActionListener(e -> openMemberListFrame());
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int gridY, String extraText) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);

        if (extraText != null && !extraText.isEmpty()) {
            JLabel formatLabel = new JLabel(extraText);
            gbc.gridx = 3;
            gbc.gridwidth = 1;
            panel.add(formatLabel, gbc);
        }
    }

    private void openMemberListFrame() {
        new MemberListFrame(memberMap).setVisible(true);
    }

    private void handleDelete() {
        String memberId = userIdField.getText();
        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "회원 ID를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } else if (!memberMap.containsKey(memberId)) {
            JOptionPane.showMessageDialog(null, "회원 정보가 존재하지 않습니다.", "오류", JOptionPane.WARNING_MESSAGE);
        } else {
            int confirm = JOptionPane.showConfirmDialog(null, "해당 회원을 삭제하시겠습니까?", "회원 삭제 확인",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                memberMap.remove(memberId);
                JOptionPane.showMessageDialog(null, "회원 정보가 삭제되었습니다.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemberManagementFrame().setVisible(true));
    }
}

class MemberListFrame extends JFrame {
    public MemberListFrame(Map<String, Member> memberMap) {
        setTitle("회원 목록 보기");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"회원 ID", "이름", "연락처", "가입일", "대여 가능 여부", "연체 횟수"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Member member : memberMap.values()) {
            model.addRow(new Object[]{
                member.getMemberId(), member.getName(), member.getPhone(),
                member.getJoinDate(), member.isLoanEligible() ? "Yes" : "No",
                member.getOverdueCount()
            });
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table));
    }
}

class Member {
    private String memberId, password, name, phone, joinDate;
    private boolean loanEligible;
    private int overdueCount;

    public Member(String memberId, String password, String name, String phone, String joinDate, boolean loanEligible, int overdueCount) {
        this.memberId = memberId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.joinDate = joinDate;
        this.loanEligible = loanEligible;
        this.overdueCount = overdueCount;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public boolean isLoanEligible() {
        return loanEligible;
    }

    public int getOverdueCount() {
        return overdueCount;
    }
}














