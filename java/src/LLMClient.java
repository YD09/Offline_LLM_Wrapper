import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LLMClient {

    private static final String LLM_API_URL = "http://localhost:5000/query";

    public static String getLLMResponse(String prompt) {
        try {
            // Prepare JSON payload
            String jsonInput = "{\"prompt\": \"" + prompt.replace("\"", "\\\"") + "\"}";

            // Setup connection
            URL url = new URL(LLM_API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send request
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }

            // Extract "response" field from JSON
            String res = response.toString();
            int start = res.indexOf(":") + 2;
            int end = res.lastIndexOf("}") - 1;
            return res.substring(start, end);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with LLM server.";
        }
    }

    public static void main(String[] args) {
        String prompt = "Write a 2-line summary for a backend developer skilled in Java and cloud.";
        String response = getLLMResponse(prompt);
        System.out.println("LLM Response: " + response);
    }
} 
