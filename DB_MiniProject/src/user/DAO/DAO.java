package user.DAO;

import java.sql.*;

public class DAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";
//    private String driver = "oracle.jdbc.driver.OracleDriver";
//    private String url = "jdbc:oracle:thin:@localhost:1521:xe";
//    private String userid = "system";
//    private String passwd = "1234";
    private Connection conn;

    public DAO() {
        try {
            Class.forName(driver); // JDBC 드라이버 로드
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 예약 데이터 가져오기
    public ResultSet getReservations(String userId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userid, passwd);

            String sql = "SELECT r.rsid, r.userid, r.bookid, b.bookname, r.rsdate, r.rsstate FROM reservations r "
            			+ "JOIN books b ON r.bookid = b.bookid WHERE r.userid = ?"; // 예약 테이블에서 모든 데이터 선택

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rs;
    }

    // 대여 데이터 가져오기
    public ResultSet getRentals(String userId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userid, passwd);

            String sql = "SELECT r.rentalid, r.userid, r.bookid, b.bookname, r.rentaldate, r.returnduedate, r.returndate, r.rentalstate FROM rentals r "
            			+ "JOIN books b ON r.bookid = b.bookid WHERE r.userid = ?"; // 대여 테이블에서 모든 데이터 선택

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rs;
    }

    // 예약 삭제 메서드
    public boolean deleteReservation(int reservationId) {
        PreparedStatement pstmt = null;
        try {
        	conn = DriverManager.getConnection(url, userid, passwd);
            String sql = "DELETE FROM reservations WHERE rsID = ?"; // 예약 삭제 쿼리
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            int affectedRows = pstmt.executeUpdate(); // 쿼리 실행

            return affectedRows > 0; // 삭제된 행이 있으면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 오류 발생 시 false 반환
        } finally {
            // PreparedStatement 자원 해제
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 자원 해제 메서드
    public void close() {
        try {
            if (conn != null) conn.close(); // 연결 종료
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
