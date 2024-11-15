package user.UserUI;

import DTO.USERS;
import user.DAO.UserInfoDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

public class UserInfo extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JPasswordField userPasswordField;
    private JTextField userNameField;
    private JTextField userPhoneField;
    private JLabel userIdLabel;
    private JLabel userRegDateLabel;
    private JLabel userRentalYNLabel;
    private JLabel userDelayCountLabel;
    
    private String userId;
    private UserInfoDAO dao = new UserInfoDAO();

    public UserInfo(String userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // 내 정보 수정 폼
        JPanel infoPanel = createInfoPanel();
        // 리뷰 목록 테이블
        JPanel reviewPanel = getReviewPanel();

        // SplitPane 설정 (상단 패널과 하단 패널을 분리)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, reviewPanel);
        splitPane.setDividerLocation(300);  // 분할 위치 조정

        add(splitPane, BorderLayout.CENTER);

        loadUserInfo();
    }

    // 내 정보 수정 폼
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 사용자 ID
        JLabel userIdLabel = new JLabel("사용자 ID :");
        this.userIdLabel = new JLabel();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userIdLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9;
        panel.add(this.userIdLabel, gbc);

        // 비밀번호
        JLabel userPasswordLabel = new JLabel("비밀번호 :");
        userPasswordField = new JPasswordField();
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userPasswordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9;
        panel.add(userPasswordField, gbc);

        // 이름
        JLabel userNameLabel = new JLabel("이름 :");
        userNameField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userNameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9;
        panel.add(userNameField, gbc);

        // 전화번호
        JLabel userPhoneLabel = new JLabel("전화번호 :");
        userPhoneField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userPhoneLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.9;
        panel.add(userPhoneField, gbc);

        // 가입일
        JLabel userRegDateLabel = new JLabel("가입일 :");
        this.userRegDateLabel = new JLabel();
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userRegDateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.9;
        panel.add(this.userRegDateLabel, gbc);

        // 대여 가능 여부
        JLabel userRentalYNLabel = new JLabel("대여 가능 여부 :");
        this.userRentalYNLabel = new JLabel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userRentalYNLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.9;
        panel.add(this.userRentalYNLabel, gbc);

        // 연체 횟수
        JLabel userDelayCountLabel = new JLabel("연체 횟수 :");
        this.userDelayCountLabel = new JLabel();
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(userDelayCountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.9;
        panel.add(this.userDelayCountLabel, gbc);

        // 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton("정보 수정");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUserInfo();
            }
        });
        JButton deleteButton = new JButton("회원 탈퇴");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    // 내 정보 수정
    private void updateUserInfo() {
        String password = new String(userPasswordField.getPassword());
        String userName = userNameField.getText();
        String userPhone = userPhoneField.getText();

        if (password.isEmpty() || userName.isEmpty() || userPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.");
            return;
        }

        USERS user = new USERS();
        user.setUserID(userId);
        user.setPassword(password);
        user.setUserName(userName);
        user.setTel(userPhone);

        if (dao.updateUserInfo(user) > 0) {
            JOptionPane.showMessageDialog(this, "정보가 수정되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "정보 수정에 실패했습니다.");
        }
    }

    // 회원탈퇴
    private void deleteUser() {
        int confirm = JOptionPane.showConfirmDialog(this, "정말로 탈퇴하시겠습니까?", 
                "회원 탈퇴", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteUser(userId) > 0) {
                JOptionPane.showMessageDialog(this, "회원 탈퇴가 완료되었습니다.");
                // 탈퇴 후 UI나 메인페이지로
            } else {
                JOptionPane.showMessageDialog(this, "회원 탈퇴에 실패했습니다.");
            }
        }
    }

    // 내 정보 로드
    private void loadUserInfo() {
        USERS user = dao.getUserInfo(userId);
        if (user != null) {
            userPasswordField.setText(user.getPassword());
            userIdLabel.setText(user.getUserID());
            userNameField.setText(user.getUserName());
            userPhoneField.setText(user.getTel());
            userRegDateLabel.setText(user.getRegdate().toString());
            userRentalYNLabel.setText(user.getRentalYN());
            userDelayCountLabel.setText(String.valueOf(user.getDelayCount()));
        }
    }
    
    // 리뷰 목록 테이블
    private JPanel getReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"리뷰번호", "ID", "도서번호", "점수", "리뷰 내용", "작성일"};
        Object[][] reviews = dao.getReviewList(userId);
        tableModel = new DefaultTableModel(reviews, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가
            }
        };

        table = new JTable(tableModel);

        // ID 컬럼 숨기기
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // "리뷰 삭제" 버튼 추가
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteReviewButton = new JButton("리뷰 삭제");
        deleteReviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteReview();
            }
        });
        
        buttonPanel.add(deleteReviewButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        return panel;
    }

    // 선택된 리뷰 삭제
    private void deleteReview() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 리뷰를 선택하세요.");
            return;
        }

        int reviewId = (int) table.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "이 리뷰를 삭제하시겠습니까?", 
                "리뷰 삭제", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteReview(userId, reviewId) > 0) {
                JOptionPane.showMessageDialog(this, "리뷰가 삭제되었습니다.");
                // 테이블 갱신
                loadReviewList();
            } else {
                JOptionPane.showMessageDialog(this, "리뷰 삭제에 실패했습니다.");
            }
        }
    }

    // 리뷰 목록 갱신
    private void loadReviewList() {
        Object[][] reviews = dao.getReviewList(userId);
        tableModel.setDataVector(reviews, new String[]{"리뷰번호", "ID", "도서번호", "점수", "리뷰 내용", "작성일"});
    }
}
