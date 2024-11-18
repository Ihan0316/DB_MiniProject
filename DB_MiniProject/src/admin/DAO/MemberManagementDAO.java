package admin.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.REVIEWS;
import DTO.USERS;

public class MemberManagementDAO {
	private static Connection con;
	private static PreparedStatement pstmt;
	private static ResultSet rs;
	
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";

	public MemberManagementDAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<USERS> getAllUsers() {
		con = null;
		pstmt = null;
		rs = null;
		String query;
		List<USERS> users = new ArrayList<>();
		try {
			con = DriverManager.getConnection(url, userid, passwd);

			query = "SELECT * from users";
			pstmt = con.prepareStatement(query);
			// 쿼리 실행
			rs = pstmt.executeQuery();
			// 결과 처리
			while (rs.next()) {
				USERS dto = new USERS();
				dto.setUserID(rs.getString("userid"));
				dto.setUserName(rs.getString("username"));
				dto.setTel(rs.getString("tel"));
				dto.setRegdate(rs.getDate("regdate"));
				dto.setRentalYN(rs.getString("rentalyn"));
				dto.setDelayCount(rs.getInt("delaycount"));
				users.add(dto);
			}
		    } catch (SQLException e) {
		        e.printStackTrace();
		    } finally {
		        closeResources();
		    }
		    return users;
		}
	
	public void deleteUser(String userID) {
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
	            pstmt1.setString(1, userID);
	            pstmt1.executeUpdate();
	            // 대여 정보 삭제
	            pstmt2.setString(1, userID);
	            pstmt2.executeUpdate();
	            // 예약 정보 삭제
	            pstmt3.setString(1, userID);
	            pstmt3.executeUpdate();
	            // 희망도서신청 삭제
	            pstmt4.setString(1, userID);
	            pstmt4.executeUpdate();
	            // 회원 삭제
	            pstmt5.setString(1, userID);
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
	}
	
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
}
