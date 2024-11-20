# Movie App

The Movie App is a JavaFX-based desktop application that provides an engaging experience for movie enthusiasts. Users can explore current movies, top-rated titles, and upcoming releases, and they can mark their favorite movies for quick access.

---

## Features

- **Dashboard**: Displays currently playing movies.
- **Top Rated**: Shows movies with the highest ratings.
- **Trending**: Lists upcoming movies.
- **Favorite**: Allows users to add and view their favorite movies.
- **Local Database**: Supports storing favorite movies using MySQL.

---

## Prerequisites

Before running the application, ensure you have the following installed:

- [Java 17 or later](https://www.oracle.com/java/technologies/javase-downloads.html)
- [JavaFX](https://openjfx.io/)
- [MySQL](https://www.mysql.com/)
- An API key from [The Movie Database (TMDB)](https://www.themoviedb.org/).

---

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-repo/movie-app.git
   ```
2. **Set up the MySQL database**:
   - Create a new database named `moviecard`.
   - Use the following SQL to create the required table:
     ```sql
     CREATE TABLE moviecard (
         id INT AUTO_INCREMENT PRIMARY KEY,
         title VARCHAR(255),
         overview TEXT,
         release_date DATE,
         image TEXT
     );
     ```
3. **Update the database configuration**:
   Open `HelloController.java` and update the database connection string with your MySQL credentials:

   ```java
   Connection connection = DriverManager.getConnection(
       "jdbc:mysql://127.0.0.1:3306/moviecard",
       "your-username",
       "your-password"
   );
   ```

4. **Add your TMDB API Key**:
   Replace the placeholder API key in `DataFetcher.java`:

   1. **.env File Setup**:

   - Instructions on where to place the `.env` file (inside `src/main/resources`).
   - Guide to adding the `MOVIE_API_KEY` in the `.env` file.

   ```java
   String apiKey = System.getenv("MOVIE_API_KEY");
   con.setRequestProperty("Authorization", "Bearer " + apikey);
   ```

5. **Run the application**:
   - Open the project in your favorite IDE (e.g., IntelliJ IDEA or Eclipse).
   - Run the `main` method in `HelloApplication.java`.

---

## Technologies Used

- **Java**: Programming language for application logic.
- **JavaFX**: Framework for the graphical user interface.
- **TMDB API**: Provides movie data.
- **MySQL**: Database for storing favorite movies.

---

---

## Future Enhancements

- Add user authentication.
- Support more languages.
- Implement a watchlist feature.

---
