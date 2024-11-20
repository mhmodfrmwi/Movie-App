package com.example.movieapp;

import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import io.github.cdimascio.dotenv.Dotenv;
public class DataFetcher implements Runnable {
    private final String apiUrl;
    private final HelloController controller;

    public DataFetcher(String apiUrl, HelloController controller) {
        this.apiUrl = apiUrl;
        this.controller = controller;
    }

    @Override
    public void run() {
        fetchData(apiUrl);
    }

    private void fetchData(String apiUrl) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            // Retrieve API key from environment variables
            Dotenv dotenv = Dotenv.configure()
                    .directory("src/main/resources") // Specify the path to your .env file
                    .load();
            String apiKey = dotenv.get("MOVIE_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API Key is not set in environment variables.");
            }

            url = new URL(apiUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);

            int status = con.getResponseCode();
            if (status != 200) {
                throw new IOException("Failed to fetch data: HTTP " + status);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(content.toString());
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            // Create a list to hold MovieCard instances
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);
                MovieCard movieCard = new MovieCard(movieObject);
                movieCard.setMovieData(
                        movieObject.getString("title"),
                        movieObject.getString("overview"),
                        movieObject.getString("release_date"),
                        movieObject.getString("poster_path")
                );
                Platform.runLater(() -> {
                    controller.addMovieCard(movieCard);
                    controller.deleteAllData();
                });
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
