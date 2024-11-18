package admin.UI;

import java.awt.*;

import javax.swing.*;

import DTO.REVIEWS;
import DTO.RENTALS; // RENTALS DTO import
import DTO.RECOMMENDBOOKS; // RECOMMENDBOOKS DTO import
import admin.DAO.ReviewDao;

import javax.swing.table.DefaultTableModel;
import DTO.RESERVATIONS;

import admin.DAO.RecommendBooksDAO;
import admin.DAO.RentalsDAO;
import admin.DAO.ReservationsDAO;

import java.util.List;

public class ADMIN_UI extends JFrame {

	private RecommendBooksDAO recommendBooksDAO;
	private ReservationsDAO reservationsDAO;
	private RentalsDAO rentalsDAO;

	public ADMIN_UI() {
        recommendBooksDAO = new RecommendBooksDAO(); 
        reservationsDAO = new ReservationsDAO();
        rentalsDAO = new RentalsDAO();
        adminMainWindow(); // UI 설정 메서드 호출
    }

    // UI 구성 요소
    public void adminMainWindow() {
        setTitle("관리자 도서 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 400);
        setLocationRelativeTo(null);

        // 로그아웃
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setSize(1100, 50);
        
        JButton logout = new JButton("로그아웃");
        JLabel nameLabel = new JLabel("관리자님 환영합니다.");
        logoutPanel.add(nameLabel);
        logoutPanel.add(logout);

        logout.addActionListener(e -> {
        	try {
	            // 현재 실행 중인 JAR 또는 클래스 경로 가져오기
	            String javaHome = System.getProperty("java.home");
	            String javaBin = javaHome + "/bin/java";
	            String classPath = System.getProperty("java.class.path");
	            String mainClassName = "Main"; // default package에 있는 Main 클래스 이름
	            JOptionPane.showMessageDialog(this, "다음에 또 만나요~", "로그아웃", JOptionPane.INFORMATION_MESSAGE);
				
	            // 새 프로세스 실행 (default package에서 Main 클래스를 실행)
	            ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-cp", classPath, mainClassName);
	            processBuilder.start();

	            // 현재 애플리케이션 종료
	            System.exit(0);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
        });

        // 내부 패널 생성 (그리드 요소를 중앙 정렬)
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 30, 30)); // 2x3 배열, 간격 30px
        gridPanel.setSize(1100, 350);

        // 버튼 생성
        JButton bookInfoButton = new JButton("도서 정보 관리");
        JButton userInfoButton = new JButton("회원 정보 관리");
        JButton reviewButton = new JButton("리뷰 관리");
        JButton reservationButton = new JButton("예약 및 대여 관리");
        JButton recommendBookButton = new JButton("희망도서 신청 관리");
        JButton categoryButton = new JButton("카테고리 관리");

        bookInfoButton.setPreferredSize(new Dimension(300, 100));
        userInfoButton.setPreferredSize(new Dimension(300, 100));
        reviewButton.setPreferredSize(new Dimension(300, 100));
        reservationButton.setPreferredSize(new Dimension(300, 100));
        recommendBookButton.setPreferredSize(new Dimension(300, 100));
        categoryButton.setPreferredSize(new Dimension(300, 100));

        // 버튼을 그리드 패널에 추가
        gridPanel.add(bookInfoButton);
        gridPanel.add(userInfoButton);
        gridPanel.add(reviewButton);
        gridPanel.add(reservationButton);
        gridPanel.add(recommendBookButton);
        gridPanel.add(categoryButton);

        // 그리드 패널을 가운데 정렬하기 위한 감싸는 패널
        JPanel gridCenterPanel = new JPanel(new GridBagLayout()); // 중앙 정렬 레이아웃
        gridCenterPanel.add(gridPanel);

        // 버튼 동작 설정
        bookInfoButton.addActionListener(e -> showBookInfo());
        userInfoButton.addActionListener(e -> showUserInfo());
        reviewButton.addActionListener(e -> showReviews());
        reservationButton.addActionListener(e -> showReservationManagementWindow());
        recommendBookButton.addActionListener(e -> showRecommendBooksWindow());
        categoryButton.addActionListener(e -> showCategories());

        // 외부 패널 생성 및 레이아웃 설정
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(logoutPanel, BorderLayout.NORTH); // 로그아웃 패널 상단 추가
        outerPanel.add(gridCenterPanel, BorderLayout.CENTER); // 그리드 패널을 중앙에 추가

        // 외부 패널을 프레임에 추가
        add(outerPanel);
        setVisible(true);
    }

