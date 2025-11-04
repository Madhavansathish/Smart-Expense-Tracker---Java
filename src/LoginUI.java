import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginUI extends Application {

    private Stage window;
    private User loggedInUser; // ✅ Store logged-in user

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("🔐 Smart Expense Tracker - Login");

        // === Login Form ===
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        Label messageLabel = new Label();

        VBox loginLayout = new VBox(10, userLabel, usernameField, passLabel, passwordField, loginBtn, registerBtn, messageLabel);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f4f8, #dee2e6);");

        // === Button Actions ===
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            loggedInUser = UserManager.loginUser(username, password);

            if (loggedInUser != null) {
                messageLabel.setText("✅ Login successful! Welcome, " + loggedInUser.getFullName());
                openExpenseTracker(loggedInUser);
            } else {
                messageLabel.setText("❌ Invalid credentials. Try again.");
            }
        });

        registerBtn.setOnAction(e -> openRegistrationWindow());

        Scene scene = new Scene(loginLayout, 350, 300);
        window.setScene(scene);
        window.show();
    }

    // ✅ Open the Expense Tracker Dashboard
    private void openExpenseTracker(User user) {
        try {
            ExpenseTrackerUI app = new ExpenseTrackerUI(user); // ✅ Pass logged-in user
            Stage newStage = new Stage();
            app.start(newStage);
            window.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Registration Window
    private void openRegistrationWindow() {
        Stage regStage = new Stage();
        regStage.setTitle("📝 Register New User");

        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Button registerBtn = new Button("Register");
        Label messageLabel = new Label();

        VBox layout = new VBox(10, nameLabel, nameField, userLabel, userField, emailLabel, emailField, passLabel, passField, registerBtn, messageLabel);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e9f5ec, #d2f0d3);");

        registerBtn.setOnAction(e -> {
            String name = nameField.getText();
            String username = userField.getText();
            String email = emailField.getText();
            String password = passField.getText();

            User newUser = new User(username, email, password, name);
            boolean success = UserManager.registerUser(newUser);

            if (success) {
                messageLabel.setText("✅ Registration successful! You can log in now.");
            } else {
                messageLabel.setText("❌ Registration failed. Try another username or email.");
            }
        });

        Scene scene = new Scene(layout, 350, 400);
        regStage.setScene(scene);
        regStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
