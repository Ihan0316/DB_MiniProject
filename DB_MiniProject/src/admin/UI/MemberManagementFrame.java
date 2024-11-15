package admin.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MemberManagementFrame extends JFrame {
    private JTextField userIdField, userNameField, telField, regDateField;
    private JPasswordField passwordField; // 새 비밀번호 필드를 제외한 기존 비밀번호 입력 필드만 사용
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

        JLabel descriptionLabel = new JLabel("도서관 회원 정보를 관리합니다.");

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

        // 버튼 패널을 추가하고, 가운데 정렬을 위해 GridBagLayout을 사용
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 가운데 정렬
        JButton saveButton = new JButton("저장");
        JButton addButton = new JButton("추가");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        buttonPanel.add(saveButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;  // 가운데 정렬
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);

        // 버튼 이벤트 리스너
        saveButton.addActionListener(e -> handleSaveOrAdd(true));
        addButton.addActionListener(e -> handleSaveOrAdd(false));
        editButton.addActionListener(e -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());
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
            // 필드 옆에 형식 안내 텍스트 추가
            JLabel formatLabel = new JLabel(extraText);
            gbc.gridx = 3;  // 텍스트를 필드 옆에 배치
            gbc.gridwidth = 1;
            panel.add(formatLabel, gbc);
        }
    }

    private boolean validateFields() {
        if (userIdField.getText().isEmpty() || userNameField.getText().isEmpty() || telField.getText().isEmpty()
                || regDateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "모든 내용을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int delayCount = (int) delayCountSpinner.getValue();
        if (delayCount < 0 || delayCount > 50) {
            JOptionPane.showMessageDialog(null, "연체 횟수는 0에서 50까지 입력할 수 있습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String regDate = regDateField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(regDate);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "가입일 형식이 잘못되었습니다. (yyyy-mm-dd)", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isMemberIdDuplicate(String memberId) {
        return memberMap.containsKey(memberId);
    }

    private void clearFields() {
        userIdField.setText("");
        passwordField.setText("");
        userNameField.setText("");
        telField.setText("");
        regDateField.setText("");
        delayCountSpinner.setValue(0);
        yesRadioButton.setSelected(false);
        noRadioButton.setSelected(false);
    }

    private void handleSaveOrAdd(boolean isSave) {
        if (validateFields()) {
            String memberId = userIdField.getText();

            // 저장 또는 추가일 경우 중복 ID 확인
            if (isSave && isMemberIdDuplicate(memberId)) {
                JOptionPane.showMessageDialog(null, "회원 정보가 중복되어 저장되지 않았습니다.", "중복 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 비밀번호 입력 확인
            char[] password = passwordField.getPassword();
            if (password.length == 0) {
                JOptionPane.showMessageDialog(null, "비밀번호를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String passwordString = new String(password);

            int delayCount = (int) delayCountSpinner.getValue();
            Member newMember = new Member(memberId, passwordString, userNameField.getText(), telField.getText(),
                    regDateField.getText(), yesRadioButton.isSelected(), delayCount);

            if (isSave) {
                // 새로 추가할 경우, 기존 회원 ID가 없으면 추가
                memberMap.put(memberId, newMember);
                JOptionPane.showMessageDialog(null, "회원 정보가 저장되었습니다.");
            } else {
                // 수정일 경우, 해당 ID의 정보를 수정
                memberMap.put(memberId, newMember);
                JOptionPane.showMessageDialog(null, "회원 정보가 수정되었습니다.");
            }

            printFields(newMember);
            clearFields();
        }
    }

    private void handleEdit() {
        String memberId = userIdField.getText();
        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "회원 ID를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } else if (!memberMap.containsKey(memberId)) {
            JOptionPane.showMessageDialog(null, "해당 회원 정보가 존재하지 않아 수정할 수 없습니다.", "오류", JOptionPane.WARNING_MESSAGE);
        } else {
            Member member = memberMap.get(memberId);
            userNameField.setText(member.getName());
            telField.setText(member.getPhone());
            regDateField.setText(member.getJoinDate());
            delayCountSpinner.setValue(member.getOverdueCount());
            if (member.isLoanEligible()) {
                yesRadioButton.setSelected(true);
            } else {
                noRadioButton.setSelected(true);
            }

            int confirm = JOptionPane.showConfirmDialog(null, "회원 정보를 수정하시겠습니까?", "회원 수정 확인",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 비밀번호 수정 없이 다른 정보만 수정
                member.setName(userNameField.getText());
                member.setPhone(telField.getText());
                member.setJoinDate(regDateField.getText());
                member.setLoanEligibility(yesRadioButton.isSelected());
                member.setOverdueCount((int) delayCountSpinner.getValue());

                // 회원 정보 수정 후 다시 업데이트
                memberMap.put(memberId, member);  // 수정된 정보를 다시 맵에 업데이트

                JOptionPane.showMessageDialog(null, "회원 정보가 수정되었습니다.");
                printFields(member);
            }
        }
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
                Member member = memberMap.remove(memberId);
                JOptionPane.showMessageDialog(null, "회원 정보가 삭제되었습니다.");
                clearFields();
                System.out.println("삭제된 회원 정보:");
                printFields(member);
            }
        }
    }

    private void printFields(Member member) {
        System.out.println("회원 정보:");
        System.out.println("회원 ID: " + member.getMemberId());
        System.out.println("이름: " + member.getName());
        System.out.println("연락처: " + member.getPhone());
        System.out.println("가입일: " + member.getJoinDate());
        System.out.println("대여 가능 여부: " + (member.isLoanEligible() ? "Yes" : "No"));
        System.out.println("연체 횟수: " + member.getOverdueCount());
        System.out.println("----------------------------");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemberManagementFrame().setVisible(true));
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

    public String getPassword() {
        return password;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public void setLoanEligibility(boolean loanEligible) {
        this.loanEligible = loanEligible;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }
}













