package user.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DTO.BOOKS;
import DTO.BookDetailWrapper;
import DTO.CATEGORIES;
import DTO.RENTALS;
import DTO.RESERVATIONS;
import DTO.REVIEWS;
import DTO.USERS;

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

	public String userName(String userId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		String username = "";
		try {
			con = DriverManager.getConnection(url, userid, passwd);

			query = "SELECT username FROM users WHERE userid = ?";
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, userId);

			// 쿼리 실행
			rs = pstmt.executeQuery();
			// 결과 처리
			if (rs.next()) {
				USERS dto = new USERS();
				dto.setUserName(rs.getString("username"));
				username = dto.getUserName();
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
		return username;
	}

	public Object[][] searchBooks(String category, String name) {
		ArrayList<BOOKS> list = new ArrayList<>();
		ArrayList<CATEGORIES> categoryList = new ArrayList<>();
		Object[][] rowData = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;

		try {
			con = DriverManager.getConnection(url, userid, passwd);

			if (category.equals("전체")) {
				if (name.isEmpty()) {
					query = "SELECT b.bookID, " + "b.bookName, " + "b.writer, " + "b.publisher, " + "b.pubDate, "
							+ "b.bookCTG, " + "b.description, " + "b.stock, " + "c.categoryName " + "FROM books b "
							+ "JOIN categories c ON b.bookCTG = c.categoryID order by b.bookID";
					pstmt = con.prepareStatement(query);
				} else {
					query = "SELECT b.bookID, " + "b.bookName, " + "b.writer, " + "b.publisher, " + "b.pubDate, "
							+ "b.bookCTG, " + "b.description, " + "b.stock, " + "c.categoryName " + "FROM books b "
							+ "JOIN categories c ON b.bookCTG = c.categoryID "
							+ "WHERE b.bookName LIKE ? order by b.bookID";
					pstmt = con.prepareStatement(query);
					pstmt.setString(1, "%" + name.toUpperCase() + "%");
				}
			} else {
				if (name.isEmpty()) {
					query = "SELECT b.bookID, " + "b.bookName, " + "b.writer, " + "b.publisher, " + "b.pubDate, "
							+ "b.bookCTG, " + "b.description, " + "b.stock, " + "c.categoryName " + "FROM books b "
							+ "JOIN categories c ON b.bookCTG = c.categoryID "
							+ "WHERE c.categoryName = ? order by b.bookID";
					pstmt = con.prepareStatement(query);
					pstmt.setString(1, category);
				} else {
					query = "SELECT b.bookID, " + "b.bookName, " + "b.writer, " + "b.publisher, " + "b.pubDate, "
							+ "b.bookCTG, " + "b.description, " + "b.stock, " + "c.categoryName " + "FROM books b "
							+ "JOIN categories c ON b.bookCTG = c.categoryID " + "WHERE c.categoryName = ? "
							+ "AND b.bookName LIKE ? order by b.bookID";
					pstmt = con.prepareStatement(query);
					pstmt.setString(1, category);
					pstmt.setString(2, "%" + name.toUpperCase() + "%");
				}

			}
			// 쿼리 실행
			rs = pstmt.executeQuery();

			// 결과 처리
			while (rs.next()) {
				BOOKS dto = new BOOKS();
				CATEGORIES cate_dto = new CATEGORIES();
				dto.setBookID(rs.getInt("bookid"));
				dto.setBookName(rs.getString("bookname"));
				dto.setWriter(rs.getString("writer"));
				dto.setPublisher(rs.getString("publisher"));
				dto.setPubDate(rs.getDate("pubdate"));
				dto.setBookCTG(rs.getString("bookctg"));
				dto.setStock(rs.getInt("stock"));
				cate_dto.setCategoryName(rs.getString("categoryName"));
				categoryList.add(cate_dto); // 카테고리 객체를 리스트에 추가

				// 리스트에 BOOKS 객체 추가
				list.add(dto);
			}

			// List에서 Object[][]로 변환
			rowData = new Object[list.size()][7];
			for (int i = 0; i < list.size(); i++) {
				BOOKS dto = list.get(i);
				CATEGORIES cate_dto = categoryList.get(i);

				rowData[i][0] = dto.getBookID();
				rowData[i][1] = dto.getBookName();
				rowData[i][2] = dto.getWriter();
				rowData[i][3] = dto.getPublisher();
				rowData[i][4] = dto.getPubDate();
				rowData[i][5] = cate_dto.getCategoryName(); // 카테고리 필드 추가
				rowData[i][6] = dto.getStock();

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
		return rowData;
	}

	public BookDetailWrapper bookdetail(int bookID) {

		BOOKS book_dto = new BOOKS();
		RENTALS rental_dto = new RENTALS();
		RESERVATIONS res_dto = new RESERVATIONS();
		USERS user_dto = new USERS();
		REVIEWS review_dto = new REVIEWS();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			// query = "SELECT * FROM books join rentals on books.bookid = rentals.bookid
			// where books.bookid = ? ";
			query = "SELECT b.bookID, b.bookName, b.writer,  b.publisher,  b.pubDate,  b.bookCTG, b.DESCRIPTION,  b.stock, \r\n"
					+ "    u.userID,  u.userName, \r\n"
					+ "    r.rentalId,  r.rentalDate,  r.returnDueDate, r.returnDate, r.rentalState, \r\n"
					+ "    rs.rsID,  rs.rsDate,  rs.rsState, \r\n"
					+ "    rev.reviewID,   rev.score,  rev.review,  rev.reviewDate \r\n" + "FROM  \r\n"
					+ "    books b \r\n" + "LEFT JOIN rentals r \r\n" + "    ON b.bookID = r.bookID  \r\n"
					+ "LEFT JOIN users u \r\n" + "    ON r.userID = u.userID  \r\n" + "LEFT JOIN reservations rs \r\n"
					+ "    ON b.bookID = rs.bookID  \r\n" + "LEFT JOIN reviews rev \r\n"
					+ "    ON b.bookID = rev.bookID \r\n" + "WHERE  \r\n"
					+ "    b.bookID =? order by rev.reviewID desc ,r.rentalId desc";
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, bookID);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				book_dto.setBookID(rs.getInt("bookid"));
				book_dto.setBookName(rs.getString("bookname"));
				book_dto.setWriter(rs.getString("writer"));
				book_dto.setPublisher(rs.getString("publisher"));
				book_dto.setPubDate(rs.getDate("pubdate"));
				book_dto.setBookCTG(rs.getString("bookctg"));
				book_dto.setStock(rs.getInt("stock"));
				book_dto.setDescription(rs.getString("DESCRIPTION"));
				rental_dto.setRentalId(rs.getInt("rentalid"));
				rental_dto.setRentalDate(rs.getDate("rentaldate"));
				rental_dto.setReturnDueDate(rs.getDate("returnduedate"));
				rental_dto.setReturnDate(rs.getDate("returndate"));
				rental_dto.setRentalState(rs.getString("rentalstate"));
				user_dto.setUserID(rs.getString("userid"));
				user_dto.setUserName(rs.getString("username"));
				res_dto.setRsID(rs.getInt("rsid"));
				res_dto.setRsDate(rs.getDate("rsdate"));
				res_dto.setRsState(rs.getString("rsstate"));
				review_dto.setReviewID(rs.getInt("reviewid"));
				review_dto.setScore(rs.getInt("score"));
				review_dto.setReview(rs.getString("review"));
				review_dto.setReviewDate(rs.getDate("reviewdate"));
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
		return new BookDetailWrapper(book_dto, rental_dto, user_dto, res_dto, review_dto);
	}

	public Object[][] searchReviews(int bookID) {
		ArrayList<REVIEWS> list = new ArrayList<REVIEWS>();
		Object[][] rowData = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			query = "SELECT * FROM REVIEWS where bookid = ? ";
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, bookID);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				REVIEWS dto = new REVIEWS();
				dto.setReviewID(rs.getInt("reviewid"));
				dto.setUserID(rs.getString("userid"));
				dto.setBookID(rs.getInt("bookid"));
				dto.setScore(rs.getInt("score"));
				dto.setReview(rs.getString("review"));
				dto.setReviewDate(rs.getDate("reviewdate"));
				list.add(dto);
			}
			rowData = new Object[list.size()][4];
			for (int i = 0; i < list.size(); i++) {
				REVIEWS dto = list.get(i);
				String scorestar = "";
				switch (dto.getScore()) {
				case 1:
					scorestar = "★";
					break;
				case 2:
					scorestar = "★★";
					break;
				case 3:
					scorestar = "★★★";
					break;
				case 4:
					scorestar = "★★★★";
					break;
				case 5:
					scorestar = "★★★★★";
					break;
				}
				rowData[i][0] = dto.getUserID();
				rowData[i][1] = dto.getReview();
				rowData[i][2] = scorestar;
				rowData[i][3] = dto.getReviewDate();
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
		return rowData;
	}

	public int rentalBooks(String userId, int bookId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result1 = 0;
		String date = "";
		try {
			con = DriverManager.getConnection(url, userid, passwd);

			String sql = "INSERT INTO rentals(rentalid,userid,bookid,rentaldate,returnduedate,returndate,rentalstate)"
					+ "VALUES(rentalid_SEQ.NEXTVAL,?,?,sysdate,sysdate+7,'','대여')";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setInt(2, bookId);
			result1 = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return result1;
	}
	
	public String getReturndueDate(int bookId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String date = null;
		try {
			con = DriverManager.getConnection(url, userid, passwd);

			String sql = "SELECT returnduedate FROM (SELECT returnduedate FROM rentals WHERE bookid = ? ORDER BY returnduedate DESC) WHERE ROWNUM = 1";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bookId);			
			rs = pstmt.executeQuery();
			if (rs.next()) {
	            Date returnDueDate = rs.getDate("returnduedate"); // ResultSet에서 값을 가져옴
	            if (returnDueDate != null) {
	                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	                date = format1.format(returnDueDate); // 날짜를 문자열로 변환
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return date;
	}

	public int RevervationBook(String userId, int bookId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result1 = 0;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
 
			String sql = "INSERT INTO reservations(rsid,userid,bookid,rsdate,rsstate)"
					+ "VALUES(reservationid_SEQ.NEXTVAL,?,?,sysdate,'N')";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setInt(2, bookId);
			result1 = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return result1;
	}

	public int addReviews(String userId, int bookId, String stars, String review) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result1 = 0;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			String sql = "INSERT INTO reviews(reviewid,userid,bookid,score,review,reviewdate)"
					+ "VALUES(reviewid_SEQ.NEXTVAL,?,?,?,?,sysdate)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setInt(2, bookId);
			pstmt.setString(3, stars);
			pstmt.setString(4, review);
			result1 = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return result1;
	}

	// 카테고리 리스트 가져오기
	public String[] searchCategory() {
		ArrayList<String> categoryList = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;

		try {
			con = DriverManager.getConnection(url, userid, passwd);

			query = "SELECT CATEGORYNAME FROM categories";
			pstmt = con.prepareStatement(query);
			// 쿼리 실행
			rs = pstmt.executeQuery();
			// 결과 처리
			while (rs.next()) {
				categoryList.add(rs.getString("categoryname"));
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
		return categoryList.toArray(new String[0]);
	}

}
