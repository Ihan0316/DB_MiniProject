package admin.UI;

import admin.DAO.*;
import DTO.REVIEWS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminReviewUi extends JFrame {
    private ReviewDao reviewDao;

    public AdminReviewUi() {
        reviewDao = new ReviewDao();

        setTitle("리뷰 관리");
        setSize(800, 400);
        setLocationRelativeTo(null); // 화면 가운데 위치
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 테이블 모델 설정
        String[] columnNames = {"리뷰아이디", "사용자아이디", "책제목", "점수", "한줄리뷰", "작성날짜"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 모든 셀을 비활성화
                return false;
            }
        };
        JTable reviewTable = new JTable(tableModel);

        // 데이터베이스에서 리뷰를 가져와서 테이블에 추가
        List<REVIEWS> reviews = reviewDao.getAllReviews();
        for (REVIEWS review : reviews) {
            // 점수를 '★'로 변환
            String scoreInStars = getStars(review.getScore());

            Object[] rowData = {
                review.getReviewID(),
                review.getUserID(),
                review.getBookName(),
                scoreInStars,  // 점수를 ★로 표시
                review.getReview(),
                review.getReviewDate()
            };
            System.out.println("추가된 리뷰: " + rowData[1]);
            tableModel.addRow(rowData);
            reviewTable.revalidate();
            reviewTable.repaint();
        }

        // 테이블을 스크롤 팬에 추가
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 삭제 버튼 추가
        JButton deleteButton = new JButton("리뷰삭제");
        deleteButton.addActionListener(e -> {
            int selectedRow = reviewTable.getSelectedRow();
            if (selectedRow != -1) {
                int reviewID = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "리뷰를 삭제하시겠습니까?", "삭제확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // 삭제
                    reviewDao.deleteReview(reviewID);
                    // 테이블에서 삭제
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "리뷰가 삭제되었습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 리뷰를 선택하세요.");
            }
        });
        
        // 하단에 버튼 배치하기
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    // 점수를 '★'로 변환하는 메서드
    private String getStars(int score) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < score) {
                stars.append("★");  // 점수만큼 별 추가
            }
        }
        return stars.toString();
    }
}
