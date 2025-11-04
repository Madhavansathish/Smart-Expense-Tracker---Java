import java.sql.*;

public class UserManager {

    // Register a new user
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password, full_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getFullName());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("⚠️ Registration failed: " + e.getMessage());
            return false;
        }
    }

    // Authenticate login
    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Login failed: " + e.getMessage());
        }
        return null; // Invalid login
    }

    // Fetch user details by ID
    public static void getUserDetails(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("👤 User Details:");
                System.out.println("Full Name: " + rs.getString("full_name"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error fetching user: " + e.getMessage());
        }
    }
}
