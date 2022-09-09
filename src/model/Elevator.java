package model;

import java.util.Arrays;
import java.util.Collections;

public class Elevator {
    private static int state = 0;
    private static final int MAX_CAPACITY = 5;
    private static int capacity = 0;
    private static final int[] comingPeople = new int[MAX_CAPACITY];
    private static int[][] floorsAndPeople;
    private static int floorCount;
    private static int peopleCount;
    private static int countPeopleUp = 0;
    private static int countPeopleDown = 0;
    private static int freeSpace;
    private static int[] storage;

    public static int getState() {
        return state;
    }

    public static void setState(int state) {
        Elevator.state = state;
    }

    public static int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capacity) {
        Elevator.capacity = capacity;
    }

    public static int[] getComingPeople() {
        return comingPeople;
    }

    public static void setComingPeople(int[] people) {
        for(int i = 0; i < MAX_CAPACITY; i++) {
            if(comingPeople[i] == 0) {
                comingPeople[i] = people[i];
            }
        }
        Arrays.sort(comingPeople);
    }

    public static void setNeededFloorForPerson() {
        for (int i = floorsAndPeople.length - 1, n = floorsAndPeople.length; i >= 0; i--, n--) {
            for (int j = 0; j < floorsAndPeople[i].length; j++) {
                floorsAndPeople[i][j] = Person.getNeededFloor();
                if (floorsAndPeople[i][j] == n) {
                    if (n <= floorCount / 2) {
                        floorsAndPeople[i][j] = getRandomNumber(n + 1, floorCount);
                    } else {
                        floorsAndPeople[i][j] = getRandomNumber(1, n-1);
                    }
                }
            }
        }
    }

    public static void console() {
        for (int i = floorsAndPeople.length - 1, n = floorsAndPeople.length; i >= 0; i--, n--) {
            System.out.print(" Floor " + n + ": ");
            for (int j = 0; j < floorsAndPeople[i].length; j++) {
                System.out.printf("%3d", floorsAndPeople[i][j]);
            }

            if (Elevator.getState() == 1 && Level.getCurrentLevel() == n) {
                System.out.print(" Need up: ");
                for (int b = 0; b < Elevator.getMaxCapacity(); b++) {
                    System.out.printf("%3d", storage[b]);
                }
            } else if (Elevator.getState() == 2 && Level.getCurrentLevel() == n) {
                System.out.print(" Need down: ");
                for (int b = 0; b < Elevator.getMaxCapacity(); b++) {
                    System.out.printf("%3d", storage[b]);
                }
            }
            System.out.println();
        }
    }

    public static int getPeopleNeedUp(int floor) {
        int needUp = 0;
        for (int j = 0; j < floorsAndPeople[floor - 1].length; j++) {
            if (floorsAndPeople[floor - 1][j] >= floor && floorsAndPeople[floor - 1][j] > 0) {
                needUp++;
            }
        }
        return needUp;
    }

    public static int getPeopleNeedDown(int floor) {
        int needDown = 0;
        for (int j = 0; j < floorsAndPeople[floor - 1].length; j++) {
            if (floorsAndPeople[floor - 1][j] < floor && floorsAndPeople[floor - 1][j] > 0) {
                needDown++;
            }
        }
        return needDown;
    }

    public static void boarding() {
        if (Level.getCurrentLevel() == 1) {
            Elevator.setState(1);
        } else if (Level.getCurrentLevel() == floorCount) {
            Elevator.setState(2);
        }

        if (peopleCount == 0) {
            Elevator.setState(0);
            System.out.println("Elevator is waiting!");
            System.exit(1);
        } else if (peopleCount > 0) {
            countPeopleUp = getPeopleNeedUp(Level.getCurrentLevel());
            countPeopleDown = getPeopleNeedDown(Level.getCurrentLevel());
            enter();
        }
    }


    public static void enter() {
        int[] elevator = new int[Elevator.getMaxCapacity()];
        int[] tempUp = new int[peopleCount]; // peopleUp
        int[] tempDown = new int[peopleCount]; // peopleDown

        calculateFreePlace();

        if (countPeopleUp > 0 && Elevator.getState() == 1 || Elevator.getState() == 0 && countPeopleUp > 0) {
            for (int j = 0; j < peopleCount; j++) {
                if (floorsAndPeople[Level.getCurrentLevel() - 1][j] >= Level.getCurrentLevel() && floorsAndPeople[Level.getCurrentLevel() - 1][j] > 0) {
                    tempUp[j] = floorsAndPeople[Level.getCurrentLevel() - 1][j];
                }
            }
            reverseUpOrDown(elevator, tempUp, countPeopleUp);

        } else if (countPeopleDown > 0 && Elevator.getState() == 2 || Elevator.getState() == 0 && countPeopleDown > 0) {
            for (int j = 0; j < peopleCount; j++) {
                if (floorsAndPeople[Level.getCurrentLevel() - 1][j] < Level.getCurrentLevel() && floorsAndPeople[Level.getCurrentLevel() - 1][j] > 0) {
                    tempDown[j] = floorsAndPeople[Level.getCurrentLevel() - 1][j];
                }
            }
            reverseUpOrDown(elevator, tempDown, countPeopleDown);
        }

        for (int j = 0; j < Elevator.getMaxCapacity(); j++) {
            for (int i = 0; i < floorsAndPeople[Level.getCurrentLevel() - 1].length; i++) {
                if (floorsAndPeople[Level.getCurrentLevel() - 1][i] != 0 && floorsAndPeople[Level.getCurrentLevel() - 1][i] == elevator[j]) {
                    floorsAndPeople[Level.getCurrentLevel() - 1][i] = 0;
                    break;
                }
            }
        }
        Elevator.setComingPeople(elevator);
        storage = Elevator.getComingPeople();
    }

    private static void reverseUpOrDown(int[] elevator, int[] tempUp, int peopleNeedUp) {
        tempUp = reverseSort(tempUp);

        if (peopleNeedUp >= freeSpace) {
            for (int j = 0; j < freeSpace; j++) {
                if (tempUp[j] > 0) {
                    elevator[j] = tempUp[j];
                }
            }
            Elevator.setCapacity(Elevator.getMaxCapacity());
        } else {
            for (int j = 0; j < peopleNeedUp; j++) {
                if (tempUp[j] > 0) {
                    elevator[j] = tempUp[j];
                }
            }
            Elevator.setCapacity(Elevator.getCapacity() + peopleNeedUp);
        }
    }

    public static int findMinNeeded() {
        int minNeededFloor = floorCount +1;
        for (int j = 0; j < Elevator.getComingPeople().length; j++) {
            if (Elevator.getComingPeople()[j] != 0) {
                minNeededFloor = Math.min(minNeededFloor, Elevator.getComingPeople()[j]);
            }
        }
        return minNeededFloor;
    }

    public static int findMaxNeededFloor() {
        int maxNeededFloor = -1;
        for (int j = 0; j < Elevator.getComingPeople().length; j++) {
            if (Elevator.getComingPeople()[j] != 0) {
                maxNeededFloor = Math.max(maxNeededFloor, Elevator.getComingPeople()[j]);
            }
        }
        return maxNeededFloor;
    }

    public static void calculateFreePlace() {
        freeSpace = Elevator.getMaxCapacity() - Elevator.getCapacity();
    }

    public static void moving() {
        int max = findMaxNeededFloor();
        int min = findMinNeeded();
        int floorUp = Level.getCurrentLevel() + 1;
        int floorDown = Level.getCurrentLevel() - 1;
        calculateFreePlace();

        if (Elevator.getState() == 1) {
            if (freeSpace == 0) {
                Level.setCurrentLevel(min);
            } else {
                int nextFloor = 0;
                for (int i = floorUp, k = floorCount; i <= floorCount; i++, k--) {
                    if (Level.getStart(i)) {
                        nextFloor = i;
                        break;
                    } else {
                        if (min != floorCount +1) {
                            nextFloor = min;
                            break;
                        } else {
                            if (!Level.getStart(k)) {
                                nextFloor = k;
                                break;
                            } else {
                                Elevator.setState(2);
                            }
                        }
                    }
                }
                Level.setCurrentLevel(nextFloor);
            }
        } else if (Elevator.getState() == 2) {
            if (freeSpace == 0) {
                Level.setCurrentLevel(max);
            } else {
                int nextFloor = 0;
                for (int k = floorDown, i = 1; k >= 1; k--, i++) {
                    if (!Level.getStart(k)) {
                        nextFloor = k;
                        break;
                    } else {
                        if (max != -1) {
                            nextFloor = max;
                            break;
                        } else {
                            if (Level.getStart(i)) {
                                nextFloor = i;
                                break;
                            } else {
                                Elevator.setState(1);
                                nextFloor = 1;
                            }
                        }
                    }
                }
                Level.setCurrentLevel(nextFloor);
            }
        }
    }

    public static void exit() {
        int count = 0;
        for (int j = 0; j < Elevator.getMaxCapacity(); j++) {
            if (Elevator.getComingPeople()[j] == Level.getCurrentLevel()) {
                count++;
                Elevator.getComingPeople()[j] = 0;
            }
        }
        Elevator.setCapacity(Elevator.getCapacity() - count);
    }

    public static int getFeeSpace(int[][] numbers) {
        int count = 0;
        for (int[] number : numbers) {
            for (int i : number) {
                if (i == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int getFloorCount() {
        return floorCount;
    }

    public static int[] reverseSort(int[] a) {
        return Arrays.stream(a).boxed()
                .sorted(Collections.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1) + min);
    }

    public static void runLift() {
        System.out.println("Hi! Enter floor count:");

        floorCount = getRandomNumber(5, 20);
        peopleCount = getRandomNumber(0, 10);
        floorsAndPeople = new int[floorCount][peopleCount];
        setNeededFloorForPerson();

        System.out.println("People on the each level: " + peopleCount + ".");
        console();
        int i = 1;

        do {
            System.out.println("*** Step " + i + " ***");
            boarding();
            moving();
            exit();
            console();
            i++;
        } while(getFeeSpace(floorsAndPeople) != peopleCount * floorCount);
        System.out.println("There are no any people!");
        System.exit(1);
    }

}
