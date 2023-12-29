package com.example.movieapp;

public class Movie {
    private int id;
    private boolean adult;

    public Movie(int id, boolean adult) {
        this.id = id;
        this.adult = adult;
    }

    public int getId() {
        return id;
    }

    public boolean isAdult() {
        return adult;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", adult=" + adult +
                '}';
    }
}
