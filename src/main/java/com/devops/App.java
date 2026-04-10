package com.devops;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

    public int add(int a, int b) {
        return a + b;
    }

    static String readStatus() {
        try {
            return new String(Files.readAllBytes(Paths.get("/app/status.txt")));
        } catch (Exception e) {
            return "BUILD_STATUS=UNKNOWN\nBUILD_ERRORS=No status file found";
        }
    }

    static String parseStatus(String content, String key) {
        for (String line : content.split("\n")) {
            if (line.startsWith(key + "=")) {
                return line.replace(key + "=", "");
            }
        }
        return "Unknown";
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                App app = new App();

                // Read build status
                String statusContent = readStatus();
                String buildStatus = parseStatus(statusContent, "BUILD_STATUS");
                String testStatus = parseStatus(statusContent, "TEST_STATUS");

                // Run live test cases
                boolean test1 = app.add(2, 3) == 5;
                boolean test2 = app.add(-3, 2) == -1;
                boolean test3 = app.add(0, 0) == 0;
                int passed = (test1 ? 1 : 0) + (test2 ? 1 : 0) + (test3 ? 1 : 0);

                String buildColor = buildStatus.equals("PASSED") ? "#4CAF50" : "#f44336";
                String testColor = testStatus.equals("PASSED") ? "#4CAF50" : "#f44336";

                String response = "<html><head><style>"
                    + "body{font-family:Arial;padding:30px;background:#f4f4f4}"
                    + "h1{color:#333}"
                    + ".card{background:#fff;padding:20px;border-radius:8px;margin:15px 0;box-shadow:0 2px 4px rgba(0,0,0,0.1)}"
                    + ".pass{color:#4CAF50;font-weight:bold}"
                    + ".fail{color:#f44336;font-weight:bold}"
                    + ".badge{padding:6px 14px;border-radius:20px;color:white;font-weight:bold}"
                    + "table{border-collapse:collapse;width:100%}"
                    + "th,td{padding:12px;border:1px solid #ccc;text-align:left}"
                    + "th{background:#4CAF50;color:white}"
                    + "pre{background:#1e1e1e;color:#f8f8f2;padding:15px;border-radius:6px;overflow-x:auto}"
                    + "</style></head><body>"
                    + "<h1>Student App — CI/CD Dashboard</h1>"

                    // Build Status Card
                    + "<div class='card'>"
                    + "<h2>Jenkins Build Status</h2>"
                    + "<p>Compile: <span class='badge' style='background:" + buildColor + "'>" + buildStatus + "</span></p>"
                    + "<p>Tests: <span class='badge' style='background:" + testColor + "'>" + testStatus + "</span></p>"
                    + "</div>"

                    // Live Test Results
                    + "<div class='card'>"
                    + "<h2>Live Test Results</h2>"
                    + "<table>"
                    + "<tr><th>Test Case</th><th>Expected</th><th>Result</th></tr>"
                    + "<tr><td>add(2, 3)</td><td>5</td><td>" + (test1 ? "<span class='pass'>PASS ✓</span>" : "<span class='fail'>FAIL ✗</span>") + "</td></tr>"
                    + "<tr><td>add(-3, 2)</td><td>-1</td><td>" + (test2 ? "<span class='pass'>PASS ✓</span>" : "<span class='fail'>FAIL ✗</span>") + "</td></tr>"
                    + "<tr><td>add(0, 0)</td><td>0</td><td>" + (test3 ? "<span class='pass'>PASS ✓</span>" : "<span class='fail'>FAIL ✗</span>") + "</td></tr>"
                    + "</table>"
                    + "<p><strong>Summary: " + passed + "/3 tests passed</strong></p>"
                    + "</div>"

                    // Raw Build Log
                    + "<div class='card'>"
                    + "<h2>Build Log</h2>"
                    + "<pre>" + statusContent.replace("<", "&lt;").replace(">", "&gt;") + "</pre>"
                    + "</div>"

                    + "</body></html>";

                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.start();
        System.out.println("Server started at http://localhost:" + port);
    }
}