    // 리뷰를 조회하는 메서드
    private void showReviews() {
        // 리뷰를 보여주는 로직 작성
    	openAdminReviewUi();
    
    	List<REVIEWS> reviews = ReviewDao.getAllReviews();
    	StringBuilder reviewText = new StringBuilder("리뷰목록:\n");
    	
    	for (REVIEWS review : reviews) {
    		reviewText.append("리뷰 ID: ").append(review.getReviewID())
    		          .append(", 사용자 ID: ").append(review.getUserID())
                      .append(", 책 ID: ").append(review.getBookID())
                      .append(", 점수: ").append(review.getScore())
                      .append(", 내용: ").append(review.getReview())
                      .append(", 날짜: ").append(review.getReviewDate())
                      .append("\n");
    	}
    }
    
    private void openAdminReviewUi() {
		new AdminReviewUi();
		
	}

	//도서 정보를 조회하는 메서드
	private void showBookInfo() {
		JOptionPane.showMessageDialog(this, "도서 정보를 조회합니다.");
	}

	// 회원 정보를 조회하는 메서드
	private void showUserInfo() {
		JOptionPane.showMessageDialog(this, "회원 정보를 조회합니다.");
	}

	// 예약 및 대여 관리 창을 보여주는 메서드
	private void showReservationManagementWindow() {
		JFrame reservationFrame = new JFrame("예약 및 대여 관리");
		reservationFrame.setSize(800, 600);
		reservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane();

		// 예약 관리 탭
		JPanel reservationPanel = new JPanel();
		reservationPanel.setLayout(new BorderLayout());

		// 예약 목록 테이블
		String[] reservationColumnNames = { "ID", "회원 ID", "도서 ID", "예약일", "상태" };
		DefaultTableModel reservationModel = new DefaultTableModel(reservationColumnNames, 0);
		JTable reservationTable = new JTable(reservationModel);
		JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
		reservationPanel.add(reservationScrollPane, BorderLayout.CENTER);

		// 예약 관련 버튼
		JPanel reservationButtonPanel = new JPanel();
		JButton cancelReservationButton = new JButton("예약 취소");
		cancelReservationButton.addActionListener(e -> {
			int selectedRow = reservationTable.getSelectedRow();
			if (selectedRow >= 0) {
				int reservationId = (int) reservationModel.getValueAt(selectedRow, 0);
				reservationsDAO.cancelReservation(reservationId);
				reservationModel.removeRow(selectedRow);
				JOptionPane.showMessageDialog(reservationFrame, "예약이 취소되었습니다.");
			} else {
				JOptionPane.showMessageDialog(reservationFrame, "취소할 예약을 선택하세요.");
			}
		});

		JButton completeReservationButton = new JButton("예약 완료");
		completeReservationButton.addActionListener(e -> {
			int selectedRow = reservationTable.getSelectedRow();
			if (selectedRow >= 0) {
				int reservationId = (int) reservationModel.getValueAt(selectedRow, 0);
				reservationsDAO.completeReservation(reservationId);
				reservationModel.setValueAt("완료", selectedRow, 4); // 상태 업데이트
				JOptionPane.showMessageDialog(reservationFrame, "예약이 완료되었습니다.");
			} else {
				JOptionPane.showMessageDialog(reservationFrame, "완료할 예약을 선택하세요.");
			}
		});

		reservationButtonPanel.add(cancelReservationButton);
		reservationButtonPanel.add(completeReservationButton);
		reservationPanel.add(reservationButtonPanel, BorderLayout.SOUTH);

		// 예약 정보 조회 및 테이블에 추가
		List<RESERVATIONS> reservations = reservationsDAO.getAllReservations();
		for (RESERVATIONS reservation : reservations) {
			Object[] row = { reservation.getRsID(), reservation.getUserID(), reservation.getBookID(),
					reservation.getRsDate(), reservation.getRsState() };
			reservationModel.addRow(row);
		}

		tabbedPane.addTab("예약 관리", reservationPanel);

		// 대여 관리 탭
		JPanel rentalPanel = new JPanel();
		rentalPanel.setLayout(new BorderLayout());

		// 대여 목록 테이블
		String[] rentalColumnNames = { "ID", "회원 ID", "도서 ID", "대여일", "상태" };
		DefaultTableModel rentalModel = new DefaultTableModel(rentalColumnNames, 0);
		JTable rentalTable = new JTable(rentalModel);
		JScrollPane rentalScrollPane = new JScrollPane(rentalTable);
		rentalPanel.add(rentalScrollPane, BorderLayout.CENTER);

		// 대여 관련 버튼
		JPanel rentalButtonPanel = new JPanel();
		JButton registerRentalButton = new JButton("대여 등록");
		registerRentalButton.addActionListener(e -> {
			// 대여 등록 로직 구현
			String userID = JOptionPane.showInputDialog("회원 ID 입력:");
			String bookIDString = JOptionPane.showInputDialog("도서 ID 입력:");

			// 입력 값 검증
			if (userID == null || userID.trim().isEmpty() || bookIDString == null || bookIDString.trim().isEmpty()) {
				JOptionPane.showMessageDialog(reservationFrame, "회원 ID와 도서 ID를 입력해야 합니다.");
				return;
			}

			int bookID;
			try {
				bookID = Integer.parseInt(bookIDString);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(reservationFrame, "유효한 도서 ID를 입력하세요.");
				return;
			}

			// 현재 날짜 및 반납 기한 설정
			java.util.Date rentalDate = new java.util.Date();
			java.util.Date returnDueDate = new java.util.Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

			RENTALS rental = new RENTALS();
			rental.setUserID(userID);
			rental.setBookID(bookID);
			rental.setRentalDate(rentalDate);
			rental.setReturnDueDate(returnDueDate);
			rental.setRentalState("대여중");

			// DB에 대여 정보 등록
			rentalsDAO.registerRental(rental);
			JOptionPane.showMessageDialog(reservationFrame, "대여 등록 완료되었습니다.");

			// 대여 목록 테이블에 추가
			Object[] row = { rental.getRentalId(), // rentalId는 DB에 등록 후 자동 생성되도록 설정해야 함
					rental.getUserID(), rental.getBookID(), rental.getRentalDate(), rental.getRentalState() };
			rentalModel.addRow(row);
		});

		JButton cancelRentalButton = new JButton("대여 취소");
		cancelRentalButton.addActionListener(e -> {
			int selectedRow = rentalTable.getSelectedRow();
			if (selectedRow >= 0) {
				int rentalId = (int) rentalModel.getValueAt(selectedRow, 0);
				rentalsDAO.cancelRental(rentalId);
				rentalModel.removeRow(selectedRow);
				JOptionPane.showMessageDialog(reservationFrame, "대여가 취소되었습니다.");
			} else {
				JOptionPane.showMessageDialog(reservationFrame, "취소할 대여를 선택하세요.");
			}
		});

		JButton returnCompleteButton = new JButton("반납 완료");
		returnCompleteButton.addActionListener(e -> {
			int selectedRow = rentalTable.getSelectedRow();
			if (selectedRow >= 0) {
				int rentalId = (int) rentalModel.getValueAt(selectedRow, 0);
				rentalsDAO.completeRental(rentalId);
				rentalModel.setValueAt("완료", selectedRow, 4); // 상태 업데이트
				JOptionPane.showMessageDialog(reservationFrame, "반납이 완료되었습니다.");
			} else {
				JOptionPane.showMessageDialog(reservationFrame, "반납할 대여를 선택하세요.");
			}
		});

		rentalButtonPanel.add(registerRentalButton);
		rentalButtonPanel.add(cancelRentalButton);
		rentalButtonPanel.add(returnCompleteButton);
		rentalPanel.add(rentalButtonPanel, BorderLayout.SOUTH);

		// 대여 정보 조회 및 테이블에 추가
		List<RENTALS> rentals = rentalsDAO.getAllRentals();
		for (RENTALS rental : rentals) {
			Object[] row = { rental.getRentalId(), rental.getUserID(), rental.getBookID(), rental.getRentalDate(),
					rental.getRentalState() };
			rentalModel.addRow(row);
		}

		tabbedPane.addTab("대여 관리", rentalPanel);

		reservationFrame.add(tabbedPane);
		reservationFrame.setVisible(true);
	}

