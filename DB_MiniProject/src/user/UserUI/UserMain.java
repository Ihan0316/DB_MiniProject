package user.UserUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DTO.BOOKS;
import user.DAO.LSH_DAO;

public class UserMain extends JFrame {

	LSH_DAO lsh_dao = new LSH_DAO();
	private JTextField searchField; // 검색 입력창
	private JButton searchButton; // 검색 버튼
	private JComboBox<String> bookCategory;
	private JPanel BookListPanel;
	private JPanel SearchPanel;
	private JPanel SearchDetailPanel; // SearchDetailPanel을 클래스 인스턴스 변수로 설정
	private String[] bookCategoryColumn = { "전체", "인문", "사회", "과학", "소설", "수필", "시" };
	private JTextField bookIdField;
	private JTextField bookNameField;
	private JTextField authorField;
	private JTextField publisherField ;
	private JTextField releaseDateField;
	private JTextField categoryField;
	private JTextField stockField;
	private JTextArea summaryArea; 
	
	public void userMainWindow() {
		setTitle("도서 관리 시스템");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 400);
		setLocationRelativeTo(null);

		JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
		JButton bookList = new JButton("도서 목록");
		JButton button2 = new JButton("회원 정보");
		JButton button3 = new JButton("내 예약/대여");
		JButton button4 = new JButton("희망도서신청");

		gridPanel.add(bookList);
		gridPanel.add(button2);
		gridPanel.add(button3);
		gridPanel.add(button4);

		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
		outerPanel.add(gridPanel, BorderLayout.CENTER);
		bookList.addActionListener(e -> BookList());

