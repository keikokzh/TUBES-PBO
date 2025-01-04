import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3308/TUBESPBO";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Menambahkan data
    public static void addBirthday(String name, String date) {
        String query = "INSERT INTO birthdays (name, date) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, date);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mendapatkan semua data
    public static List<String[]> getAllBirthdays() {
        List<String[]> birthdays = new ArrayList<>();
        String query = "SELECT * FROM birthdays";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                birthdays.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("name"), rs.getString("date")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return birthdays;
    }

    // Update data
    public static void updateBirthday(int id, String name, String date) {
        String query = "UPDATE birthdays SET name = ?, date = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, date);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menghapus data
    public static void deleteBirthday(int id) {
        String query = "DELETE FROM birthdays WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mencari berdasarkan nama
    public static List<String[]> searchByName(String name) {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT * FROM birthdays WHERE name LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("name"), rs.getString("date")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Mencari berdasarkan bulan
    public static List<String[]> searchByMonth(String month) {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT * FROM birthdays WHERE MONTH(date) = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("name"), rs.getString("date")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<String[]> searchByYear(String year) {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT * FROM birthdays WHERE YEAR(date) = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("name"), rs.getString("date")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
}
