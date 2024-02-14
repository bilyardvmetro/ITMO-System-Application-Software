package Modules;

import CollectionObject.Coordinates;
import CollectionObject.Vehicle;
import CollectionObject.VehicleType;

import java.util.Date;
import java.util.Scanner;
import java.util.Stack;

import static CollectionObject.VehicleType.*;

public class CollectionService {
    private Long elementsCount = 0L; // TODO: придумать как генерировать id автоматом по-другому
    private Scanner InputScanner;
    public Stack<Vehicle> collection;

    public CollectionService() {
        this.InputScanner = new Scanner(System.in);
        this.collection = new Stack<>();
    }

    public void addElement(){
        System.out.println(getLastId());
        System.out.println("Введите имя");
        String name = InputScanner.nextLine();

        System.out.println("Введите координаты через пробел в формате: x y");
        String inputCoordinates = InputScanner.nextLine();
        String[] parsedCoordinates = inputCoordinates.split(" ");
        Coordinates coordinates = new Coordinates(
                Float.parseFloat(parsedCoordinates[0]),
                Double.parseDouble(parsedCoordinates[1])
        );

        Date creationDate = new Date();

        System.out.println("Введите мощность двигателя");
        Double enginePower = Double.parseDouble(InputScanner.nextLine());

        System.out.println("Введите объём двигателя");
        Float capacity = Float.parseFloat(InputScanner.nextLine());

        System.out.println("Введите пробег");
        float distanceTravelled = Float.parseFloat(InputScanner.nextLine());

        System.out.print("""
                Введите один из доступных типов транспортного средства:
                BOAT
                HOVERBOARD
                SPACESHIP
                """);
        String type = InputScanner.nextLine().toUpperCase();
        VehicleType vehicleType = null;
        switch (type){
            case "BOAT":
                vehicleType = BOAT;
                break;
            case "HOVERBOARD":
                vehicleType = HOVERBOARD;
                break;
            case "SPACESHIP":
                vehicleType = SPACESHIP;
                break;
        }

        Vehicle newElement = new Vehicle(
                getLastId(),
                name,
                coordinates,
                creationDate,
                enginePower,
                capacity,
                distanceTravelled,
                vehicleType
        );
        elementsCount++;
        String s = newElement.toString();
        System.out.println(s);
    }

    // todo: доработать эту функцию
    public long getLastId(){
        if (!collection.empty()){
            return collection.indexOf(collection.lastElement());
        }
        return 0;
    }
}
