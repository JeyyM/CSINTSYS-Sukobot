package solver;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashMap;

public class SokoBot {
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        // Uses the GlobalMap static class to create a one time copy of the mapData which doesn't change
        GlobalMap.setMap(mapData);

        // Class for calculating the heurstic
        Heuristic calculator = new Heuristic();

        int input = 0;
        int totalStates = 0;

        // Used for A* calculation on move cost
        int moveCost = 0;

        // Where all the states will be added
        ArrayList<State> statesList = new ArrayList<>();

        // Uses a priority queue to track the lowest heuristics
        PriorityQueue<State> statequeue = new PriorityQueue<State>(30000, new Heuristic());

        //Used for faster duplicate checking
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

        // Used to track the boxes so that the whole itemData array is no longer needed
        ArrayList<Coordinate> boxCoordinates = new ArrayList<>();
        ArrayList<Coordinate> goalCoordinates = new ArrayList<>();
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

        // Uses a hash map for faster duplicate detection
        HashMap<String, Boolean> boxCoords = new HashMap<String, Boolean>();

        // Creation of the initial state
        State initialState = new State(initialPosition, width, height, goalCoordinates, 0);
        initialState.setBoxCoordinates(boxCoordinates);
        initialState.setGoalCoordinates(goalCoordinates);
        int goalCount = initialState.countGoals(goalCoordinates);
        initialState.setGoals(goalCount);

        initialState.setHeuristicValue(calculator.calcManDist(width, height, goalCoordinates, boxCoordinates, goalCount, initialState.getPath(), initialPosition));

        statesList.add(initialState);
        statequeue.add(initialState);

        // Bot loop
        while(!statequeue.isEmpty()) {
            // Adds a state to the explored list
            // Dequeues from priority queue
            State currState = statequeue.poll();

            // Creates states to revalidate as a duplicate
            ArrayList<State> newStates = currState.createStates(goalCoordinates, currState.getHeuristicValue());
            totalStates++;

            // Checks for a winning state
            for (State newState : newStates) {
                if (newState.countGoals(goalCoordinates) == goalCoordinates.size()) {
                    System.out.println("Goal state reached!");
                    System.out.println(newState.getPath());
                    System.out.println("Path Cost: " + newState.getMoveCost());
                    System.out.println("Total States: " + totalStates);
                    return newState.getPath();
                }

                // Detects existing state by turning it into a string then finding it in the state hashmap
                boolean existing = false;
                String currBoxCoords = toBoxCoords(width, height, newState.getBoxCoordinates(), newState.getPlayerPosition());
                if (boxCoords.get(currBoxCoords) != null) {
                    existing = true;
                }

                // if it is a valid state, add it to priority queue and list of states
                if (!existing) {
                    statequeue.add(newState);
                    statesList.add(newState);
                    boxCoords.put(currBoxCoords, true);
                }
            }
        }

        return "";
    }

    // Used to turn every state's cell into a string identifier
    // The format is the player's coordinate | list of all boxcoordinates from top left to bottom right
    private String toBoxCoords(int width, int height, ArrayList<Coordinate> boxCoordinates, Coordinate playerPosition) {
        StringBuilder boxCoords = new StringBuilder();

        boxCoordinates.sort((c1, c2) -> {
            int pos1 = c1.y * width + c1.x;
            int pos2 = c2.y * width + c2.x;
            return Integer.compare(pos1, pos2);
        });

        for (Coordinate box : boxCoordinates) {
            boxCoords.append(box.y * width + box.x);
        }

        String playerCoords = Integer.toString(playerPosition.y * width + playerPosition.x);

        return playerCoords + '|' + boxCoords.toString();
    }
}

// javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
// java -classpath out main.Driver plains2 bot