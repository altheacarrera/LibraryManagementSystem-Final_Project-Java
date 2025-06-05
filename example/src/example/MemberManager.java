package example;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MemberManager {

    public static void addMember(String studNum, String firstName, String lastName, String mi,
                                 String gender, String year, String program, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM members WHERE student_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, studNum);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Student Number already exists.");
                return;
            }

            String sql = "INSERT INTO members (student_number, firstname, lastname, mi, gender, year, program, address) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studNum);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, mi);
            stmt.setString(5, gender);
            stmt.setString(6, year);
            stmt.setString(7, program);
            stmt.setString(8, address);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Member added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean memberExists(String studNum) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM members WHERE student_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studNum);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateMember(int id, String studNum, String firstName, String lastName, String mi,
                                    String gender, String year, String program, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE members SET student_number = ?, firstname = ?, lastname = ?, mi = ?, gender = ?, " +
                         "year = ?, program = ?, address = ? WHERE member_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studNum);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, mi);
            stmt.setString(5, gender);
            stmt.setString(6, year);
            stmt.setString(7, program);
            stmt.setString(8, address);
            stmt.setInt(9, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Member updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMember(int memberId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM members WHERE member_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Member deleted.");
            } else {
                JOptionPane.showMessageDialog(null, "Member not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadMembers(DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM members";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("member_id"),
                    rs.getString("student_number"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("mi"),
                    rs.getString("gender"),
                    rs.getString("year"),
                    rs.getString("program"),
                    rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadMembers(DefaultTableModel model, String studNum) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM members WHERE student_number LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + studNum + "%");
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("member_id"),
                    rs.getString("student_number"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("mi"),
                    rs.getString("gender"),
                    rs.getString("year"),
                    rs.getString("program"),
                    rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
