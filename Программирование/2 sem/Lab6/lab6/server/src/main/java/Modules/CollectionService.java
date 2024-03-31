package Modules;

import CollectionObject.Vehicle;
import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Exceptions.*;

import java.util.*;
import java.util.stream.Collectors;


public class CollectionService {
    protected static Long elementsCount = 0L;
    private Date initializationDate;
    protected static Stack<Vehicle> collection;
    private CompareVehicles comparator;


    public CollectionService() {
        collection = new Stack<>();
        this.initializationDate = new Date();
        comparator = new CompareVehicles();
    }

    private class CompareVehicles implements Comparator<Vehicle>{

        @Override
        public int compare(Vehicle o1, Vehicle o2) {
            return (int) (o1.getCoordinates().getX() - o2.getCoordinates().getX());
        }

        @Override
        public Comparator<Vehicle> reversed() {
            return Comparator.super.reversed();
        }
    }

    public Stack<Vehicle> add(VehicleModel element){

        elementsCount+=1;
        Vehicle newElement = new Vehicle(
                elementsCount,
                element.getName(),
                element.getCoordinates(),
                new Date(),
                element.getEnginePower(),
                element.getCapacity(),
                element.getDistanceTravelled(),
                element.getType()
        );

        collection.add(newElement);
        return sortByCoords(collection);
    }

    public String info(){
        return "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + initializationDate + "\n"
                + "Количество элементов: " + collection.size();
    }

    public Stack<Vehicle> show(){
        return sortByCoords(collection);
    }

    public Stack<Vehicle> update(long current_id, VehicleModel element) throws NonExistingElementException {
        if (!collection.contains(collection.get((int) current_id))){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }
        // TODO: 31.03.2024 проверить работает ли лямбда корректно
        collection.forEach(vehicle -> {
            if (current_id == vehicle.getId()){
                collection.remove(vehicle);

                Vehicle newElement = new Vehicle(
                        current_id,
                        element.getName(),
                        element.getCoordinates(),
                        new Date(),
                        element.getEnginePower(),
                        element.getCapacity(),
                        element.getDistanceTravelled(),
                        element.getType()
                );

                collection.add(newElement);
            }
        });

//        for (Vehicle vehicle:collection) {
//            if (current_id == vehicle.getId()){
//                collection.remove(vehicle);
//
//                Vehicle newElement = new Vehicle(
//                        current_id,
//                        element.getName(),
//                        element.getCoordinates(),
//                        new Date(),
//                        element.getEnginePower(),
//                        element.getCapacity(),
//                        element.getDistanceTravelled(),
//                        element.getType()
//                );
//
//                collection.add(newElement);
//                break;
//            }
//        }

        return sortByCoords(collection);
    }

    public Stack<Vehicle> removeById(long id) throws NonExistingElementException {
        if (!collection.removeIf(vehicle -> vehicle.getId() == id)){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }
        return sortByCoords(collection);
    }

    public Stack<Vehicle> clear(){
        collection.clear();
        return sortByCoords(collection);
    }

    public Stack<Vehicle> removeGreater(long startId) throws NonExistingElementException {
        long endId = collection.size();
        if (startId > endId){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }

        collection.removeIf(vehicle -> vehicle.getId() >= startId);
        return sortByCoords(collection);
    }

    public Stack<Vehicle> reorder(){
        collection.sort(comparator.reversed());

        return collection;
    }

    public Stack<Vehicle> removeAllByType(VehicleType type) throws NonExistingElementException {
        var vehiclesToRemove = collection.stream().filter(vehicle -> vehicle.getType().equals(type)).toList();

        if (vehiclesToRemove.isEmpty()){
            throw new NonExistingElementException("Элементов с таким типом ТС не существует");
        }

        collection.removeAll(vehiclesToRemove);
        return sortByCoords(collection);
    }

    public int countGreaterThanEnginePower(double enginePower){
        return (int) collection.stream().filter(vehicle -> vehicle.getEnginePower() > enginePower).count();
    }

    public Stack<Vehicle> filterStartsWithName(String name) throws NonExistingElementException {
        var filteredCollection = collection.stream().filter(vehicle -> vehicle.getName().startsWith(name)).collect(Collectors.toCollection(Stack::new));

        if (filteredCollection.isEmpty()){
            throw new NonExistingElementException("Элементов с таким именем не существует");
        }
        return sortByCoords(filteredCollection);
    }

    private Stack<Vehicle> sortByCoords(Stack<Vehicle> collection){
        return collection.stream().sorted(comparator).collect(Collectors.toCollection(Stack::new));
    }
}
