import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Register routes
        server.createContext("/register", Server::handleRegister);
        server.createContext("/login", Server::handleLogin);
        server.createContext("/addExpense", Server::handleAddExpense);
        server.createContext("/getExpenses", Server::handleGetExpenses);

        // CORS handling for frontend fetch() calls
        server.createContext("/", exchange -> {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
        });

        System.out.println("🚀 Server running at http://localhost:8080");
        server.start();
    }

    // ---------------- HANDLERS ----------------

    private static void handleRegister(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> data = parseBody(readBody(exchange));
            User user = new User(data.get("username"), data.get("email"), data.get("password"), data.get("fullName"));
            boolean success = UserManager.registerUser(user);
            sendResponse(exchange, success ? "✅ Registered successfully" : "❌ Registration failed");
        }
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> data = parseBody(readBody(exchange));
            User user = UserManager.loginUser(data.get("username"), data.get("password"));
            sendResponse(exchange, user != null ? "✅ Login successful" : "❌ Invalid credentials");
        }
    }

    private static void handleAddExpense(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> data = parseBody(readBody(exchange));
            Expense expense = new Expense(
                Integer.parseInt(data.get("userId")),
                Integer.parseInt(data.get("categoryId")),
                Double.parseDouble(data.get("amount")),
                data.get("description"),
                java.time.LocalDate.now()
            );

            boolean success = ExpenseManager.addExpense(expense);
            sendResponse(exchange, success ? "✅ Expense added" : "❌ Failed to add expense");
        }
    }

    private static void handleGetExpenses(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            int userId = Integer.parseInt(query.split("=")[1]);
            List<Expense> expenses = ExpenseManager.getUserExpenses(userId);

            StringBuilder sb = new StringBuilder("[");
            for (Expense e : expenses) {
                sb.append(String.format(
                    "{\"category\":\"%s\", \"amount\":%.2f, \"desc\":\"%s\", \"date\":\"%s\"},",
                    e.getCategoryName(), e.getAmount(), e.getDescription(), e.getExpenseDate()
                ));
            }
            if (sb.length() > 1) sb.setLength(sb.length() - 1);
            sb.append("]");
            sendJson(exchange, sb.toString());
        }
    }

    // ---------------- HELPERS ----------------

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseBody(String body) {
        Map<String, String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        addCORS(exchange);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendJson(HttpExchange exchange, String json) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        addCORS(exchange);
        sendResponse(exchange, json);
    }

    private static void addCORS(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }
}
