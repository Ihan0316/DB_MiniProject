package admin.UI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import DTO.RECOMMENDBOOKS;
import DTO.RENTALS;
import DTO.RESERVATIONS;
import admin.DAO.RecommendBooksDAO;
import admin.DAO.RentalsDAO;
import admin.DAO.ReservationsDAO;

public class ADMIN_UI extends JFrame {

    private RecommendBooksDAO recommendBooksDAO;
    private ReservationsDAO reservationsDAO;
    private RentalsDAO rentalsDAO;

    public ADMIN_UI() {
        recommendBooksDAO = new RecommendBooksDAO();
        reservationsDAO = new ReservationsDAO();
        rentalsDAO = new RentalsDAO();

        setTitle("도서 관리 프로그램");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 3));

        JButton bookInfoButton = new JButton("도서 정보");
        JButton userInfoButton = new JButton("회원 정보");
        JButton reviewButton = new JButton("리뷰");
        JButton reservationButton = new JButton("예약 및 대여 관리");
        JButton recommendBookButton = new JButton("희망 도서 신청");
        JButton categoryButton = new JButton("카테고리 관리");

        bookInfoButton.addActionListener(e -> showBookInfo());
        userInfoButton.addActionListener(e -> showUserInfo());
        reviewButton.addActionListener(e -> showReviews());
        reservationButton.addActionListener(e -> showReservationManagementWindow());
        recommendBookButton.addActionListener(e -> showRecommendBooksWindow());
        categoryButton.addActionListener(e -> showCategories());

        add(bookInfoButton);
        add(userInfoButton);
        add(reviewButton);
        add(reservationButton);
        add(recommendBookButton);
        add(categoryButton);

        setVisible(true);
    }

    private void showBookInfo() {
        JOptionPane.showMessageDialog(this, "도서 정보를 조회합니다.");
    }

    private void showUserInfo() {
        JOptionPane.showMessageDialog(this, "회원 정보를 조회합니다.");
    }

    private void showReviews() {
        JOptionPane.showMessageDialog(this, "리뷰를 조회합니다.");
    }

    private void showReservationManagementWindow() {
        JFrame reservationFrame = new JFrame("예약 및 대여 관리");
        reservationFrame.setSize(800, 600);
        reservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 예약 관리 탭
        JPanel reservationPanel = new JPanel();
        reservationPanel.setLayout(new BorderLayout());
        String[] reservationColumnNames = { "ID", "회원 ID", "도서명", "예약일", "상태" };
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
                String status = (String) reservationModel.getValueAt(selectedRow, 4);
                if ("Y".equals(status)) {
                    JOptionPane.showMessageDialog(reservationFrame, "완료된 예약은 취소할 수 없습니다.");
                    return;
                }
                int reservationId = (int) reservationModel.getValueAt(selectedRow, 0);
                reservationsDAO.cancelReservation(reservationId);
                reservationModel.setValueAt("N", selectedRow, 4);
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
                reservationModel.setValueAt("Y", selectedRow, 4);
                JOptionPane.showMessageDialog(reservationFrame, "예약이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "완료할 예약을 선택하세요.");
            }
        });

        reservationButtonPanel.add(cancelReservationButton);
        reservationButtonPanel.add(completeReservationButton);
        reservationPanel.add(reservationButtonPanel, BorderLayout.SOUTH);

        List<RESERVATIONS> reservations = reservationsDAO.getAllReservations();
        for (RESERVATIONS reservation : reservations) {
            String bookName = rentalsDAO.getBookNameById(reservation.getBookID());
            Object[] row = { reservation.getRsID(), reservation.getUserID(), bookName, reservation.getRsDate(),
                    reservation.getRsState() };
            reservationModel.addRow(row);
        }

        tabbedPane.addTab("예약 관리", reservationPanel);

        // 대여 관리 탭
        JPanel rentalPanel = new JPanel();
        rentalPanel.setLayout(new BorderLayout());
        String[] rentalColumnNames = { "ID", "회원 ID", "도서명", "대여일", "상태" };
        DefaultTableModel rentalModel = new DefaultTableModel(rentalColumnNames, 0);
        JTable rentalTable = new JTable(rentalModel);
        JScrollPane rentalScrollPane = new JScrollPane(rentalTable);
        rentalPanel.add(rentalScrollPane, BorderLayout.CENTER);

        // 대여 관련 버튼
        JPanel rentalButtonPanel = new JPanel();
        JButton registerRentalButton = new JButton("대여 등록");
        registerRentalButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(0, 2));
            JTextField userIdField = new JTextField();
            JTextField bookIdField = new JTextField();

            inputPanel.add(new JLabel("회원 ID:"));
            inputPanel.add(userIdField);
            inputPanel.add(new JLabel("도서 ID:"));
            inputPanel.add(bookIdField);

            int result = JOptionPane.showConfirmDialog(reservationFrame, inputPanel, "대여 등록",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String userID = userIdField.getText().trim();
                String bookIDString = bookIdField.getText().trim();

                if (userID.isEmpty() || bookIDString.isEmpty()) {
                    JOptionPane.showMessageDialog(reservationFrame, "모든 필드를 입력해야 합니다.");
                    return;
                }

                int bookID;
                try {
                    bookID = Integer.parseInt(bookIDString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(reservationFrame, "유효한 도서 ID를 입력하세요.");
                    return;
                }

                if (!rentalsDAO.isUserExists(userID)) {
                    JOptionPane.showMessageDialog(reservationFrame, "존재하지 않는 회원 ID입니다.");
                    return;
                }

                String bookName = rentalsDAO.getBookNameById(bookID);
                if (bookName == null) {
                    JOptionPane.showMessageDialog(reservationFrame, "존재하지 않는 도서 ID입니다.");
                    return;
                }

                java.util.Date rentalDate = new java.util.Date();
                java.util.Date returnDueDate = new java.util.Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

                RENTALS rental = new RENTALS();
                rental.setUserID(userID);
                rental.setBookID(bookID);
                rental.setRentalDate(rentalDate);
                rental.setReturnDueDate(returnDueDate);
                rental.setRentalState("대여");

                // Register rental and get the generated RentalID
                int rentalId = rentalsDAO.registerRentalAndGetId(rental); // 수정된 메서드 사용
                rental.setRentalId(rentalId); // ID 설정

                JOptionPane.showMessageDialog(reservationFrame, "대여 등록 완료되었습니다.");

                Object[] row = { rental.getRentalId(), rental.getUserID(), bookName, rental.getRentalDate(), rental.getRentalState() };
                rentalModel.addRow(row);
            }
        });

        JButton cancelRentalButton = new JButton("대여 취소");
        cancelRentalButton.addActionListener(e -> {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow >= 0) {
                String status = (String) rentalModel.getValueAt(selectedRow, 4);
                if ("Y".equals(status)) {
                    JOptionPane.showMessageDialog(reservationFrame, "완료된 대여는 취소할 수 없습니다.");
                    return;
                }
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
                rentalModel.setValueAt("Y", selectedRow, 4); // 상태 업데이트
                JOptionPane.showMessageDialog(reservationFrame, "반납이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "반납할 대여를 선택하세요.");
            }
        });

        JButton lateRentalButton = new JButton("연체");
        lateRentalButton.addActionListener(e -> {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow >= 0) {
                int rentalId = (int) rentalModel.getValueAt(selectedRow, 0);
                rentalModel.setValueAt("연체", selectedRow, 4); // 테이블에서 상태 업데이트
                JOptionPane.showMessageDialog(reservationFrame, "대여가 연체로 설정되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "연체할 대여를 선택하세요.");
            }
        });

        rentalButtonPanel.add(registerRentalButton);
        rentalButtonPanel.add(cancelRentalButton);
        rentalButtonPanel.add(returnCompleteButton);
        rentalButtonPanel.add(lateRentalButton);
        rentalPanel.add(rentalButtonPanel, BorderLayout.SOUTH);

        // 대여 정보 조회 및 테이블에 추가
        List<RENTALS> rentals = rentalsDAO.getAllRentals();
        for (RENTALS rental : rentals) {
            String bookName = rentalsDAO.getBookNameById(rental.getBookID());
            Object[] row = { rental.getRentalId(), rental.getUserID(), bookName, rental.getRentalDate(), rental.getRentalState() };
            rentalModel.addRow(row);
        }

        tabbedPane.addTab("대여 관리", rentalPanel);
        reservationFrame.add(tabbedPane);
        reservationFrame.setVisible(true);
    }

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

                // 행 삭제
                model.removeRow(selectedRow);
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
                model.removeRow(selectedRow);
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

    private void showCategories() {
        JOptionPane.showMessageDialog(this, "카테고리 관리를 진행합니다.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ADMIN_UI::new);
    }
}
