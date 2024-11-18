package admin.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.REVIEWS;

public class BookManagementDAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";

	public BookManagementDAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// 도서 목록
	public Object[][] getBookList() {
	    List<REVIEWS> result = new ArrayList<REVIEWS>();
	    Object[][] rowData = null;
	    String query = "SELECT r.reviewid, r.userid, r.bookid, b.bookname, r.score, r.review, r.reviewdate FROM REVIEWS r "
	    				+ "JOIN BOOKS b ON r.bookid = b.bookid WHERE r.userid = ? ORDER BY r.reviewdate DESC";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {

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
}
