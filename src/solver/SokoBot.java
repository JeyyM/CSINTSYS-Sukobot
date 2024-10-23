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
            ArrayList<State> newStates = currState.createStates(goalCoordinates, currState.getHeuristicValue());
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
                if (boxCoords.get(currBoxCoords) != null) {
                    existing = true;
                }

                if (!existing) {
                    statequeue.add(newState);
                    statesList.add(newState);
                    boxCoords.put(currBoxCoords, true);
                }
            }
        }

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
}

// javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
// java -classpath out main.Driver plains2 bot