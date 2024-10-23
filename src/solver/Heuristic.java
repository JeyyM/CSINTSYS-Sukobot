package solver;

import java.util.ArrayList;
import java.lang.Math;

import java.util.Collections;
import java.util.Comparator;

public class Heuristic {
    public static double calcManDist(char[][] mapData, int width, int height, ArrayList<Coordinate> goalCoordinates, ArrayList<Coordinate> crateCoordinates, int goals, String path, Coordinate playerPosition) {
        double heuristicValue = 0;

        //Already in a goal state
        if(goals == goalCoordinates.size())
            return 0;
        else {
            //iterate for each coordinate of crate to each coordinate of goal spot
            int minManDist = 9999;
            int manDist = 0;
            for(int i = 0; i < crateCoordinates.size(); i++) {
                minManDist = 9999;
                manDist = 0;

                //Filter only for crates not on the goal spot
                if (mapData[crateCoordinates.get(i).y][crateCoordinates.get(i).x] == '.')
                    continue;

                for(int j = 0; j < goalCoordinates.size(); j++) {
                    //Filter only for goal spots that are vacant
                    if(itemsData[goalCoordinates.get(j).y][goalCoordinates.get(j).x] == '$')
                        continue;

                    manDist = Math.abs(crateCoordinates.get(i).x - goalCoordinates.get(j).x) + Math.abs(crateCoordinates.get(i).y - goalCoordinates.get(j).y);
                    if(minManDist > manDist)
                        minManDist = manDist;
                    if(minManDist == 1)
                        break;
                }
                heuristicValue += minManDist;
            }
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

//    public static Coordinate getBoxCoordinate(int x, int y, ArrayList<Coordinate> boxCoordinates) {
//        for (Coordinate box : boxCoordinates) {
//            if (box.x == x && box.y == y) {
//                return box;
//            }
//        }
//        return null;
//    }

//    public static double calcManDist(char[][] mapData, int width, int height, ArrayList<Coordinate> goalCoordinates, ArrayList<Coordinate> crateCoordinates, int goals, String path, Coordinate playerPosition) {
//        double heuristicValue = 0;
//
//        //Already in a goal state
//        if(goals == goalCoordinates.size())
//            return 0;
//        else {
//            //iterate for each coordinate of crate to each coordinate of goal spot
//            int minManDist = 9999;
//            int manDist = 0;
//            for(int i = 0; i < crateCoordinates.size(); i++) {
//                minManDist = 9999;
//                manDist = 0;
//
//                //Filter only for crates not on the goal spot
//
//                if (mapData[crateCoordinates.get(i).y][crateCoordinates.get(i).x] == '.')
//                    continue;
//
//                for(int j = 0; j < goalCoordinates.size(); j++) {
//                    //Filter only for goal spots that are vacant
//                    if (getBoxCoordinate(goalCoordinates.get(j).x, goalCoordinates.get(j).y, crateCoordinates) != null)
//                        continue;
//
//                    manDist = Math.abs(crateCoordinates.get(i).x - goalCoordinates.get(j).x) + Math.abs(crateCoordinates.get(i).y - goalCoordinates.get(j).y);
//                    if(minManDist > manDist)
//                        minManDist = manDist;
//                    if(minManDist == 1)
//                        break;
//                }
//                heuristicValue += minManDist;
//            }
//        }
//
//        return heuristicValue;
//    }
}