import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    private static final String url = "jdbc:h2:mem:testdb"; // In-memory database
    private static final String username = "sa";
    private static final String password = "";
    private Connection connection;

    //Connection constructor
    public DBManager() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to in-memory database");
            displayMenuList();
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    //Create and insert the DB tables
    public void initializaDB(){
        try {
            // Create and set default schema
            String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS MOVIE";
            Statement statement = connection.createStatement();
            statement.execute(createSchemaSQL);

            String setSchemaSQL = "SET SCHEMA MOVIE";
            statement.execute(setSchemaSQL);


            String createTableSQLPeople = "CREATE TABLE PEOPLE(" +
                    "name VARCHAR(100) , " +
                    "nationality VARCHAR(50) NOT NULL,"+
                    "CONSTRAINT pk_name PRIMARY KEY(name))";
            statement.execute(createTableSQLPeople);

            String createTableSQLMovies = "CREATE TABLE MOVIES(" +
                    "movie_id INT PRIMARY KEY AUTO_INCREMENT,"+
                    "title VARCHAR(128) NOT NULL,"+
                    "director_name VARCHAR(100) NOT NULL,"+
                    "length_in_secs INT NOT NULL,"+
                    " CONSTRAINT uq_title_director UNIQUE (title, director_name),"+
                    "CONSTRAINT fk_director_name FOREIGN KEY (director_name) REFERENCES People(name))";
            statement.execute(createTableSQLMovies);

            String createTableSQLActors = "CREATE TABLE ACTORS(" +
                    "movie_id INT,"+
                    "actor_name VARCHAR(100) ,"+
                    "CONSTRAINT uq_actor_movie UNIQUE(movie_id, actor_name),"+
                    "CONSTRAINT fk_movie_id FOREIGN KEY(movie_id) REFERENCES Movies(movie_id),"+
                    " CONSTRAINT fk_actors_name FOREIGN KEY(actor_name) REFERENCES People(name))";
            statement.execute(createTableSQLActors);



            String insertPeopleSQL = "INSERT INTO PEOPLE (name, nationality) VALUES " +
                    "('Steven Spielberg', 'American'), " +
                    "('Christopher Nolan', 'British'), " +
                    "('Tom Hanks', 'American'), " +
                    "('Leonardo DiCaprio', 'American'), " +
                    "('Matt Damon', 'American'), " +
                    "('Anne Hathaway', 'American'), " +
                    "('Tom Hardy', 'British'), " +
                    "('Cillian Murphy', 'Irish'), " +
                    "('Heath Ledger', 'Australian'), " +
                    "('Matthew McConaughey', 'American'), " +
                    "('Christian Bale', 'British');";
            statement.execute(insertPeopleSQL);

            String insertMoviesSQL = "INSERT INTO MOVIES (TITLE, DIRECTOR_NAME, LENGTH_IN_SECS) VALUES " +
                    "('Inception', 'Christopher Nolan', 8880), " +
                    "('The Dark Knight', 'Christopher Nolan', 9120), " +
                    "('Catch Me If You Can', 'Steven Spielberg', 8280), " +
                    "('Saving Private Ryan', 'Steven Spielberg', 10200), " +
                    "('Interstellar', 'Christopher Nolan', 10140);";
            statement.execute(insertMoviesSQL);


            String insertActorsSQL = "INSERT INTO ACTORS (MOVIE_ID, ACTOR_NAME) VALUES " +
                    "(1, 'Leonardo DiCaprio'), (1, 'Tom Hardy'), " +       // Inception
                    "(2, 'Christian Bale'), (2, 'Heath Ledger'), " +      // The Dark Knight
                    "(3, 'Tom Hanks'), (3, 'Leonardo DiCaprio'), " +      // Catch Me If You Can
                    "(4, 'Tom Hanks'), (4, 'Matt Damon'), " +            // Saving Private Ryan
                    "(5, 'Matthew McConaughey'), (5, 'Anne Hathaway');"; // Interstellar
            statement.execute(insertActorsSQL);


        }
        catch (SQLException e){
            System.out.println("Error Initialization DB: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
    public void displayMenuList(){
        System.out.println("l to list movies\n" +
                "\t -v for verbose mode\n" +
                "\t -t \"1st word of title\" to filter by that title\n"+
                "\t -a \"1st word of actor's name.*\" to filter by that actor\n"+
                "\t -d \"1st word of director's name.*\" to filter by that director\n"+
                "\t -la for ascending order by their length\n"+
                "\t -ld for descending order by their length\n"+
                "a -p to add people\n"+
                "a -m to add a movie\n"+
                "d -p \"name\"  to delete person from the people database and their roles in movies\n");
    }
}

