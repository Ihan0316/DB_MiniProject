package admin.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DTO.RENTALS;

public class RentalsDAO {
    private Connection conn;

    // DB 연결 설정
    private Connection setupDatabaseConnection() {
        try {
            if (conn == null || conn.isClosed()) { // 기존 연결이 없거나 닫혀 있을 경우
                String driver = "oracle.jdbc.driver.OracleDriver";
                String url = "jdbc:oracle:thin:@localhost:1521:xe";
                String user = "scott";
                String password = "tiger";
                Class.forName(driver);
                conn = DriverManager.getConnection(url, user, password);
            }
            return conn; // Connection 객체를 반환
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DB 연결 실패: " + e.getMessage());
            return null; // 연결 실패 시 null 반환
        }
    }

    // 모든 대여 정보 조회
    public List<RENTALS> getAllRentals() {
        List<RENTALS> rentals = new ArrayList<>();
        String query = "SELECT rentalId, userID, bookID, rentalDate, returnDueDate, returnDate, rentalState FROM rentals";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = setupDatabaseConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

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
            closeResources(pstmt, rs, conn);
        }
        return rentals;
    }

    // 대여 등록 및 RentalID 반환
    public int registerRentalAndGetId(RENTALS rental) {
        String query = "INSERT INTO rentals (rentalId, userID, bookID, rentalDate, returnDueDate, rentalState) "
                     + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int generatedRentalId = getNextRentalId(); // 시퀀스에서 다음 RentalID 가져오기

        try {
            conn = setupDatabaseConnection();  // DB 연결
            conn.setAutoCommit(false);  // 트랜잭션 시작
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, generatedRentalId); // Use the generated RentalID
            pstmt.setString(2, rental.getUserID());
            pstmt.setInt(3, rental.getBookID());
            pstmt.setDate(4, new java.sql.Date(rental.getRentalDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(rental.getReturnDueDate().getTime()));
            pstmt.setString(6, rental.getRentalState());

            pstmt.executeUpdate(); // 대여 등록
            
            conn.commit();  // 트랜잭션 커밋

        } catch (SQLException e) {
            System.err.println("대여 등록 오류: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();  // 예외 발생 시 롤백
                } catch (SQLException rollbackEx) {
                    System.err.println("롤백 오류: " + rollbackEx.getMessage());
                }
            }
        } finally {
            // PreparedStatement와 Connection 자원 해제
            closeResources(pstmt, conn);
        }

        return generatedRentalId; // 반환된 RentalID
    }

    // 대여 취소
    public void cancelRental(int rentalId) {
        String query = "DELETE FROM rentals WHERE rentalId = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = setupDatabaseConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, rentalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("대여 취소 오류: " + e.getMessage());
        } finally {
            closeResources(pstmt, conn);
        }
    }

    // 대여 완료 처리
    public void completeRental(int rentalId) {
        String query = "UPDATE rentals SET rentalState = '완료', returnDueDate = null, returnDate = SYSDATE WHERE rentalId = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = setupDatabaseConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, rentalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("대여 완료 오류: " + e.getMessage());
        } finally {
            closeResources(pstmt, conn);
        }
    }

    // 도서 ID로 도서명 조회
    public String getBookNameById(int bookID) {
        String bookName = null; // 기본값 (존재하지 않는 경우 처리)
        String query = "SELECT bookName FROM books WHERE bookID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = setupDatabaseConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, bookID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bookName = rs.getString("bookName");
            }
        } catch (SQLException e) {
            System.err.println("도서명 조회 오류: " + e.getMessage());
        } finally {
            closeResources(pstmt, rs, conn);
        }
        return bookName;
    }

    // 회원 ID 존재 여부 확인
    public boolean isUserExists(String userId) {
        String query = "SELECT COUNT(*) FROM users WHERE userID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = setupDatabaseConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // 1보다 크면 존재
            }
        } catch (SQLException e) {
            System.err.println("회원 존재 여부 확인 오류: " + e.getMessage());
        } finally {
            closeResources(pstmt, rs, conn);
        }
        return false; // 존재하지 않음
    }

    // RentalID 자동 생성
    public int getNextRentalId() {
        String query = "SELECT SCOTT.RENTALID_SEQ.NEXTVAL FROM dual"; // SCOTT.RENTALID_SEQ는 시퀀스의 이름입니다.
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int rentalId = 0;

        try {
            conn = setupDatabaseConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                rentalId = rs.getInt(1); // 시퀀스의 다음의 rentalID를 불러옴
            }
        } catch (SQLException e) {
            System.err.println("대여 ID 가져오기 오류: " + e.getMessage());
        } finally {
            closeResources(pstmt, rs, conn);
        }

        return rentalId; // 다음의 rentalID 반환
    }

    // DB 연결 해제
    private void closeResources(PreparedStatement pstmt, ResultSet rs, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.err.println("자원 해제 오류: " + e.getMessage());
        }
    }

    // Overloaded closeResources for PreparedStatement and Connection only
    private void closeResources(PreparedStatement pstmt, Connection conn) {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.err.println("자원 해제 오류: " + e.getMessage());
        }
    }
}
