public class TestConnection {
    public static void main(String[] args) {
        if (DatabaseConnection.connect() != null) {
            System.out.println("✅ Test connection successful!");
        } else {
            System.out.println("❌ Test connection failed!");
        }
    }
}
