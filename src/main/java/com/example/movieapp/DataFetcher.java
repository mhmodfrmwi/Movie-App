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

    // Constructor
    public DataFetcher(String apiUrl, HelloController controller) {
        this.apiUrl = apiUrl;
        this.controller = controller;
    }

    @Override
    public void run() {
        fetchData(apiUrl);
    }

    private void fetchData(String apiUrl) {
        HttpURLConnection con = null;

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("src/main/resources")
                    .load();
            String apiKey = dotenv.get("MOVIE_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API Key is not set in environment variables.");
            }

            URL url = new URL(apiUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);

            int status = con.getResponseCode();
            if (status != 200) {
                throw new IOException("Failed to fetch data: HTTP " + status);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            processResponse(content.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void processResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);
                MovieCard movieCard = new MovieCard(movieObject);
                movieCard.setMovieData(
                        movieObject.getString("title"),
                        movieObject.getString("overview"),
                        movieObject.getString("release_date"),
                        movieObject.getString("poster_path")
                );

                Platform.runLater(() -> controller.addMovieCard(movieCard));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // New method: Fetch movies by genre
    public void fetchMoviesByGenre(String genreId) {
        String genreUrl = apiUrl + "&with_genres=" + genreId;
        fetchData(genreUrl);
    }

    // New method: Fetch popular movies
    public void fetchPopularMovies() {
        String popularUrl = "https://api.themoviedb.org/3/movie/popular?language=en-US&page=1";
        fetchData(popularUrl);
    }

    // New method: Fetch movies by release date range
    public void fetchMoviesByDateRange(String startDate, String endDate) {
        String dateRangeUrl = apiUrl + "&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
        fetchData(dateRangeUrl);
    }

    // New method: Fetch movies with a specific keyword
    public void fetchMoviesByKeyword(String keyword) {
        String keywordUrl = apiUrl + "&query=" + keyword.replace(" ", "%20");
        fetchData(keywordUrl);
    }

    // New method: Fetch trending movies
    public void fetchTrendingMovies(String timeWindow) {
        String trendingUrl = "https://api.themoviedb.org/3/trending/movie/" + timeWindow + "?language=en-US";
        fetchData(trendingUrl);
    }
}
