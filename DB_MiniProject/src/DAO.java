import java.sql.*;
import javax.swing.JOptionPane;

public class DAO {

    String driver = "oracle.jdbc.driver.OracleDriver";
    String url = "jdbc:oracle:thin:@localhost:1521:xe";
    String userid = "system";
    String passwd = "1234";

    public DAO() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String userID, String password, String userName, String tel) {
        String sql = "INSERT INTO Users (userID, password, userName, tel, regdate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, userid, passwd);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userID);
            pstmt.setString(2, password);
            pstmt.setString(3, userName);
            pstmt.setString(4, tel);
            pstmt.setDate(5, new Date(System.currentTimeMillis()));

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                JOptionPane.showMessageDialog(null, "중복된 ID입니다. 다른 ID를 사용해주세요.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean authenticateUser(String userID, String password) {
        String sql = "SELECT COUNT(*) FROM Users WHERE userID = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url, userid, passwd);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userID);
            pstmt.setString(2, password);
            
         // 사용자 인증 성공
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // 인증 실패
    }
}
