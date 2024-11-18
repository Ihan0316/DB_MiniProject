package admin.UI;

import javax.swing.*;
import java.awt.*;

import DTO.BOOKS;
import DTO.CATEGORIES;
import admin.DAO.BookManagementDAO;

public class AddBookDialog extends JDialog {
    private JTextField bookNameField, writerField, publisherField, pubDateField, stockField;
    private JTextArea descriptionField;
    private JComboBox<CATEGORIES> bookCTGComboBox;
    private BookManagementDAO dao;
    private BookManagementFrame parentFrame;  // BookManagementFrame 참조 추가

    public AddBookDialog(BookManagementFrame parent) {
        super(parent, "도서 추가", true);
        dao = new BookManagementDAO();
        parentFrame = parent;  // parentFrame 초기화

        setSize(450, 450);  // 다이얼로그 크기 조정
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // JTextField 크기 늘리기
        bookNameField = new JTextField(20);
        writerField = new JTextField(20);
        publisherField = new JTextField(20);
        pubDateField = new JTextField(20);
        stockField = new JTextField(20);
        descriptionField = new JTextArea(3, 20);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);

        bookCTGComboBox = new JComboBox<>();
        loadCategories();

        // 레이블과 필드 추가
        addLabelAndField(panel, gbc, "제목:", bookNameField, 0);
        addLabelAndField(panel, gbc, "저자:", writerField, 1);
        addLabelAndField(panel, gbc, "출판사:", publisherField, 2);
        addLabelAndField(panel, gbc, "출판 연도:", pubDateField, 3);
        addLabelAndField(panel, gbc, "재고 수량:", stockField, 4);
        addLabelAndField(panel, gbc, "카테고리:", bookCTGComboBox, 5);

        // 설명 영역
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("설명:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JScrollPane scrollPane = new JScrollPane(descriptionField);
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JButton saveButton = new JButton("도서 저장");
        saveButton.addActionListener(e -> saveBook());

        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    private void loadCategories() {
        for (CATEGORIES category : dao.getBookCTG()) {
            bookCTGComboBox.addItem(category);
        }
    }

    private void saveBook() {
        if (validateFields()) {
            String bookName = bookNameField.getText();
            String writer = writerField.getText();
            String publisher = publisherField.getText();
            String pubDate = pubDateField.getText();
            String stock = stockField.getText();
            String description = descriptionField.getText();

            // 선택된 카테고리 가져오기
            CATEGORIES selectedCategory = (CATEGORIES) bookCTGComboBox.getSelectedItem();
            int categoryId = selectedCategory.getCategoryID();

            // BOOKS 객체 생성
            BOOKS book = new BOOKS();
            book.setBookName(bookName);
            book.setWriter(writer);
            book.setPublisher(publisher);
            book.setPubDate(java.sql.Date.valueOf(pubDate));
            book.setBookCTG(String.valueOf(categoryId));
            book.setDescription(description);
            book.setStock(Integer.parseInt(stock));

            // 도서 추가
            int result = dao.insertBook(book);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "도서가 추가되었습니다.");
                parentFrame.loadBookList();  // 도서 목록 새로 고침
                dispose();  // 다이얼로그 닫기
            } else {
                JOptionPane.showMessageDialog(this, "도서 추가에 실패했습니다.");
            }
        }
    }

    private boolean validateFields() {
        // 필수 입력 값 체크
        if (bookNameField.getText().isEmpty() || writerField.getText().isEmpty() || publisherField.getText().isEmpty() ||
            pubDateField.getText().isEmpty() || stockField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력해야 합니다.");
            return false;
        }

        // 출판 연도 형식 (yyyy-MM-dd) 체크
        String pubDate = pubDateField.getText();
        String pubDateRegex = "^\\d{4}-\\d{2}-\\d{2}$";  // yyyy-MM-dd 형식
        if (!pubDate.matches(pubDateRegex)) {
            JOptionPane.showMessageDialog(this, "출판 연도는 yyyy-MM-dd 형식으로 입력해야 합니다.");
            return false;
        }

        // 날짜 유효성 검사 (예: 2024-02-30과 같은 잘못된 날짜 방지)
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);  // lenient(false)로 설정하여 날짜 유효성 검사
            sdf.parse(pubDate);  // 이 코드에서 날짜가 유효하지 않으면 예외 발생
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "잘못된 날짜입니다. 올바른 날짜 형식으로 입력해 주세요.");
            return false;
        }

        // 재고 수량 숫자 형식 체크
        try {
            Integer.parseInt(stockField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "재고 수량은 숫자로 입력해야 합니다.");
            return false;
        }

        return true;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;

        // 라벨의 너비 확장
        gbc.weightx = 0.2; // 라벨에 여유 공간을 할당
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // 필드에 여유 공간을 할당
        panel.add(field, gbc);
    }
}
