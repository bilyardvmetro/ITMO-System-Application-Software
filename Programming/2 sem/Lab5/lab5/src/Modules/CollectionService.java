package Modules;

import CollectionObject.*;

import Exceptions.*;

import java.util.*;

import static CollectionObject.VehicleType.*;

public class CollectionService {
    private Long elementsCount = 0L;
    private Date initializationDate;
    protected static Stack<Vehicle> collection;
    private boolean isReversed = false;

    public CollectionService() {
        collection = new Stack<>();
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

    private record VehicleWithoutId (
            String name, Coordinates coordinates, Date creationDate, double enginePower,
            float capacity, float distanceTravelled, VehicleType vehicleType){}

    public void addElement(){
        VehicleWithoutId source = createElement();

        elementsCount+=1;
        Vehicle newElement = new Vehicle(
                elementsCount,
                source.name,
                source.coordinates,
                source.creationDate,
                source.enginePower,
                source.capacity,
                source.distanceTravelled,
                source.vehicleType
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

    public void update(long current_id){
        for (Vehicle vehicle:collection) {
            if (current_id == vehicle.getId()){
                collection.remove(vehicle);

                VehicleWithoutId source = createElement();
                Vehicle newElement = new Vehicle(
                        current_id,
                        source.name,
                        source.coordinates,
                        source.creationDate,
                        source.enginePower,
                        source.capacity,
                        source.distanceTravelled,
                        source.vehicleType
                );

                collection.addElement(newElement);
                System.out.println("Элемент с id " + current_id + " успешно изменён");
                break;

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
                break;
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
            else {
                System.out.println("Элементов с таким именем не существует");
            }
        }
    }

    private String askString(Scanner InputScanner) {
        while(true) {
            try {
                var name = InputScanner.nextLine();
                if (name.isEmpty()){
                    throw new EmptyFieldException("Поле не может быть пустым. Введите его ещё раз: ");
                }
                return name.trim();
            }catch(EmptyFieldException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private float askX(Scanner InputScanner) {
        while(true) {
            try {
                return Float.parseFloat(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private double askY(Scanner InputScanner) {
        while(true) {
            try {
                return Double.parseDouble(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private float askFloat(Scanner InputScanner) {
        while(true) {
            try {
                float num = Float.parseFloat(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }

            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private double askDouble(Scanner InputScanner) {
        while(true) {
            try {
                double num = Double.parseDouble(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private VehicleType askVehicleType(Scanner InputScanner) {
        while (true){
            try {
                String type = InputScanner.nextLine().toUpperCase();
                VehicleType vehicleType;
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
                    default:
                        throw new EmptyFieldException("Такого типа транспортного средства не существует. " +
                                "Заполните тип корректно: ");
                }
                return vehicleType;
            } catch (EmptyFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private VehicleWithoutId createElement(){
        Scanner InputScanner = new Scanner(System.in);

        System.out.println("Введите имя");
        String name = askString(InputScanner);

        System.out.println("Введите координату x:");
        float x = askX(InputScanner);

        System.out.println("Введите координату y:");
        double y = askY(InputScanner);

        Coordinates coordinates = new Coordinates(x, y);

        Date creationDate = new Date();

        System.out.println("Введите мощность двигателя");
        double enginePower = askDouble(InputScanner);

        System.out.println("Введите объём двигателя");
        float capacity = askFloat(InputScanner);

        System.out.println("Введите пробег");
        float distanceTravelled = askFloat(InputScanner);

        System.out.print("""
                Введите один из доступных типов транспортного средства:
                BOAT
                HOVERBOARD
                SPACESHIP
                """);
        VehicleType vehicleType = askVehicleType(InputScanner);

        return new VehicleWithoutId(name, coordinates, creationDate, enginePower, capacity, distanceTravelled, vehicleType);
    }
}
