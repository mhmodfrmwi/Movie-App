package com.example.movieapp;

public class Movie {

    private int id;
    private boolean adult;

    // Constructor
    public Movie(int id, boolean adult) {
        this.id = id;
        this.adult = adult;
    }

    // Getters
    public int getId() {
        return id;
    }

    public boolean isAdult() {
        return adult;
    }

    // Setters (if required in future)
    public void setId(int id) {
        this.id = id;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    // Utility Functions

    /**
     * Check if the movie is suitable for all audiences.
     *
     * @return true if not adult, false otherwise.
     */
    public boolean isFamilyFriendly() {
        return !adult;
    }

    /**
     * Returns a formatted string with the movie's details.
     *
     * @return formatted string representation of the movie.
     */
    public String getMovieDetails() {
        return String.format("Movie ID: %d, Adult: %b", id, adult);
    }

    @Override
    public String toString() {
        return String.format("Movie{id=%d, adult=%b}", id, adult);
    }

    // New Methods

    /**
     * Compare this movie with another movie by ID.
     *
     * @param other The other movie to compare with.
     * @return true if IDs are the same, false otherwise.
     */
    public boolean hasSameId(Movie other) {
        if (other == null) return false;
        return this.id == other.id;
    }

    /**
     * Categorize the movie based on its ID.
     * Example: Movies with even IDs are categorized as "Type A", odd IDs as "Type B".
     *
     * @return String representing the movie's category.
     */
    public String categorizeById() {
        return (id % 2 == 0) ? "Type A" : "Type B";
    }

    /**
     * Check if the movie matches a given filter.
     *
     * @param filterId The filter ID to compare with.
     * @param allowAdult Whether to allow adult content.
     * @return true if the movie matches the filter, false otherwise.
     */
    public boolean matchesFilter(int filterId, boolean allowAdult) {
        return this.id == filterId && (allowAdult || !this.adult);
    }

    /**
     * Converts the movie's details into a JSON-like string format.
     *
     * @return JSON-like representation of the movie.
     */
    public String toJson() {
        return String.format("{ \"id\": %d, \"adult\": %b }", id, adult);
    }

    /**
     * Simulate marking a movie as watched.
     *
     * @return A message confirming the movie has been marked as watched.
     */
    public String markAsWatched() {
        return String.format("Movie with ID %d has been marked as watched.", id);
    }

    /**
     * Simulate marking a movie as a favorite.
     *
     * @return A message confirming the movie has been marked as a favorite.
     */
    public String markAsFavorite() {
        return String.format("Movie with ID %d has been added to favorites.", id);
    }
    // Check if the movie is adult content
    public boolean isAdultContent() {
        return adult;
    }

    // Return a label based on the movie's category
    public String getCategoryLabel() {
        return adult ? "Adult" : "Family-friendly";
    }

    // Get the movie ID with a prefix
    public String getIdWithPrefix(String prefix) {
        return prefix + "-" + id;
    }

    // Compare the adult status of two movies
    public boolean compareByAdultStatus(Movie other) {
        if (other == null) return false;
        return this.adult == other.adult;
    }

    // Convert the movie object to an XML-like string format
    public String toXML() {
        return String.format("<movie><id>%d</id><adult>%b</adult></movie>", id, adult);
    }

    // Check if two movies belong to the same category (Type A or Type B)
    public boolean isSameCategory(Movie other) {
        if (other == null) return false;
        return this.categorizeById().equals(other.categorizeById());
    }

    // Return a detailed description of the movie
    public String getMovieDescription() {
        String description = "Movie Details: ";
        description += "ID " + id + ", " + (adult ? "Adult Content" : "Family-friendly");
        return description;
    }

    // Toggle the adult status of the movie
    public void toggleAdultStatus() {
        this.adult = !this.adult;
    }

}
