package admin.DAO;

import DTO.CATEGORIES;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
   
	// 데이터베이스 연결 정보
    private static Connection conn;
    private static PreparedStatement pstmt;
    private static ResultSet rs;
    
    // DB 연결 메서드
    private static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                String url = "jdbc:oracle:thin:@localhost:1521:xe"; // DB URL
                String user = "scott"; // DB 사용자명
                String password = "tiger"; // DB 비밀번호
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("DB 연결 오류");
            }
        }
        return conn;
    }
   
    // 모든 카테고리 목록 조회 메서드
    public List<CATEGORIES> getAllCategories() {
        List<CATEGORIES> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIES";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CATEGORIES category = new CATEGORIES();
                category.setCategoryID(rs.getInt("categoryID"));
                category.setCategoryName(rs.getString("categoryName"));
                category.setDescription(rs.getString("description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 오류 발생 시 콘솔에 출력
        } finally {
            closeResources();
        }
        return categories;
    }

    
    // 카테고리 추가 메서드
    public void addCategory(CATEGORIES newCategory) {
        String sql = "INSERT INTO CATEGORIES (categoryID, categoryName, description) VALUES (category_seq.NEXTVAL, ?, ?)";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCategory.getCategoryName());
            pstmt.setString(2, newCategory.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }
    
    
    
    // 카테고리 삭제 메서드
    public boolean deleteCategory(int categoryID) {
        boolean success = false;
        try {
            conn = getConnection();
            
            // 1. 리뷰 삭제
            String deleteReviewsSql = "DELETE FROM REVIEWS WHERE bookID IN (SELECT bookID FROM BOOKS WHERE bookCTG = ?)";
            pstmt = conn.prepareStatement(deleteReviewsSql);
            pstmt.setInt(1, categoryID);
            pstmt.executeUpdate();  // 리뷰 삭제
            pstmt.close();
            
            // 2. 예약 삭제
            String deleteReservationsSql = "DELETE FROM RESERVATIONS WHERE bookID IN (SELECT bookID FROM BOOKS WHERE bookCTG = ?)";
            pstmt = conn.prepareStatement(deleteReservationsSql);
            pstmt.setInt(1, categoryID);
            pstmt.executeUpdate();  // 예약 삭제
            pstmt.close();

            // 3. 대여 기록 삭제
            String deleteRentalsSql = "DELETE FROM RENTALS WHERE bookID IN (SELECT bookID FROM BOOKS WHERE bookCTG = ?)";
            pstmt = conn.prepareStatement(deleteRentalsSql);
            pstmt.setInt(1, categoryID);
            pstmt.executeUpdate();  // 대여 삭제
            pstmt.close();

            // 4. 책 삭제
            String deleteBooksSql = "DELETE FROM BOOKS WHERE bookCTG = ?";
            pstmt = conn.prepareStatement(deleteBooksSql);
            pstmt.setInt(1, categoryID);
            pstmt.executeUpdate();  // 책 삭제
            pstmt.close();

            // 5. 카테고리 삭제
            String deleteCategorySql = "DELETE FROM CATEGORIES WHERE categoryID = ?";
            pstmt = conn.prepareStatement(deleteCategorySql);
            pstmt.setInt(1, categoryID);
            int rowsAffected = pstmt.executeUpdate();  // 카테고리 삭제
            
            success = rowsAffected > 0;  // 삭제 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();  // 리소스 해제
        }
        return success;  // success 반환
    }



 // 카테고리 이름으로 카테고리 정보를 가져오는 메서드
    public CATEGORIES getCategoryByName(String categoryName) {
        CATEGORIES category = null;
        String sql = "SELECT * FROM CATEGORIES WHERE categoryName = ?";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, categoryName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                category = new CATEGORIES();
                category.setCategoryID(rs.getInt("categoryID"));
                category.setCategoryName(rs.getString("categoryName"));
                category.setDescription(rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return category;
    }

    
    // 리소스 해제 메서드
    private static void closeResources() {
        try { 
            if (rs != null) rs.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        try { 
            if (pstmt != null) pstmt.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        try { 
            if (conn != null) conn.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
}