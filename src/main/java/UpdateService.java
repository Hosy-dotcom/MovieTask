import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateService {
    private static Connection connection;

    //Connect to DB
    public UpdateService(Connection connection) {
        this.connection = connection;
    }

    //Update people to DB
    public  void addNewEntries(String userInput) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        //Adding a person
        if (userInput.equals("a -p")) {

            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Nationality: ");
            String nationality = scanner.nextLine();

            String query="INSERT INTO PEOPLE (name, nationality) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.setString(2, nationality);
                statement.executeUpdate();
                System.out.println("Person added successfully.");
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Person already exists");

            }
            catch (SQLException e) {
                System.out.println("Error adding person " );
            }

        }

        // Adding a movie
        else if (userInput.equals("a -m")) {

            System.out.print("Title: ");
            String title = scanner.nextLine();

            String length;
            int lengthInSecs = 0;
            while (true) {
                System.out.print("Length (hh:mm:ss): ");
                length = scanner.nextLine();
                try {
                    lengthInSecs = parseLength(length);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("- Bad input format (hh:mm:ss), try again!");
                }
            }
            String director;
            while (true) {
                System.out.print("Director: ");
                director = scanner.nextLine();

                if (personExists(director)) {
                    break;
                } else {
                    System.out.println("- We could not find \"" + director + "\", try again!");
                }
            }
            System.out.println("Starring (enter actor names, type 'exit' to finish):");
            List<String> actorNames = new ArrayList<>();
            String actor;
            while (true) {
                actor = scanner.nextLine();
                if (actor.equalsIgnoreCase("exit")) {
                    break;
                }
                if (personExists(actor)) {
                    if (!actorNames.contains(actor)) {
                        actorNames.add(actor); // Add unique actor to the list
                    }
                } else {
                    System.out.println("- We could not find \"" + actor + "\", try again!");
                }
            }
            String query="INSERT INTO MOVIES (title, director_name, length_in_secs) VALUES (?, ?, ?)";

            try (PreparedStatement movieStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
            {
                movieStatement.setString(1, title);
                movieStatement.setString(2, director);
                movieStatement.setInt(3, lengthInSecs);
                movieStatement.executeUpdate();

                ResultSet generatedKeys = movieStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int movieId = generatedKeys.getInt(1);

                    // Insert actors into the ACTORS table
                    try (PreparedStatement actorStatement = connection.prepareStatement(
                            "INSERT INTO ACTORS (movie_id, actor_name) VALUES (?, ?)")) {
                        for (String actorName : actorNames) {
                            actorStatement.setInt(1, movieId);
                            actorStatement.setString(2, actorName);
                            actorStatement.executeUpdate();
                        }
                    }

                    System.out.println("Movie added successfully.");
                }

            }catch (SQLException e) {
                System.out.println("Error adding movie: " + e.getMessage());
            }
        }

        else {
            System.out.println("Unknown subcommand for 'a'.");
        }
    }

    //Convert hh:mm:ss into length in seconds
    private static int parseLength(String length) {
        String[] parts = length.split(":");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid length format. Expected format: hh:mm:ss");
        }

        try {
            int hours = Integer.parseInt(parts[0].trim());
            int minutes = Integer.parseInt(parts[1].trim());
            int seconds = Integer.parseInt(parts[2].trim());

            if (hours < 0 || minutes < 0 || minutes >= 60 || seconds < 0 || seconds >= 60) {
                throw new IllegalArgumentException("Invalid time values.");
            }

            return hours * 3600 + minutes * 60 + seconds;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric values in length.");
        }
    }

    //Check if the person exists
    public static boolean personExists(String personName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM PEOPLE WHERE name = ?")) {
            statement.setString(1, personName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
