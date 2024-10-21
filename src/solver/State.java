package solver;

import java.util.ArrayList;

public class State {
    private char[][] itemsData;
    private char[][] mapData;

    // player position is kept as a coordinate so that getting the position for the states is faster
    // also this can be used to check for copies of the same map
    private Coordinate playerPosition;
    private ArrayList<Coordinate> boxCoordinates;
    private int goals;
    private int width;
    private int height;
    // keeps track of the paths
    private StringBuilder path;
    // to be calculated
    private double heuristicValue = 0.00;

    // Directions: Up, Down, Left, Right
    private int[][] DIRECTIONS = {
            {0, -1}, // up
            {0, 1},  // down
            {-1, 0}, // left
            {1, 0}   // right
    };

    private char[] DIRECTION_CHARS = {'u', 'd', 'l', 'r'};

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

    // Set initial box coordinates
    public void setBoxCoordinates(ArrayList<Coordinate> initialBoxCoordinates) {
        this.boxCoordinates = new ArrayList<>();
        for (Coordinate coord : initialBoxCoordinates) {
            this.boxCoordinates.add(new Coordinate(coord.x, coord.y));
        }
    }

    public ArrayList<Coordinate> getBoxCoordinates() {
        return boxCoordinates;
    }

    // Count number of boxes on goal positions
    public int countGoals(ArrayList<Coordinate> goalCoordinates) {
        int goalSpots = 0;
        for (Coordinate goal : goalCoordinates) {
            if (itemsData[goal.y][goal.x] == '$') {
                goalSpots++;
            }
        }
        return goalSpots;
    }

    // where new states are made, it returns an ArrayList which will later be checked for a winning path
    // before being added to the statesList
    public void printState() {
        System.out.printf("Current path: %s\n", getPath());
        System.out.printf("Player position: (%d, %d)\n", playerPosition.x, playerPosition.y);
        System.out.printf("Goal count: %d\n", goals);
        System.out.println("Box Positions: " + this.boxCoordinates);
        System.out.printf("Heuristic value: %.2f\n", heuristicValue);

        // Print map and item data side by side for better visualization
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.printf("[%c] ", mapData[i][j]);
            }

            System.out.print("    ");

            for (int j = 0; j < width; j++) {
                System.out.printf("[%c] ", itemsData[i][j]);
            }

            System.out.println();
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

            // Avoid out of bounds
            if (newX < 0 || newX >= width || newY < 0 || newY >= height) continue;

            // Skip wall states
            if (mapData[newY][newX] == '#') continue;

            // If it's a box, check if the box can be pushed
            if (itemsData[newY][newX] == '$') {
                int boxNewX = newX + direction[0];
                int boxNewY = newY + direction[1];

                // Avoid box going out of bounds
                if (boxNewX < 0 || boxNewX >= width || boxNewY < 0 || boxNewY >= height) continue;

                // Check if the new box position is valid
                if (mapData[boxNewY][boxNewX] == '#' || itemsData[boxNewY][boxNewX] != ' ') continue;

                // Create a new state with the box and player moved
                char[][] newItemsData = copyMap(itemsData);

                // Clear the box's original spot, then put it in the new position
                newItemsData[newY][newX] = ' ';
                newItemsData[boxNewY][boxNewX] = '$';

                // Move the player
                newItemsData[playerY][playerX] = ' ';
                newItemsData[newY][newX] = '@';

                // Copy the current boxCoordinates
                ArrayList<Coordinate> newBoxCoordinates = new ArrayList<>();
                for (Coordinate coord : this.boxCoordinates) {
                    newBoxCoordinates.add(new Coordinate(coord.x, coord.y));
                }

                // Update the box coordinates by finding the pushed box
                for (int j = 0; j < newBoxCoordinates.size(); j++) {
                    Coordinate coord = newBoxCoordinates.get(j);
                    if (coord.x == newX && coord.y == newY) {
                        coord.x = boxNewX;
                        coord.y = boxNewY;
                        break;
                    }
                }

                // Create a new state with the updated boxCoordinates
                State newState = new State(mapData, newItemsData, new Coordinate(newX, newY), width, height, goalCoordinates);
                newState.setBoxCoordinates(newBoxCoordinates);

                // Append the direction to the path
                newState.setPath(new StringBuilder(this.getPath()).append(DIRECTION_CHARS[i]));
                validStates.add(newState);

            } else if (itemsData[newY][newX] == ' ') {
                // Create a new state when the player moves to an empty space
                char[][] newItemsData = copyMap(itemsData);

                // Move the player
                newItemsData[playerY][playerX] = ' ';
                newItemsData[newY][newX] = '@';

                // Copy of boxCoordinates
                ArrayList<Coordinate> newBoxCoordinates = new ArrayList<>();
                for (Coordinate coord : this.boxCoordinates) {
                    newBoxCoordinates.add(new Coordinate(coord.x, coord.y));
                }

                // Create a new state with the same boxCoordinates
                State newState = new State(mapData, newItemsData, new Coordinate(newX, newY), width, height, goalCoordinates);
                newState.setBoxCoordinates(newBoxCoordinates);

                // Append the direction to the path
                newState.setPath(new StringBuilder(this.getPath()).append(DIRECTION_CHARS[i]));
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
