import javax.swing.*;

import admin.UI.ADMIN_UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import user.UserUI.UserMain; // UserMain 클래스 import 추가

public class Main extends JFrame {

    private JTextField idField;
    private JPasswordField passwordField;

    public Main() {
        setTitle("즐거운 도서 생활");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 150);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel);

        // 상단 패널: 아이디와 비밀번호 정보 입력
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        JLabel idLabel = new JLabel("ID:");
        idField = new JTextField(10);
        JLabel passwordLabel = new JLabel("PW:");
        passwordField = new JPasswordField(10);

        topPanel.add(idLabel);
        topPanel.add(idField);
        topPanel.add(passwordLabel);
        topPanel.add(passwordField);

        mainPanel.add(topPanel);

        // 로그인 및 회원가입 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("로그인");
        JButton signUpButton = new JButton("회원가입");
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        mainPanel.add(buttonPanel);

        // 로그인 버튼 액션
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = idField.getText();
                String password = new String(passwordField.getPassword());

                DAO dao = new DAO();
                if (dao.authenticateUser(userID, password)) {
                    JOptionPane.showMessageDialog(null, "로그인 성공!");
                    dispose(); // 현재 창 닫기
                    
                    if("admin".equals(userID)) {
                    	// Admin 창 열기
                    	ADMIN_UI adminPage = new ADMIN_UI();
                    	adminPage.setVisible(true);
                    } else {
                    	// UserMain 창 열기
                        UserMain homePage = new UserMain(userID);
                        homePage.setVisible(true);
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(null, "잘못된 정보입니다.");
                }
            }
        });

        // 회원가입 창 열기
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegistrationWindow(); 
            }
        });

        setVisible(true);
    }

    // 회원가입 창 클래스 정의
    public class RegistrationWindow extends JFrame {
        public RegistrationWindow() {
            setTitle("Sign Up");
            setSize(300, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // ID 필드
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("ID:"), gbc);

            gbc.gridx = 1;
            JTextField usernameField = new JTextField(8);
            panel.add(usernameField, gbc);

            // Password 필드
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Password:"), gbc);

            gbc.gridx = 1;
            JPasswordField passwordField = new JPasswordField(8);
            panel.add(passwordField, gbc);

            // User Name 필드
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("User Name:"), gbc);

            gbc.gridx = 1;
            JTextField userNameField = new JTextField(8);
            panel.add(userNameField, gbc);

            // Phone Number 필드
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(new JLabel("Phone Number:"), gbc);

            gbc.gridx = 1;
            JTextField phoneField = new JTextField(8);
            panel.add(phoneField, gbc);

            // Register 버튼
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            JButton registerButton = new JButton("회원가입");
            panel.add(registerButton, gbc);

            add(panel);
            setVisible(true);

            // 회원가입 버튼 액션 리스너
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String userID = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String userName = userNameField.getText();
                    String tel = phoneField.getText();

                    DAO dao = new DAO();
                    boolean isRegistered = dao.registerUser(userID, password, userName, tel);

                    if (isRegistered) {
                        JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
                        dispose(); // 회원가입 창 닫기
                    } else {
                        JOptionPane.showMessageDialog(null, "회원가입에 실패했습니다. 다시 시도해주세요.");
                    }
                }
            });
        }
    }
    
    public static void main(String[] args) {
        new Main(); 
    }
}
