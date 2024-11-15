package admin.UI;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class BookManagementFrame extends JFrame {
    private JTextField bookIdField, bookNameField, writerField, publisherField, pubDateField, stockField;
    private JTextArea descriptionField;  // 설명을 위한 JTextArea로 변경
    private JComboBox<String> bookCTGComboBox; // 카테고리 선택을 위한 String 타입 JComboBox
    private Map<String, Book> bookMap; // 도서 정보를 저장할 Map (도서 ID를 key로)
    private JTable bookTable;  // 테이블 추가
    private TableModel tableModel; // 테이블 모델

    // 카테고리 배열 선언
    private Category[] categories = {
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
        setSize(400, 600);  // 창 크기 조정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 도서 정보 저장을 위한 Map 초기화
        bookMap = new HashMap<>();

        // UI 구성: GridBagLayout을 사용하여 레이아웃 설정
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 라벨과 입력 필드를 위한 컴포넌트들 초기화
        bookIdField = new JTextField(15);
        bookNameField = new JTextField(15);
        writerField = new JTextField(15);
        publisherField = new JTextField(15);
        pubDateField = new JTextField(15);
        stockField = new JTextField(15);

        // 설명 필드는 JTextArea로 변경
        descriptionField = new JTextArea(3, 20);  // 여러 줄을 입력할 수 있도록
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);

        // 카테고리 콤보박스 선언
        bookCTGComboBox = new JComboBox<>();
        loadCategories(); // 카테고리 데이터 로드

        // GridBagConstraints 초기화
        gbc.insets = new Insets(10, 10, 10, 10); // 각 컴포넌트 간 간격
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 라벨 및 입력 필드 추가
        addLabelAndField(panel, gbc, "도서 ID:", bookIdField, 0);
        addLabelAndField(panel, gbc, "제목:", bookNameField, 1);
        addLabelAndField(panel, gbc, "저자:", writerField, 2);
        addLabelAndField(panel, gbc, "출판사:", publisherField, 3);
        addLabelAndField(panel, gbc, "출판 연도:", pubDateField, 4);
        addLabelAndField(panel, gbc, "카테고리:", bookCTGComboBox, 5);
        addLabelAndField(panel, gbc, "재고 수량:", stockField, 6);

        // 설명 영역 추가
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        panel.add(new JLabel("설명:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JScrollPane scrollPane = new JScrollPane(descriptionField); // JTextArea에 스크롤 추가
        panel.add(scrollPane, gbc);

        // 버튼 패널 구성
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("저장");
        JButton addButton = new JButton("추가");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        buttonPanel.add(saveButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // 레이아웃에 버튼 추가
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3; // 버튼들이 한 줄에 들어가게 설정
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);

        // 저장 버튼 이벤트 처리
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    String bookId = bookIdField.getText();

                    // 도서 ID 중복 체크
                    if (isBookIdDuplicate(bookId)) {
                        JOptionPane.showMessageDialog(null, "도서 정보가 중복되어 저장되지 않았습니다.", "중복 오류",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        // 카테고리 값 가져오기 (콤보박스에서 선택된 값)
                        Category selectedCategory = getCategoryByName((String) bookCTGComboBox.getSelectedItem());

                        // 도서 정보 저장
                        Book newBook = new Book(bookId, bookNameField.getText(), writerField.getText(),
                                publisherField.getText(), pubDateField.getText(), selectedCategory, stockField.getText(),
                                descriptionField.getText());

                        // 도서 정보 Map에 추가
                        bookMap.put(bookId, newBook);

                        // 저장 완료 메시지
                        JOptionPane.showMessageDialog(null, "도서 정보가 저장되었습니다.");

                        // 콘솔에 저장된 도서 정보 출력
                        printBookInfoToConsole(newBook);

                        // 필드 초기화
                        clearFields();
                    }
                }
            }

            // 콘솔에 도서 정보 출력하는 메서드
            private void printBookInfoToConsole(Book book) {
                System.out.println("저장된 도서 정보:");
                System.out.println("도서 ID: " + book.getBookId());
                System.out.println("제목: " + book.getTitle());
                System.out.println("저자: " + book.getAuthor());
                System.out.println("출판사: " + book.getPublisher());
                System.out.println("출판 연도: " + book.getYear());
                System.out.println("카테고리 ID: " + book.getCategory().getId());
                System.out.println("카테고리 이름: " + book.getCategory().getName());
                System.out.println("재고 수량: " + book.getStock());
                System.out.println("설명: " + book.getDescription());
                System.out.println("----------------------------------------------------");
            }
        });

        // 추가 버튼 이벤트 처리
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    String bookId = bookIdField.getText();
                    if (isBookIdDuplicate(bookId)) {
                        JOptionPane.showMessageDialog(null, "도서 정보가 중복되어 추가되지 않았습니다.", "중복 오류",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(null, "도서 정보를 추가하시겠습니까?", "도서 추가 확인",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            // 도서 정보 저장
                            Category selectedCategory = getCategoryByName((String) bookCTGComboBox.getSelectedItem());

                            Book newBook = new Book(bookId, bookNameField.getText(), writerField.getText(),
                                    publisherField.getText(), pubDateField.getText(), selectedCategory, stockField.getText(),
                                    descriptionField.getText());
                            bookMap.put(bookId, newBook);

                            JOptionPane.showMessageDialog(null, "도서 정보가 추가되었습니다.");
                            clearFields(); // 필드 비우기
                        }
                    }
                }
            }
        });

        // 수정 버튼 이벤트 처리
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = bookIdField.getText();
                if (bookId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "도서 ID를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                } else if (!bookMap.containsKey(bookId)) {
                    JOptionPane.showMessageDialog(null, "해당 도서 정보가 존재하지 않아 수정할 수 없습니다.", "오류",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    Book book = bookMap.get(bookId);
                    // 기존 도서 정보를 불러와서 입력 필드에 표시
                    bookNameField.setText(book.getTitle());
                    writerField.setText(book.getAuthor());
                    publisherField.setText(book.getPublisher());
                    pubDateField.setText(book.getYear());
                    bookCTGComboBox.setSelectedItem(book.getCategory().getName()); // 카테고리 이름만 선택
                    stockField.setText(book.getStock());
                    descriptionField.setText(book.getDescription());

                    int confirm = JOptionPane.showConfirmDialog(null, "도서 정보를 수정하시겠습니까?", "도서 수정 확인",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // 수정된 정보로 업데이트
                        Category selectedCategory = getCategoryByName((String) bookCTGComboBox.getSelectedItem());
                        book.setTitle(bookNameField.getText());
                        book.setAuthor(writerField.getText());
                        book.setPublisher(publisherField.getText());
                        book.setYear(pubDateField.getText());
                        book.setCategory(selectedCategory);
                        book.setStock(stockField.getText());
                        book.setDescription(descriptionField.getText());

                        JOptionPane.showMessageDialog(null, "도서 정보가 수정되었습니다.");
                        clearFields(); // 필드 비우기
                    }
                }
            }
        });

        // 삭제 버튼 이벤트 처리
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = bookIdField.getText();
                if (bookId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "도서 ID를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                } else if (!bookMap.containsKey(bookId)) {
                    JOptionPane.showMessageDialog(null, "해당 도서 정보가 존재하지 않아 삭제할 수 없습니다.", "오류",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(null, "도서 정보를 삭제하시겠습니까?", "도서 삭제 확인",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        bookMap.remove(bookId);
                        JOptionPane.showMessageDialog(null, "도서 정보가 삭제되었습니다.");
                        clearFields(); // 필드 비우기
                    }
                }
            }
        });
    }

    // 카테고리 목록을 JComboBox에 로드
    private void loadCategories() {
        // 카테고리 배열을 반복하여 JComboBox에 추가
        for (Category category : categories) {
            bookCTGComboBox.addItem(category.getName());  // 카테고리 이름만 추가
        }
    }

    // 카테고리 이름으로 Category 객체를 반환하는 메서드
    private Category getCategoryByName(String categoryName) {
        for (Category category : categories) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null; // 만약 없는 카테고리라면 null 반환
    }

    // 도서 ID 중복 체크
    private boolean isBookIdDuplicate(String bookId) {
        return bookMap.containsKey(bookId);
    }

    // 입력 필드 유효성 검사
    private boolean validateFields() {
        if (bookIdField.getText().isEmpty() || bookNameField.getText().isEmpty() || writerField.getText().isEmpty() ||
                publisherField.getText().isEmpty() || pubDateField.getText().isEmpty() || stockField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "모든 필드를 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // 필드 초기화
    private void clearFields() {
        bookIdField.setText("");
        bookNameField.setText("");
        writerField.setText("");
        publisherField.setText("");
        pubDateField.setText("");
        stockField.setText("");
        descriptionField.setText("");
    }

    // 라벨과 입력 필드를 추가하는 메서드
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BookManagementFrame().setVisible(true);
            }
        });
    }
}

class Book {
    private String bookId;
    private String title;
    private String author;
    private String publisher;
    private String year;
    private Category category;
    private String stock;
    private String description;

    public Book(String bookId, String title, String author, String publisher, String year, Category category, String stock, String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.category = category;
        this.stock = stock;
        this.description = description;
    }

    // Getter 및 Setter 추가
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

class Category {
    private String id;
    private String name;

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}











