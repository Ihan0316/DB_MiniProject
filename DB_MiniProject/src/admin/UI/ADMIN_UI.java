package admin.UI;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import DTO.RENTALS; // RENTALS DTO import
import DTO.RECOMMENDBOOKS; // RECOMMANDBOOKS DTO import
import admin.DAO.RecommendBooksDAO;
import admin.DAO.RentalsDAO;

public class ADMIN_UI extends JFrame {

    public ADMIN_UI() {
        setTitle("도서 관리 프로그램");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 3)); // 2행 3열의 버튼 레이아웃

        // 각 버튼 생성
        JButton bookInfoButton = new JButton("도서 정보");
        JButton userInfoButton = new JButton("회원 정보");
        JButton reviewButton = new JButton("리뷰");
        JButton rentalButton = new JButton("예약&대여");
        JButton recommendBookButton = new JButton("희망 도서 신청");
        JButton categoryButton = new JButton("카테고리 관리");

        // 버튼 이벤트 핸들러 추가
        bookInfoButton.addActionListener(e -> showBookInfo());
        userInfoButton.addActionListener(e -> showUserInfo());
        reviewButton.addActionListener(e -> showReviews());
        rentalButton.addActionListener(e -> rentalsWindow());
        recommendBookButton.addActionListener(e -> recommendBooksWindow());
        categoryButton.addActionListener(e -> showCategories());

        // 버튼을 프레임에 추가
        add(bookInfoButton);
        add(userInfoButton);
        add(reviewButton);
        add(rentalButton);
        add(recommendBookButton);
        add(categoryButton);

        setVisible(true);
    }

    // 도서 정보를 조회하는 메서드
    private void showBookInfo() {
        JOptionPane.showMessageDialog(this, "도서 정보를 조회합니다.");
    }

    // 회원 정보를 조회하는 메서드
    private void showUserInfo() {
        JOptionPane.showMessageDialog(this, "회원 정보를 조회합니다.");
    }

    // 리뷰를 조회하는 메서드
    private void showReviews() {
        JOptionPane.showMessageDialog(this, "리뷰를 조회합니다.");
    }

    // 예약 및 대여 정보를 표시하는 메서드
    private void rentalsWindow() {
        RentalsDAO rentalsDAO = new RentalsDAO();
        try {
            List<RENTALS> rentalData = rentalsDAO.getAllRentals();
            String[] columnNames = { "Rental ID", "User ID", "Book ID", "Rental Date", "Return Due Date", "Return Date", "Rental State" };
            Object[][] data = new Object[rentalData.size()][columnNames.length];

            // 대여 데이터 추가
            for (int row = 0; row < rentalData.size(); row++) {
                RENTALS rental = rentalData.get(row);
                data[row] = new Object[] {
                    rental.getRentalId(),
                    rental.getUserID(),
                    rental.getBookID(),
                    rental.getRentalDate(),
                    rental.getReturnDueDate(),
                    rental.getReturnDate(),
                    rental.getRentalState()
                };
            }

            // JTable로 데이터를 표시
            showTableWindow("예약 및 대여 정보", columnNames, data);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "예약 및 대여 정보를 불러오는 데 오류가 발생했습니다: " + e.getMessage());
        } finally {
            rentalsDAO.closeConnection(); // 리소스 해제
        }
    }

    // 희망 도서 신청 정보를 표시하는 메서드
    private void recommendBooksWindow() {
        RecommendBooksDAO recommendBooksDAO = new RecommendBooksDAO();
        try {
            List<RECOMMENDBOOKS> recommendBooks = recommendBooksDAO.getAllRecommendBooks();
            String[] columnNames = { "Recommend ID", "User ID", "Book Name", "Writer", "Publisher", "Publication Date", "Recommend Date", "Complete" };
            Object[][] data = new Object[recommendBooks.size()][columnNames.length];

            // 추천 도서 데이터 추가
            for (int i = 0; i < recommendBooks.size(); i++) {
            	RECOMMENDBOOKS book = recommendBooks.get(i);
                data[i] = new Object[] {
                    book.getRecommendID(),
                    book.getUserID(),
                    book.getBookName(),
                    book.getWriter(),
                    book.getPublisher(),
                    book.getPubDate(),
                    book.getReDate(),
                    book.getCompleteYN()
                };
            }

            // JTable로 데이터를 표시
            showTableWindow("희망 도서 신청", columnNames, data);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "희망 도서 신청 정보를 불러오는 데 오류가 발생했습니다: " + e.getMessage());
        } finally {
            recommendBooksDAO.closeConnection(); // 리소스 해제
        }
    }

    // JTable을 포함한 새로운 창을 띄우는 메서드
    private void showTableWindow(String title, String[] columnNames, Object[][] data) {
        JFrame tableFrame = new JFrame(title);
        tableFrame.setSize(800, 600);
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        tableFrame.add(scrollPane);
        tableFrame.setVisible(true);
    }

    // 카테고리 관리 정보를 조회하는 메서드
    private void showCategories() {
        JOptionPane.showMessageDialog(this, "카테고리 관리를 조회합니다.");
    }

    // 메인 메서드에서 UI 시작
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ADMIN_UI());
    }
}
