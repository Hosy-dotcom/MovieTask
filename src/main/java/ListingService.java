import java.sql.*;

public class ListingService {
    private  Connection connection;
    private String titleKeyword = null;
    private String directorKeyword = null;
    private String actorKeyword = null;
    private boolean verbose = false;
    private boolean sortAsc = false;
    private boolean sortDesc = false;

    //setting connection from DBManager
    public ListingService(Connection connection) {

        this.connection = connection;
    }

    //Filter the movies by keyword
    public void filterMovies(String userInput) throws SQLException {

        titleKeyword = null;
        directorKeyword = null;
        actorKeyword = null;
        verbose = false;
        sortAsc = false;
        sortDesc = false;

        String[] parts = userInput.split(" ");
        try {
            for (int i = 1; i < parts.length; i++) {
                switch (parts[i]) {
                    case "-t":
                        titleKeyword = parseRegex(parts[++i]);
                        break;
                    case "-d":
                        directorKeyword = parseRegex(parts[++i]);
                        break;
                    case "-a":
                        actorKeyword = parseRegex(parts[++i]);
                        break;
                    case "-v":
                        verbose = true;
                        break;
                    case "-la":
                        if (sortDesc) {
                            throw new IllegalArgumentException("Cannot use both -la and -ld.");
                        }
                        sortAsc = true;
                        break;
                    case "-ld":
                        if (sortAsc) {
                            throw new IllegalArgumentException("Cannot use both -la and -ld.");
                        }
                        sortDesc = true;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid switch or format: " + parts[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        listQuery();
    }

    //Filtering the Regex
    private static String parseRegex(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.substring(1, input.length() - 1).replace(".*", "%");
        } else {
            throw new IllegalArgumentException("Invalid or missing quoted value.");
        }
    }

    //Convert length in seconds to hh:mm:ss format
    private static String formatLengthInSec(int lengthInSecs) {
        int hours = lengthInSecs / 3600;
        int minutes = (lengthInSecs % 3600) / 60;
        int seconds = lengthInSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    //Actual SQL query for listing
    private String queryBuilder(){
        String query = """
            SELECT m.title, m.director_name, m.length_in_secs,
                   GROUP_CONCAT(a.actor_name) AS actors
            FROM MOVIES m
            LEFT JOIN ACTORS a ON m.movie_id = a.movie_id
            WHERE 1=1
        """;
        if (titleKeyword != null) {
            query += " AND LOWER(m.title) LIKE LOWER(?)";
        }
        if (directorKeyword != null) {
            query += " AND LOWER(m.director_name) LIKE LOWER(?)";
        }
        if (actorKeyword != null) {
            query += """
            AND EXISTS (
                SELECT 1 FROM ACTORS a WHERE a.movie_id = m.movie_id AND LOWER(a.actor_name) LIKE LOWER(?)
            )
        """;
        }
        query += " GROUP BY m.movie_id, m.title, m.director_name, m.length_in_secs";
        if (sortAsc) {
            query += " ORDER BY m.length_in_secs ASC, m.title ASC";
        }
        else if (sortDesc) {
            query += " ORDER BY m.length_in_secs DESC, m.title ASC";
        }
        else {
            query += " ORDER BY m.title ASC";
        }

        return query;
    }

    //Actual listing
    private  void listQuery() throws SQLException {
        String query = queryBuilder();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 1;
            if (titleKeyword != null) {
                statement.setString(paramIndex++, titleKeyword);

            }
            if (directorKeyword != null) {
                statement.setString(paramIndex++, directorKeyword);

            }
            if (actorKeyword != null) {
                statement.setString(paramIndex++, actorKeyword);

            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No movies found matching the given filters.");
                    return;
                }
                while (resultSet.next()) {

                        String title = resultSet.getString("title");
                        String directorName = resultSet.getString("director_name");
                        int lengthInSecs = resultSet.getInt("length_in_secs");
                        String actors = resultSet.getString("actors");

                        System.out.println(title + " BY " + directorName + ", " + formatLengthInSec(lengthInSecs));
                        if (verbose) {
                            System.out.println("Starring:");
                            if (actors != null) {
                                for (String actor : actors.split(",")) {
                                    System.out.println("- " + actor.trim());
                                }
                            } else {
                                System.out.println("- No actors found");
                            }
                        }

                    }

            }
        }
    }
}
