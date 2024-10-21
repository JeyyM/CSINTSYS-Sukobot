package solver;

import java.util.ArrayList;

public class State {
    private char[][] itemsData;
    private char[][] mapData;

    // player position is kept as a coordinate so that getting the position for the states is faster
    // also this can be used to check for copies of the same map
    private Coordinate playerPosition;
    private int goals;
    // to be calculated
    private double heuristicValue = 0.0;
    private int width;
    private int height;
    // keeps track of the paths
    private StringBuilder path;

    // Directions: Up, Down, Left, Right
    // note how the values being changed
    final int[][] DIRECTIONS = {
            {0, -1},
            {0, 1},
            {-1, 0},
            {1, 0}
    };

    private final char[] DIRECTION_CHARS = {'u', 'd', 'l', 'r'};

    // Constructor
    public State(char[][] mapData, char[][] itemsData, Coordinate playerPosition, int width, int height, ArrayList<Coordinate> goalCoordinates) {
        this.mapData = mapData;
        this.itemsData = itemsData;
        this.playerPosition = playerPosition;
        this.width = width;
        this.height = height;
        this.path = new StringBuilder();
        this.goals = countGoals(goalCoordinates);
    }

    public Coordinate getPlayerPosition() {
        return playerPosition;
    }

    public String getPath() {
        return path.toString();
    }

    public void setPath(StringBuilder newPath) {
        this.path = newPath;
    }

    // note how this gets goalCoordinates, they were preloaded in the main
    // so that counting goals doesn't become n^2 by checking every cell
    public int countGoals(ArrayList<Coordinate> goalCoordinates) {
        int goalSpots = 0;
        for (Coordinate goal : goalCoordinates) {
            if (itemsData[goal.x][goal.y] == '$') {
                goalSpots++;
            }
        }
        return goalSpots;
    }


    // prints the state, nothing special
    public void printState() {
        System.out.printf("Current path: %s\n", getPath());
        System.out.printf("Player position: (%d, %d)\n", playerPosition.x, playerPosition.y);
        System.out.printf("Goal count: %d\n", goals);
        System.out.printf("Heuristic value: %.2f\n", heuristicValue);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.printf("[%c] ", mapData[i][j]);
            }

            System.out.print("    ");

            for (int j = 0; j < width; j++) {
                System.out.printf("[%c] ", itemsData[i][j]);
            }

            System.out.printf("\n");
        }
    }

    // where new states are made, it returns an ArrayList which will later be checked for a winning path
    // before being added to the statesList
    public ArrayList<State> createStates(ArrayList<Coordinate> goalCoordinates) {
        ArrayList<State> validStates = new ArrayList<>();
        int playerX = playerPosition.x;
        int playerY = playerPosition.y;

        // a for loop for each direction
        // note how continue is being used to skip when a state is a dud
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int[] direction = DIRECTIONS[i];
            int newX = playerX + direction[0];
            int newY = playerY + direction[1];

            char nextSpot = itemsData[newY][newX];

            // Skip wall states
            if (mapData[newY][newX] == '#') continue;

            // If it's a box, check if the box can be pushed
            if (itemsData[newY][newX] == '$') {
                // see how it adds one more time in the direction
                // gets 1 extra spot ahead
                int boxNewX = newX + direction[0];
                int boxNewY = newY + direction[1];

                // check mapData for a wall but check itemsData to make sure it isn't another box
                if (mapData[boxNewY][boxNewX] == '#' || itemsData[boxNewY][boxNewX] != ' ') continue;

                // Create new state with the box and player moved
                char[][] newItemsData = copyMap(itemsData);
                // clear box's original spot then put it in the new + 1 extra
                newItemsData[newY][newX] = ' ';
                newItemsData[boxNewY][boxNewX] = '$';

                // Move the player
                newItemsData[playerY][playerX] = ' ';
                newItemsData[newY][newX] = '@';

                // Create a new state and append the direction to the path
                State newState = new State(mapData, newItemsData, new Coordinate(newX, newY), width, height, goalCoordinates);
                // StringBuilder to append the path
                newState.setPath(new StringBuilder(this.getPath()).append(DIRECTION_CHARS[i]));
                validStates.add(newState);

            } else if (itemsData[newY][newX] == ' ') {
                // blank extra
                char[][] newItemsData = copyMap(itemsData);
                // move player
                newItemsData[playerY][playerX] = ' ';
                newItemsData[newY][newX] = '@';

                // Create a new state and append the direction to the path
                State newState = new State(mapData, newItemsData, new Coordinate(newX, newY), width, height, goalCoordinates);
                newState.setPath(new StringBuilder(this.getPath()).append(DIRECTION_CHARS[i]));  // Clone the current path and append
                validStates.add(newState);
            }
        }

        return validStates;
    }


    // To copy a current state
    public char[][] copyMap(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}
