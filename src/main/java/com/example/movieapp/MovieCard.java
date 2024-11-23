package com.example.movieapp;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a visual card for displaying movie details, allowing interactions such as marking as favorite.
 */
public class MovieCard extends HBox {

    private ImageView posterImageView;
    private Label titleLabel;
    private Label overviewLabel;
    private Label releaseDateLabel;
    private CheckBox favorite;
    private JSONObject jsonObject;

    private String movieId;
    private String posterPath;

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    /**
     * Default constructor for MovieCard.
     * Initializes and configures components without setting movie data.
     */
    public MovieCard() {
        initializeComponents();
        configureLayout();
        configureActions();
    }

    /**
     * Constructor with JSON data.
     *
     * @param jsonObject The JSON object containing movie details.
     */
    public MovieCard(JSONObject jsonObject) {
        this();
        this.jsonObject = jsonObject;
        setMovieData(
                jsonObject.optString("title", "N/A"),
                jsonObject.optString("overview", "Overview not available"),
                jsonObject.optString("release_date", "Unknown release date"),
                jsonObject.optString("poster_path", "")
        );
    }

    private void initializeComponents() {
        posterImageView = new ImageView();
        posterImageView.setFitWidth(150);
        posterImageView.setPreserveRatio(true);

        titleLabel = new Label();
        overviewLabel = new Label();
        releaseDateLabel = new Label();
        favorite = new CheckBox("Favorite");

        styleLabels();
    }

    private void configureLayout() {
        VBox labelsVBox = new VBox(titleLabel, overviewLabel, releaseDateLabel);
        labelsVBox.setSpacing(5);
        labelsVBox.setPadding(new Insets(5));

        HBox contentHBox = new HBox(posterImageView, labelsVBox, favorite);
        contentHBox.setSpacing(10);

        setPadding(new Insets(10));
        setSpacing(10);
        getChildren().add(contentHBox);
    }

    private void configureActions() {
        favorite.setOnAction(event -> handleFavoriteToggle());
        posterImageView.setOnMouseClicked(event -> displayMovieInfo());
    }

    private void styleLabels() {
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(new Font("Arial", 16));

        overviewLabel.setTextFill(Color.LIGHTGRAY);
        overviewLabel.setFont(new Font("Arial", 12));
        overviewLabel.setWrapText(true);

        releaseDateLabel.setTextFill(Color.LIGHTGRAY);
        releaseDateLabel.setFont(new Font("Arial", 12));
    }

    public void setMovieData(String title, String overview, String releaseDate, String posterPath) {
        this.movieId = title; // Set movieId as the title for simplicity.

        titleLabel.setText(title);
        overviewLabel.setText(overview);
        releaseDateLabel.setText(releaseDate);

        this.posterPath = posterPath;

        if (posterPath != null && !posterPath.isEmpty()) {
            loadPosterImage(posterPath);
        } else {
            posterImageView.setImage(null);
        }
    }

    private void loadPosterImage(String posterPath) {
        try {
            URL posterUrl = new URL(IMAGE_BASE_URL + posterPath);
            Image posterImage = new Image(posterUrl.toExternalForm());
            posterImageView.setImage(posterImage);
        } catch (MalformedURLException e) {
            System.err.println("Invalid poster URL: " + posterPath);
        }
    }

    private void handleFavoriteToggle() {
        if (favorite.isSelected()) {
            addToFavorites();
        } else {
            removeFromFavorites();
        }
    }

    private void addToFavorites() {
        HelloController.addToDatabase(
                titleLabel.getText(),
                overviewLabel.getText(),
                releaseDateLabel.getText(),
                posterPath
        );
    }

    private void removeFromFavorites() {
        HelloController.removefromdatabase(titleLabel.getText());
    }

    private void displayMovieInfo() {
        Dialog<Void> dialog = createInfoDialog();
        dialog.showAndWait();
    }

    private Dialog<Void> createInfoDialog() {
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: #E6E6E6; -fx-padding: 10px;");

        Label titleLabel = new Label("Title: " + this.titleLabel.getText());
        Label overviewLabel = new Label("Overview: " + this.overviewLabel.getText());
        Label releaseDateLabel = new Label("Release Date: " + this.releaseDateLabel.getText());

        infoBox.getChildren().addAll(titleLabel, overviewLabel, releaseDateLabel);

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(getScene().getWindow());
        dialog.setTitle("Movie Information");
        dialog.getDialogPane().setContent(infoBox);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        return dialog;
    }

    // Added Functions

    public void highlight() {
        setStyle("-fx-background-color: #FFD700; -fx-border-color: #000;");
    }

    public void unhighlight() {
        setStyle("");
    }

    public String getReleaseDate() {
        return releaseDateLabel.getText();
    }

    public String getOverview() {
        return overviewLabel.getText();
    }

    public String getImagePath() {
        return posterPath;
    }

    public boolean isFavorite() {
        return favorite.isSelected();
    }

    public void toggleFavoriteState() {
        favorite.setSelected(!favorite.isSelected());
        handleFavoriteToggle();
    }

    public void resetCard() {
        titleLabel.setText("N/A");
        overviewLabel.setText("Overview not available");
        releaseDateLabel.setText("Unknown release date");
        posterImageView.setImage(null);
        favorite.setSelected(false);
    }

    public void updateMovieData(JSONObject newData) {
        this.jsonObject = newData;
        setMovieData(
                newData.optString("title", titleLabel.getText()),
                newData.optString("overview", overviewLabel.getText()),
                newData.optString("release_date", releaseDateLabel.getText()),
                newData.optString("poster_path", posterPath)
        );
    }

    public void setPosterImage(URL newPosterUrl) {
        try {
            Image newImage = new Image(newPosterUrl.toExternalForm());
            posterImageView.setImage(newImage);
        } catch (Exception e) {
            System.err.println("Error setting new poster image: " + e.getMessage());
        }
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public boolean isLoaded() {
        return jsonObject != null;
    }

    public void resetStyle() {
        setStyle("");
    }

    public void onClick(Runnable action) {
        setOnMouseClicked(event -> action.run());
    }

    public String getTitle() {
        return titleLabel.getText();
    }
}
