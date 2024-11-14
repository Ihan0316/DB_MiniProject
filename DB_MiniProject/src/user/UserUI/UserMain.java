package user.UserUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;



public class UserMain extends JFrame {

	
	public void userMainWindow() {
		setTitle("3x2 버튼 배열 - 패딩 추가");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        // 내부 패널 생성 및 GridLayout 설정 (3x2 배열, 버튼 간격 10px)
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
        outerPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); // 상, 좌, 하, 우 20px 여백 추가
        outerPanel.add(gridPanel, BorderLayout.CENTER); // 내부 패널 추가

        // 프레임에 외부 패널 추가
        add(outerPanel);
        
        setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(() -> {
            UserMain mainFrame = new UserMain();
            mainFrame.userMainWindow();
        });
	}

}
