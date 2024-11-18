package admin.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import admin.DAO.BookManagementDAO;

public class BookManagementFrame extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel; // 테이블 데이터 관리
    private BookManagementDAO dao = new BookManagementDAO();

    public BookManagementFrame() {
        setTitle("도서 정보 관리");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 도서 목록 조회
        loadBookList();

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JButton addButton = new JButton("도서 추가");
        JButton deleteButton = new JButton("도서 삭제");

        addButton.addActionListener(e -> openAddBookDialog());
        deleteButton.addActionListener(e -> deleteBook());

        panel.add(addButton);
        panel.add(deleteButton);

        return panel;
    }

    public void loadBookList() {
        Object[][] bookData = dao.getBookList();
        String[] columnNames = { "도서ID", "제목", "저자", "출판사", "출판연도", "카테고리", "설명", "재고" };

        tableModel = new DefaultTableModel(bookData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가
            }
        };
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        if (getContentPane().getComponentCount() > 1) {
            getContentPane().remove(0);  // 이전에 추가된 테이블이 있으면 제거
        }

        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void openAddBookDialog() {
        // 도서 추가 창 열기
        new AddBookDialog(this).setVisible(true);
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);  // 선택된 도서 ID

            int result = dao.deleteBook(bookId);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "도서가 삭제되었습니다.");
                loadBookList();  // 삭제 후 목록 새로 고침
            } else {
                JOptionPane.showMessageDialog(this, "도서 삭제에 실패했습니다.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 도서를 선택하세요.");
        }
    }
}
