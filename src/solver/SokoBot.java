package solver;

import java.util.Scanner;
import java.util.ArrayList;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    Scanner scanner = new Scanner(System.in);

    // Where all the states will be added
    ArrayList<State> statesList = new ArrayList<>();

    // For making duplicate checking easier
    ArrayList<Coordinate> boxCoordinates = new ArrayList<>();
    // For making goal checking easier
    ArrayList<Coordinate> goalCoordinates = new ArrayList<>();
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

    // Store goal and box positions for easier duplicate checking
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (mapData[i][j] == '.') {
          goalCoordinates.add(new Coordinate(j, i));
        }
        if (itemsData[i][j] == '$') {
          boxCoordinates.add(new Coordinate(j, i));
        }
      }
    }

    // Create the initial state
    State initialState = new State(mapData, itemsData, initialPosition, width, height, goalCoordinates);
    initialState.setBoxCoordinates(boxCoordinates);
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

          boolean existing = false;

          // Check if the new state is a duplicate of any existing state
          for (State existingState : statesList) {
            // Get player positions
            Coordinate existingPosition = existingState.getPlayerPosition();
            Coordinate newPosition = newState.getPlayerPosition();

            // Check if the player positions are the same using x and y values
            if (existingPosition.x == newPosition.x && existingPosition.y == newPosition.y) {
              boolean sameBoxes = true;

              // Compare each box coordinate in the existing with the new state
              for (int i = 0; i < existingState.getBoxCoordinates().size(); i++) {
                Coordinate box = existingState.getBoxCoordinates().get(i);
                Coordinate newBox = newState.getBoxCoordinates().get(i);

                if (box.x != newBox.x || box.y != newBox.y) {
                  sameBoxes = false;
                  break;
                }
              }

              // If all boxes match, mark the state as existing
              if (sameBoxes) {
                existing = true;
                break;
              }
            }
          }

          // If not a duplicate, add the new state to the statesList
          if (!existing) {
            statesList.add(newState);
          }
        }

      } else {
        System.out.println("Invalid index. Please try again.");
      }

    } while (true);

    return "No solution found.";
  }
}

// hi