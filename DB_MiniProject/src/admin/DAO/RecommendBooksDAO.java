package admin.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DTO.RECOMMENDBOOKS; // RECOMMENDBOOKS DTO import

public class RecommendBooksDAO {

    private Connection conn;

    // DB 연결을 설정하는 메서드
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

    // 생성자에서 DB 연결을 처리합니다.
    public RecommendBooksDAO() {
        setupDatabaseConnection();  // Oracle DB 연결을 위한 설정 메서드 호출
    }

    // 희망 도서 신청 정보를 가져오는 메서드
    public List<RECOMMENDBOOKS> getAllRecommendBooks() throws SQLException {
        List<RECOMMENDBOOKS> recommendBooks = new ArrayList<>();
        String query = "SELECT recommendID, userID, bookName, writer, publisher, pubDate, reDate, completeYN FROM recommendBooks";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                RECOMMENDBOOKS recommendBook = new RECOMMENDBOOKS();
                recommendBook.setRecommendID(rs.getInt("recommendID"));
                recommendBook.setUserID(rs.getString("userID"));
                recommendBook.setBookName(rs.getString("bookName"));
                recommendBook.setWriter(rs.getString("writer"));
                recommendBook.setPublisher(rs.getString("publisher"));
                recommendBook.setPubDate(rs.getDate("pubDate"));
                recommendBook.setReDate(rs.getDate("reDate"));
                recommendBook.setCompleteYN(rs.getString("completeYN"));
                recommendBooks.add(recommendBook);
            }
        }

        return recommendBooks;
    }

    // 연결 해제 메서드
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
