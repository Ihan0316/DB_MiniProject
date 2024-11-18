package admin.UI;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BookListFrame extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private Map<String, Book> bookMap;
    private BookManagementFrame bookManagementFrame;
	private AbstractButton bookNameField;
	private AbstractButton writerField;
	private AbstractButton publisherField;
	private AbstractButton pubDateField;
	private Object bookCTGComboBox;
	private AbstractButton stockField;
	private AbstractButton descriptionField;

    public BookListFrame(Map<String, Book> bookMap) {
        this.bookMap = bookMap;

        setTitle("도서 목록");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnNames = {"도서 ID", "제목", "저자", "출판사", "출판 연도", "카테고리", "재고", "설명"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);

        updateTable(bookMap);

        // 행 선택 리스너
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    String bookId = (String) tableModel.getValueAt(selectedRow, 0);
                    Book selectedBook = bookMap.get(bookId);
                    if (bookManagementFrame != null) {
                        bookManagementFrame.populateFieldsForEditing(selectedBook); // 선택된 도서 정보 수정창에 표시
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> deleteSelectedBook());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    void updateTable(Map<String, Book> bookMap) {
        tableModel.setRowCount(0); // 테이블 초기화
        for (Book book : bookMap.values()) {
            tableModel.addRow(new Object[] {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getYear(),
                book.getCategory().getName(),
                book.getStock(),
                book.getDescription()
            });
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            // 삭제할 도서의 ID 가져오기
            String bookId = (String) tableModel.getValueAt(selectedRow, 0);

            // 삭제 확인 대화 상자 표시
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "선택된 도서 목록 내용을 삭제하시겠습니까?", 
                "삭제 확인", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);

            // 사용자가 "예"를 선택한 경우
            if (confirmation == JOptionPane.YES_OPTION) {
                bookMap.remove(bookId); // 선택된 도서 삭제
                updateTable(bookMap); // 테이블 갱신
            }
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 도서를 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
        }
    }
}
