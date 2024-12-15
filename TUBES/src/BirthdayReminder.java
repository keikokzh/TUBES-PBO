import java.sql.Connection;
import java.sql.PreparedStatement;

public class BirthdayReminder extends BaseReminder {

    public BirthdayReminder(String name, String date) {
        super(name, date);
    }

    @Override
    public String getReminderMessage() {
        return "Don't forget " + name + "'s birthday on " + date + "!";
    }

    @Override
    public void saveToDatabase() {
        Connection conn = DatabaseHelper.connect();
        if (conn != null) {
            try {
                String query = "INSERT INTO birthdays (name, date) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, date);
                stmt.executeUpdate();
                System.out.println("Birthday saved successfully!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabaseHelper.close(conn);
            }
        }
    }

    @Override
    public void loadFromDatabase() {
        // Implementasi untuk memuat ulang tahun dari database jika diperlukan
    }
}
