package user.DAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ReserceList extends JFrame {

    private JTable reservationTable; // 예약 테이블
    private DefaultTableModel reservationTableModel; // 예약 테이블 모델

    public ReserceList() {
        setTitle("3x2 버튼 배열 - 패딩 추가");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        // 내부 패널 생성 및 GridLayout 설정 (3x2 배열, 버튼 간격 30px)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));

        JButton button1 = new JButton("도서 목록");
        JButton button2 = new JButton("회원 정보");
        JButton button3 = new JButton("내 예약/대여");
        JButton button4 = new JButton("희망도서신청");

        gridPanel.add(button1);
        gridPanel.add(button2);
        gridPanel.add(button3);
        gridPanel.add(button4);

        // "내 예약/대여" 버튼 클릭 시 동작
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReservationsAndRentals();
            }
        });

        // 외부 패널 생성 및 여백 추가
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); // 여백 추가
        outerPanel.add(gridPanel, BorderLayout.CENTER); // 내부 패널 추가

        // 프레임에 외부 패널 추가
        add(outerPanel);
        setVisible(true);
    }
    
    private void showReservationsAndRentals() {
        JFrame reservationFrame = new JFrame("내 예약 및 대여 목록");
        reservationFrame.setSize(800, 600);
        reservationFrame.setLocationRelativeTo(null);
        reservationFrame.setLayout(new BorderLayout()); // BorderLayout으로 설정

        // 대여 테이블
        String[] rentalColumnNames = {"대여 ID", "회원 ID", "도서 ID", "대여 날짜", "반납 예정일", "반납일", "상태"};
        DefaultTableModel rentalTableModel = new DefaultTableModel(rentalColumnNames, 0);

        DAO dao = new DAO(); // DAO 인스턴스 생성
        try {
            ResultSet rentalRs = dao.getRentals(); // 대여 데이터 가져오는 메서드 호출

            while (rentalRs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rentalRs.getInt("rentalId"));
                row.add(rentalRs.getString("userID"));
                row.add(rentalRs.getInt("bookID"));
                row.add(rentalRs.getDate("rentalDate"));
                row.add(rentalRs.getDate("returnDueDate"));
                row.add(rentalRs.getDate("returnDate"));
                row.add(rentalRs.getString("rentalState"));
                rentalTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // 대여 테이블 생성
        JTable rentalTable = new JTable(rentalTableModel);
        JScrollPane rentalScrollPane = new JScrollPane(rentalTable);
        rentalScrollPane.setBorder(null); // JScrollPane의 여백 제거

        // 대여 제목 추가
        JPanel rentalPanel = new JPanel(new BorderLayout());
        rentalPanel.add(new JLabel("대여 목록", JLabel.CENTER), BorderLayout.NORTH);
        rentalPanel.add(rentalScrollPane, BorderLayout.CENTER);

        // 예약 테이블
        String[] reservationColumnNames = {"예약 ID", "회원 ID", "도서 ID", "예약 날짜", "상태"};
        DefaultTableModel reservationTableModel = new DefaultTableModel(reservationColumnNames, 0); // 예약 테이블 모델 초기화

        try {
            ResultSet reservationRs = dao.getReservations();

            while (reservationRs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(reservationRs.getInt("rsID"));
                row.add(reservationRs.getString("userID"));
                row.add(reservationRs.getInt("bookID"));
                row.add(reservationRs.getDate("rsDate"));
                row.add(reservationRs.getString("rsState"));
                reservationTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // 예약 테이블 생성
        JTable reservationTable = new JTable(reservationTableModel);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        reservationScrollPane.setBorder(null); // JScrollPane의 여백 제거

        // 예약 제목 추가
        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationPanel.add(new JLabel("예약 목록", JLabel.CENTER), BorderLayout.NORTH);
        reservationPanel.add(reservationScrollPane, BorderLayout.CENTER);

        // 삭제 버튼 추가
        JButton deleteButton = new JButton("예약 취소");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = reservationTable.getSelectedRow();
                if (selectedRow != -1) {
                    int reservationId = (int) reservationTableModel.getValueAt(selectedRow, 0); // 예약 ID 가져오기

                    // DAO를 통해 예약 삭제
                    if (dao.deleteReservation(reservationId)) {
                        reservationTableModel.removeRow(selectedRow); // 테이블에서 행 삭제
                        JOptionPane.showMessageDialog(reservationFrame, "예약한 도서가 삭제되었습니다.");
                    } else {
                        JOptionPane.showMessageDialog(reservationFrame, "예약 삭제에 실패했습니다.");
                    }
                } else {
                    JOptionPane.showMessageDialog(reservationFrame, "예약 취소 할 도서를 선택하세요.");
                }
            }
        });

        // 삭제 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.add(deleteButton);

        // 프레임에 대여와 예약 패널 추가
        JPanel mainPanel = new JPanel(new GridLayout(2, 1)); // 대여와 예약 테이블을 수직으로 배치
        mainPanel.add(rentalPanel);
        mainPanel.add(reservationPanel);

        reservationFrame.add(mainPanel, BorderLayout.CENTER); // 메인 패널은 중앙에 배치
        reservationFrame.add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널을 하단에 배치

        reservationFrame.setVisible(true);

        dao.close(); // DAO 자원 해제
    }





    public static void main(String[] args) {
        new ReserceList();
    }
}
