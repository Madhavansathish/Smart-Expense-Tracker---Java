public class UserTest {
    public static void main(String[] args) {
        System.out.println("=== Testing User Registration & Login ===");

        // Create a new user object
        User user = new User("adi", "adi@example.com", "adi123", "Adithya G");

        // Register user
        boolean registered = UserManager.registerUser(user);
        if (registered) {
            System.out.println("✅ User registered successfully!");
        } else {
            System.out.println("⚠️ Registration failed (username/email might already exist).");
        }

        // Try logging in
        User loggedInUser = UserManager.loginUser("adi", "adi123");
        if (loggedInUser != null) {
            System.out.println("✅ Login successful!");
            System.out.println("Welcome, " + loggedInUser.getFullName() + "!");
        } else {
            System.out.println("❌ Invalid credentials.");
        }
    }
}
