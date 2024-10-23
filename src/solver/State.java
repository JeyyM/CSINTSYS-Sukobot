package solver;

import java.util.ArrayList;

public class State {
    // One used instance of the map data
    private char[][] mapData = GlobalMap.getMap();

    private Heuristic calculator = new Heuristic();

    // Item locations
    private Coordinate playerPosition;
    private ArrayList<Coordinate> boxCoordinates;
    private ArrayList<Coordinate> goalCoordinates;

    private int goals;
    private int width;
    private int height;

    private double heuristicValue = 0.00;

    // keeps track of the paths
    private StringBuilder path;

    // Changes best on the action's cost
    private double moveCost = 0;

    // Directions specifiers
    private int[][] DIRECTIONS = {
            {0, -1}, // up
            {0, 1},  // down
            {-1, 0}, // left
            {1, 0}   // right
    };
    private char[] DIRECTION_CHARS = {'u', 'd', 'l', 'r'};

    // Constructor
    public State(Coordinate playerPosition, int width, int height, ArrayList<Coordinate> goalCoordinates, double prevMoveCost) {
        this.playerPosition = playerPosition;
        this.width = width;
        this.height = height;
        this.path = new StringBuilder();
        this.moveCost = prevMoveCost;
    }

    // Getters and Setters
    public Coordinate getPlayerPosition() {
        return playerPosition;
    }

