package model;

public class Person {
    private static int neededFloor;

    public static int getNeededFloor() {
        Person.neededFloor = Elevator.getRandomNumber(1, Elevator.getFloorCount());
        return neededFloor;
    }

}
