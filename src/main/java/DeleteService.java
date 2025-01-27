import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteService {
    private  Connection connection;

    //Connect to DB
    public DeleteService(Connection connection) {
        this.connection = connection;
    }

    //Delete People from DB
    public  void deletePeople(String userInput) throws SQLException {
        String personKeyword = null;

        try {
            if (userInput.contains("-p")) {
                int startIndex = userInput.indexOf("-p") + 2;
                String remainingInput = userInput.substring(startIndex).trim();
                if (remainingInput.startsWith("\"") && remainingInput.endsWith("\"")) {
                    personKeyword = remainingInput.substring(1, remainingInput.length() - 1); // Extract the quoted value
                } else {
                    throw new IllegalArgumentException("Invalid or missing quoted value. Ensure the value is enclosed in double quotes.");
                }
            }else {
                throw new IllegalArgumentException("Missing -p flag in the command.");
            }
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (!UpdateService.personExists(personKeyword)) {
            System.out.println("Error: Person does not exist.");
            return;
        }
        if (isDirector(personKeyword)) {
            System.out.println("Error: Cannot delete person because they are director.");
            return;
        }
        try (PreparedStatement deleteActorsStatement = connection.prepareStatement(
                "DELETE FROM ACTORS WHERE actor_name = ?");
             PreparedStatement deletePersonStatement = connection.prepareStatement(
                     "DELETE FROM PEOPLE WHERE name = ?")) {
            deleteActorsStatement.setString(1, personKeyword);
            deleteActorsStatement.executeUpdate();

            deletePersonStatement.setString(1, personKeyword);
            deletePersonStatement.executeUpdate();

            System.out.println("Person deleted successfully.");

        }
        catch (SQLException e) {
            System.out.println("Error while deleting person: " + e.getMessage());
        }
    }

    //Check if the Person is director
    private  boolean isDirector(String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT 1 FROM MOVIES WHERE director_name = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }

    }
}
