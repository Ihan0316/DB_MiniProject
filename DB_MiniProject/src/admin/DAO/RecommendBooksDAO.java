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
        String query = "SELECT * FROM RECOMMENDBOOKS";

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
                
                // 상태 값 변환 (DB에는 그대로 'Y', 'N', 'R' 저장되며, UI에서만 변환된 값 표시)
                String completeYN = rs.getString("completeYN");
                if ("Y".equals(completeYN)) {
                    book.setCompleteYN("승인");  // 'Y' -> '승인'
                } else if ("N".equals(completeYN)) {
                    book.setCompleteYN("반려");  // 'N' -> '반려'
                } else if ("R".equals(completeYN)) {
                    book.setCompleteYN("대기");  // 'R' -> '대기'
                } else {
                    book.setCompleteYN("알 수 없음");  // 기타 상태
                }

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
