package admin.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DTO.RECOMMENDBOOKS;

public class RecommendBooksDAO {

    private Connection conn;

    public RecommendBooksDAO() {
        // Direct connection setup to Oracle DB
        setupDatabaseConnection();
    }

    private void setupDatabaseConnection() {
        try {
        	String driver = "oracle.jdbc.driver.OracleDriver";
        	String url = "jdbc:oracle:thin:@localhost:1521:xe";
        	String userid = "scott";
        	String passwd = "tiger";
//            String driver = "oracle.jdbc.driver.OracleDriver";
//            String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Oracle DB URL (Oracle Express)
//            String userid = "system";  // Oracle DB 사용자명
//            String passwd = "oracle";  // Oracle DB 비밀번호
            Class.forName(driver);  // Oracle JDBC 드라이버 로드
            conn = DriverManager.getConnection(url, userid, passwd);  // DB 연결
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RECOMMENDBOOKS> getAllRecommendBooks() {
        List<RECOMMENDBOOKS> recommendBooks = new ArrayList<>();
        String query = "SELECT * FROM RECOMMENDBOOKS WHERE completeYN = 'N'";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                RECOMMENDBOOKS book = new RECOMMENDBOOKS();
                book.setRecommendID(rs.getInt("recommendID"));
                book.setUserID(rs.getString("userID"));
                book.setBookName(rs.getString("bookName"));
                book.setWriter(rs.getString("writer"));
                book.setPublisher(rs.getString("publisher"));
                book.setPubDate(rs.getDate("pubDate"));
                book.setReDate(rs.getDate("reDate"));
                book.setCompleteYN(rs.getString("completeYN"));
                recommendBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendBooks;
    }

    public void approveRecommendBook(int recommendBookId) {
        String query = "UPDATE RECOMMENDBOOKS SET completeYN = 'Y' WHERE recommendID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recommendBookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejectRecommendBook(int recommendBookId) {
        String query = "UPDATE RECOMMENDBOOKS SET completeYN = 'R' WHERE recommendID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recommendBookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRecommendBook(RECOMMENDBOOKS book) {
        String query = "INSERT INTO RECOMMENDBOOKS (RECOMMENDID, USERID, BOOKNAME, WRITER, PUBLISHER, PUBDATE, REDATE, COMPLETEYN) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, book.getRecommendID());
            stmt.setString(2, book.getUserID());
            stmt.setString(3, book.getBookName());
            stmt.setString(4, book.getWriter());
            stmt.setString(5, book.getPublisher());
            stmt.setDate(6, new java.sql.Date(book.getPubDate().getTime()));
            stmt.setDate(7, book.getReDate() != null ? new java.sql.Date(book.getReDate().getTime()) : null);
            stmt.setString(8, book.getCompleteYN());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
