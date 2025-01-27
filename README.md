# Java Project: Nokia Movie Task

A simple Java-based application for managing a movie database. It allows adding, deleting, and filtering movies, directors, and actors via a command-line interface (CLI).

---

## Features

- **Sort & View**: Sort results by movie length or other criteria with optional verbose details.
    - Example: `"l"` to list movies.
    - Example: `"l -v"` to list movies with starring actors.
  
- **Filter Movies**: Search movies by title, director, or actor using specific switches.
    - Example: `"l -a "Tom.*"` to list movies with starring actors whose names start with "Tom".
    - Example: `"l -a "Tom.*" -v"` to list movies with starring actors whose names start with "Tom" with actor details.
    - Example: `"l -d "Steven.*"` to list movies directed by directors whose names start with "Steven".
    - Example: `"l -d "Steven.*" -v"` to list movies directed by directors whose names start with "Steven" with actor details.
    - Example: `"l -t "The.*"` to list movies with titles starting with "The".
    - Example: `"l -t "The.*" -v"` to list movies with titles starting with "The" with actor details.
    - Example: `"l -a "Tom.*" -d "Steven.*" -ld -v"` to list movies starring actors whose names start with "Tom" and directed by directors whose names start with "Steven", sorted by length in descending order with actor details.

- **Add New Entries**: Add people (directors, actors) and movies.
    - Example: `"a -p"` to add people.
    - Example: `"a -m"` to add a movie.

- **Delete Entries**: Remove actors from the database while maintaining integrity (directors cannot be deleted).
    - Example: `"d -p "Tom Hanks"` to delete Tom Hanks from the people database and their roles in movies.

---

## Requirements

- **Java Version**: Java 8 or higher.
- **Database**: In-memory H2 Database (embedded).
- **Libraries**: JDBC (Java Database Connectivity) for database operations.

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Hosy-dotcom/MovieTask.git
   cd MovieTask
