package admin.UI;

import admin.DAO.*;
import DTO.REVIEWS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminReviewUi extends JFrame {
	 private ReviewDao ReviewDao;
	 
    private ReviewDao reviewDao;

    public AdminReviewUi() {
        reviewDao = new ReviewDao();

        setTitle("리뷰 관리");
        setSize(600, 400);
        setLocationRelativeTo(null); // 화면 가운데 위치
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 테이블 모델 설정
        String[] columnNames = {"Review ID", "User ID", "Book ID", "Score", "Review", "Review Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable reviewTable = new JTable(tableModel);

        // 데이터베이스에서 리뷰를 가져와서 테이블에 추가
        List<REVIEWS> reviews = reviewDao.getAllReviews();
        for (REVIEWS review : reviews) {
            Object[] rowData = {
                review.getReviewID(),
                review.getUserID(),
                review.getBookID(),
                review.getScore(),
                review.getReview(),
                review.getReviewDate()
            };
            tableModel.addRow(rowData);
        }

        // 테이블을 스크롤 팬에 추가
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        add(scrollPane, BorderLayout.CENTER);
        
        //삭제 버튼 추가
        JButton deleteButton = new JButton("리뷰삭제");
        deleteButton.addActionListener(e -> {
        	int selectedRow = reviewTable.getSelectedRow();
        	if(selectedRow != -1) {
        		int reviewID = (int) tableModel.getValueAt(selectedRow, 0);
        		int confirm = JOptionPane.showConfirmDialog(this, "리뷰를 삭제하시겠습니까?", "삭제확인", JOptionPane.YES_NO_OPTION);
        		if (confirm == JOptionPane.YES_OPTION) {
        			//삭제
        			reviewDao.deleteReview(reviewID);
        			//테이블에서 삭제
        			tableModel.removeRow(selectedRow);
        			JOptionPane.showMessageDialog(this, "리뷰가 삭제되었습니다.");
        		}
        	} else {
        		JOptionPane.showMessageDialog(this, "삭제할 리뷰를 선택하세요.");
        	}
        });
        
        //하단에 버튼 배치하기
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);

       
    }
}
