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
            String user = "scott"; 
            String password = "tiger"; 

            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
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
