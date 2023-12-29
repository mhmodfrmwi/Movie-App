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
}
