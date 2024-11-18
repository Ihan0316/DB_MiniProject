package admin.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.BOOKS;
import DTO.CATEGORIES;

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
	    List<BOOKS> result = new ArrayList<BOOKS>();
	    Object[][] rowData = null;
	    String query = "SELECT b.bookid, b.bookname, b.writer, b.publisher, b.pubdate, c.categoryName AS bookCTG, b.description, b.stock "
	    		+ "FROM BOOKS b JOIN CATEGORIES c ON b.bookCTG = c.categoryId ORDER BY b.bookid DESC";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	            	BOOKS dto = new BOOKS();
	                dto.setBookID(rs.getInt("BOOKID"));
	                dto.setBookName(rs.getString("BOOKNAME"));
	                dto.setWriter(rs.getString("WRITER"));
	                dto.setPublisher(rs.getString("PUBLISHER"));
	                dto.setPubDate(rs.getDate("PUBDATE"));
	                dto.setBookCTG(rs.getString("BOOKCTG"));
	                dto.setDescription(rs.getString("DESCRIPTION"));
	                dto.setStock(rs.getInt("STOCK"));
	                
	                result.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // List에서 Object[][]로 변환
	    rowData = new Object[result.size()][8];
	    for (int i = 0; i < result.size(); i++) {
	    	BOOKS dto = result.get(i);
	        rowData[i][0] = dto.getBookID();
	        rowData[i][1] = dto.getBookName();
	        rowData[i][2] = dto.getWriter();
	        rowData[i][3] = dto.getPublisher();
	        rowData[i][4] = dto.getPubDate();
	        rowData[i][5] = dto.getBookCTG();
	        rowData[i][6] = dto.getDescription();
	        rowData[i][7] = dto.getStock();
	    }

	    return rowData;
	}
	
	// 카테고리 목록
	public List<CATEGORIES> getBookCTG() {
		List<CATEGORIES> result = new ArrayList<CATEGORIES>();
	    String query = "SELECT * FROM CATEGORIES";

	    try (
	        Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	            	CATEGORIES dto = new CATEGORIES();
	                dto.setCategoryID(rs.getInt("CATEGORYID"));
	                dto.setCategoryName(rs.getString("CATEGORYNAME"));
	                dto.setDescription(rs.getString("DESCRIPTION"));
	                
	                result.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return result;
	}
	
	// 도서 추가
	public Integer insertBook(BOOKS book) {
	    int result = 0;
	    String query = "INSERT INTO BOOKS VALUES (bookid_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
	    
	    try (
	    	Connection con = DriverManager.getConnection(url, userid, passwd);
	        PreparedStatement pstmt = con.prepareStatement(query)
	    ) {
	        pstmt.setString(1, book.getBookName());
	        pstmt.setString(2, book.getWriter());
	        pstmt.setString(3, book.getPublisher());
	        pstmt.setDate(4, new Date(book.getPubDate().getTime()));
	        pstmt.setInt(5, Integer.parseInt(book.getBookCTG()));
	        pstmt.setString(6, book.getDescription());
	        pstmt.setInt(7, book.getStock());

	        result = pstmt.executeUpdate();
	        System.out.println(result + "개의 레코드가 저장");

	    } catch (SQLException e) {
	        e.printStackTrace();
	        result = 0;
	    }

	    return result;
	}
	
	// 도서 삭제
	public Integer deleteBook(int bookId) {
	    int result = 0;
	    String deleteReviewsSQL = "DELETE FROM REVIEWS WHERE bookID = ?";
	    String deleteReservationsSQL = "DELETE FROM RESERVATIONS WHERE bookID = ?";
	    String deleteRentalsSQL = "DELETE FROM RENTALS WHERE bookID = ?";
	    String deleteBookSQL = "DELETE FROM BOOKS WHERE bookID = ?";
	    
	    // 데이터베이스 연결 및 트랜잭션 처리
	    try (Connection con = DriverManager.getConnection(url, userid, passwd)) {
	        con.setAutoCommit(false); // 자동 커밋 비활성화 (트랜잭션 처리)
	        
	        // 1. REVIEWS 테이블에서 해당 도서의 리뷰 삭제
	        try (PreparedStatement pstmt = con.prepareStatement(deleteReviewsSQL)) {
	            pstmt.setInt(1, bookId);
	            pstmt.executeUpdate();
	        }
	        
	        // 2. RESERVATIONS 테이블에서 해당 도서의 예약 삭제
	        try (PreparedStatement pstmt = con.prepareStatement(deleteReservationsSQL)) {
	            pstmt.setInt(1, bookId);
	            pstmt.executeUpdate();
	        }
	        
	        // 3. RENTALS 테이블에서 해당 도서의 대여 삭제
	        try (PreparedStatement pstmt = con.prepareStatement(deleteRentalsSQL)) {
	            pstmt.setInt(1, bookId);
	            pstmt.executeUpdate();
	        }
	        
	        // 4. BOOKS 테이블에서 도서 삭제
	        try (PreparedStatement pstmt = con.prepareStatement(deleteBookSQL)) {
	            pstmt.setInt(1, bookId);
	            result = pstmt.executeUpdate();
	        }
	        
	        // 트랜잭션 커밋
	        con.commit();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        result = 0;
	        // 오류 발생 시 롤백
	        try (Connection con = DriverManager.getConnection(url, userid, passwd)) {
	            con.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    }
	    return result;
	}

}
