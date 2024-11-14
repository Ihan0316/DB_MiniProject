package user.UserUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserMain extends JFrame {
    
    public void userMainWindow() {
        setTitle("도서 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        // 내부 패널 생성 및 GridLayout 설정 (2x2 배열, 버튼 간격 30px)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        
        JButton button1 = new JButton("도서 목록");
        JButton button2 = new JButton("회원 정보");
        JButton button3 = new JButton("내 예약/대여");
        JButton button4 = new JButton("희망도서신청");
        
        Dimension buttonSize = new Dimension(10, 10);
        button1.setPreferredSize(buttonSize);
        button2.setPreferredSize(buttonSize);
        button3.setPreferredSize(buttonSize);
        button4.setPreferredSize(buttonSize);
        
        gridPanel.add(button1);
        gridPanel.add(button2);
        gridPanel.add(button3);
        gridPanel.add(button4);

        // 외부 패널 생성 및 여백 추가
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); // 상, 좌, 하, 우 50px 여백 추가
        outerPanel.add(gridPanel, BorderLayout.CENTER); // 내부 패널 추가

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRecommendBookDialog();
            }
        });

        // 프레임에 외부 패널 추가
        add(outerPanel);
        
        setVisible(true);
    }

    // 희망도서신청
    private void openRecommendBookDialog() {
        JDialog dialog = new JDialog(this, "희망도서 신청", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        RecommendBook recommendBookMain = new RecommendBook();
        dialog.add(recommendBookMain);
        dialog.setVisible(true);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserMain mainFrame = new UserMain();
            mainFrame.userMainWindow();
        });
    }
}