    public String getPath() {
        return path.toString();
    }
    public void setPath(StringBuilder newPath) {
        this.path = newPath;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }
    public void setHeuristicValue(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public double getMoveCost() {
        return moveCost;
    }
    
    // For initialization of first state's items
    public void setBoxCoordinates(ArrayList<Coordinate> initialBoxCoordinates) {
        this.boxCoordinates = new ArrayList<>();
        for (Coordinate coord : initialBoxCoordinates) {
            this.boxCoordinates.add(new Coordinate(coord.x, coord.y));
        }
    }

    public Coordinate getBoxCoordinate(int x, int y) {
        for (Coordinate box : boxCoordinates) {
            if (box.x == x && box.y == y) {
                return box;
            }
        }
        return null;
    }

    public ArrayList<Coordinate> getBoxCoordinates() {
        return boxCoordinates;
    }

    public void setGoalCoordinates(ArrayList<Coordinate> initialGoalCoordinates) {
        this.goalCoordinates = new ArrayList<>();
        for (Coordinate coord : initialGoalCoordinates) {
            this.goalCoordinates.add(new Coordinate(coord.x, coord.y));
        }
    }

    // Count number of boxes on goal positions
    public int countGoals(ArrayList<Coordinate> goalCoordinates) {
        int goalSpots = 0;

        for (Coordinate goal : goalCoordinates) {
            for (Coordinate box : boxCoordinates) {
                if (goal.x == box.x && goal.y == box.y) {
                    goalSpots++;
                    break;
                }
            }
        }

        return goalSpots;
    }

    public int getGoals(){
        return this.goals;
    }

    public void setGoals(int newGoals){
        this.goals = newGoals;
    }

    // For debugging
    public void printState() {
        System.out.printf("Current path: %s\n", getPath());
        System.out.printf("Player position: (%d, %d)\n", playerPosition.x, playerPosition.y);
        System.out.printf("Goal count: %d\n", goals);
        System.out.println("Box Positions: " + this.boxCoordinates);
        System.out.println("Goal Positions: " + this.goalCoordinates);
        System.out.printf("Heuristic value: %.2f\n", heuristicValue);

        // Print map and item data side by side for better visualization
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.printf("[%c] ", mapData[i][j]);
            }

            System.out.print("    ");

            for (int j = 0; j < width; j++) {
                if (playerPosition.x == j && playerPosition.y == i) {
                    System.out.print("[@] ");
                } else if (getBoxCoordinate(j, i) != null) {
                    System.out.print("[$] ");
                } else {
                    System.out.print("[ ] ");
                }
            }

            System.out.println();
        }
    }

    // Returns an ArrayList of states after validating the 4 directions
    public ArrayList<State> createStates(ArrayList<Coordinate> goalCoordinates, double currCost) {
        ArrayList<State> validStates = new ArrayList<>();
        // Current player's position
        int playerX = playerPosition.x;
        int playerY = playerPosition.y;

        // a for loop for each direction
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int currMoveCost = 0;
            // 3 for a movement with no action
            // 2 for moving a box
            // 0 for putting a box onto a goal

            int[] direction = DIRECTIONS[i];

            // Moves my 1 based on current axis
            int newX = playerX + direction[0];
            int newY = playerY + direction[1];

            // Checks if the destination is a box
            Coordinate boxAtNewPosition = getBoxCoordinate(newX, newY);
            Coordinate newPlayerPosition = new Coordinate(newX, newY);

            // Avoid out of bounds
            if (newX < 0 || newX >= width || newY < 0 || newY >= height) continue;

            // Skip wall states
            if (mapData[newY][newX] == '#') continue;

            // If a box is to be moved
            if (boxAtNewPosition != null) {
                // new box coordinates
                int boxNewX = newX + direction[0];
                int boxNewY = newY + direction[1];

                // Avoid box going out of bounds
                if (boxNewX < 0 || boxNewX >= width || boxNewY < 0 || boxNewY >= height) continue;

                // Check if the new box position is valid
                if (mapData[boxNewY][boxNewX] == '#' || getBoxCoordinate(boxNewX, boxNewY) != null) continue;

                // Deadlock check
                if (deadlockCheck(boxNewX, boxNewY, goalCoordinates, mapData)) continue;

                // Goal check
                if (mapData[boxNewY][boxNewX] == '.')
                    currMoveCost = 0;
                else
                    currMoveCost = 2;

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

                // Create a new state with the updated boxCoordinates after verifying that they are valid
                State newState = new State(new Coordinate(newX, newY), width, height, goalCoordinates, currMoveCost + currCost);
                newState.setBoxCoordinates(newBoxCoordinates);
                newState.setGoalCoordinates(this.goalCoordinates);
                int goalCount = newState.countGoals(goalCoordinates);
                newState.setGoals(goalCount);

                // calculate state's heuristic
                double heuristicValue = calculator.calcManDist(width, height, goalCoordinates, newBoxCoordinates, goalCount, newState.getPath(), newPlayerPosition);
                heuristicValue -= goals;
                newState.setHeuristicValue(heuristicValue);

                // Append the direction to the path
                newState.setPath(new StringBuilder(this.getPath()).append(DIRECTION_CHARS[i]));
                validStates.add(newState);

            } else {
                currMoveCost = 3;
                ArrayList<Coordinate> newBoxCoordinates = new ArrayList<>();
                for (Coordinate coord : this.boxCoordinates) {
                    newBoxCoordinates.add(new Coordinate(coord.x, coord.y));
                }

                // Create a new state with the same boxCoordinates
                State newState = new State(new Coordinate(newX, newY), width, height, goalCoordinates, currMoveCost + currCost);
                newState.setBoxCoordinates(newBoxCoordinates);
                newState.setGoalCoordinates(this.goalCoordinates);
                int goalCount = newState.countGoals(goalCoordinates);
                newState.setGoals(goalCount);

                double heuristicValue = calculator.calcManDist(width, height, goalCoordinates, newBoxCoordinates, goalCount, newState.getPath(), newPlayerPosition);
                heuristicValue -= goals;
                
                newState.setHeuristicValue(heuristicValue);

                // Append the direction to the path
                newState.setPath(new StringBuilder(this.getPath()).append(DIRECTION_CHARS[i]));
                validStates.add(newState);
            }
        }

        return validStates;
    }

    // For checking a corner box
    public boolean deadlockCheck(int boxX, int boxY, ArrayList<Coordinate> goalCoordinates, char[][] mapData) {
        // If the goal is in the corner, it is allowed
        for (Coordinate goal : goalCoordinates) {
            if (goal.x == boxX && goal.y == boxY) {
                return false;
            }
        }

        // Checks the directions for a wall
        boolean[] wallPresence = new boolean[DIRECTIONS.length];

        for (int i = 0; i < DIRECTIONS.length; i++) {
            int[] direction = DIRECTIONS[i];
            int newX = boxX + direction[0];
            int newY = boxY + direction[1];

            if (mapData[newY][newX] == '#') {
                wallPresence[i] = true;
            }
        }

        // If they form a corner, then it is invalid
        boolean upLeftCorner = wallPresence[0] && wallPresence[2];
        boolean upRightCorner = wallPresence[0] && wallPresence[3];
        boolean downLeftCorner = wallPresence[1] && wallPresence[2];
        boolean downRightCorner = wallPresence[1] && wallPresence[3];

        if (upLeftCorner || upRightCorner || downLeftCorner || downRightCorner) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        String player = playerPosition.toString();
        String box = "| ";

        for(Coordinate b : boxCoordinates) {
            box = box + b.toString() + " ";
        }

        player = player + box;
        double d = getHeuristicValue();
        double m = getMoveCost();
        player = player + " M:" + m;
        player = player + " H:" + d;

        return player;
    }

}