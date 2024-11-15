package admin.UI;

import javax.swing.*;
import DTO.REVIEWS;
import admin.DAO.ReviewDao;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class ADMIN_UI extends JFrame {

    public ADMIN_UI() {
        setTitle("도서 관리 프로그램");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2)); // 3행 2열의 버튼 레이아웃

        // 각 버튼 생성
        JButton bookInfoButton = new JButton("도서 정보");
        JButton userInfoButton = new JButton("회원 정보");
        JButton reviewButton = new JButton("리뷰");
        JButton rentalButton = new JButton("예약&대여");
        JButton recommendBookButton = new JButton("희망 도서 신청");
        JButton categoryButton = new JButton("카테고리 관리");

        // 버튼 이벤트 핸들러 추가
        bookInfoButton.addActionListener(e -> showBookInfo());
        userInfoButton.addActionListener(e -> showUserInfo());
        reviewButton.addActionListener(e -> showReviews());
        rentalButton.addActionListener(e -> showRentals());
        recommendBookButton.addActionListener(e -> showRecommendBooks());
        categoryButton.addActionListener(e -> showCategories());

        // 버튼을 프레임에 추가
        add(bookInfoButton);
        add(userInfoButton);
        add(reviewButton);
        add(rentalButton);
        add(recommendBookButton);
        add(categoryButton);

        setVisible(true);
    }

    // 버튼 클릭 시 실행될 메서드 정의 (기능 별로 세부 구현 필요)
    private void showBookInfo() {
        // 도서 정보를 보여주는 로직 작성
        JOptionPane.showMessageDialog(this, "도서 정보를 조회합니다.");
    }

    private void showUserInfo() {
        // 회원 정보를 보여주는 로직 작성
        JOptionPane.showMessageDialog(this, "회원 정보를 조회합니다.");
    }

    private void showReviews() {
        // 리뷰를 보여주는 로직 작성
    	openAdminReviewUi();
    
    	List<REVIEWS> reviews = ReviewDao.getAllReviews();
    	StringBuilder reviewText = new StringBuilder("리뷰목록:\n");
    	
    	for (REVIEWS review : reviews) {
    		reviewText.append("리뷰 ID: ").append(review.getReviewID())
    		          .append(", 사용자 ID: ").append(review.getUserID())
                      .append(", 책 ID: ").append(review.getBookID())
                      .append(", 점수: ").append(review.getScore())
                      .append(", 내용: ").append(review.getReview())
                      .append(", 날짜: ").append(review.getReviewDate())
                      .append("\n");
    	}
    }

    private void openAdminReviewUi() {
		new AdminReviewUi();
		
	}

	private void showRentals() {
        // 예약 및 대여 정보를 보여주는 로직 작성
        JOptionPane.showMessageDialog(this, "예약 및 대여 정보를 조회합니다.");
    }

    private void showRecommendBooks() {
        // 희망 도서 신청 정보를 보여주는 로직 작성
        JOptionPane.showMessageDialog(this, "희망 도서 신청 정보를 조회합니다.");
    }

    private void showCategories() {
        // 카테고리 정보를 보여주는 로직 작성
    	new AdminCategoryUi();//카테고리 창 열기
    }

    public static void main(String[] args) {
        // 데이터베이스 연결 및 DAO 인스턴스 생성
        SwingUtilities.invokeLater(() -> new ADMIN_UI());
    }
}
