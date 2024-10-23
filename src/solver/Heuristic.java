package solver;

import java.util.ArrayList;
import java.lang.Math;

import java.util.Collections;
import java.util.Comparator;

public class Heuristic implements Comparator<State> {
    static char[][] mapData = GlobalMap.getMap();

    public static Coordinate getBoxCoordinate(int x, int y, ArrayList<Coordinate> boxCoordinates) {
        for (Coordinate box : boxCoordinates) {
            if (box.x == x && box.y == y) {
                return box;
            }
        }
        return null;
    }

    //Calculate sum of Manhattan Distances between each crate to its NEAREST goal spot
    public static double calcManDist(int width, int height, ArrayList<Coordinate> goalCoordinates, ArrayList<Coordinate> crateCoordinates, int goals, String path, Coordinate playerPosition) {
        double heuristicValue = 0;

        //Already in a goal state
        if(goals == goalCoordinates.size())
            return 0;
        else {
            //iterate for each coordinate of crate to each coordinate of goal spot
            int minManDist = Integer.MAX_VALUE;
            int manDist = 0;
            
            int minCrateDist = Integer.MAX_VALUE;
            int crateDist = 0;
            for(int i = 0; i < crateCoordinates.size(); i++) {
                minManDist = Integer.MAX_VALUE;
                manDist = 0;

                //Filter only for crates not on the goal spot
                if (mapData[crateCoordinates.get(i).y][crateCoordinates.get(i).x] == '.')
                    continue;

                for(int j = 0; j < goalCoordinates.size(); j++) {
                    //Filter only for goal spots that are vacant
//                    if(itemsData[goalCoordinates.get(j).y][goalCoordinates.get(j).x] == '$')
                    if (getBoxCoordinate(goalCoordinates.get(j).x, goalCoordinates.get(j).y, crateCoordinates) != null)
                        continue;

                    manDist = Math.abs(crateCoordinates.get(i).x - goalCoordinates.get(j).x) + Math.abs(crateCoordinates.get(i).y - goalCoordinates.get(j).y);
                    if(minManDist > manDist)
                        minManDist = manDist;
                    if(minManDist == 1)
                        break;
                }
                heuristicValue += minManDist;
                
                crateDist = Math.abs(crateCoordinates.get(i).x - playerPosition.x) + Math.abs(crateCoordinates.get(i).y - playerPosition.y);
                
                if (minCrateDist > crateDist)
                    minCrateDist = crateDist;
            }
            heuristicValue += (0.75 * minCrateDist);
        }
        
        return heuristicValue;
    }

    public static void sortDescending(ArrayList<State> stateList) {
        Collections.sort(stateList, new Comparator<State>() {
            @Override
            public int compare(State s1, State s2) {
                return Double.compare(s2.getHeuristicValue(), s1.getHeuristicValue());
            }
        });
    }

    /**
     * compare function for priority queue
     * currently compares heuristic value only
     * -1 means it gets put in front
     * 1 means it gets put in the back
     * idk what 0 means rn
     */
    public int compare(State s1, State s2) {
        /*
        double finalCost1 = s1.getMoveCost() + s1.getHeuristicValue() * 1.5;
        double finalCost2 = s2.getMoveCost() + s2.getHeuristicValue() * 1.5;
        
        if (finalCost1 < finalCost2) {
            return -1;
        }
        else if (finalCost1 > finalCost2) {
            return 1;
        }
        else if (finalCost1 == finalCost2) {
            if (s1.getHeuristicValue() < s2.getHeuristicValue())
                return -1;
            else
                return 1;
        }
        */
        
        if (s1.getMoveCost() < s2.getMoveCost()) { 
            return -1;
        }
        else if (s1.getMoveCost() > s2.getMoveCost()) {
            return 1;
        }
        else {
            if (s1.getHeuristicValue() < s2.getHeuristicValue()) {
                return -1;
            }
            else {
                return 1;
            }
        }
        
        /*
        if (s1.getMoveCost() < s2.getMoveCost())
            return -1;
        else if (s1.getMoveCost() == s2.getMoveCost() && s1.getHeuristicValue() < s2.getHeuristicValue())
            return -1;
        else if (s1.getMoveCost() > s2.getMoveCost())
            return 1;
        else if (s1.getMoveCost() == s2.getMoveCost() && s1.getHeuristicValue() > s2.getHeuristicValue())
            return 1;
        */
        // return 0;
    }
}
