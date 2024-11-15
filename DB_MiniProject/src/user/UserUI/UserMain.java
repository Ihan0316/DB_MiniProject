package user.UserUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DTO.BOOKS;
import DTO.BookDetailWrapper;
import DTO.RENTALS;
import user.DAO.LSH_DAO;

public class UserMain extends JFrame {
	public String userId;

    public UserMain(String userId) {
    	this.userId = userId;
        userMainWindow(); // 생성자에서 UI 설정 메서드 호출
    }

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
	private JTextField publisherField;
	private JTextField releaseDateField;
	private JTextField categoryField;
	private JTextField stockField;
	private JTextArea summaryArea;

	public void userMainWindow() {
		setTitle("도서 관리 시스템");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 400);
		setLocationRelativeTo(null);

        // 내부 패널 생성 및 GridLayout 설정 (3x2 배열, 버튼 간격 30px)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        JButton bookList = new JButton("도서 목록");
        JButton userInfo = new JButton("내 정보");
        JButton button3 = new JButton("내 예약/대여");
        JButton rcmBook = new JButton("희망도서신청");
        
        gridPanel.add(bookList);
        gridPanel.add(userInfo);
        gridPanel.add(button3);
        gridPanel.add(rcmBook);
        
        userInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserInfoDialog();
            }
        });
        rcmBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRecommendBookDialog();
            }
        });
        
        bookList.addActionListener(e -> BookList());
        
        // 외부 패널 생성 및 여백 추가
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); // 여백 추가
        outerPanel.add(gridPanel, BorderLayout.CENTER); // 내부 패널 추가

		add(outerPanel);
		setVisible(true);
	}
	
	// 희망도서신청
    private void openRecommendBookDialog() {
        JDialog dialog = new JDialog(this, "희망도서 신청", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        RecommendBook recommendBookMain = new RecommendBook(userId);
        dialog.add(recommendBookMain);
        dialog.setVisible(true);
    }
    
    // 내 정보
    private void openUserInfoDialog() {
    	JDialog dialog = new JDialog(this, "내 정보", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        UserInfo userInfo = new UserInfo(userId);
        dialog.add(userInfo);
        dialog.setVisible(true);
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
					bookDetailWindow((int) selectedBook);
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
		mainFrame.setSize(600, 830); // Main frame size
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null); // Center the window

		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new BorderLayout());

		// 도서 상세 내용 표시
		JPanel bookDetailPanel = new JPanel();
		bookDetailPanel.setLayout(new GridBagLayout());
		bookDetailPanel.setPreferredSize(new Dimension(600, 550)); // Set content panel size
		bookDetailPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // Padding for each component
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Simulate fetching book details
		BookDetailWrapper list = lsh_dao.bookdetail(bookId);
		BOOKS book = list.getBookDto();
		RENTALS rental =list.getRentalDto();
		
		String bookID = Integer.toString(book.getBookID());
		String bookName = book.getBookName();
		String writer = book.getWriter();
		String publisher = book.getPublisher();
		String pubdate = book.getPubDate().toString();
		String bookcategory = book.getBookCTG();
		String stock = Integer.toString(book.getStock());
		String description = book.getDescription();
		String rentAvail = rental.getRentalState() != null ? rental.getRentalState() : "Y";
		String returnDate = rental.getReturnDueDate() != null ? rental.getReturnDueDate().toString() : "-";

		// Define Labels and Fields for the book details
		JLabel bookIdLabel = new JLabel("책 번호: ");
		JLabel bookNameLabel = new JLabel("책 이름: ");
		JLabel authorLabel = new JLabel("저자: ");
		JLabel publisherLabel = new JLabel("출판사: ");
		JLabel releaseDateLabel = new JLabel("출시일: ");
		JLabel categoryLabel = new JLabel("책 카테고리: ");
		JLabel stockLabel = new JLabel("재고: ");
		JLabel summaryLabel = new JLabel("줄거리: ");
		JLabel availRent = new JLabel("대여가능 여부: ");
		JLabel exReturnDate = new JLabel("반납 예정일: ");

		// Book details fields
		JTextField bookIdField = new JTextField(bookID);
		JTextField bookNameField = new JTextField(bookName);
		JTextField authorField = new JTextField(writer);
		JTextField publisherField = new JTextField(publisher);
		JTextField releaseDateField = new JTextField(pubdate);
		JTextField categoryField = new JTextField(bookcategory);
		JTextField stockField = new JTextField(stock);
		JTextArea summaryArea = new JTextArea(8, 40);
		JTextField availRentField = new JTextField(rentAvail);
		JTextField exReturnDateField = new JTextField(returnDate);
		
		summaryArea.setText(description);
		summaryArea.setWrapStyleWord(true);
		summaryArea.setLineWrap(true);
		summaryArea.setCaretPosition(0);
		summaryArea.setEditable(false); // Read-only

		bookIdField.setBackground(null);
		bookIdField.setBorder(null);
		bookNameField.setBackground(null);
		bookNameField.setBorder(null);
		authorField.setBackground(null);
		authorField.setBorder(null);
		publisherField.setBackground(null);
		publisherField.setBorder(null);
		releaseDateField.setBackground(null);
		releaseDateField.setBorder(null);
		categoryField.setBackground(null);
		categoryField.setBorder(null);
		stockField.setBackground(null);
		stockField.setBorder(null);
		summaryArea.setBorder(null);
		summaryArea.setBackground(null);
		availRentField.setBackground(null);
		availRentField.setBorder(null);
		exReturnDateField.setBackground(null);
		exReturnDateField.setBorder(null);
		
		JButton rentBtn = new JButton("대여하기");
		JButton reserveBtn = new JButton("예약하기");
		

		// Add components to the bookDetailPanel (book details)
		gbc.weightx = 1.0;
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
		bookDetailPanel.add(availRent, gbc);
		gbc.gridx = 1;
		bookDetailPanel.add(availRentField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		bookDetailPanel.add(exReturnDate, gbc);
		gbc.gridx = 1;
		bookDetailPanel.add(exReturnDateField, gbc);

		
		gbc.gridx = 0;
		gbc.gridy = 9;
		bookDetailPanel.add(summaryLabel, gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		bookDetailPanel.add(new JScrollPane(summaryArea), gbc);
		backgroundPanel.add(bookDetailPanel, BorderLayout.NORTH);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		btnPanel.setPreferredSize(new Dimension(600, 50));
		btnPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		btnPanel.add(rentBtn);
		btnPanel.add(reserveBtn);
		
		backgroundPanel.add(btnPanel, BorderLayout.CENTER);
		
		// 한줄평 패널
		JPanel commentPanel = new JPanel();
		commentPanel.setLayout(new GridBagLayout());
		commentPanel.setPreferredSize(new Dimension(600, 200));
		commentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// GridBagConstraints 초기화
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // Padding
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridwidth = 1;
		gbc.weightx = 0;
		JLabel commentLabel = new JLabel("한줄평: ");
		gbc.gridx = 0;
		gbc.gridy = 0;
		commentPanel.add(commentLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0; // 가로로 확장
		JTextField commentArea = new JTextField(20);
		commentPanel.add(commentArea, gbc);

		JLabel starLabel = new JLabel("별점");
		gbc.weightx = 0;
		gbc.gridx = 2;
		commentPanel.add(starLabel, gbc);

		String[] star = { "5", "4", "3", "2", "1" };
		JComboBox<String> stars = new JComboBox<String>(star);
		gbc.gridx = 3;
		commentPanel.add(stars, gbc);
		JButton submitButton = new JButton("등록");
		gbc.gridx = 4;
		commentPanel.add(submitButton, gbc);

		// 댓글 테이블
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 5; // 전체 폭을 차지하도록 설정
		gbc.fill = GridBagConstraints.BOTH; // 테이블이 패널의 남은 공간을 채우도록 설정
		gbc.weightx = 1.0; // 가로 확장
		gbc.weighty = 1.0; // 세로 확장

		// Table to display comments
		String[] columnNames = { "사용자명", "한줄평", "별점", "등록일" };
		Object[][] data = lsh_dao.searchReviews(bookId);
		System.out.println("data length"+data.length);
		JTable commentsTable = new JTable(data, columnNames);
		
		// Set column widths 600
		commentsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // 사용자명
		commentsTable.getColumnModel().getColumn(1).setPreferredWidth(300); // 한줄평
		commentsTable.getColumnModel().getColumn(2).setPreferredWidth(50);  // 별점
		commentsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 등록일

		// Center-align text in all columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER); // 가운데 정렬

		for (int i = 0; i < commentsTable.getColumnCount(); i++) {
		    commentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		
		JScrollPane tableScrollPane = new JScrollPane(commentsTable);
		commentsTable.setRowHeight(25);
		commentPanel.add(tableScrollPane, gbc);

		// Add the commentPanel (comments section) to the background panel (bottom part)
		backgroundPanel.add(commentPanel, BorderLayout.SOUTH);

		// Add the background panel to the main frame
		mainFrame.add(backgroundPanel);

		// Make the main frame visible
		mainFrame.setVisible(true);
	}
}
