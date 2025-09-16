
import java.util.*;

class Movie {

    String title;
    List<String> genres;

    Movie(String title, List<String> genres) {
        this.title = title;
        this.genres = genres;
    }
}

public class RecommendationSystem {

    // Sample movies database
    static List<Movie> movies = Arrays.asList(
            new Movie("Inception", Arrays.asList("Sci-Fi", "Thriller")),
            new Movie("Interstellar", Arrays.asList("Sci-Fi", "Drama")),
            new Movie("The Dark Knight", Arrays.asList("Action", "Thriller")),
            new Movie("Titanic", Arrays.asList("Romance", "Drama")),
            new Movie("Avengers", Arrays.asList("Action", "Sci-Fi")),
            new Movie("The Notebook", Arrays.asList("Romance", "Drama")),
            new Movie("Shutter Island", Arrays.asList("Thriller", "Drama"))
    );

    // Recommend movies based on preferred genres
    public static List<Movie> recommendMovies(List<String> userPreferences) {
        List<Movie> recommendations = new ArrayList<>();

        for (Movie movie : movies) {
            for (String genre : userPreferences) {
                if (movie.genres.contains(genre) && !recommendations.contains(movie)) {
                    recommendations.add(movie);
                }
            }
        }
        return recommendations;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("🎬 Welcome to AI Movie Recommendation System!");
        System.out.println("Enter your favorite genres (comma separated, e.g., Sci-Fi, Drama):");

        String input = sc.nextLine();
        List<String> userPreferences = Arrays.asList(input.split(","));

        // Trim spaces
        userPreferences.replaceAll(String::trim);

        List<Movie> recommended = recommendMovies(userPreferences);

        System.out.println("\n✅ Recommended Movies for You:");
        for (Movie movie : recommended) {
            System.out.println("- " + movie.title + " (" + movie.genres + ")");
        }
        sc.close();
    }
}