package user.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import DTO.BOOKS;

public class LSH_DAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";

	public LSH_DAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Object[][] searchBooks(String category, String name) {
	    ArrayList<BOOKS> list = new ArrayList<>();
	    Object[][] rowData = null;
	    
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String query;
	    
	    System.out.println("category="+category+",name="+name);
	    try {
	        con = DriverManager.getConnection(url, userid, passwd);

	        // 카테고리와 이름에 따른 조건 설정
	        if (category.equals("전체")) {
	            if (name.isEmpty()) {
	                query = "SELECT * FROM books";
	                pstmt = con.prepareStatement(query);
	            } else {
	                query = "SELECT * FROM BOOKS WHERE UPPER(BOOKNAME) LIKE ?";
	                pstmt = con.prepareStatement(query);
	                pstmt.setString(1, "%" + name.toUpperCase() + "%");
	            }
	        } else {
	            if (name.isEmpty()) {
	                query = "SELECT * FROM BOOKS WHERE BOOKCTG = ?";
	                pstmt = con.prepareStatement(query);
	                pstmt.setString(1, category);
	            } else {
	                query = "SELECT * FROM BOOKS WHERE UPPER(BOOKNAME) LIKE ? AND BOOKCTG = ?";
	                pstmt = con.prepareStatement(query);
	                pstmt.setString(1, "%" + name.toUpperCase() + "%");
	                pstmt.setString(2, category);
	            }
	        }
	        // 쿼리 실행
	        rs = pstmt.executeQuery();
	        System.out.println(query);
	        // 결과 처리
	        while (rs.next()) {
	            BOOKS dto = new BOOKS();
	            dto.setBookID(rs.getInt("bookid"));
	            dto.setBookName(rs.getString("bookname"));
	            dto.setWriter(rs.getString("writer"));
	            dto.setPublisher(rs.getString("publisher"));
	            dto.setPubDate(rs.getDate("pubdate"));
	            dto.setBookCTG(rs.getString("bookctg"));
	            dto.setStock(rs.getInt("stock"));
	            System.out.println("dto:"+dto.toString());
	            list.add(dto);
	        }

	        // List에서 Object[][]로 변환
	        rowData = new Object[list.size()][7];
	        for (int i = 0; i < list.size(); i++) {
	            BOOKS dto = list.get(i);
	            rowData[i][0] = dto.getBookID();
	            rowData[i][1] = dto.getBookName();
	            rowData[i][2] = dto.getWriter();
	            rowData[i][3] = dto.getPublisher();
	            rowData[i][4] = dto.getPubDate();
	            rowData[i][5] = dto.getBookCTG(); // 카테고리 필드 추가
	            rowData[i][6] = dto.getStock();
	           
	        }
	        System.out.println(rowData.length);

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (con != null) con.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return rowData;
	}
	public BOOKS bookdetail(int bookID) {

		BOOKS dto = new BOOKS();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			query = "SELECT * FROM books where bookid = ? ";
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, bookID);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto.setBookID(rs.getInt("bookid"));
	            dto.setBookName(rs.getString("bookname"));
	            dto.setWriter(rs.getString("writer"));
	            dto.setPublisher(rs.getString("publisher"));
	            dto.setPubDate(rs.getDate("pubdate"));
	            dto.setBookCTG(rs.getString("bookctg"));
	            dto.setStock(rs.getInt("stock"));
	            dto.setDescription(rs.getString("DESCIPTION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dto;
	}
	
}
