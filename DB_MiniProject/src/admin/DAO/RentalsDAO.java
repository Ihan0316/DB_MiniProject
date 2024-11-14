package admin.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date; // java.sql.Date 사용
import DTO.RENTALS; // DTO 패키지에서 RENTALS 클래스를 임포트

public class RentalsDAO {

    private Connection conn;

    // DB 연결을 설정하는 메서드
    private void setupDatabaseConnection() {
        try {
            String driver = "oracle.jdbc.driver.OracleDriver";
            String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Oracle DB URL
            String user = "system";  // Oracle DB 사용자명
            String password = "oracle";  // Oracle DB 비밀번호
            Class.forName(driver);  // Oracle JDBC 드라이버 로드
            conn = DriverManager.getConnection(url, user, password);  // DB 연결
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 생성자에서 DB 연결을 처리합니다.
    public RentalsDAO() {
        setupDatabaseConnection();  // DB 연결 설정
    }

    // 대여 정보를 가져오는 메서드
    public List<RENTALS> getAllRentals() throws SQLException {
        List<RENTALS> rentals = new ArrayList<>();
        String query = "SELECT rentalId, userID, bookID, rentalDate, returnDueDate, returnDate, rentalState FROM rentals";
        
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
        }
        return rentals;
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
