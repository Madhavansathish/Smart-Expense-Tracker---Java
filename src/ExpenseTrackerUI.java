import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;

public class ExpenseTrackerUI extends Application {

    private TextField amountField, descField;
    private ComboBox<String> categoryBox;
    private DatePicker datePicker;
    private TextArea outputArea;
    private User currentUser;

    public ExpenseTrackerUI(User user) {
        this.currentUser = user;
    }

    public ExpenseTrackerUI() {}

    @Override
    public void start(Stage stage) {
        String userName = (currentUser != null) ? currentUser.getFullName() : "Guest";
        stage.setTitle("💰 Smart Expense Tracker - " + userName);

        Label categoryLabel = new Label("Category:");
        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Food", "Travel", "Shopping", "Bills", "Entertainment");
        categoryBox.setPromptText("Select category");

        Label amountLabel = new Label("Amount:");
        amountField = new TextField();
        amountField.setPromptText("Enter amount");

        Label descLabel = new Label("Description:");
        descField = new TextField();
        descField.setPromptText("Enter a short description");

        Label dateLabel = new Label("Expense Date:");
        datePicker = new DatePicker(LocalDate.now());

        Button addBtn = new Button("➕ Add Expense");
        Button viewBtn = new Button("📋 View Expenses");
        Button chartBtn = new Button("📊 View Chart");
        Button monthlyBtn = new Button("📅 Monthly Summary"); // ✅ NEW
        Button logoutBtn = new Button("🚪 Logout");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(250);

        HBox topButtons = new HBox(10, addBtn, viewBtn, chartBtn, monthlyBtn, logoutBtn);
        topButtons.setPadding(new Insets(10));

        VBox form = new VBox(10,
                new Label("👤 Logged in as: " + userName),
                categoryLabel, categoryBox,
                amountLabel, amountField,
                descLabel, descField,
                dateLabel, datePicker,
                topButtons,
                outputArea
        );
        form.setPadding(new Insets(20));

        addBtn.setOnAction(e -> addExpense());
        viewBtn.setOnAction(e -> viewExpenses());
        chartBtn.setOnAction(e -> showExpenseChart());
        monthlyBtn.setOnAction(e -> showMonthlyChart()); // ✅ new
        logoutBtn.setOnAction(e -> logout(stage));

        Scene scene = new Scene(form, 520, 560);
        stage.setScene(scene);
        stage.show();
    }

    private void addExpense() {
        String category = categoryBox.getValue();
        String desc = descField.getText();
        String amtText = amountField.getText();
        LocalDate date = datePicker.getValue();

        if (category == null || amtText.isEmpty() || date == null) {
            outputArea.setText("⚠️ Please fill all fields including date.");
            return;
        }

        try {
            double amount = Double.parseDouble(amtText);
            int categoryId = getCategoryId(category);
            int userId = currentUser.getUserId();

            Expense expense = new Expense(userId, categoryId, amount, desc, date);
            boolean success = ExpenseManager.addExpense(expense);

            if (success) {
                outputArea.setText("✅ Expense added successfully!");
                amountField.clear();
                descField.clear();
                categoryBox.setValue(null);
                datePicker.setValue(LocalDate.now());
            } else {
                outputArea.setText("❌ Failed to add expense. Check DB connection.");
            }

        } catch (Exception e) {
            outputArea.setText("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewExpenses() {
        outputArea.clear();
        try {
            int userId = currentUser.getUserId();
            List<Expense> list = ExpenseManager.getUserExpenses(userId);

            if (list.isEmpty()) {
                outputArea.setText("No expenses found.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-15s %-10s %-12s %-30s\n", "CATEGORY", "AMOUNT", "DATE", "DESCRIPTION"));
            sb.append("=".repeat(70)).append("\n");

            for (Expense exp : list) {
                sb.append(String.format("%-15s %-10.2f %-12s %-30s\n",
                        exp.getCategoryName(), exp.getAmount(), exp.getExpenseDate(), exp.getDescription()));
            }

            sb.append("=".repeat(70)).append("\n");
            outputArea.setText(sb.toString());
            outputArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px; -fx-control-inner-background: #f8f9fa;");

        } catch (Exception e) {
            outputArea.setText("❌ Failed to load expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showExpenseChart() {
        try {
            int userId = currentUser.getUserId();
            List<Expense> expenses = ExpenseManager.getUserExpenses(userId);

            if (expenses.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("No expenses to display in chart.");
                alert.showAndWait();
                return;
            }

            Map<String, Double> totals = new HashMap<>();
            double grandTotal = 0;
            for (Expense e : expenses) {
                totals.put(e.getCategoryName(),
                        totals.getOrDefault(e.getCategoryName(), 0.0) + e.getAmount());
                grandTotal += e.getAmount();
            }

            PieChart chart = new PieChart();
            chart.setTitle("Expense Breakdown by Category");

            for (Map.Entry<String, Double> entry : totals.entrySet()) {
                String category = entry.getKey();
                double value = entry.getValue();
                double percentage = (value / grandTotal) * 100;
                String label = String.format("%s (%.1f%%)", category, percentage);
                chart.getData().add(new PieChart.Data(label, value));
            }

            chart.setLegendVisible(false);
            chart.setLabelsVisible(true);

            VBox layout = new VBox(10, chart);
            layout.setPadding(new Insets(20));

            Scene chartScene = new Scene(layout, 550, 450);
            Stage chartStage = new Stage();
            chartStage.setTitle("📊 Expense Summary");
            chartStage.setScene(chartScene);
            chartStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ NEW: Monthly Bar Chart with total spending label
    private void showMonthlyChart() {
        try {
            int userId = currentUser.getUserId();
            int year = LocalDate.now().getYear();

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Month");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Expenses (₹)");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Monthly Expense Summary (" + year + ")");
            barChart.setLegendVisible(false);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            double yearlyTotal = 0;

            for (int month = 1; month <= 12; month++) {
                double total = ExpenseManager.getTotalExpensesByMonth(userId, year, month);
                yearlyTotal += total;
                String monthName = java.time.Month.of(month).name().substring(0, 3);
                series.getData().add(new XYChart.Data<>(monthName, total));
            }

            barChart.getData().add(series);
            Label totalLabel = new Label(String.format("💵 Total Spent in %d: ₹%.2f", year, yearlyTotal));
            totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            VBox layout = new VBox(10, barChart, totalLabel);
            layout.setPadding(new Insets(20));

            Scene chartScene = new Scene(layout, 600, 450);
            Stage chartStage = new Stage();
            chartStage.setTitle("📅 Monthly Expense Summary");
            chartStage.setScene(chartScene);
            chartStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading monthly chart: " + e.getMessage()).showAndWait();
        }
    }

    private void logout(Stage currentStage) {
        currentStage.close();
        try {
            LoginUI loginApp = new LoginUI();
            Stage loginStage = new Stage();
            loginApp.start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryId(String categoryName) {
        return switch (categoryName) {
            case "Food" -> 1;
            case "Travel" -> 2;
            case "Shopping" -> 3;
            case "Bills" -> 4;
            case "Entertainment" -> 5;
            default -> 0;
        };
    }

    public static void main(String[] args) {
        launch();
    }
}
