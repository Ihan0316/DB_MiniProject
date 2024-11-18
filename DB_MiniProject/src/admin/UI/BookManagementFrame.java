package admin.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BookManagementFrame extends JFrame {
    private JTextField bookIdField, bookNameField, writerField, publisherField, pubDateField, stockField;
    private JTextArea descriptionField;
    private JComboBox<String> bookCTGComboBox;
    private JTable bookTable;
    private DefaultTableModel tableModel; // 테이블 데이터 관리
    private Map<String, Book> bookMap;

    private final Category[] categories = {
        new Category("1", "소설"),
        new Category("2", "교육"),
        new Category("3", "과학"),
        new Category("4", "역사"),
        new Category("5", "기술"),
        new Category("6", "자기개발"),
        new Category("7", "외국어")
    };

    public BookManagementFrame() {
        setTitle("도서 정보 관리");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        bookMap = new HashMap<>();

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        pack(); // 창 크기를 레이아웃에 맞게 자동 조정
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        bookIdField = new JTextField(15);
        bookNameField = new JTextField(15);
        writerField = new JTextField(15);
        publisherField = new JTextField(15);
        pubDateField = new JTextField(15);
        stockField = new JTextField(15);

        descriptionField = new JTextArea(3, 20);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);

        bookCTGComboBox = new JComboBox<>();
        loadCategories();

        addLabelAndField(panel, gbc, "도서 ID:", bookIdField, 0);
        addLabelAndField(panel, gbc, "제목:", bookNameField, 1);
        addLabelAndField(panel, gbc, "저자:", writerField, 2);
        addLabelAndField(panel, gbc, "출판사:", publisherField, 3);
        addLabelAndField(panel, gbc, "출판 연도:", pubDateField, 4);
        addLabelAndField(panel, gbc, "카테고리:", bookCTGComboBox, 5);
        addLabelAndField(panel, gbc, "재고 수량:", stockField, 6);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("설명:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JScrollPane scrollPane = new JScrollPane(descriptionField);
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JButton saveButton = new JButton("저장");
        JButton viewListButton = new JButton("목록 보기");

        saveButton.addActionListener(e -> saveBook());
        viewListButton.addActionListener(e -> openBookListFrame());

        panel.add(saveButton);
        panel.add(viewListButton);

        return panel;
    }

    private void loadCategories() {
        for (Category category : categories) {
            bookCTGComboBox.addItem(category.getName());
        }
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    private void saveBook() {
        if (validateFields()) {
            String bookId = bookIdField.getText();

            if (bookMap.containsKey(bookId)) {
                showMessage("도서 ID가 중복됩니다.", "중복 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Category selectedCategory = getCategoryByName((String) bookCTGComboBox.getSelectedItem());
            Book newBook = new Book(bookId, bookNameField.getText(), writerField.getText(), publisherField.getText(),
                                    pubDateField.getText(), selectedCategory, stockField.getText(), descriptionField.getText());

            bookMap.put(bookId, newBook);
            showMessage("도서 정보가 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        }
    }

    private void deleteBook() {
        String bookId = bookIdField.getText();
        if (!bookMap.containsKey(bookId)) {
            showMessage("해당 도서가 존재하지 않습니다.", "삭제 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        bookMap.remove(bookId);
        showMessage("도서 정보가 삭제되었습니다.", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
    }

    private void openBookListFrame() {
        new BookListFrame(bookMap).setVisible(true); // 목록 보기 버튼 클릭 시 새로운 창 열기
    }

    private boolean validateFields() {
        if (bookIdField.getText().isEmpty() || bookNameField.getText().isEmpty() || writerField.getText().isEmpty() ||
            publisherField.getText().isEmpty() || pubDateField.getText().isEmpty() || stockField.getText().isEmpty()) {
            showMessage("모든 필드를 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(pubDateField.getText()); 
            Integer.parseInt(stockField.getText()); 
        } catch (NumberFormatException e) {
            showMessage("출판 연도와 재고 수량은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void clearFields() {
        bookIdField.setText("");
        bookNameField.setText("");
        writerField.setText("");
        publisherField.setText("");
        pubDateField.setText("");
        stockField.setText("");
        descriptionField.setText("");
    }

    private Category getCategoryByName(String categoryName) {
        for (Category category : categories) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookManagementFrame().setVisible(true));
    }
    
    public void populateFieldsForEditing(Book selectedBook) {
        bookIdField.setText(selectedBook.getBookId());
        bookNameField.setText(selectedBook.getTitle());
        writerField.setText(selectedBook.getAuthor());
        publisherField.setText(selectedBook.getPublisher());
        pubDateField.setText(selectedBook.getYear());
        stockField.setText(selectedBook.getStock());
        descriptionField.setText(selectedBook.getDescription());
        bookCTGComboBox.setSelectedItem(selectedBook.getCategory().getName());
    }
}

class Book {
    private String bookId, title, author, publisher, year, stock, description;
    private Category category;

    public Book(String bookId, String title, String author, String publisher, String year, 
                Category category, String stock, String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.category = category;
        this.stock = stock;
        this.description = description;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getYear() { return year; }
    public Category getCategory() { return category; }
    public String getStock() { return stock; }
    public String getDescription() { return description; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setYear(String year) { this.year = year; }
    public void setCategory(Category category) { this.category = category; }
    public void setStock(String stock) { this.stock = stock; }
    public void setDescription(String description) { this.description = description; }
}

class Category {
    private String id;
    private String name;

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
















