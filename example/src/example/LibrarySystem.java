package example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarySystem extends JFrame {

    DefaultTableModel bookModel = new DefaultTableModel(
        new String[]{"ID", "Title", "Author", "Category", "Copies", "Available"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    DefaultTableModel memberModel = new DefaultTableModel(
        new String[]{"ID", "Student Number", "Firstname", "Lastname", "MI", "Gender", "Year", "Program", "Address"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    DefaultTableModel borrowingModel = new DefaultTableModel(
        new String[]{"ID", "Member", "Book", "Borrow Date", "Return Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public LibrarySystem() {
        setTitle("LIBRARY MANAGEMENT SYSTEM");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

     // -------------------- Books Tab --------------------
        JPanel bookPanel = new JPanel(new BorderLayout());

        JTextField tfTitle = new JTextField();
        tfTitle.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfAuthor = new JTextField();
        tfAuthor.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfCategory = new JTextField();
        tfCategory.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfCopies = new JTextField();
        tfCopies.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JTable bookTable = new JTable(bookModel);
        bookTable.setFont(new Font("Tahoma", Font.PLAIN, 13));
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 15));
        applyRowSelectionHighlight(bookTable);

        JButton addBook = new JButton("Add Book");
        addBook.setFont(new Font("Tahoma", Font.BOLD, 13));
        JButton updateBook = new JButton("Update Book");
        updateBook.setFont(new Font("Tahoma", Font.BOLD, 13));
        JButton deleteBook = new JButton("Delete Book");
        deleteBook.setFont(new Font("Tahoma", Font.BOLD, 13));

        addBook.addActionListener(e -> {
            try {
                String title = tfTitle.getText().trim();
                String author = tfAuthor.getText().trim();
                String category = tfCategory.getText().trim();
                int copies = Integer.parseInt(tfCopies.getText().trim());
                BookManager.addBook(title, author, category, copies);
                BookManager.loadBooks(bookModel);
                clearBookFields(tfTitle, tfAuthor, tfCategory, tfCopies);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(bookPanel, "Fill all book fields correctly.");
            }
        });

        updateBook.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row != -1) {
                try {
                    int id = (int) bookModel.getValueAt(row, 0);
                    String title = tfTitle.getText().trim();
                    String author = tfAuthor.getText().trim();
                    String category = tfCategory.getText().trim();
                    int copies = Integer.parseInt(tfCopies.getText().trim());
                    BookManager.updateBook(id, title, author, category, copies);
                    BookManager.loadBooks(bookModel);
                    clearBookFields(tfTitle, tfAuthor, tfCategory, tfCopies);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookPanel, "Select a row and fill book fields correctly.");
                }
            }
        });

        deleteBook.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row != -1) {
                int id = (int) bookModel.getValueAt(row, 0);
                BookManager.deleteBook(id);
                BookManager.loadBooks(bookModel);
                clearBookFields(tfTitle, tfAuthor, tfCategory, tfCopies);
            }
        });

        bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            int lastSelectedRow = -1;

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = bookTable.rowAtPoint(e.getPoint());

                if (row == lastSelectedRow) {
                    bookTable.clearSelection();
                    clearBookFields(tfTitle, tfAuthor, tfCategory, tfCopies);
                    lastSelectedRow = -1;
                } else {
                    if (row != -1) {
                        tfTitle.setText(bookModel.getValueAt(row, 1).toString());
                        tfAuthor.setText(bookModel.getValueAt(row, 2).toString());
                        tfCategory.setText(bookModel.getValueAt(row, 3).toString());
                        tfCopies.setText(bookModel.getValueAt(row, 4).toString());
                        lastSelectedRow = row;
                    }
                }
            }
        });

        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setPreferredSize(new Dimension(700, 0));

        JTextField tfBookSearch = new JTextField(20);
        tfBookSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tfBookSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void search() {
                BookManager.loadBooks(bookModel, tfBookSearch.getText().trim());
            }
        });

        JPanel bookTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label_5 = new JLabel("Search title:");
        label_5.setFont(new Font("Tahoma", Font.BOLD, 13));
        bookTopPanel.add(label_5);
        bookTopPanel.add(tfBookSearch);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(bookTopPanel, BorderLayout.NORTH);
        centerPanel.add(bookScrollPane, BorderLayout.CENTER);

        JPanel bookFormPanel = new JPanel();
        bookFormPanel.setLayout(new BoxLayout(bookFormPanel, BoxLayout.Y_AXIS));
        bookFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tfTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, tfTitle.getPreferredSize().height));
        tfAuthor.setMaximumSize(new Dimension(Integer.MAX_VALUE, tfAuthor.getPreferredSize().height));
        tfCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, tfCategory.getPreferredSize().height));
        tfCopies.setMaximumSize(new Dimension(Integer.MAX_VALUE, tfCopies.getPreferredSize().height));

        JLabel lblNewLabel = new JLabel("ADD NEW BOOK");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        bookFormPanel.add(lblNewLabel);
        bookFormPanel.add(Box.createVerticalStrut(10));

        JLabel label = new JLabel("Title:");
        label.setFont(new Font("Tahoma", Font.BOLD, 13));
        bookFormPanel.add(label);
        bookFormPanel.add(tfTitle);
        bookFormPanel.add(Box.createVerticalStrut(5));

        JLabel label_1 = new JLabel("Author:");
        label_1.setFont(new Font("Tahoma", Font.BOLD, 13));
        bookFormPanel.add(label_1);
        bookFormPanel.add(tfAuthor);
        bookFormPanel.add(Box.createVerticalStrut(5));

        JLabel label_2 = new JLabel("Category:");
        label_2.setFont(new Font("Tahoma", Font.BOLD, 13));
        bookFormPanel.add(label_2);
        bookFormPanel.add(tfCategory);
        bookFormPanel.add(Box.createVerticalStrut(5));

        JLabel label_3 = new JLabel("Copies:");
        label_3.setFont(new Font("Tahoma", Font.BOLD, 13));
        bookFormPanel.add(label_3);
        bookFormPanel.add(tfCopies);
        bookFormPanel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(addBook);
        buttonPanel.add(updateBook);
        buttonPanel.add(deleteBook);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, addBook.getPreferredSize().height));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookFormPanel.add(buttonPanel);

        JSplitPane bookSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, bookFormPanel);
        bookSplit.setResizeWeight(0.75);

        bookPanel.add(bookSplit, BorderLayout.CENTER);
        tabs.add("Books", bookPanel);

        BookManager.loadBooks(bookModel);




     // -------------------- Members Tab --------------------
        JPanel memberPanel = new JPanel(new BorderLayout());

        JTextField tfStudNum = new JTextField();
        tfStudNum.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfFirstName = new JTextField();
        tfFirstName.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfLastName = new JTextField();
        tfLastName.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfMI = new JTextField();
        tfMI.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"", "Male", "Female", "Other"});
        cbGender.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JComboBox<String> cbYear = new JComboBox<>(new String[]{"", "1st", "2nd", "3rd", "4th"});
        cbYear.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JComboBox<String> cbProgram = new JComboBox<>(new String[]{"", "BSIT", "BSAIS", "BEED", "BSE", "BSCRIM", "BSHM", "BSA"});
        cbProgram.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfAddress = new JTextField();
        tfAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfMemberSearch = new JTextField(20);
        tfMemberSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JTable memberTable = new JTable(memberModel);
        memberTable.setFont(new Font("Tahoma", Font.PLAIN, 13));
        memberTable.setRowHeight(25);
        memberTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 15));
        applyRowSelectionHighlight(memberTable);

        JButton addMember = new JButton("Add Member");
        addMember.setFont(new Font("Tahoma", Font.BOLD, 13));
        JButton updateMember = new JButton("Update Member");
        updateMember.setFont(new Font("Tahoma", Font.BOLD, 13));
        JButton deleteMember = new JButton("Delete Member");
        deleteMember.setFont(new Font("Tahoma", Font.BOLD, 13));

        addMember.addActionListener(e -> {
            try {
                String studNum = tfStudNum.getText().trim();
                String firstName = tfFirstName.getText().trim();
                String lastName = tfLastName.getText().trim();
                String mi = tfMI.getText().trim();
                String gender = (String) cbGender.getSelectedItem();
                String year = (String) cbYear.getSelectedItem();
                String program = (String) cbProgram.getSelectedItem();
                String address = tfAddress.getText().trim();

                if (studNum.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || mi.isEmpty() ||
                    gender == null || gender.isEmpty() ||
                    year == null || year.isEmpty() ||
                    program == null || program.isEmpty() ||
                    address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in ALL fields.");
                    return;
                }

                if (MemberManager.memberExists(studNum)) {
                    JOptionPane.showMessageDialog(this, "Student Number already exists.");
                    return;
                }

                MemberManager.addMember(studNum, firstName, lastName, mi, gender, year, program, address);
                MemberManager.loadMembers(memberModel);
                clearMemberFields(tfStudNum, tfFirstName, tfLastName, tfMI, cbGender, cbYear, cbProgram, tfAddress);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fill all fields correctly.");
            }
        });

        updateMember.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row != -1) {
                try {
                    int id = (int) memberModel.getValueAt(row, 0);
                    String studNum = tfStudNum.getText().trim();
                    String firstName = tfFirstName.getText().trim();
                    String lastName = tfLastName.getText().trim();
                    String mi = tfMI.getText().trim();
                    String gender = (String) cbGender.getSelectedItem();
                    String year = (String) cbYear.getSelectedItem();
                    String program = (String) cbProgram.getSelectedItem();
                    String address = tfAddress.getText().trim();

                    if (studNum.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || mi.isEmpty() ||
                        gender == null || gender.isEmpty() ||
                        year == null || year.isEmpty() ||
                        program == null || program.isEmpty() ||
                        address.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill in ALL fields.");
                        return;
                    }

                    MemberManager.updateMember(id, studNum, firstName, lastName, mi, gender, year, program, address);
                    MemberManager.loadMembers(memberModel);
                    clearMemberFields(tfStudNum, tfFirstName, tfLastName, tfMI, cbGender, cbYear, cbProgram, tfAddress);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Select a row and fill fields correctly.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a member row to update.");
            }
        });

        deleteMember.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row != -1) {
                int id = (int) memberModel.getValueAt(row, 0);
                MemberManager.deleteMember(id);
                MemberManager.loadMembers(memberModel);
                clearMemberFields(tfStudNum, tfFirstName, tfLastName, tfMI, cbGender, cbYear, cbProgram, tfAddress);
            }
        });

        memberTable.addMouseListener(new java.awt.event.MouseAdapter() {
            int lastSelectedRow = -1;
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = memberTable.rowAtPoint(e.getPoint());
                if (row == lastSelectedRow) {
                    memberTable.clearSelection();
                    clearMemberFields(tfStudNum, tfFirstName, tfLastName, tfMI, cbGender, cbYear, cbProgram, tfAddress);
                    lastSelectedRow = -1;
                } else {
                    if (row != -1) {
                        tfStudNum.setText(memberModel.getValueAt(row, 1).toString());
                        tfFirstName.setText(memberModel.getValueAt(row, 2).toString());
                        tfLastName.setText(memberModel.getValueAt(row, 3).toString());
                        tfMI.setText(memberModel.getValueAt(row, 4).toString());
                        cbGender.setSelectedItem(memberModel.getValueAt(row, 5).toString());
                        cbYear.setSelectedItem(memberModel.getValueAt(row, 6).toString());
                        cbProgram.setSelectedItem(memberModel.getValueAt(row, 7).toString());
                        tfAddress.setText(memberModel.getValueAt(row, 8).toString());
                        lastSelectedRow = row;
                    }
                }
            }
        });

        tfMemberSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }

            private void search() {
                MemberManager.loadMembers(memberModel, tfMemberSearch.getText().trim());
            }
        });

        JPanel memberTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label_4 = new JLabel("Search Student No:");
        label_4.setFont(new Font("Tahoma", Font.BOLD, 13));
        memberTopPanel.add(label_4);
        memberTopPanel.add(tfMemberSearch);
        memberPanel.add(memberTopPanel, BorderLayout.NORTH);

        JScrollPane memberScrollPane = new JScrollPane(memberTable);
        memberScrollPane.setPreferredSize(new Dimension(700, 0));

        JPanel memberFormPanel = new JPanel();
        memberFormPanel.setLayout(new BoxLayout(memberFormPanel, BoxLayout.Y_AXIS));
        memberFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField[] memberTextFields = {tfStudNum, tfFirstName, tfLastName, tfMI, tfAddress};
        for (JTextField tf : memberTextFields) {
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, tf.getPreferredSize().height));
            tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        cbGender.setMaximumSize(new Dimension(Integer.MAX_VALUE, cbGender.getPreferredSize().height));
        cbGender.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbYear.setMaximumSize(new Dimension(Integer.MAX_VALUE, cbYear.getPreferredSize().height));
        cbYear.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbProgram.setMaximumSize(new Dimension(Integer.MAX_VALUE, cbProgram.getPreferredSize().height));
        cbProgram.setAlignmentX(Component.LEFT_ALIGNMENT);

        memberFormPanel.add(new JLabel("MEMBER INFORMATION")).setFont(new Font("Tahoma", Font.BOLD, 18));
        memberFormPanel.add(Box.createVerticalStrut(5));

        String[] labels = {"Student Number:", "First Name:", "Last Name:", "Middle Initial:", "Gender:", "Year:", "Program:", "Address:"};
        Component[] inputs = {tfStudNum, tfFirstName, tfLastName, tfMI, cbGender, cbYear, cbProgram, tfAddress};

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Tahoma", Font.BOLD, 13));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            memberFormPanel.add(lbl);
            memberFormPanel.add(inputs[i]);
            memberFormPanel.add(Box.createVerticalStrut(5));
        }

        addMember.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateMember.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteMember.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel memberButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        memberButtonPanel.add(addMember);
        memberButtonPanel.add(updateMember);
        memberButtonPanel.add(deleteMember);
        memberButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, addMember.getPreferredSize().height));
        memberButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberFormPanel.add(memberButtonPanel);

        JSplitPane memberSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, memberScrollPane, memberFormPanel);
        memberSplit.setResizeWeight(0.75);
        memberPanel.add(memberSplit, BorderLayout.CENTER);

        tabs.add("Members", memberPanel);
        MemberManager.loadMembers(memberModel);

     // -------------------- Borrowings Tab --------------------
        String[] borrowingColumns = {
            "ID", "Student Number", "Member", "Title", "Author", "Category", "Borrowed Copies", "Status", "Date Borrowed", "Return Date"
        };

        DefaultTableModel borrowingModel = new DefaultTableModel(borrowingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable borrowingTable = new JTable(borrowingModel);

	     borrowingTable.setFont(new Font("Tahoma", Font.PLAIN, 13));
	     borrowingTable.setRowHeight(25);
	     borrowingTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 15)); 
	
	     applyRowSelectionHighlight(borrowingTable);


        borrowingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = borrowingTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    borrowingTable.setRowSelectionInterval(row, row);

                    Object[] options = {"Return", "Delete", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(
                        null,
                        "Choose an action for this borrowing record:",
                        "Select Action",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]  
                    );

                    if (choice == JOptionPane.YES_OPTION) { 
                        String status = borrowingTable.getModel().getValueAt(row, 7).toString(); 
                        if ("Returned".equalsIgnoreCase(status)) {
                            JOptionPane.showMessageDialog(null, "This book has already been returned.");
                            return;
                        }

                        int borrowingId = (int) borrowingTable.getModel().getValueAt(row, 0);
                        int borrowedCopies = Integer.parseInt(borrowingTable.getModel().getValueAt(row, 6).toString()); 

                        String input = JOptionPane.showInputDialog(
                            null,
                            "Enter number of copies to return (borrowed: " + borrowedCopies + "):",
                            "Return Copies",
                            JOptionPane.QUESTION_MESSAGE
                        );

                        if (input == null) {
                            return; 
                        }

                        int copiesToReturn;
                        try {
                            copiesToReturn = Integer.parseInt(input.trim());
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
                            return;
                        }

                        if (copiesToReturn <= 0) {
                            JOptionPane.showMessageDialog(null, "Number of copies to return must be positive.");
                            return;
                        }

                        if (copiesToReturn > borrowedCopies) {
                            JOptionPane.showMessageDialog(null, "Cannot return more copies than borrowed.");
                            return;
                        }

                        if (copiesToReturn == borrowedCopies) {
                            
                            boolean success = BorrowingManager.returnBookFully(borrowingId);
                            if (success) {
                                JOptionPane.showMessageDialog(null, "All copies returned successfully.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to return book.");
                            }
                        } else {
                            
                            boolean success = BorrowingManager.returnBookPartially(borrowingId, copiesToReturn);
                            if (success) {
                                JOptionPane.showMessageDialog(null, copiesToReturn + " copies returned successfully. Remaining copies are still borrowed.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to return book copies.");
                            }
                        }

                        BorrowingManager.loadBorrowings((DefaultTableModel) borrowingTable.getModel());

                    } else if (choice == JOptionPane.NO_OPTION) { 
                        String status = borrowingTable.getModel().getValueAt(row, 7).toString(); 
                        if (!"Returned".equalsIgnoreCase(status)) {
                            JOptionPane.showMessageDialog(null, "Cannot delete record. The book has not been returned yet.");
                            return;
                        }

                        int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to delete this borrowing record?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            int borrowingId = (int) borrowingTable.getModel().getValueAt(row, 0);
                            boolean success = BorrowingManager.deleteBorrowing(borrowingId);
                            if (success) {
                                JOptionPane.showMessageDialog(null, "Record deleted successfully.");
                                BorrowingManager.loadBorrowings((DefaultTableModel) borrowingTable.getModel());
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to delete record.");
                            }
                        }
                    }
                    
                }
            }
        });


        JScrollPane borrowingScrollPane = new JScrollPane(borrowingTable);
        borrowingScrollPane.setPreferredSize(new Dimension(700, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        JLabel searchLabel = new JLabel("Search Student No:");
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        JTextField tfSearchStudentNumber = new JTextField(20); 
        tfSearchStudentNumber.setFont(new Font("Tahoma", Font.PLAIN, 13));

        searchPanel.add(searchLabel);
        searchPanel.add(tfSearchStudentNumber);
        
        tfSearchStudentNumber.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterBorrowings();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterBorrowings();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterBorrowings();
            }

            private void filterBorrowings() {
                String keyword = tfSearchStudentNumber.getText().trim();
                if (keyword.isEmpty()) {
                    BorrowingManager.loadBorrowings(borrowingModel);
                } else {
                    BorrowingManager.loadBorrowingsByStudentNumber(keyword, borrowingModel);
                }
            }
        });

        JTextField tfStudentNumber = new JTextField();
        tfStudentNumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfBorrowBookId = new JTextField();
        tfBorrowBookId.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JTextField tfCopiesToBorrow = new JTextField();
        tfCopiesToBorrow.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JTextField[] borrowingFields = {tfStudentNumber, tfBorrowBookId, tfCopiesToBorrow};
        for (JTextField tf : borrowingFields) {
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, tf.getPreferredSize().height));
        }

        JButton btnBorrow = new JButton("Borrow Book");
        btnBorrow.setFont(new Font("Tahoma", Font.BOLD, 13));
        JButton btnRefreshBorrowings = new JButton("Refresh List");
        btnRefreshBorrowings.setFont(new Font("Tahoma", Font.BOLD, 13));

        JPanel borrowingFormPanel = new JPanel();
        borrowingFormPanel.setLayout(new BoxLayout(borrowingFormPanel, BoxLayout.Y_AXIS));
        borrowingFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblNewLabel_1 = new JLabel("NEW BOOK BORROWED ENTRY");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 18));
        borrowingFormPanel.add(lblNewLabel_1);
        
        Component verticalStrut_1 = Box.createVerticalStrut(5);
        borrowingFormPanel.add(verticalStrut_1);
        
        Component verticalStrut = Box.createVerticalStrut(5);
        borrowingFormPanel.add(verticalStrut);
        
        Component verticalStrut_2 = Box.createVerticalStrut(5);
        borrowingFormPanel.add(verticalStrut_2);

        JLabel label_6 = new JLabel("Student Number:");
        label_6.setFont(new Font("Tahoma", Font.BOLD, 13));
        borrowingFormPanel.add(label_6);
        borrowingFormPanel.add(tfStudentNumber);
        borrowingFormPanel.add(Box.createVerticalStrut(5));

        JLabel label_7 = new JLabel("Book ID:");
        label_7.setFont(new Font("Tahoma", Font.BOLD, 13));
        borrowingFormPanel.add(label_7);
        borrowingFormPanel.add(tfBorrowBookId);
        borrowingFormPanel.add(Box.createVerticalStrut(5));

        JLabel label_8 = new JLabel("Copies to Borrow:");
        label_8.setFont(new Font("Tahoma", Font.BOLD, 13));
        borrowingFormPanel.add(label_8);
        borrowingFormPanel.add(tfCopiesToBorrow);
        borrowingFormPanel.add(Box.createVerticalStrut(10));

        JPanel borrowingButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        borrowingButtonPanel.add(btnBorrow);
        borrowingButtonPanel.add(btnRefreshBorrowings);
        borrowingButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnBorrow.getPreferredSize().height));
        borrowingButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        borrowingFormPanel.add(borrowingButtonPanel);


        JSplitPane borrowingSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, borrowingScrollPane, borrowingFormPanel);
        borrowingSplit.setResizeWeight(0.75);

        JPanel borrowingPanel = new JPanel(new BorderLayout());
        borrowingPanel.add(searchPanel, BorderLayout.NORTH);
        borrowingPanel.add(borrowingSplit, BorderLayout.CENTER);

        tabs.add("Borrowings", borrowingPanel);

        MemberManager.loadMembers(memberModel);
        BookManager.loadBooks(bookModel);
        BorrowingManager.loadBorrowings(borrowingModel);

        btnBorrow.addActionListener(e -> {
            String studentNumber = tfStudentNumber.getText().trim();
            String bookIdStr = tfBorrowBookId.getText().trim();
            String copiesStr = tfCopiesToBorrow.getText().trim();

            if (studentNumber.isEmpty() || bookIdStr.isEmpty() || copiesStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            try {
                int bookId = Integer.parseInt(bookIdStr);
                int copies = Integer.parseInt(copiesStr);

                boolean success = BorrowingManager.borrowBook(studentNumber, bookId, copies);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Book borrowed successfully.");
                    BorrowingManager.loadBorrowings(borrowingModel);
                    tfStudentNumber.setText("");
                    tfBorrowBookId.setText("");
                    tfCopiesToBorrow.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Not enough available copies.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Book ID and Copies must be numbers.");
            }
        });

        btnRefreshBorrowings.addActionListener(e -> {
            tfSearchStudentNumber.setText("");
            BorrowingManager.loadBorrowings(borrowingModel);
        });

        getContentPane().add(tabs);
        setVisible(true);
    }

    private void applyRowSelectionHighlight(JTable table) {
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(Color.LIGHT_GRAY);
        table.setSelectionBackground(Color.LIGHT_GRAY);
    }

    private void clearBookFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    public void clearMemberFields(JTextField tfStudNum, JTextField tfFirstName, JTextField tfLastName,
                                  JTextField tfMI, JComboBox<String> cbGender, JComboBox<String> cbYear,
                                  JComboBox<String> cbProgram, JTextField tfAddress) {
        tfStudNum.setText("");
        tfFirstName.setText("");
        tfLastName.setText("");
        tfMI.setText("");
        cbGender.setSelectedIndex(0);
        cbYear.setSelectedIndex(0);
        cbProgram.setSelectedIndex(0);
        tfAddress.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySystem());
    }
}
