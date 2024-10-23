package solver;

import java.util.ArrayList;
import java.lang.Math;

import java.util.Collections;
import java.util.Comparator;

public class Heuristic {
    //Calculate sum of Manhattan Distances between each crate to its NEAREST goal spot
    public static double calcManDist(char[][] mapData, int width, int height, ArrayList<Coordinate> goalCoordinates, ArrayList<Coordinate> crateCoordinates, int goals, String path, Coordinate playerPosition) {
        double heuristicValue = 0;

        int goalCount = goalCoordinates.size();
        int boxCount = crateCoordinates.size();

        if (goals == goalCount) {
            return 0;
        }

        boolean[] boxBind = new boolean[boxCount];
        int[] goalMinDist = new int[goalCount];

        for (int i = 0; i < goalCount; i++) {
            goalMinDist[i] = Integer.MAX_VALUE;
        }

        for (int i = 0; i < goalCount; i++) {
            Coordinate goal = goalCoordinates.get(i);

            int minDistance = Integer.MAX_VALUE;
            int closestBoxInd = -1;

            for (int j = 0; j < boxCount; j++) {
                if (boxBind[j]) continue;

                Coordinate box = crateCoordinates.get(j);
                int manDist = Math.abs(box.x - goal.x) + Math.abs(box.y - goal.y);

                if (manDist < minDistance) {
                    minDistance = manDist;
                    closestBoxInd = j;
                }
            }

            if (closestBoxInd != -1) {
                boxBind[closestBoxInd] = true;
                goalMinDist[i] = minDistance;
            }
        }

        for (int i = 0; i < goalCount; i++) {
            heuristicValue += goalMinDist[i] * 20;
        }

        heuristicValue += (goals * 0.5);
        heuristicValue -= path.length() * 0.001;

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
}