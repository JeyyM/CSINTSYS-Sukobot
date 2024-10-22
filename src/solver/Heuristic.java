package solver;

import java.util.ArrayList;
import java.lang.Math;

public class Heuristic {
//TEST COMMENT FOR GIT
    //Calculate sum of Manhattan Distances between each crate to its NEAREST goal spot
    public static double calcManDist(char[][] mapData, char[][] itemsData, int width, int height, ArrayList<Coordinate> goalCoordinates, ArrayList<Coordinate> crateCoordinates, int goals) {
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

    //Sort heuristic values of ArrayList of states using bubble sort
    public static void sortNonDecreasing(ArrayList<State> stateList) {
        State temp;
        boolean swapped;

        for(int i = 0; i < stateList.size() - 1; i++) {
            swapped = false;
            for(int j = 0; j < stateList.size() - i - 1; j++) {
                if(stateList.get(j).getHeuristicValue() > stateList.get(j + 1).getHeuristicValue()) {
                    temp = stateList.get(j);
                    stateList.set(j, stateList.get(j + 1));
                    stateList.set(j + 1, temp);
                    swapped = true;
                }
            }

            if(swapped == false)
                break;
        }
    }
}
