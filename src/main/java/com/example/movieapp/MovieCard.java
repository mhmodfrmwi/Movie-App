package com.example.movieapp;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
public class MovieCard extends HBox {

    private ImageView posterImageView;
    private Label titleLabel;
    private Label overviewLabel;
    private Label releaseDateLabel;
    private CheckBox favorite;
    private JSONObject jsonObject;
    public MovieCard() {
        posterImageView = new ImageView();
        posterImageView.setFitWidth(150);
        posterImageView.setPreserveRatio(true);
        titleLabel = new Label();
        overviewLabel = new Label();
        releaseDateLabel = new Label();
        favorite = new CheckBox();
        VBox labelsVBox = new VBox(titleLabel, overviewLabel, releaseDateLabel);
        labelsVBox.setSpacing(5);

        HBox contentHBox = new HBox(posterImageView, labelsVBox);
        contentHBox.setSpacing(10);

        setPadding(new Insets(10));
        setSpacing(10);

        getChildren().add(contentHBox);

        favorite.setOnAction(event -> {
            if (favorite.isSelected()) {
                addToDatabase();
            }
            if (!favorite.isSelected()){
                removefromdatabase();
            }
        });
        posterImageView.setOnMouseClicked(event -> {
                displayMovieInfo();
        });
    }
    public MovieCard(JSONObject jsonObject) {
        posterImageView = new ImageView();
        posterImageView.setFitWidth(150); // Set the desired width
        posterImageView.setPreserveRatio(true);
        this.jsonObject=jsonObject;
        titleLabel = new Label();
        overviewLabel = new Label();
        releaseDateLabel = new Label();
        favorite = new CheckBox();
        VBox labelsVBox = new VBox(titleLabel, overviewLabel, releaseDateLabel);
        labelsVBox.setSpacing(5);

        HBox contentHBox = new HBox(posterImageView, labelsVBox,favorite);
        contentHBox.setSpacing(10);

        setPadding(new Insets(10));
        setSpacing(10);

        getChildren().add(contentHBox);

        favorite.setOnAction(event -> {
            if (favorite.isSelected()) {
                addToDatabase();
            }
            if (!favorite.isSelected()){
                removefromdatabase();
            }
        });
        posterImageView.setOnMouseClicked(event -> {
                displayMovieInfo();

        });
    }

    String image;
    public void setMovieData(String title, String overview, String releaseDate, String posterPath) {
        // Set data to UI components
        titleLabel.setText("Title: " + title);
//        overviewLabel.setText("Overview: " + overview);
        releaseDateLabel.setText("Release Date: " + releaseDate);
//        overviewLabel.setVisible(false);
        image=posterPath;
        titleLabel.setTextFill(Color.WHITE);
        overviewLabel.setTextFill(Color.WHITE);
        releaseDateLabel.setTextFill(Color.WHITE);

        try {
            Image posterImage = new Image(new URL("https://image.tmdb.org/t/p/w500" + posterPath).toExternalForm());
            posterImageView.setImage(posterImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    private void addToDatabase() {
        String title = titleLabel.getText();
        String overview = overviewLabel.getText();
        String releaseDate = releaseDateLabel.getText();
        System.out.println(title);
        System.out.println(overview);
        System.out.println(releaseDate);
        HelloController.addToDatabase(title,overview,releaseDate,image);
    }
    private void removefromdatabase(){
        String title = titleLabel.getText();
        HelloController.removefromdatabase(title);
    }

    private void displayMovieInfo() {
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: #E6E6E6; -fx-padding: 10px;");

        Label titleLabel = new Label("Title: " + this.titleLabel.getText());
        Label overviewLabel = new Label("Overview: " + this.overviewLabel.getText());
        Label releaseDateLabel = new Label("Release Date: " + this.releaseDateLabel.getText());

        infoBox.getChildren().addAll(titleLabel, overviewLabel, releaseDateLabel);

        // Create a Dialog to display the movie information
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(getScene().getWindow());
        dialog.setTitle("Movie Information");
        dialog.getDialogPane().setContent(infoBox);

        // Add a close button to the dialog
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        // Show the dialog
        dialog.showAndWait();
    }



    public CheckBox getFavoriteCheckBox() {
        return favorite;
    }
}
