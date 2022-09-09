package model;

public class Level {
    private static int currentLevel = 1;
    private static boolean start;

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public static void setCurrentLevel(int currentLevel) {
        Level.currentLevel = currentLevel;
    }

    public static void setStart(boolean start) {
        Level.start = start;
    }

    public static boolean getStart(int level) {
        if(Elevator.getPeopleNeedUp(level) > 0 && Elevator.getState() == 1 ||
        Level.getCurrentLevel() == 1) {
            setStart(true);
        } else if(Elevator.getPeopleNeedDown(level) > 0 && Elevator.getState() == 2 ||
            Level.getCurrentLevel() == Elevator.getFloorCount()) {
                setStart(false);
        }
        return start;
    }

}
