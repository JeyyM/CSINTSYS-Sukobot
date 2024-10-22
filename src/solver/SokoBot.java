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

    // Where all the states will be added
    ArrayList<State> statesList = new ArrayList<>();

    // For making duplicate checking easier
    ArrayList<Coordinate> boxCoordinates = new ArrayList<>();
    // For making goal checking easier
    ArrayList<Coordinate> goalCoordinates = new ArrayList<>();
    int input = 0;

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
    initialState.setGoalCoordinates(goalCoordinates);

    initialState.setHeuristicValue(calculator.calcManDist(mapData, itemsData, width, height, goalCoordinates, boxCoordinates, initialState.countGoals(goalCoordinates)));

    statesList.add(initialState);

    // Input loop: keep generating states until a goal state is found
    do {
      // Print current states with their index
//      System.out.println("Current States:");
//      for (int i = 0; i < statesList.size(); i++) {
//        System.out.printf("Index %d: =============================================\n", i);
//        statesList.get(i).printState();
//      }

      // Automatic selection: Start from index 0 and move up if the state is visited
      while (input < statesList.size() && statesList.get(input).getVisited()) {
        input++;  // Move to the next index if already visited
      }

      // Reset input to 0 if we reach the end of the list
      if (input >= statesList.size()) {
        input = 0;
      }

      // Select the current state if it is unvisited and within bounds
      if (input >= 0 && input < statesList.size() && !statesList.get(input).getVisited()) {
        State selectedState = statesList.get(input);
        selectedState.setVisited();

        System.out.printf("SELECTED STATE: %d\n", input);

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
            calculator.sortNonDecreasing(statesList);
          }
        }

        // Reset the input back to 0 to start from the best state again
        input = 0;
      } else {
        // Print message when the state is already visited
        System.out.println("State already visited.");
      }

      // Check if all states have been visited
      if (statesList.stream().allMatch(State::getVisited)) {
        System.out.println("All states have been visited.");
        break;
      }

    } while (true);

//    MANUAL INPUTTING SECTION
//    do {
//      // Print current states with their index
//      System.out.println("Current States:");
//      for (int i = 0; i < statesList.size(); i++) {
//        System.out.printf("Index %d: =============================================\n", i);
//        statesList.get(i).printState();
//      }
//
//      System.out.print("Enter a state index (or -1 to exit): ");
//      input = scanner.nextInt();
//
//      if (input == -1) break;
//
//      if (input >= 0 && input < statesList.size()) {
//        if (statesList.get(input).getVisited() == false) {
//
//          State selectedState = statesList.get(input);
//          selectedState.setVisited();
//
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
//              calculator.sortNonDecreasing(statesList);
//            }
//          }
//        } else {
//          System.out.println("State already visited.");
//        }
//      } else {
//        System.out.println("Invalid index. Please try again.");
//      }
//
//    } while (true);

// RANDOM SELECTION
//    Random random = new Random();
//    Set<Integer> visitedIndices = new HashSet<>();
//
//    do {
//      // Print current states with their index
////      System.out.println("Current States:");
////      for (int i = 0; i < statesList.size(); i++) {
////        System.out.printf("Index %d: =============================================\n", i);
////        statesList.get(i).printState();
////      }
//
//      // Generate a random index that hasn't been visited yet
//      int randomIndex = random.nextInt(statesList.size());
//
//      // Ensure the random index hasn't been visited
//      while (visitedIndices.contains(randomIndex)) {
//        randomIndex = random.nextInt(statesList.size());
//      }
//
//      // Mark the random index as visited
//      visitedIndices.add(randomIndex);
//
//      // Check if the random index is within bounds
//      if (randomIndex >= 0 && randomIndex < statesList.size()) {
//        // Check if the state has already been visited
//        if (!statesList.get(randomIndex).getVisited()) {
//          State selectedState = statesList.get(randomIndex);
//          selectedState.setVisited();
//          selectedState.printState();
//
//          System.out.printf("SELECTED STATE: %d\n", randomIndex);
//
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
//            }
//          }
//        } else {
//          // Log when the state is already visited
//          System.out.printf("STATE %d ALREADY VISITED\n", randomIndex);
//        }
//      }
//
//      // If all indices have been visited, break the loop
//      if (visitedIndices.size() == statesList.size()) {
//        System.out.println("All states have been visited.");
//        break;
//      }
//
//    } while (true);

    return "lrlrlrlrlr";
  }
}

// javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
// java -classpath out main.Driver plains2 bot