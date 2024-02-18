package Modules;

import CollectionObject.Coordinates;
import CollectionObject.Vehicle;
import CollectionObject.VehicleType;
import java.util.Comparator;

import java.util.*;

import static CollectionObject.VehicleType.*;

public class CollectionService {
    private Long elementsCount = 0L; // TODO: придумать как генерировать id автоматом по-другому
    private Date initializationDate;
    protected Stack<Vehicle> collection;
    private boolean isReversed = false;

    public CollectionService() {
        this.collection = new Stack<>();
        this.initializationDate = new Date();
    }

    private class CompareVehicles implements Comparator<Vehicle>{

        @Override
        public int compare(Vehicle o1, Vehicle o2) {
            return o1.getName().length() - o2.getName().length();
        }

        @Override
        public Comparator<Vehicle> reversed() {
            return Comparator.super.reversed();
        }
    }

    public void addElement(){
        // TODO: 18.02.2024 сделать валидации полей и улучшить механизм id
        Scanner InputScanner = new Scanner(System.in);
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

        elementsCount+=1;

        Vehicle newElement = new Vehicle(
                elementsCount,
                name,
                coordinates,
                creationDate,
                enginePower,
                capacity,
                distanceTravelled,
                vehicleType
        );
        collection.addElement(newElement);
        System.out.println("Элемент успешно добавлен");
    }

    public void info(){
        System.out.println("Тип коллекции: " + collection.getClass());
        System.out.println("Дата создания: " + initializationDate);
        System.out.println("Количество элементов: " + collection.size());
    }

    public void show(){
        if (collection.isEmpty()){
            System.out.println("В коллекции пока нету ни одного элемента");
        } else{
            for (Vehicle vehicle: collection) {
                System.out.println(vehicle + "\n");
            }
        }
    }

    public void update(String new_name, long current_id){
        // TODO: 18.02.2024 добавить изменение всех полей, а не только name 
        for (Vehicle vehicle:collection) {
            if (current_id == vehicle.getId()){
                vehicle.setName(new_name);
                System.out.println("Имя элемента с id " + current_id + " успешно удалён");
            } else{
                System.out.println("Элемента с таким id не существует");
            }
        }
    }

    public void removeById(long id){
        for (Vehicle vehicle:collection) {
            if (id == vehicle.getId()){
                collection.remove(vehicle);
                System.out.println("Элемент с id " + id + " успешно удалён");
            } else{
                System.out.println("Элемента с таким id не существует");
            }
        }
    }

    public void clear(){
        collection.clear();
        System.out.println("Все элементы успешно удалены");
    }

    public void removeGreater(long startId){
        // TODO: 19.02.2024 добавить ремув по всем полям
        long endId = collection.size();

        if (startId > endId){
            System.out.println("Элемента с таким id не существует");
        }

        while (startId <= endId){
            collection.pop();
            System.out.println("Элемент с id " + endId + " успешно удалён");
            endId--;
        }

    }

    public void reorder(){
        CompareVehicles comparator = new CompareVehicles();
        if (!isReversed){
            collection.sort(comparator.reversed());
            isReversed = true;
            System.out.println("///Коллекция отсортирована по убыванию/// \n");
        } else {
            collection.sort(comparator);
            System.out.println("///Коллекция отсортирована по возрастанию/// \n");
        }
        show();
    }

    public void removeAllByType(VehicleType type){
        LinkedList<Vehicle> vehiclesToRemove = new LinkedList<>();
        for (Vehicle vehicle : collection) {
            if (vehicle.getType().equals(type)){
                vehiclesToRemove.add(vehicle);
            }
        }
        collection.removeAll(vehiclesToRemove);
        System.out.println("Транспортные средства с типом " + type + " успешно удалены");
    }

    public void countGreaterThanEnginePower(double enginePower){
        int counter = 0;
        for (Vehicle vehicle : collection) {
            if (vehicle.getEnginePower() > enginePower){
                counter++;
            }
        }
        System.out.println("Количество транспортных средств с мощностью двигателя выше заданной: " + counter);
    }

    public void filterStartsWithName(String name){
        for (Vehicle vehicle : collection) {
            if (vehicle.getName().equals(name)){
                System.out.println(vehicle + "\n");
            }
        }
    }

}
