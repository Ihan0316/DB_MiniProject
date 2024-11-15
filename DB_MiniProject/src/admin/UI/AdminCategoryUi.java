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
        setLayout(new GridLayout(0,2,10,10));
        
        // 카테고리 이름과 설명을 입력할 수 있는 필드
        JLabel categoryNameLabel = new JLabel("카테고리 이름:");
        categoryNameField = new JTextField(20);
        
        JLabel descriptionLabel = new JLabel("카테고리 설명:");
        descriptionArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        
        // 카테고리 추가 버튼
        JButton addButton = new JButton("카테고리 추가");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCategory();
            }
        });
        
        // 카테고리 삭제 버튼
        JButton deleteButton = new JButton("카테고리 삭제");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCategory();
            }
        });
        
     // 카테고리 수정 버튼 추가
        JButton updateButton = new JButton("카테고리 수정");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCategory();
            }
        });

        
        // 카테고리 목록을 표시할 JList
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(categoryList);
        
        //JList 크기 조정
        categoryList.setVisibleRowCount(10);
        categoryList.setFixedCellHeight(17);
        categoryList.setFixedCellWidth(350);
        
     // 목록에서 선택하면 정보를 입력란에 표시하기
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedCategory = categoryList.getSelectedValue();
                if (selectedCategory != null) {
                    loadCategoryDetails(selectedCategory); // 메서드 호출
                }
            }
        });

        // UI에 컴포넌트 추가
        add(categoryNameLabel);
        add(categoryNameField);
        add(descriptionLabel);
        add(scrollPane);
        add(addButton);
        add(deleteButton);
        add(listScrollPane);
        loadCategories();  // 초기 카테고리 목록 로드
        setVisible(true);
    }
    
 // 선택한 카테고리의 정보를 입력란에 표시하는 메서드
    private void loadCategoryDetails(String categoryName) {
        CategoryDao categoryDao = new CategoryDao();
        try {
            // DB에서 선택한 카테고리 정보 가져오기
            CATEGORIES category = categoryDao.getCategoryByName(categoryName);
            // 입력란에 해당 카테고리 정보 채우기
            categoryNameField.setText(category.getCategoryName());
            descriptionArea.setText(category.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

	// 카테고리 추가 메서드
    private void addCategory() {
        String categoryName = categoryNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        if (categoryName.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 새로운 카테고리 객체 생성
        CATEGORIES newCategory = new CATEGORIES();
        newCategory.setCategoryName(categoryName);
        newCategory.setDescription(description);
        
        // 카테고리 추가 DB 처리
        CategoryDao categoryDao = new CategoryDao();
        categoryDao.addCategory(newCategory);
        JOptionPane.showMessageDialog(this, "카테고리가 추가되었습니다.");
        clearFields();
        loadCategories();  // 카테고리 목록 갱신
    }
    
    // 카테고리 삭제 메서드
    private void deleteCategory() {
        String selectedCategory = categoryList.getSelectedValue();  // 선택된 카테고리 가져오기
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "삭제할 카테고리를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 카테고리 삭제 DB 처리
        CategoryDao categoryDao = new CategoryDao();
        boolean success = categoryDao.deleteCategory(selectedCategory);
        if (success) {
            JOptionPane.showMessageDialog(this, "카테고리가 삭제되었습니다.");
            loadCategories();  // 카테고리 목록 갱신
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 카테고리가 존재하지 않습니다.", "삭제 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 카테고리 목록 로드 메서드
    private void loadCategories() {
        CategoryDao categoryDao = new CategoryDao();
        try {
            List<CATEGORIES> categories = categoryDao.getAllCategories();  // 모든 카테고리 목록을 DB에서 조회
            listModel.clear();  // 기존 목록을 비우고 새로 갱신
            System.out.println("카테고리 목록 크기: " + categories.size()); // 로드된 카테고리 수 확인
            for (CATEGORIES category : categories) {
                System.out.println("카테고리 이름: " + category.getCategoryName()); // 각 카테고리 이름 확인
                listModel.addElement(category.getCategoryName());  // 카테고리 이름을 JList에 추가
            }
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 콘솔에 출력
            JOptionPane.showMessageDialog(this, "카테고리 목록 로드 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

 // 수정된 카테고리를 DB에 저장하는 메서드
    private void updateCategory() {
        String categoryName = categoryNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String selectedCategory = categoryList.getSelectedValue();

        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "수정할 카테고리를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (categoryName.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 수정된 카테고리 객체 생성
        CATEGORIES updatedCategory = new CATEGORIES();
        updatedCategory.setCategoryName(categoryName);
        updatedCategory.setDescription(description);

        // 카테고리 수정 DB 처리
        CategoryDao categoryDao = new CategoryDao();
        boolean success = categoryDao.updateCategory(selectedCategory, updatedCategory);
        if (success) {
            JOptionPane.showMessageDialog(this, "카테고리가 수정되었습니다.");
            loadCategories();  // 카테고리 목록 갱신
        } else {
            JOptionPane.showMessageDialog(this, "수정 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    // 입력 필드 초기화 메서드
    private void clearFields() {
        categoryNameField.setText("");
        descriptionArea.setText("");
    }
    public static void main(String[] args) {
        new AdminCategoryUi(); // 카테고리 관리 UI 실행
    }
}