		add(outerPanel);
		setVisible(true);
	}

	public void BookList() {
		JFrame BookListFrame = new JFrame("도서 목록");

		BookListFrame.setSize(1000, 600);
		BookListFrame.setLocationRelativeTo(null);
		BookListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		BookListPanel = new JPanel(new BorderLayout());

		SearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		SearchPanel.add(new JLabel("검색 범위"));
		bookCategory = new JComboBox<>(bookCategoryColumn);
		SearchPanel.add(bookCategory);
		searchField = new JTextField(20);
		SearchPanel.add(searchField);

		SearchDetailPanel = new JPanel(new BorderLayout());

		// 기본 검색 수행 (초기 테이블 데이터 로드)
		String initialCategory = (String) bookCategory.getSelectedItem();
		String initialSearchTerm = "";
		loadTableData(initialCategory, initialSearchTerm); // 초기 테이블 데이터 로드

		searchButton = new JButton("검색");
		searchButton.addActionListener(e -> {
			String searchTerm = searchField.getText();
			String category = (String) bookCategory.getSelectedItem();
			loadTableData(category, searchTerm); // 검색 실행
		});
		SearchPanel.add(searchButton);
		// Enter 키 이벤트로 검색 실행
		searchField.addActionListener(e -> {
			String searchTerm = searchField.getText();
			String category = (String) bookCategory.getSelectedItem();

			// 검색 수행 및 테이블 데이터 갱신
			loadTableData(category, searchTerm);
		});

		SearchPanel.setPreferredSize(new Dimension(1000, 50));
		BookListPanel.add(SearchPanel, BorderLayout.NORTH);
		BookListPanel.add(SearchDetailPanel, BorderLayout.CENTER);

		BookListFrame.add(BookListPanel);
		BookListFrame.setVisible(true);
	}

	private void loadTableData(String category, String searchTerm) {
		Object[][] data = lsh_dao.searchBooks(category, searchTerm);
		System.out.println("data length" + data.length);
		String[] columnNames = { "책번호", "책이름", "작가", "출판사", "출시일", "책카테고리", "재고" };

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable bookTable = new JTable(model);
		bookTable.setRowHeight(25);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < bookTable.getColumnCount(); i++) {
			bookTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		
		// 테이블 더블 클릭시 이벤트 발생
		bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mouseClicked(java.awt.event.MouseEvent e) {
						// 더블 클릭 여부 확인 - 클릭횟수 2회
						if (e.getClickCount() == 2) {
							int row = bookTable.rowAtPoint(e.getPoint());

							// 특정 셀을 더블 클릭했을 때 이벤트 실행
							// 예시: 더블 클릭한 직원의 ID를 가져와서 수정 작업
							Object selectedBook = bookTable.getValueAt(row, 0); // ID가 두 번째 열에 있다고 가정
							bookDetailWindow((int)selectedBook);
							System.out.println("선택된 책 ID: " + selectedBook);
						}
					}
				});

		SearchDetailPanel.removeAll();

		JScrollPane tableScrollPane = new JScrollPane(bookTable);
		tableScrollPane.setPreferredSize(new Dimension(1000, 500)); // 스크롤 패널 크기 조정
		tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 세로 스크롤 항상 보이게

		SearchDetailPanel.add(tableScrollPane, BorderLayout.CENTER);
		SearchDetailPanel.revalidate();
		SearchDetailPanel.repaint();
	}
	public void bookDetailWindow(int bookId) {
		// Create the main frame for the book detail window
        JFrame mainFrame = new JFrame("도서 상세 보기");
        mainFrame.setSize(1000, 800); // Main frame size
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null); // Center the window

        // Create a background panel to cover the whole frame
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BorderLayout());
        

        // Create a content panel for the book details
        JPanel bookDetailPanel = new JPanel();
        bookDetailPanel.setLayout(new GridBagLayout());
        bookDetailPanel.setPreferredSize(new Dimension(600, 600)); // Set content panel size
        bookDetailPanel.setBackground(Color.red);
        // Create GridBagConstraints for aligning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for each component
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Simulate fetching book details
        BOOKS list = lsh_dao.bookdetail(bookId);
        String bookID = Integer.toString(list.getBookID());
        String bookName = list.getBookName();
        String writer = list.getWriter();
        String publisher = list.getPublisher();
        String pubdate = list.getPubDate().toString();
        String bookcategory = list.getBookCTG();
        String stock = Integer.toString(list.getStock());
        String description = list.getDescription();

        // Define Labels and Fields for the book details
        JLabel bookIdLabel = new JLabel("책 번호: ");
        JLabel bookNameLabel = new JLabel("책 이름: ");
        JLabel authorLabel = new JLabel("저자: ");
        JLabel publisherLabel = new JLabel("출판사: ");
        JLabel releaseDateLabel = new JLabel("출시일: ");
        JLabel categoryLabel = new JLabel("책 카테고리: ");
        JLabel stockLabel = new JLabel("재고: ");
        JLabel summaryLabel = new JLabel("줄거리: ");
        
        // Book details fields
        JTextField bookIdField = new JTextField(bookID);
        JTextField bookNameField = new JTextField(bookName);
        JTextField authorField = new JTextField(writer);
        JTextField publisherField = new JTextField(publisher);
        JTextField releaseDateField = new JTextField(pubdate);
        JTextField categoryField = new JTextField(bookcategory);
        JTextField stockField = new JTextField(stock);
        JTextArea summaryArea = new JTextArea(8, 40);
        summaryArea.setText(description);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setLineWrap(true);
        summaryArea.setCaretPosition(0);
        summaryArea.setEditable(false); // Read-only

        // Remove borders for text fields
        bookIdField.setBorder(null);
        bookNameField.setBorder(null);
        authorField.setBorder(null);
        publisherField.setBorder(null);
        releaseDateField.setBorder(null);
        categoryField.setBorder(null);
        stockField.setBorder(null);

        // Add components to the bookDetailPanel (book details)
        gbc.gridx = 0;
        gbc.gridy = 0;
        bookDetailPanel.add(bookIdLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(bookIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bookDetailPanel.add(bookNameLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(bookNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        bookDetailPanel.add(authorLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        bookDetailPanel.add(publisherLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(publisherField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        bookDetailPanel.add(releaseDateLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(releaseDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        bookDetailPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        bookDetailPanel.add(stockLabel, gbc);
        gbc.gridx = 1;
        bookDetailPanel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        bookDetailPanel.add(summaryLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        bookDetailPanel.add(new JScrollPane(summaryArea), gbc);

        // Add the bookDetailPanel to the background panel (top part)
        backgroundPanel.add(bookDetailPanel, BorderLayout.NORTH);

        // Create a comments panel below the book details
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new GridBagLayout());
        commentPanel.setPreferredSize(new Dimension(800,300));
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Separator between details and comments
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(500, 2));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        commentPanel.add(separator, gbc);

        // Add a text field and button for adding comments
        JLabel commentLabel = new JLabel("댓글 작성: ");
        JTextArea commentArea = new JTextArea(4, 40);
        JButton submitButton = new JButton("댓글 제출");

        gbc.gridx = 0;
        gbc.gridy = 1;
        commentPanel.add(commentLabel, gbc);
        gbc.gridx = 1;
        commentPanel.add(new JScrollPane(commentArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        commentPanel.add(submitButton, gbc);

        // Table to display comments
        String[] columnNames = {"사용자명", "한줄평", "별점"};
        Object[][] data = {
            {"홍길동", "재미있어요!", 5},
            {"김철수", "내용이 좋습니다.", 4}
        };
        JTable commentsTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(commentsTable);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        commentPanel.add(tableScrollPane, gbc);

        // Add the commentPanel (comments section) to the background panel (bottom part)
        backgroundPanel.add(commentPanel, BorderLayout.CENTER);

        // Add the background panel to the main frame
        mainFrame.add(backgroundPanel);

        // Make the main frame visible
        mainFrame.setVisible(true);
	}
	

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			UserMain mainFrame = new UserMain();
			mainFrame.userMainWindow();
		});
	}
}
