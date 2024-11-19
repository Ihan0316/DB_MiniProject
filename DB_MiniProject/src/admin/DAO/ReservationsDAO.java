package admin.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DTO.RESERVATIONS;

public class ReservationsDAO {
	private Connection conn;

	// DB 연결 설정
	private void setupDatabaseConnection() {
		try {
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String user = "scott";
			String password = "tiger";
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			System.err.println("DB 드라이버 로드 실패: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("DB 연결 실패: " + e.getMessage());
		}
	}

	// 모든 예약 정보 조회
	public List<RESERVATIONS> getAllReservations() {
		List<RESERVATIONS> reservations = new ArrayList<>();
		String query = "SELECT * FROM reservations";

		setupDatabaseConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				RESERVATIONS reservation = new RESERVATIONS();
				reservation.setRsID(rs.getInt("rsID")); // 예약 ID
				reservation.setUserID(rs.getString("userID")); // 사용자 ID
				reservation.setBookID(rs.getInt("bookID")); // 도서 ID
				reservation.setRsDate(rs.getDate("rsDate")); // 예약일
				reservation.setRsState(rs.getString("rsState")); // 예약 상태
				reservations.add(reservation);
			}
		} catch (SQLException e) {
			System.err.println("예약 정보 조회 오류: " + e.getMessage());
		} finally {
			closeConnection();
		}
		return reservations;
	}

	// 예약 취소 처리 (삭제)
	public void cancelReservation(int reservationId) {
		String query = "DELETE FROM reservations WHERE rsID = ?";
		setupDatabaseConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, reservationId);
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				System.err.println("예약 ID " + reservationId + "을(를) 찾을 수 없습니다.");
			}
		} catch (SQLException e) {
			System.err.println("예약 취소 오류: " + e.getMessage());
		} finally {
			closeConnection();
		}
	}

	// 예약 완료 처리
	public void completeReservation(int reservationId) {
		String query = "UPDATE reservations SET rsState = 'Y' WHERE rsID = ?";
		setupDatabaseConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, reservationId);
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				System.err.println("예약 ID " + reservationId + "을(를) 찾을 수 없습니다.");
			}
		} catch (SQLException e) {
			System.err.println("예약 완료 오류: " + e.getMessage());
		} finally {
			closeConnection();
		}
	}

	// 도서 ID로 도서명 조회
	public String getBookNameById(int bookID) {
		String bookName = null; // 기본값 (존재하지 않는 경우 처리)
		String query = "SELECT bookName FROM books WHERE bookID = ?";
		setupDatabaseConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, bookID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				bookName = rs.getString("bookName");
			}
		} catch (SQLException e) {
			System.err.println("도서명 조회 오류: " + e.getMessage());
		} finally {
			closeConnection();
		}
		return bookName;
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
