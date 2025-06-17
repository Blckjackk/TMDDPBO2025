# AzzamLova Game

## Overview
Azzam Love is a 2D love-themed game built using Java with Swing GUI. Players control the main character (Azzam) to collect colorful hearts using a lasso and deliver them to a girl in the top right corner of the screen.

## Architecture (MVVM)
This project follows the MVVM (Model-View-ViewModel) architecture pattern:

### Model Components:
- **`DatabaseManager.java`**: Handles MySQL database connections and operations
- **`PlayerResult.java`**: Data class for storing player scores and results

### ViewModel Components:
- **`GameEngine.java`**: Core game logic including heart spawning, lasso mechanics, and collision detection
- **`InputController.java`**: Processes keyboard and mouse inputs to control the game

### View Components:
- **`MainMenuView.java`**: Main menu interface with username input, high score table, and play/quit buttons
- **`GamePanel.java`**: Game rendering and display

### Database Structure:
The game uses a MySQL database with a single table:
```sql
CREATE TABLE thasil (
  username VARCHAR(100) PRIMARY KEY,
  skor INT NOT NULL,
  count INT NOT NULL
);
```

## Gameplay Flow

1. **Main Menu**: Players start at the main menu where they enter their username and see the high score table.
2. **Game Start**: After clicking "Play," the game begins with the player controlling Azzam.
3. **Collecting Hearts**: Colorful hearts appear from different directions. Players aim and click to throw a lasso to catch them.
4. **Delivery**: Once caught, hearts automatically move toward the girl character.
5. **Scoring**: Each heart color has a different point value:
   - Blue: 3 points
   - Green: 4 points
   - Yellow: 5 points
   - Red: 6 points
   - Orange: 7 points
   - Purple: 2 points
6. **End Game**: Players can press SPACE to end the game and return to the menu.

## Features
- **Database Integration**: Saves and displays high scores in MySQL database
- **Colorful Hearts**: Six different heart types with varying point values
- **Lasso Mechanics**: Click to throw a lasso that can catch hearts
- **Background Music**: Plays during the game
- **Controls**: 
  - **Arrow Keys**: Move Azzam around the screen
  - **Mouse**: Click to throw the lasso
  - **SPACE**: End game and return to menu

## Project Requirements

### Basic Requirements
- Java JDK 8 or later
- MySQL Server
- mysql-connector-j-9.2.0.jar (included in lib folder)

### Assets
The game uses various image assets:
- Character images (Azzam and the girl)
- Background images
- Heart images in different colors
- Rope/lasso images

## Installation and Setup

1. **Database Setup**:
   - Create a MySQL database named `azzam_love_db`
   - Use the provided `setup_database.sql` script to create the required table
   - Alternatively, run the following SQL commands manually:
   ```sql
   CREATE DATABASE IF NOT EXISTS azzam_love_db;
   USE azzam_love_db;
   CREATE TABLE thasil (
     username VARCHAR(100) PRIMARY KEY,
     skor INT NOT NULL,
     count INT NOT NULL
   );
   ```
   
2. **JDBC Driver Setup**:
   - Make sure MySQL JDBC connector (mysql-connector-j-9.2.0.jar) is properly added to your project
   - For IntelliJ IDEA: Project Structure → Modules → Dependencies → + → JARs or directories → select the connector in the lib folder
   - For command line: Include it in the classpath when compiling/running
   
3. **Compile and Run**:
   - Using IntelliJ IDEA: Simply run the Main class
   - Using command line: 
   ```
   javac -cp ".;lib/mysql-connector-j-9.2.0.jar" Main.java
   java -cp ".;lib/mysql-connector-j-9.2.0.jar" Main
   ```
   (Use colons instead of semicolons on Linux/Mac: `".:lib/mysql-connector-j-9.2.0.jar"`)
   
4. **Troubleshooting**:
   - If you encounter "No suitable driver" errors, double-check that the MySQL connector JAR is in your classpath
   - Verify database credentials (default: root with no password)
   - Check that your MySQL server is running

## Controls Summary

- **Arrow Keys (↑ ↓ ← →)**: Move Azzam around the screen
- **Mouse Click**: Throw lasso to catch hearts
- **Space**: End the current game and return to the main menu

## Credits

- Developed as a project for Design Pemrograman Berorientasi Object class
- Uses Java Swing for the GUI components
- Built on MVVM architecture pattern
- Assets located in the assets folder

## Additional Notes

- The game automatically saves scores to the database when returning to the menu
- If a username already exists in the database, the game will update the score only if the new score is higher
- Hearts move in different directions based on their spawn location
- The top row hearts move right to left
- The bottom row hearts move left to right
