package com.example.movieapp;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class HelloController {

    @FXML
    private GridPane movieContainer;

    @FXML
    private ScrollPane gridContainer;

    private final ObservableList<MovieCard> movieCards = FXCollections.observableArrayList();
    @FXML
    private Label messageLabel;
    @FXML
    private void run(ActionEvent actionEvent) {
        fetchData("https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1");
        updateMessage("Dashboard!");
    }

    @FXML
    private void run2(ActionEvent actionEvent) {
        fetchData("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1");
        updateMessage("Top Rated!");
    }

    @FXML
    private void run3(ActionEvent actionEvent) {
        fetchData("https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1");
        updateMessage("Trending!");
    }

    private void fetchData(String apiUrl) {
        movieCards.clear();

        DataFetcher dataFetcher = new DataFetcher(apiUrl, this);
        Thread thread = new Thread(dataFetcher);
        thread.start();
    }

    public void home(MouseEvent mouseEvent) {
        gridContainer.setVisible(false);
    }

    public static void addToDatabase(String title, String overview, String releaseDate, String image) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/moviecard",
                    "root",
                    "12345"
            );
            String insertQuery = "INSERT INTO moviecard (title, overview, release_date,image) VALUES (?, ?, ?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, overview);
                preparedStatement.setString(3, releaseDate);
                preparedStatement.setString(4, image);
                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("added");
                } else {
                    System.out.println("failed");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectFromDataBase() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/moviecard",
                    "root",
                    "12345"
            );
            movieCards.clear();
            String selectQuery = "SELECT * from moviecard";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String overview = resultSet.getString("overview");
                String releaseDate = resultSet.getString("release_date");
                String image = resultSet.getString("image");
                MovieCard movieCard = new MovieCard();
                movieCard.setMovieData(title, overview, releaseDate, image);
                movieCards.add(movieCard);
            }
            updateMovieContainer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMovieCard(MovieCard movieCard) {
        movieCards.add(movieCard);
        updateMovieContainer();
    }

    public static void removefromdatabase(String title) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/moviecard",
                    "root",
                    "12345"
            );
            String deleteQuery = "DELETE FROM moviecard where (title) = (?) ";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, title);
                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("deleted");
                } else {
                    System.out.println("failed");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void database(ActionEvent actionEvent) {
        selectFromDataBase();
        updateMessage("Favorite!");
    }

    public void deleteAllData() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/moviecard",
                    "root",
                    "12345"
            );
            String deleteQuery = "DELETE FROM moviecard";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
