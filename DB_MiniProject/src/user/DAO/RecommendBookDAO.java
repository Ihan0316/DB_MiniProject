package user.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.RECOMMENDBOOKS;

public class RecommendBookDAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";

	public RecommendBookDAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//희망도서 목록
	public Object[][] getRcmBookList(String userId) {
	    List<RECOMMENDBOOKS> result = new ArrayList<RECOMMENDBOOKS>();
	    Object[][] rowData = null;
	    String query = "SELECT * FROM RECOMMENDBOOKS WHERE userid = ? ORDER BY redate DESC";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	        pstmt.setString(1, userId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                RECOMMENDBOOKS dto = new RECOMMENDBOOKS();
	                dto.setRecommendID(rs.getInt("RECOMMENDID"));
	                dto.setUserID(rs.getString("USERID"));
	                dto.setBookName(rs.getString("BOOKNAME"));
	                dto.setWriter(rs.getString("WRITER"));
	                dto.setPublisher(rs.getString("PUBLISHER"));
	                dto.setPubDate(rs.getDate("PUBDATE"));
	                dto.setReDate(rs.getDate("REDATE"));
	                dto.setCompleteYN("Y".equals(rs.getString("COMPLETEYN")) ? "완료" : "진행중");
	                
	                result.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // List에서 Object[][]로 변환
	    rowData = new Object[result.size()][8];
	    for (int i = 0; i < result.size(); i++) {
	        RECOMMENDBOOKS dto = result.get(i);
	        rowData[i][0] = dto.getRecommendID();
	        rowData[i][1] = dto.getUserID();
	        rowData[i][2] = dto.getBookName();
	        rowData[i][3] = dto.getWriter();
	        rowData[i][4] = dto.getPublisher();
	        rowData[i][5] = dto.getPubDate();
	        rowData[i][6] = dto.getReDate();
	        rowData[i][7] = dto.getCompleteYN();
	    }

	    return rowData;
	}
	
	//희망도서 신청
	public Integer insertRcmBook(RECOMMENDBOOKS book) {
	    int result = 0;
	    String query = "INSERT INTO RECOMMENDBOOKS (RECOMMENDID, USERID, BOOKNAME, WRITER, PUBLISHER, PUBDATE, REDATE, COMPLETEYN) " +
	                   "VALUES (rcmbook_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, 'N')";
	    
	    try (
	    	Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	        pstmt.setString(1, book.getUserID());
	        pstmt.setString(2, book.getBookName());
	        pstmt.setString(3, book.getWriter());
	        pstmt.setString(4, book.getPublisher());
	        pstmt.setDate(5, new Date(book.getPubDate().getTime()));

	        result = pstmt.executeUpdate();
	        System.out.println(result + "개의 레코드가 저장");

	    } catch (SQLException e) {
	        e.printStackTrace();
	        result = 0;
	    }

	    return result;
	}
	
	//희망도서 신청 취소
	public int cancelRcmBook(int rcmId) {
		int result = 0;
	    String sql = "DELETE FROM RECOMMENDBOOKS WHERE recommendid = ?";
	    
	    try (
	    	Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, rcmId);
	        result = pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        result = 0;
	    }
	    return result;
	}
}
