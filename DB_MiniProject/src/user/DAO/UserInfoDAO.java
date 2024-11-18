package user.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.RECOMMENDBOOKS;
import DTO.REVIEWS;
import DTO.USERS;

public class UserInfoDAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";

	public UserInfoDAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//내 정보 가져오기
	public USERS getUserInfo(String userId) {
		USERS result = new USERS();
	    String query = "SELECT * FROM USERS WHERE userid = ?";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	        pstmt.setString(1, userId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	            	result.setUserID(rs.getString("USERID"));
	            	result.setPassword(rs.getString("PASSWORD"));
	            	result.setUserName(rs.getString("USERNAME"));
	            	result.setTel(rs.getString("TEL"));
	            	result.setRegdate(rs.getDate("REGDATE"));
	            	result.setRentalYN("Y".equals(rs.getString("RENTALYN")) ? "대여 가능" : "대여 불가");
	            	result.setDelayCount(rs.getInt("DELAYCOUNT"));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}
	
	//내 정보 수정
	public Integer updateUserInfo(USERS updInfo) {
		int result = 0;
	    String query = "UPDATE USERS SET password = ?, username = ?, tel = ? WHERE userid = ?";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	    	pstmt.setString(1, updInfo.getPassword());
	    	pstmt.setString(2, updInfo.getUserName());
	    	pstmt.setString(3, updInfo.getTel());
	    	pstmt.setString(4, updInfo.getUserID());
	    	result = pstmt.executeUpdate();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;
	    }

	    return result;
	}
	
	// 회원탈퇴
	public Integer deleteUser(String userId) {
	    int result = 0;

	    // FK 걸린 테이블 먼저 삭제 후 회원삭제
	    String deleteReviews = "DELETE FROM REVIEWS WHERE userid = ?";
	    String deleteRentals = "DELETE FROM RENTALS WHERE userid = ?";
	    String deleteReservations = "DELETE FROM RESERVATIONS WHERE userid = ?";
	    String deleteRecommendBooks = "DELETE FROM RECOMMENDBOOKS WHERE userid = ?";
	    String deleteUserQuery = "DELETE FROM USERS WHERE userid = ?";

	    try (Connection con = DriverManager.getConnection(url, userid, passwd);
	    ) {
	        con.setAutoCommit(false);

	        try (
	        	PreparedStatement pstmt1 = con.prepareStatement(deleteReviews);
	            PreparedStatement pstmt2 = con.prepareStatement(deleteRentals);
	            PreparedStatement pstmt3 = con.prepareStatement(deleteReservations);
	            PreparedStatement pstmt4 = con.prepareStatement(deleteRecommendBooks);
	            PreparedStatement pstmt5 = con.prepareStatement(deleteUserQuery)) {
	            
	            // 리뷰 삭제
	            pstmt1.setString(1, userId);
	            pstmt1.executeUpdate();
	            // 대여 정보 삭제
	            pstmt2.setString(1, userId);
	            pstmt2.executeUpdate();
	            // 예약 정보 삭제
	            pstmt3.setString(1, userId);
	            pstmt3.executeUpdate();
	            // 희망도서신청 삭제
	            pstmt4.setString(1, userId);
	            pstmt4.executeUpdate();
	            // 회원 삭제
	            pstmt5.setString(1, userId);
	            result = pstmt5.executeUpdate();

	            con.commit(); // 커밋
	            
	        } catch (Exception e) {
	            con.rollback(); // 예외 발생 시 롤백
	            e.printStackTrace();
	            result = 0;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;
	    }

	    return result;
	}

	
	//리뷰 목록
	public Object[][] getReviewList(String userId) {
	    List<REVIEWS> result = new ArrayList<REVIEWS>();
	    Object[][] rowData = null;
	    String query = "SELECT r.reviewid, r.userid, r.bookid, b.bookname, r.score, r.review, r.reviewdate FROM REVIEWS r "
	    				+ "JOIN BOOKS b ON r.bookid = b.bookid WHERE r.userid = ? ORDER BY r.reviewdate DESC";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	        pstmt.setString(1, userId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	            	REVIEWS dto = new REVIEWS();
	                dto.setReviewID(rs.getInt("REVIEWID"));
	                dto.setUserID(rs.getString("USERID"));
	                dto.setBookID(rs.getInt("BOOKID"));
	                dto.setBookName(rs.getString("BOOKNAME"));
	                dto.setScore(rs.getInt("SCORE"));
	                dto.setReview(rs.getString("REVIEW"));
	                dto.setReviewDate(rs.getDate("REVIEWDATE"));
	                
	                result.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // List에서 Object[][]로 변환
	    rowData = new Object[result.size()][7];
	    for (int i = 0; i < result.size(); i++) {
	    	REVIEWS dto = result.get(i);
	        rowData[i][0] = dto.getReviewID();
	        rowData[i][1] = dto.getUserID();
	        rowData[i][2] = dto.getBookID();
	        rowData[i][3] = dto.getBookName();
	        rowData[i][4] = dto.getScore();
	        rowData[i][5] = dto.getReview();
	        rowData[i][6] = dto.getReviewDate();
	    }

	    return rowData;
	}
	
	//리뷰삭제
	public Integer deleteReview(String userId, int reviewId) {
		int result = 0;
	    String query = "DELETE FROM REVIEWS WHERE userid = ? AND reviewid = ?";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	    	pstmt.setString(1, userId);
	    	pstmt.setInt(2, reviewId);
	    	result = pstmt.executeUpdate();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;
	    }

	    return result;
	}
}