	// 추천 도서 신청 창을 보여주는 메서드
	private void showRecommendBooksWindow() {
		JFrame recommendBooksFrame = new JFrame("희망 도서 목록");
		recommendBooksFrame.setSize(800, 600);
		recommendBooksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		recommendBooksFrame.setLayout(new BorderLayout());

		List<RECOMMENDBOOKS> recommendBooks = recommendBooksDAO.getAllRecommendBooks();

		// 테이블 모델 생성
		String[] columnNames = { "ID", "회원 ID", "도서 제목", "저자", "출판사", "출판일", "신청일", "상태" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// 데이터 추가
		for (RECOMMENDBOOKS book : recommendBooks) {
			Object[] row = { book.getRecommendID(), book.getUserID(), book.getBookName(), book.getWriter(),
					book.getPublisher(), book.getPubDate(), book.getReDate(), book.getCompleteYN() };
			model.addRow(row);
		}

		// JTable 생성
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);

		// 승인 및 반려 버튼
		JButton approveButton = new JButton("승인");
		approveButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				int recommendBookId = (int) model.getValueAt(selectedRow, 0);
				recommendBooksDAO.approveRecommendBook(recommendBookId);
				JOptionPane.showMessageDialog(recommendBooksFrame, "추천 도서가 승인되었습니다.");

				// 상태를 'Y'로 변경
				model.setValueAt("Y", selectedRow, 7); // 7번째 열이 completeYN
			} else {
				JOptionPane.showMessageDialog(recommendBooksFrame, "승인할 도서를 선택하세요.");
			}
		});

		JButton rejectButton = new JButton("반려");
		rejectButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				int recommendBookId = (int) model.getValueAt(selectedRow, 0);
				recommendBooksDAO.rejectRecommendBook(recommendBookId);
				JOptionPane.showMessageDialog(recommendBooksFrame, "추천 도서가 반려되었습니다.");

				// 테이블에서 해당 행 삭제
				model.removeRow(selectedRow); // 선택된 행 삭제
			} else {
				JOptionPane.showMessageDialog(recommendBooksFrame, "반려할 도서를 선택하세요.");
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(approveButton);
		buttonPanel.add(rejectButton);

		recommendBooksFrame.add(scrollPane, BorderLayout.CENTER);
		recommendBooksFrame.add(buttonPanel, BorderLayout.SOUTH);
		recommendBooksFrame.setVisible(true);
	}

	// 카테고리 관리 정보를 조회하는 메서드
    private void showCategories() {
        // 카테고리 정보를 보여주는 로직 작성
    	new AdminCategoryUi();//카테고리 창 열기
    }
}
