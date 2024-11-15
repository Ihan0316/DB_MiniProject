package admin.DAO;

import DTO.REVIEWS;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {

	// 데이터베이스 연결 정보
	private static Connection conn;
	private static PreparedStatement pstmt;
	private static ResultSet rs;

	// DB 연결 메서드
	private static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                String url = "jdbc:oracle:thin:@localhost:1521:xe"; // DB URL
                String user = "scott"; // DB 사용자명
                String password = "tiger"; // DB 비밀번호
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("DB 연결 오류");
            }
        }
        return conn;
    }


	// 모든 리뷰 목록 조회 메서드 (인스턴스 메서드로 변경)
	public static List<REVIEWS> getAllReviews() {
	    List<REVIEWS> reviews = new ArrayList<>();
	    String sql = "SELECT r.reviewID, r.userID, r.bookID, b.bookName, r.score, r.review, r.reviewDate " +
	                 "FROM REVIEWS r " +
	                 "JOIN BOOKS b ON r.bookID = b.bookID";
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while (rs.next()) {
	            REVIEWS review = new REVIEWS();
	            review.setReviewID(rs.getInt("reviewID"));
	            review.setUserID(rs.getString("userID"));
	            review.setBookID(rs.getInt("bookID"));
	            review.setBookName(rs.getString("bookName"));  // 책 이름 설정
	            review.setScore(rs.getInt("score"));
	            review.setReview(rs.getString("review"));
	            review.setReviewDate(rs.getDate("reviewDate"));
	            reviews.add(review);
	        }
	        System.out.println("조회된 리뷰 수: " + reviews.size());
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        closeResources();
	    }
	    return reviews;
	}


	// 리소스 해제 메서드 (try-catch 구문 간소화)
	private static void closeResources() {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

      //리뷰삭제메서드
	public void deleteReview(int reviewID) {
		String sql = "DELETE FROM REVIEWS WHERE reviewID = ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reviewID);
			pstmt.executeUpdate(); // 실행
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
	}

	public REVIEWS getReviewById(int i) {
		// TODO Auto-generated method stub
		return null;
	}
}
