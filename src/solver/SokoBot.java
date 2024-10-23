package solver;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashMap;

public class SokoBot {
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        GlobalMap.setMap(mapData);

        Scanner scanner = new Scanner(System.in);
        Heuristic calculator = new Heuristic();
        int input = 0;
        int totalStates = 0;

        // Where all the states will be added

        ArrayList<State> statesList = new ArrayList<>();
        int moveCost = 0;
        PriorityQueue<State> statequeue = new PriorityQueue<State>(30000, new Heuristic());

        // For making duplicate checking easier
        ArrayList<Coordinate> boxCoordinates = new ArrayList<>();
//        ArrayList<String> boxCoords = new ArrayList<>();
        HashMap<String, Boolean> boxCoords = new HashMap<String, Boolean>();

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
        State initialState = new State(initialPosition, width, height, goalCoordinates, 0);
        initialState.setBoxCoordinates(boxCoordinates);
        initialState.setGoalCoordinates(goalCoordinates);
        int goalCount = initialState.countGoals(goalCoordinates);
        initialState.setGoals(goalCount);

        initialState.setHeuristicValue(calculator.calcManDist(width, height, goalCoordinates, boxCoordinates, goalCount, initialState.getPath(), initialPosition));

        statesList.add(initialState);
        statequeue.add(initialState);

        while(!statequeue.isEmpty()) {
          State currState = statequeue.poll();

//          System.out.println("SELECTED STATE: " + currState.toString());
          //System.out.println(statequeue);

          ArrayList<State> newStates = currState.createStates(goalCoordinates, currState.getHeuristicValue());
          //System.out.println("Current States " + totalStates);
          totalStates++;

          // Check if any of the new states is a goal state
          for (State newState : newStates) {
            // If all goals are filled, return the path
            if (newState.countGoals(goalCoordinates) == goalCoordinates.size()) {
              System.out.println("Goal state reached!");
              System.out.println(newState.getPath());
              System.out.println("Path Cost: " + newState.getMoveCost());
                System.out.println("Total States: " + totalStates);
              return newState.getPath();
            }

            boolean existing = false;
              String currBoxCoords = toBoxCoords(width, height, newState.getBoxCoordinates(), newState.getPlayerPosition());

//              String currBoxCoords = toBoxCoords(width, height, newState.getItemsData());
              //System.out.println("Current State: " + newState.toString());
              //System.out.println("Box Coord of State: " + currBoxCoords);


              if (boxCoords.get(currBoxCoords) != null) {
                //System.out.println("********** this exists !! ***************");
                // System.out.println(existingState.toString());
                existing = true;
            }

            // Check if the new state is a duplicate of any existing state
            /*
            for (State existingState : statesList) {
              // Get player positions
              Coordinate existingPosition = existingState.getPlayerPosition();
              Coordinate newPosition = newState.getPlayerPosition();

              // Check if the player positions are the same using x and y values
              if (existingPosition.x == newPosition.x && existingPosition.y == newPosition.y) {
                //boolean sameBoxes = true;

                /*
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
                    System.out.println("********** this exists !! ***************");
                    System.out.println(existingState.toString());
                  existing = true;
                  break;
                }





              }
            }
            */

            // If not a duplicate, add the new state to the statesList
            if (!existing) {
                statequeue.add(newState);
                statesList.add(newState);
//                boxCoords.add(currBoxCoords);
                boxCoords.put(currBoxCoords, true);

                //System.out.println("boxCoords length: " + boxCoords.size());
                //if (totalStates > 10000)
                  //System.out.println(statequeue);
            }
          }
        }

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

    private String toBoxCoords(int width, int height, ArrayList<Coordinate> boxCoordinates, Coordinate playerPosition) {
        StringBuilder boxCoords = new StringBuilder();

        boxCoordinates.sort((c1, c2) -> {
            int pos1 = c1.y * width + c1.x;
            int pos2 = c2.y * width + c2.x;
            return Integer.compare(pos1, pos2);
        });

        for (Coordinate box : boxCoordinates) {
            boxCoords.append(box.y * width + box.x).append(',');
        }

        String playerCoords = Integer.toString(playerPosition.y * width + playerPosition.x);

        return playerCoords + '|' + boxCoords.toString();
    }


//  private String toBoxCoords(int width, int height, char[][] itemsData) {
//      StringBuilder boxCoords = new StringBuilder();
//      String playerCoords = new String();
//      for (int i = 0; i < height; i++) {
//          for (int j = 0; j < width; j++) {
//              if (itemsData[i][j] == '$') {
//                  boxCoords.append(Integer.toString(i * width + j));
//              }
//              if (itemsData[i][j] == '@') {
//                  playerCoords = Integer.toString(i * width + j);
//              }
//          }
//      }
//      return playerCoords + '|' + boxCoords.toString();
//  }
}

// javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
// java -classpath out main.Driver plains2 bot