/* 희망도서신청 화면 */
package user.UserUI;

import DTO.RECOMMENDBOOKS;
import user.DAO.RecommendBookDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.table.DefaultTableModel;
import java.util.Date;

public class RecommendBook extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField bookNameField;
    private JTextField writerField;
    private JTextField publisherField;
    private JTextField pubDateField;
    private String userId;
    
    RecommendBookDAO dao = new RecommendBookDAO();

    public RecommendBook(String userId) {
    	this.userId = userId;
        setLayout(new BorderLayout());

        // 희망도서 신청 폼
        JPanel requestPanel = createRequestPanel();
        // 신청 목록 테이블
        JPanel listPanel = getListPanel();

        // SplitPane 설정 (상단 패널과 하단 패널을 분리)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, requestPanel, listPanel);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);

        add(splitPane, BorderLayout.CENTER);
    }

    // 희망도서 신청 폼
    private JPanel createRequestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 도서명
        JLabel bookNameLabel = new JLabel("도서명 :");
        bookNameField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(bookNameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9;
        panel.add(bookNameField, gbc);

        // 저자
        JLabel writerLabel = new JLabel("저자 :");
        writerField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(writerLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9;
        panel.add(writerField, gbc);

        // 출판사
        JLabel publisherLabel = new JLabel("출판사 :");
        publisherField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(publisherLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9;
        panel.add(publisherField, gbc);

        // 출판일
        JLabel pubDateLabel = new JLabel("출판일 (YYYY-MM-DD) :");
        pubDateField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(pubDateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.9;
        panel.add(pubDateField, gbc);

        // 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("도서 신청");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitRecommendation();
            }
        });
        JButton cancelButton = new JButton("신청 취소");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRecommendation();
            }
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    // 신청 목록 테이블
    private JPanel getListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"신청번호", "유저명", "도서명", "저자", "출판사", "출판일", "신청일", "완료 여부"};
        Object[][] data = dao.getRcmBookList(userId);
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가
            }
        };
        table = new JTable(tableModel);

        // 유저명 컬럼 숨기기
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 상/좌/하/우에 10px 여백을 추가
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // 희망도서 신청
    private void submitRecommendation() {
        String bookName = bookNameField.getText();
        String writer = writerField.getText();
        String publisher = publisherField.getText();
        String pubDateStr = pubDateField.getText();
        Date pubDate = null;

        if (bookName.isEmpty() || writer.isEmpty() || publisher.isEmpty() || pubDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.");
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            pubDate = dateFormat.parse(pubDateStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "출판일은 형식에 맞게 입력하세요.");
            return;
        }

        // 데이터 저장
        RECOMMENDBOOKS recommendBook = new RECOMMENDBOOKS();
        recommendBook.setUserID(userId);
        recommendBook.setBookName(bookName);
        recommendBook.setWriter(writer);
        recommendBook.setPublisher(publisher);
        recommendBook.setPubDate(pubDate);

        if (dao.insertRcmBook(recommendBook) > 0) {
            JOptionPane.showMessageDialog(this, "희망도서 신청이 완료되었습니다.");
            
            String[] columnNames = {"신청번호", "유저명", "도서명", "저자", "출판사", "출판일", "신청일", "완료 여부"};
            Object[][] newData = dao.getRcmBookList(userId);
            DefaultTableModel newTableModel = new DefaultTableModel(newData, columnNames);
            table.setModel(newTableModel);
            table.revalidate();
            table.repaint();
            
        } else {
            JOptionPane.showMessageDialog(this, "희망도서 신청에 실패했습니다.");
        }

        bookNameField.setText("");
        writerField.setText("");
        publisherField.setText("");
        pubDateField.setText("");
    }
    
    // 희망도서 신청 취소
    private void cancelRecommendation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Object selectedValue = table.getValueAt(selectedRow, 0);  // 신청번호
            int rcmId = (int) selectedValue;
            
            if("완료".equals(table.getValueAt(selectedRow, 7))) { // 완료 여부
            	JOptionPane.showMessageDialog(this, "처리 완료된 신청은 취소할 수 없습니다.");
            	return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "희망도서 신청을 취소하시겠습니까?", 
                    "취소 확인", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            // 신청 취소 처리
            if (confirm == JOptionPane.YES_OPTION) {
            	if (dao.cancelRcmBook(rcmId) > 0) {
                    JOptionPane.showMessageDialog(this, "희망도서 신청이 취소되었습니다.");
                    
                    // 테이블 갱신
                    String[] columnNames = {"신청번호", "유저명", "도서명", "저자", "출판사", "출판일", "신청일", "완료 여부"};
                    Object[][] newData = dao.getRcmBookList(userId);
                    DefaultTableModel newTableModel = new DefaultTableModel(newData, columnNames);
                    table.setModel(newTableModel);
                    table.revalidate();
                    table.repaint();
                    
                } else {
                    JOptionPane.showMessageDialog(this, "희망도서 신청 취소에 실패했습니다.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "취소할 신청을 선택하세요.");
        }
    }
}
