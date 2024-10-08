package solver;

import java.util.Scanner;
import java.util.ArrayList;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    // where all of the states will be added
    ArrayList<State> statesList = new ArrayList<>();
    // for making goal checking easier
    ArrayList<Coordinate> goalCoordinates = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    StringBuilder path = new StringBuilder();
    int input;

    // Find the initial player position
    Coordinate initialPosition = null;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (itemsData[i][j] == '@') {
          initialPosition = new Coordinate(j, i);
          break;
        }
      }
      if (initialPosition != null) {
        break;
      }
    }

    // Store goal positions for easier searching
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (mapData[i][j] == '.') {
          goalCoordinates.add(new Coordinate(i, j));
        }
      }
    }

    // Create the initial state
    State initialState = new State(mapData, itemsData, initialPosition, width, height, goalCoordinates);
    statesList.add(initialState);

    // Input loop: keep generating states until a goal state is found
    do {
      // Print current states with their index
      System.out.println("Current States:");
      for (int i = 0; i < statesList.size(); i++) {
        System.out.printf("Index %d: =============================================\n", i);
        statesList.get(i).printState();
      }

      System.out.print("Enter a state index (or -1 to exit): ");
      input = scanner.nextInt();

      if (input == -1) break;

      if (input >= 0 && input < statesList.size()) {
        State selectedState = statesList.get(input);
        ArrayList<State> newStates = selectedState.createStates(goalCoordinates);

        // Check if any of the new states is a goal state
        for (State newState : newStates) {
          // If all goals are filled, return the path
          if (newState.countGoals(goalCoordinates) == goalCoordinates.size()) {
            System.out.println("Goal state reached!");
            return newState.getPath();
          }

          // Add the new state to the statesList for further exploration
          statesList.add(newState);
        }
      } else {
        System.out.println("Invalid index. Please try again.");
      }

    } while (true);

    return "lrlrlr";
  }
}

// javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
// java -classpath out main.Driver plains2 bot