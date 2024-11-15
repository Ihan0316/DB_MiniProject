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
	
	//회원탈퇴
	public Integer deleteUser(String userId) {
		int result = 0;
	    String query = "DELETE FROM USERS WHERE userid = ?";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	    	pstmt.setString(1, userId);
	    	result = pstmt.executeUpdate();
	        
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
	    String query = "SELECT * FROM REVIEWS WHERE userid = ? ORDER BY reviewdate DESC";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	        pstmt.setString(1, userId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	            	REVIEWS dto = new REVIEWS();
	                
	                
	                result.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // List에서 Object[][]로 변환
	    rowData = new Object[result.size()][6];
	    for (int i = 0; i < result.size(); i++) {
	    	REVIEWS dto = result.get(i);
	        rowData[i][0] = dto.getReviewID();
	        rowData[i][1] = dto.getUserID();
	        rowData[i][2] = dto.getBookID();
	        rowData[i][3] = dto.getScore();
	        rowData[i][4] = dto.getReview();
	        rowData[i][5] = dto.getReviewDate();
	    }

	    return rowData;
	}
	
	//리뷰삭제
	public Integer deleteReview(String userId, String reviewId) {
		int result = 0;
	    String query = "DELETE FROM REVIEWS WHERE userid = ? AND reviewid = ?";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	    	pstmt.setString(1, userId);
	    	pstmt.setString(2, reviewId);
	    	result = pstmt.executeUpdate();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;
	    }

	    return result;
	}
}
