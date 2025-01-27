import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Movie {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        dbManager.initializaDB();

        ListingService lstService = new ListingService(dbManager.getConnection());
        UpdateService updService = new UpdateService(dbManager.getConnection());
        DeleteService delService = new DeleteService(dbManager.getConnection());

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter a command (type 'exit' to quit or 'help' for the menu):");
            String userInput = scanner.nextLine();
            if (userInput.equals("exit")) {
                break;
            }

            if (userInput.startsWith("l")) {
                try {
                    lstService.filterMovies(userInput);
                } catch (SQLException e) {
                    System.out.println("Error while retrieving movies: " + e.getMessage());
                }
            }
            else if (userInput.startsWith("a")) {
                try {
                    updService.addNewEntries(userInput);
                }
                catch (SQLException e){
                    System.out.println("Error while adding new entry: " + e.getMessage());
                }
            }
            else if(userInput.startsWith("d")) {
                try{
                    delService.deletePeople(userInput);
                }
                catch (SQLException e){
                    System.out.println("Error while deleting people: " + e.getMessage());
                }
            }
            else if (userInput.equals("help")) {
                dbManager.displayMenuList();
            }
            else {
                System.out.println("Unknown command. Please try again.");
            }
        }
        dbManager.closeConnection();


    }
}

