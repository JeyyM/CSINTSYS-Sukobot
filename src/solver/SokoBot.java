package solver;

import java.util.Scanner;
import java.util.ArrayList;

// random selector
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    Scanner scanner = new Scanner(System.in);
    Heuristic calculator = new Heuristic();
    int input = 0;
    int totalState = 0;

    // Where all the states will be added
    ArrayList<State> statesList = new ArrayList<>();

    // For making duplicate checking easier
    ArrayList<Coordinate> boxCoordinates = new ArrayList<>();
    // For making goal checking easier
    ArrayList<Coordinate> goalCoordinates = new ArrayList<>();
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
    State initialState = new State(mapData, initialPosition, width, height, goalCoordinates);
    initialState.setBoxCoordinates(boxCoordinates);
    initialState.setGoalCoordinates(goalCoordinates);
    int goalCount = initialState.countGoals(goalCoordinates);
    initialState.setGoals(goalCount);

    initialState.setHeuristicValue(calculator.calcManDist(mapData, width, height, goalCoordinates, boxCoordinates, initialState.countGoals(goalCoordinates), initialState.getPath(), initialPosition));

    statesList.add(initialState);

    // USES LAST INDEX

    // new commit
    do {
      input = statesList.size() - 1;
      // Automatic selection: Start from index 0 and move up if the state is visited
      while (input >= 0 && statesList.get(input).getVisited()) {
        input--;  // Move to the next index if already visited
      }

      // Reset input to 0 if we reach the end of the list
      if (input < 0) {
        System.out.println("All states have been visited");
        break;
      }

      // Select the current state if it is unvisited and within bounds
      if (!statesList.get(input).getVisited()) {
        State selectedState = statesList.get(input);
        selectedState.setVisited();

        System.out.printf("SELECTED STATE: %d\n", input);
        totalState++;

        ArrayList<State> newStates = selectedState.createStates(goalCoordinates);

        // Check if any of the new states is a goal state
        for (State newState : newStates) {
          // If all goals are filled, return the path
          if (newState.countGoals(goalCoordinates) == goalCoordinates.size()) {
            System.out.println("Goal state reached!");
            System.out.println("States Visited: " + totalState);
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
            calculator.sortDescending(statesList);
          }
        }

        // Reset the input back to 0 to start from the best state again
        input = 0;
      } else {
        // Print message when the state is already visited
        System.out.println("State already visited.");
      }

    } while (true);

    // MANUAL INPUTTING SECTION
//    do {
//      // Print current states with their index
//      System.out.println("Current States:");
//      for (int i = 0; i < statesList.size(); i++) {
//        System.out.printf("Index %d: =============================================\n", i);
//        statesList.get(i).printState();
//      }
//
//      // Prompt user to enter a state index
//      System.out.print("Enter a state index (or -1 to exit): ");
//      input = scanner.nextInt();
//
//      // Exit the loop if user enters -1
//      if (input == -1) break;
//
//      // Check if the input is within the bounds of the statesList
//      if (input >= 0 && input < statesList.size()) {
//        // Check if the selected state has not been visited
//        if (!statesList.get(input).getVisited()) {
//
//          State selectedState = statesList.get(input);
//          selectedState.setVisited();
//
//          // Generate new states from the selected state
//          ArrayList<State> newStates = selectedState.createStates(goalCoordinates);
//
//          // Check if any of the new states is a goal state
//          for (State newState : newStates) {
//            // If all goals are filled, return the path
//            if (newState.countGoals(goalCoordinates) == goalCoordinates.size()) {
//              System.out.println("Goal state reached!");
//              return newState.getPath();
//            }
//
//            boolean existing = false;
//
//            // Check if the new state is a duplicate of any existing state
//            for (State existingState : statesList) {
//              // Get player positions
//              Coordinate existingPosition = existingState.getPlayerPosition();
//              Coordinate newPosition = newState.getPlayerPosition();
//
//              // Check if the player positions are the same using x and y values
//              if (existingPosition.x == newPosition.x && existingPosition.y == newPosition.y) {
//                boolean sameBoxes = true;
//
//                // Compare each box coordinate in the existing with the new state
//                for (int i = 0; i < existingState.getBoxCoordinates().size(); i++) {
//                  Coordinate box = existingState.getBoxCoordinates().get(i);
//                  Coordinate newBox = newState.getBoxCoordinates().get(i);
//
//                  // If any box coordinate doesn't match, mark as different
//                  if (box.x != newBox.x || box.y != newBox.y) {
//                    sameBoxes = false;
//                    break;
//                  }
//                }
//
//                // If all boxes match, mark the state as existing
//                if (sameBoxes) {
//                  existing = true;
//                  break;
//                }
//              }
//            }
//
//            // If not a duplicate, add the new state to the statesList
//            if (!existing) {
//              statesList.add(newState);
//              calculator.sortDescending(statesList);  // Adjust sorting order as needed
//            }
//          }
//        } else {
//          // Print message when the state is already visited
//          System.out.println("State already visited.");
//        }
//      } else {
//        // Print message when the input index is invalid
//        System.out.println("Invalid index. Please try again.");
//      }
//
//    } while (true);

    return "lrlrlrlrlr";
  }
}

// javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
// java -classpath out main.Driver plains2 bot