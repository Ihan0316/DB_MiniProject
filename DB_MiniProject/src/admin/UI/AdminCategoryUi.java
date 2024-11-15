package admin.UI;

import javax.swing.*;
import admin.DAO.CategoryDao;
import DTO.CATEGORIES;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminCategoryUi extends JFrame {

    private JTextField categoryNameField;  // 카테고리 이름 입력 필드
    private JTextArea descriptionArea;     // 카테고리 설명 입력 필드
    private JList<String> categoryList;    // 카테고리 목록을 보여주는 JList
    private DefaultListModel<String> listModel; // JList의 모델

    public AdminCategoryUi() {
        setTitle("카테고리 관리");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 카테고리 이름과 설명을 입력할 수 있는 필드
        JLabel categoryNameLabel = new JLabel("카테고리 이름:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(categoryNameLabel, gbc);

        categoryNameField = new JTextField(20);
        gbc.gridx = 1;
        add(categoryNameField, gbc);

        JLabel descriptionLabel = new JLabel("카테고리 설명:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        add(scrollPane, gbc);

        // 카테고리 목록을 표시할 JList
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(categoryList);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(listScrollPane, gbc);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("카테고리 추가");
        addButton.addActionListener(e -> addCategory());
        JButton deleteButton = new JButton("카테고리 삭제");
        deleteButton.addActionListener(e -> deleteCategory());
        JButton updateButton = new JButton("카테고리 수정");
        updateButton.addActionListener(e -> updateCategory());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(buttonPanel, gbc);

        // 목록에서 선택하면 정보를 입력란에 표시하기
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedCategory = categoryList.getSelectedValue();
                if (selectedCategory != null) {
                    loadCategoryDetails(selectedCategory);
                }
            }
        });

        loadCategories();  // 초기 카테고리 목록 로드
        setVisible(true);
    }

	private void loadCategories() {
		// TODO Auto-generated method stub
		
	}

	private void loadCategoryDetails(String selectedCategory) {
		// TODO Auto-generated method stub
		
	}

	private Object updateCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object deleteCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object addCategory() {
		// TODO Auto-generated method stub
		return null;
	}

    // Methods remain the same (loadCategoryDetails, addCategory, deleteCategory, loadCategories, updateCategory, clearFields, etc.)
}
