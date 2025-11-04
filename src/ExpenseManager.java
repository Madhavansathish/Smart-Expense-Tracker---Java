import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ExpenseManager {

    // ✅ Add a new expense
    public static boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (user_id, category_id, amount, description, expense_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expense.getUserId());
            pstmt.setInt(2, expense.getCategoryId());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getDescription());
            pstmt.setDate(5, java.sql.Date.valueOf(expense.getExpenseDate()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error adding expense: " + e.getMessage());
            return false;
        }
    }

    // ✅ Retrieve all expenses for a user
    public static List<Expense> getUserExpenses(int userId) {
        List<Expense> expenseList = new ArrayList<>();
        String sql = """
            SELECT e.*, c.category_name 
            FROM expenses e 
            JOIN categories c ON e.category_id = c.category_id 
            WHERE e.user_id = ? 
            ORDER BY e.expense_date DESC
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Expense exp = new Expense();
                exp.setExpenseId(rs.getInt("expense_id"));
                exp.setUserId(rs.getInt("user_id"));
                exp.setCategoryId(rs.getInt("category_id"));
                exp.setAmount(rs.getDouble("amount"));
                exp.setDescription(rs.getString("description"));
                exp.setExpenseDate(rs.getDate("expense_date").toLocalDate());
                exp.setCategoryName(rs.getString("category_name"));
                expenseList.add(exp);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving expenses: " + e.getMessage());
        }

        return expenseList;
    }

    // ✅ Get total spent by category
    public static Map<String, Double> getExpensesByCategory(int userId) {
        Map<String, Double> categoryTotals = new LinkedHashMap<>();
        String sql = """
            SELECT c.category_name, SUM(e.amount) AS total 
            FROM expenses e 
            JOIN categories c ON e.category_id = c.category_id 
            WHERE e.user_id = ? 
            GROUP BY c.category_name
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                categoryTotals.put(rs.getString("category_name"), rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving category totals: " + e.getMessage());
        }

        return categoryTotals;
    }

    // ✅ Get total expenses by month (used for bar chart)
    public static double getTotalExpensesByMonth(int userId, int year, int month) {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE user_id = ? AND YEAR(expense_date) = ? AND MONTH(expense_date) = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error retrieving monthly total: " + e.getMessage());
        }
        return 0.0;
    }

    // ✅ Get total expenses for an entire year
    public static double getTotalExpensesByYear(int userId, int year) {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE user_id = ? AND YEAR(expense_date) = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error retrieving yearly total: " + e.getMessage());
        }
        return 0.0;
    }

    // ✅ Delete expense by ID (with ownership check)
    public static boolean deleteExpense(int expenseId, int userId) {
        String sql = "DELETE FROM expenses WHERE expense_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error deleting expense: " + e.getMessage());
            return false;
        }
    }

    // ✅ Optional: Get recent N expenses (for dashboard preview)
    public static List<Expense> getRecentExpenses(int userId, int limit) {
        List<Expense> expenseList = new ArrayList<>();
        String sql = """
            SELECT e.*, c.category_name 
            FROM expenses e 
            JOIN categories c ON e.category_id = c.category_id 
            WHERE e.user_id = ? 
            ORDER BY e.expense_date DESC 
            LIMIT ?
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Expense exp = new Expense();
                exp.setExpenseId(rs.getInt("expense_id"));
                exp.setUserId(rs.getInt("user_id"));
                exp.setCategoryId(rs.getInt("category_id"));
                exp.setAmount(rs.getDouble("amount"));
                exp.setDescription(rs.getString("description"));
                exp.setExpenseDate(rs.getDate("expense_date").toLocalDate());
                exp.setCategoryName(rs.getString("category_name"));
                expenseList.add(exp);
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error fetching recent expenses: " + e.getMessage());
        }

        return expenseList;
    }
}
