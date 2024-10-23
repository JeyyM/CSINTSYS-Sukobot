package solver;

public final class GlobalMap {

    private static char[][] mapData;

    public static char[][] getMap(){
        return mapData;
    }

    public static void setMap(char[][] newMap){
        GlobalMap.mapData = newMap;
    }
}