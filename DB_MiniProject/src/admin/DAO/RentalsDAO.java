package admin.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DTO.RENTALS;

public class RentalsDAO {
    private Connection conn;

    // DB 연결 설정
    private void setupDatabaseConnection() {
        try {
        	String driver = "oracle.jdbc.driver.OracleDriver";
        	String url = "jdbc:oracle:thin:@localhost:1521:xe";
        	String userid = "scott";
        	String passwd = "tiger";
//            String driver = "oracle.jdbc.driver.OracleDriver";
//            String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Oracle DB URL
//            String userid = "system";  // Oracle DB 사용자명
//            String passwd = "oracle";  // Oracle DB 비밀번호
            Class.forName(driver);  // Oracle JDBC 드라이버 로드
            conn = DriverManager.getConnection(url, userid, passwd);  // DB 연결
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DB 연결 실패: " + e.getMessage());
        }
    }

    // 모든 대여 정보 조회
    public List<RENTALS> getAllRentals() {
        List<RENTALS> rentals = new ArrayList<>();
        String query = "SELECT rentalId, userID, bookID, rentalDate, returnDueDate, returnDate, rentalState FROM rentals";

        setupDatabaseConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                RENTALS rental = new RENTALS();
                rental.setRentalId(rs.getInt("rentalId"));
                rental.setUserID(rs.getString("userID"));
                rental.setBookID(rs.getInt("bookID"));
                rental.setRentalDate(rs.getDate("rentalDate"));
                rental.setReturnDueDate(rs.getDate("returnDueDate"));
                rental.setReturnDate(rs.getDate("returnDate"));
                rental.setRentalState(rs.getString("rentalState"));
                rentals.add(rental);
            }
        } catch (SQLException e) {
            System.err.println("대여 정보 조회 오류: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return rentals;
    }

    // 대여 등록
    public void registerRental(RENTALS rental) {
        String query = "INSERT INTO rentals (userID, bookID, rentalDate, returnDueDate, rentalState) VALUES (?, ?, ?, ?, ?)";
        setupDatabaseConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, rental.getUserID());
            pstmt.setInt(2, rental.getBookID());
            pstmt.setDate(3, new java.sql.Date(rental.getRentalDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(rental.getReturnDueDate().getTime()));
            pstmt.setString(5, rental.getRentalState());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("대여 등록 오류: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    // 대여 취소
    public void cancelRental(int rentalId) {
        String query = "DELETE FROM rentals WHERE rentalId = ?";
        setupDatabaseConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, rentalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("대여 취소 오류: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    // 대여 완료 처리
    public void completeRental(int rentalId) {
        String query = "UPDATE rentals SET rentalState = '완료', returnDate = SYSDATE WHERE rentalId = ?";
        setupDatabaseConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, rentalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("대여 완료 오류: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    // DB 연결 해제
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("연결 해제 오류: " + e.getMessage());
        }
    }
}
