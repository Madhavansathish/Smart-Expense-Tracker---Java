import java.time.LocalDate;
import java.util.*;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println(" 💰 Welcome to Expense Tracker System 💰");
        System.out.println("=======================================");

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // Step 1: Authentication Menu (Register / Login)
    private static void showAuthMenu() {
        System.out.println("\n1️⃣  Register");
        System.out.println("2️⃣  Login");
        System.out.println("3️⃣  Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 3 -> exitApp();
            default -> System.out.println("❌ Invalid choice, try again!");
        }
    }

    // Step 2: Register new user
    private static void registerUser() {
        System.out.println("\n--- Register New User ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        User newUser = new User(username, email, password, fullName);
        boolean registered = UserManager.registerUser(newUser);

        if (registered) {
            System.out.println("✅ Registration successful! You can now log in.");
        } else {
            System.out.println("⚠️ Registration failed. Username or email may already exist.");
        }
    }

    // Step 3: Login
    private static void loginUser() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        currentUser = UserManager.loginUser(username, password);

        if (currentUser != null) {
            System.out.println("✅ Login successful! Welcome, " + currentUser.getFullName() + "!");
        } else {
            System.out.println("❌ Invalid username or password.");
        }
    }

    // Step 4: Main Menu (After login)
    private static void showMainMenu() {
        System.out.println("\n--------------------------------------");
        System.out.println("👤 Logged in as: " + currentUser.getFullName());
        System.out.println("--------------------------------------");
        System.out.println("1️⃣  Add Expense");
        System.out.println("2️⃣  View All Expenses");
        System.out.println("3️⃣  View Expense by Category");
        System.out.println("4️⃣  View Monthly Total");
        System.out.println("5️⃣  Delete an Expense");
        System.out.println("6️⃣  Logout");
        System.out.println("7️⃣  Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> addExpense();
            case 2 -> viewAllExpenses();
            case 3 -> viewExpensesByCategory();
            case 4 -> viewMonthlyTotal();
            case 5 -> deleteExpense();
            case 6 -> logout();
            case 7 -> exitApp();
            default -> System.out.println("❌ Invalid choice. Try again!");
        }
    }

    // Step 5: Add expense
    private static void addExpense() {
        System.out.println("\n--- Add Expense ---");

        System.out.print("Enter category ID (1=Food, 2=Travel, 3=Shopping, 4=Bills, 5=Entertainment): ");
        int categoryId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        LocalDate date = LocalDate.now();

        Expense expense = new Expense(currentUser.getUserId(), categoryId, amount, description, date);
        boolean success = ExpenseManager.addExpense(expense);

        if (success) {
            System.out.println("✅ Expense added successfully!");
        } else {
            System.out.println("❌ Failed to add expense.");
        }
    }

    // Step 6: View all expenses
    private static void viewAllExpenses() {
        System.out.println("\n--- Your Expenses ---");
        List<Expense> expenses = ExpenseManager.getUserExpenses(currentUser.getUserId());
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            for (Expense exp : expenses) {
                System.out.printf("ID: %d | Category: %s | Amount: %.2f | Date: %s | %s%n",
                        exp.getExpenseId(), exp.getCategoryName(), exp.getAmount(),
                        exp.getExpenseDate(), exp.getDescription());
            }
        }
    }

    // Step 7: View by category
    private static void viewExpensesByCategory() {
        System.out.println("\n--- Expense Summary by Category ---");
        Map<String, Double> summary = ExpenseManager.getExpensesByCategory(currentUser.getUserId());
        if (summary.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            summary.forEach((category, total) ->
                    System.out.printf("%-15s : %.2f%n", category, total));
        }
    }

    // Step 8: View monthly total
    private static void viewMonthlyTotal() {
        System.out.print("\nEnter year (e.g., 2025): ");
        int year = scanner.nextInt();

        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine();

        double total = ExpenseManager.getTotalExpensesByMonth(currentUser.getUserId(), year, month);
        System.out.printf("💵 Total spent in %d/%d: %.2f%n", month, year, total);
    }

    // Step 9: Delete expense
    private static void deleteExpense() {
        System.out.print("\nEnter Expense ID to delete: ");
        int expenseId = scanner.nextInt();
        scanner.nextLine();

        boolean deleted = ExpenseManager.deleteExpense(expenseId, currentUser.getUserId());
        if (deleted) {
            System.out.println("✅ Expense deleted successfully!");
        } else {
            System.out.println("❌ Expense not found or could not be deleted.");
        }
    }

    // Step 10: Logout / Exit
    private static void logout() {
        System.out.println("👋 Logged out successfully!");
        currentUser = null;
    }

    private static void exitApp() {
        System.out.println("👋 Thank you for using Expense Tracker!");
        System.exit(0);
    }
}
