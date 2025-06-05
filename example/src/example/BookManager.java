package example;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookManager {

    public static void addBook(String title, String author, String category, int copies) {
        if (bookExists(title, author, category)) {
            JOptionPane.showMessageDialog(null, "Book already exists!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO books (title, author, category, copies) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setInt(4, copies);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBook(int id, String title, String author, String category, int copies) {
        if (bookExistsExcludeId(title, author, category, id)) {
            JOptionPane.showMessageDialog(null, "Another book with the same title, author, and category already exists!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE books SET title=?, author=?, category=?, copies=? WHERE book_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setInt(4, copies);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBook(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadBooks(DefaultTableModel model) {
        loadBooks(model, "");
    }

    public static void loadBooks(DefaultTableModel model, String keyword) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM books WHERE title LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                int copies = rs.getInt("copies");
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    copies,
                    copies > 0 ? "Yes" : "No" 
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean bookExists(String title, String author, String category) {
        String sql = "SELECT COUNT(*) FROM books WHERE title = ? AND author = ? AND category = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean bookExistsExcludeId(String title, String author, String category, int excludeId) {
        String sql = "SELECT COUNT(*) FROM books WHERE title = ? AND author = ? AND category = ? AND book_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setInt(4, excludeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
