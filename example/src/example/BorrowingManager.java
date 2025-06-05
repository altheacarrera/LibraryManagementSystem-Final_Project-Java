package example;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BorrowingManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void loadBorrowings(DefaultTableModel model) {
        loadBorrowingsQuery(model, null);
    }

    public static void loadBorrowingsByStudentNumber(String studentNumber, DefaultTableModel model) {
        loadBorrowingsQuery(model, studentNumber);
    }

    private static void loadBorrowingsQuery(DefaultTableModel model, String studentNumberFilter) {
        String sql = "SELECT b.borrowing_id, b.student_number, " +
                "m.firstname, m.lastname, " +
                "bk.title, bk.author, bk.category, " +
                "b.copies_borrowed, b.date_borrowed, b.return_date " +
                "FROM borrowings b " +
                "JOIN members m ON b.student_number = m.student_number " +
                "JOIN books bk ON b.book_id = bk.book_id";

        if (studentNumberFilter != null && !studentNumberFilter.isEmpty()) {
            sql += " WHERE b.student_number LIKE ?";
        }

        sql += " ORDER BY b.date_borrowed DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (studentNumberFilter != null && !studentNumberFilter.isEmpty()) {
                stmt.setString(1, "%" + studentNumberFilter + "%");
            }

            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0); 

            while (rs.next()) {
                String returnDate = rs.getString("return_date");
                String status = (returnDate == null || returnDate.isEmpty()) ? "Borrowed" : "Returned";

                model.addRow(new Object[]{
                        rs.getInt("borrowing_id"),
                        rs.getString("student_number"),
                        rs.getString("firstname") + " " + rs.getString("lastname"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("copies_borrowed"),
                        status,
                        rs.getString("date_borrowed"),
                        returnDate
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean borrowBook(String studentNumber, int bookId, int copiesToBorrow) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            conn.setAutoCommit(false);

            String checkSql = "SELECT copies FROM books WHERE book_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int available = rs.getInt("copies");
                    if (available < copiesToBorrow) {
                        conn.rollback();
                        return false;
                    }
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            String borrowSql = "INSERT INTO borrowings (student_number, book_id, copies_borrowed, date_borrowed) VALUES (?, ?, ?, ?)";
            try (PreparedStatement borrowStmt = conn.prepareStatement(borrowSql)) {
                borrowStmt.setString(1, studentNumber);
                borrowStmt.setInt(2, bookId);
                borrowStmt.setInt(3, copiesToBorrow);
                borrowStmt.setString(4, getCurrentTimestamp());
                borrowStmt.executeUpdate();
            }

            String updateSql = "UPDATE books SET copies = copies - ? WHERE book_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, copiesToBorrow);
                updateStmt.setInt(2, bookId);
                updateStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean returnBookFully(int borrowingId) {
        String sql = "UPDATE borrowings SET copies_borrowed = 0, status = 'Returned', return_date = NOW() WHERE borrowing_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, borrowingId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean returnBookPartially(int borrowingId, int copiesToReturn) {
        String selectSql = "SELECT copies_borrowed FROM borrowings WHERE borrowing_id = ?";
        String updateSql = "UPDATE borrowings SET copies_borrowed = ?, status = 'Borrowed' WHERE borrowing_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement psSelect = conn.prepareStatement(selectSql);
             PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {

            psSelect.setInt(1, borrowingId);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                int currentBorrowed = rs.getInt("copies_borrowed");
                int newBorrowed = currentBorrowed - copiesToReturn;
                if (newBorrowed < 0) return false;

                psUpdate.setInt(1, newBorrowed);
                psUpdate.setInt(2, borrowingId);
                return psUpdate.executeUpdate() > 0;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteBorrowing(int borrowingId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            conn.setAutoCommit(false);

            String selectSql = "SELECT book_id, copies_borrowed, return_date FROM borrowings WHERE borrowing_id = ?";
            int bookId;
            int copies;
            String returnDate;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, borrowingId);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }

                bookId = rs.getInt("book_id");
                copies = rs.getInt("copies_borrowed");
                returnDate = rs.getString("return_date");
            }

            if (returnDate == null || returnDate.isEmpty()) {
                String updateSql = "UPDATE books SET copies = copies + ? WHERE book_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, copies);
                    updateStmt.setInt(2, bookId);
                    updateStmt.executeUpdate();
                }
            }

            String deleteSql = "DELETE FROM borrowings WHERE borrowing_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, borrowingId);
                deleteStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void returnBook(int borrowingId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            conn.setAutoCommit(false);

            String selectSql = "SELECT book_id, copies_borrowed FROM borrowings WHERE borrowing_id = ?";
            int bookId;
            int copies;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, borrowingId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                    copies = rs.getInt("copies_borrowed");
                } else {
                    conn.rollback();
                    return;
                }
            }

            String updateBookSql = "UPDATE books SET copies = copies + ? WHERE book_id = ?";
            try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {
                updateBookStmt.setInt(1, copies);
                updateBookStmt.setInt(2, bookId);
                updateBookStmt.executeUpdate();
            }

            String updateBorrowingSql = "UPDATE borrowings SET return_date = ? WHERE borrowing_id = ?";
            try (PreparedStatement updateBorrowingStmt = conn.prepareStatement(updateBorrowingSql)) {
                updateBorrowingStmt.setString(1, getCurrentTimestamp());
                updateBorrowingStmt.setInt(2, borrowingId);
                updateBorrowingStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
