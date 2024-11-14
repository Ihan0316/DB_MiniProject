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
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return categories;
    }

    // 카테고리 추가 메서드
    public void addCategory(CATEGORIES newCategory) {
        String sql = "INSERT INTO CATEGORIES (categoryID, categoryName, description) VALUES (?, ?, ?)";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newCategory.getCategoryID());
            pstmt.setString(2, newCategory.getCategoryName());
            pstmt.setString(3, newCategory.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // 특정 카테고리 조회 메서드
    public CATEGORIES getCategoryById(int categoryID) {
        CATEGORIES category = null;
        String sql = "SELECT * FROM CATEGORIES WHERE categoryID = ?";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryID);
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

    // 카테고리 삭제 메서드
    public boolean deleteCategory(String categoryName) {
        String sql = "DELETE FROM CATEGORIES WHERE categoryName = ?";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, categoryName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 삭제된 행이 있으면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return false; // 삭제 실패
    }

    // 리소스 해제 메서드
    private static void closeResources() {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

