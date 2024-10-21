package solver;

import java.util.ArrayList;
import java.lang.Math;

public class Heuristic {

    //Calculate sum of Manhattan Distances between each crate to its NEAREST goal spot
    public static double calcManDist(char[][] mapData, char[][] itemsData, int width, int height, ArrayList<Coordinate> goalCoordinates, int goals) {
        ArrayList<Coordinate> crateCoordinates = new ArrayList<Coordinate>();
        ArrayList<Coordinate> vacantGoalCoordinates = new ArrayList<Coordinate>();
        double heuristicValue = 0;

        //Already in a goal state
        if(goals == goalCoordinates.size())
            return 0;
        else {
            //get coordinates of each crate that is NOT on goal spot to save time
            for(int i = 0; i < height; i++)
                for(int j = 0; j < width; j++) {
                    //Only add goal coordinates that's not been covered by crate
                    if(itemsData[i][j] == '$' && mapData[i][j] != '.')
                        crateCoordinates.add(new Coordinate(i, j));
                    if(itemsData[i][j] != '$' && mapData[i][j] == '.')
                        vacantGoalCoordinates.add(new Coordinate(i,j));
                }

            //iterate for each coordinate of crate to each coordinate of goal spot
            int minManDist = 9999;
            int manDist = 0;
            for(int i = 0; i < crateCoordinates.size(); i++) {
                minManDist = 9999;
                manDist = 0;
                for(int j = 0; j < vacantGoalCoordinates.size(); j++) {
                    manDist = Math.abs(crateCoordinates.get(i).x - vacantGoalCoordinates.get(j).x) + Math.abs(crateCoordinates.get(i).y - vacantGoalCoordinates.get(j).y);
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
        int i, j;
        double temp;
        boolean swapped;

        for(i = 0; i < stateList.size() - 1; i++) {
            swapped = false;
            for(j = 0; j < stateList.size() - i - 1; j++) {
                if(stateList.get(j).getHeuristicValue() > stateList.get(j + 1).getHeuristicValue()) {
                    temp = stateList.get(j).getHeuristicValue();
                    stateList.get(j).setHeuristicValue(stateList.get(j + 1).getHeuristicValue());
                    stateList.get(j + 1).setHeuristicValue(temp);
                    swapped = true;
                }
            }

            if(swapped == false)
                break;
        }
    }



}