//                    System.out.println("All data deleted from the database");
                } else {
//                    System.out.println("No data to delete in the database");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateMovieContainer() {
        int column = 0;
        int row = 0;
        gridContainer.setVisible(true);
        gridContainer.setLayoutX(154.0);
        gridContainer.setLayoutY(62.0);
        gridContainer.setPrefHeight(561.0);
        gridContainer.setPrefWidth(990);
        movieContainer.setPrefHeight(561.0);
        movieContainer.setPrefWidth(990);
        movieContainer.getChildren().clear();
        for (MovieCard card : movieCards) {
            movieContainer.add(card, column, row);
            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
    }
    private void updateMessage(String message) {
        messageLabel.setText(message);
        // Set a delay and hide the label after 3 seconds
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(30));
        visiblePause.setOnFinished(event -> messageLabel.setVisible(false));
        visiblePause.play();

        // Make the label visible
        messageLabel.setVisible(true);
    }

    public static void handleFavoriteAction(String title, String overview, String releaseDate, String image, boolean isSelected) {
        if (isSelected) {
            addToDatabase(title, overview, releaseDate, image);
        } else {
            removefromdatabase(title);
        }
    }
    // Adds a list of movies to the GridPane.
    public void addMovieCards(ObservableList<MovieCard> movies) {
        movieCards.addAll(movies);
        updateMovieContainer();
    }

    // Clears all movie cards from the UI and the ObservableList.
    public void clearMovieCards() {
        movieCards.clear();
        movieContainer.getChildren().clear();
        gridContainer.setVisible(false);
    }

    // Filters displayed movies based on a search keyword.
    public void searchMovies(String keyword) {
        ObservableList<MovieCard> filteredList = FXCollections.observableArrayList();
        for (MovieCard card : movieCards) {
            if (card.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(card);
            }
        }
        movieCards.clear();
        movieCards.addAll(filteredList);
        updateMovieContainer();
    }

    // Reloads all movie cards from the database to the UI.
    public void reloadFromDatabase() {
        selectFromDataBase();
        updateMovieContainer();
    }

    // Toggles the visibility of the ScrollPane.
    public void toggleGridVisibility() {
        gridContainer.setVisible(!gridContainer.isVisible());
    }
    /**
     * Refreshes the data by re-fetching movies from the current data source.
     */
    public void refreshMovies() {
        if (!movieCards.isEmpty()) {
            movieCards.clear();
        }
        selectFromDataBase();
        updateMessage("Movies refreshed!");
    }

    /**
     * Sorts the displayed movies alphabetically by title.
     */
    public void sortMoviesByTitle() {
        movieCards.sort((movie1, movie2) -> movie1.getTitle().compareToIgnoreCase(movie2.getTitle()));
        updateMovieContainer();
        updateMessage("Movies sorted by title!");
    }

    /**
     * Sorts the displayed movies by their release date.
     */
    public void sortMoviesByReleaseDate() {
        movieCards.sort((movie1, movie2) -> movie1.getReleaseDate().compareTo(movie2.getReleaseDate()));
        updateMovieContainer();
        updateMessage("Movies sorted by release date!");
    }

    /**
     * Marks all currently displayed movies as favorites.
     */
    public void markAllAsFavorite() {
        for (MovieCard card : movieCards) {
            addToDatabase(card.getTitle(), card.getOverview(), card.getReleaseDate(), card.getImagePath());
        }
        updateMessage("All movies marked as favorites!");
    }

    /**
     * Removes all displayed movies from the database.
     */
    public void removeAllFromDatabase() {
        deleteAllData();
        clearMovieCards();
        updateMessage("All movies removed from the database!");
    }

    /**
     * Hides the message label immediately without waiting for the timer.
     */
    public void hideMessageLabel() {
        messageLabel.setVisible(false);
    }

    /**
     * Displays a random movie card from the list.
     */
    public void showRandomMovie() {
        if (!movieCards.isEmpty()) {
            int randomIndex = (int) (Math.random() * movieCards.size());
            MovieCard randomMovie = movieCards.get(randomIndex);
            clearMovieCards();
            movieCards.add(randomMovie);
            updateMovieContainer();
            updateMessage("Random movie displayed!");
        } else {
            updateMessage("No movies available to display!");
        }
    }

    /**
     * Highlights movies that contain a specific keyword in their title.
     *
     * @param keyword The keyword to search for.
     */
    public void highlightMoviesByKeyword(String keyword) {
        for (MovieCard card : movieCards) {
            if (card.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                card.highlight(); // Assuming MovieCard has a highlight method.
            } else {
                card.unhighlight(); // Assuming MovieCard has an unhighlight method.
            }
        }
        updateMessage("Highlighted movies containing: " + keyword);
    }

    /**
     * Paginates the displayed movie cards into smaller chunks.
     *
     * @param pageNumber The page number to display.
     * @param itemsPerPage Number of items per page.
     */
    public void paginateMovies(int pageNumber, int itemsPerPage) {
        int startIndex = (pageNumber - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, movieCards.size());
        if (startIndex >= movieCards.size()) {
            updateMessage("Page number exceeds the available data!");
            return;
        }
        ObservableList<MovieCard> paginatedList = FXCollections.observableArrayList(movieCards.subList(startIndex, endIndex));
        movieCards.clear();
        movieCards.addAll(paginatedList);
        updateMovieContainer();
        updateMessage(String.format("Showing page %d with %d items per page.", pageNumber, itemsPerPage));
    }
    // Adds movie cards from a JSON string (e.g., API response)
    public void addMovieCardsFromJson(String jsonData) {
        // Parse the JSON data and create MovieCard objects
        // Assuming the jsonData is in the correct format and can be parsed
        try {
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONArray results = jsonResponse.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieData = results.getJSONObject(i);
                MovieCard movieCard = new MovieCard();
                movieCard.setMovieData(
                        movieData.getString("title"),
                        movieData.getString("overview"),
                        movieData.getString("release_date"),
                        movieData.getString("poster_path")
                );
                movieCards.add(movieCard);
            }
            updateMovieContainer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get the current number of movie cards
    public int getMovieCardCount() {
        return movieCards.size();
    }

    // Filter movies by their favorite status (e.g., show only favorite movies)
    public void filterMoviesByFavoriteStatus(boolean isFavorite) {
        ObservableList<MovieCard> filteredList = FXCollections.observableArrayList();
        for (MovieCard card : movieCards) {
            if (card.isFavorite() == isFavorite) {
                filteredList.add(card);
            }
        }
        movieCards.clear();
        movieCards.addAll(filteredList);
        updateMovieContainer();
    }

    // Update an existing movie card in the container (e.g., after modifying movie data)
    public void updateMovieCard(MovieCard movieCard) {
        int index = movieCards.indexOf(movieCard);
        if (index != -1) {
            movieCards.set(index, movieCard); // Update the card in the list
            updateMovieContainer();
        }
    }

    // Set visibility of all movie cards (useful for showing/hiding all at once)
    public void setMovieCardVisibility(boolean isVisible) {
        for (MovieCard card : movieCards) {
            card.setVisible(isVisible);
        }
        updateMovieContainer();
    }

    // Set the opacity of the entire grid container (for UI effects)
    public void setGridContainerOpacity(double opacity) {
        gridContainer.setOpacity(opacity);
    }

    // Load movie data from a file (e.g., a JSON file or CSV file)
    public void loadMovieDataFromFile(String filePath) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            addMovieCardsFromJson(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adds a movie card at a specific position (row, column) in the grid
    public void addMovieCardToSpecificPosition(MovieCard movieCard, int row, int column) {
        movieContainer.add(movieCard, column, row);
    }
}
