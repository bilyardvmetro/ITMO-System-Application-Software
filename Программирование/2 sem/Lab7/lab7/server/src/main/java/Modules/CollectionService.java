
package Modules;

import CollectionObject.Vehicle;
import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Exceptions.DBProviderException;
import Exceptions.NonExistingElementException;

import java.nio.file.DirectoryNotEmptyException;
import java.util.Comparator;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class CollectionService {
    protected static Long elementsCount = 0L;
    private Date initializationDate;
    protected static Stack<Vehicle> collection;
    private CompareVehicles comparator;
    private ReentrantLock locker;


    public CollectionService() {
        collection = new Stack<>();
        this.initializationDate = new Date();
        this.comparator = new CompareVehicles();
        this.locker = new ReentrantLock();
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

    public Stack<Vehicle> add(VehicleModel element) throws DBProviderException {

        locker.lock();
        if (DBProvider.addVehicle(element)){
            elementsCount+=1;
            Vehicle newElement = new Vehicle(
                    elementsCount,
                    element.getName(),
                    element.getCoordinates(),
                    new Date(),
                    element.getEnginePower(),
                    element.getCapacity(),
                    element.getDistanceTravelled(),
                    element.getType(),
                    element.getUser().getUsername()
            );
            collection.add(newElement);
            locker.unlock();
            return sortByCoords(collection);
        }
        locker.unlock();
        throw new DBProviderException("произошла ошибка при добавлении элемента");
    }

    public String info(){
        return "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + initializationDate + "\n"
                + "Количество элементов: " + collection.size();
    }

    public Stack<Vehicle> show(){
        return sortByCoords(collection);
    }

    public  Stack<Vehicle> update(long current_id, VehicleModel element) throws NonExistingElementException, DBProviderException {
        locker.lock();
        if (collection.stream().anyMatch(vehicle -> vehicle.getId() == current_id)){
            if (DBProvider.updateVehicle(current_id, element)) {

                for (Vehicle vehicle : collection) {
                    if (current_id == vehicle.getId()) {
                        collection.remove(vehicle);

                        Vehicle newElement = new Vehicle(
                                current_id,
                                element.getName(),
                                element.getCoordinates(),
                                new Date(),
                                element.getEnginePower(),
                                element.getCapacity(),
                                element.getDistanceTravelled(),
                                element.getType(),
                                element.getUser().getUsername()
                        );

                        collection.add(newElement);
                        break;
                    }
                }
                locker.unlock();
                return sortByCoords(collection);
            }
            locker.unlock();
            throw new DBProviderException("Произошла ошибка во время изменения элемента");
        }
        locker.unlock();
        throw new NonExistingElementException("Элемента с таким id не существует");
    }

    public  Stack<Vehicle> removeById(long id) throws NonExistingElementException, DBProviderException {
        locker.lock();
        if (DBProvider.removeVehicleById(id)){
            if (!collection.removeIf(vehicle -> vehicle.getId() == id)){
                throw new NonExistingElementException("Элемента с таким id не существует");
            }
            locker.unlock();
            return sortByCoords(collection);
        }
        locker.unlock();
        throw new DBProviderException("Произошла ошибка при удалении элемента. Возможно элемента с таким id не существует");

    }

    public  Stack<Vehicle> clear() throws DBProviderException {
        locker.lock();
        if (DBProvider.clearVehicles()){
            collection.clear();
            locker.unlock();
            return sortByCoords(collection);
        }
        locker.unlock();
        throw new DBProviderException("произошла ошибка при добавлении элемента");
    }

    public  Stack<Vehicle> removeGreater(long startId) throws NonExistingElementException, DBProviderException {
        locker.lock();
        long endId = elementsCount;
        if (startId > endId){
            locker.unlock();
            throw new NonExistingElementException("Элемента с таким id не существует");
        }

        if (DBProvider.removeVehiclesGreaterThanId(startId)){
            collection.removeIf(vehicle -> vehicle.getId() >= startId);
            locker.unlock();
            return sortByCoords(collection);
        }
        locker.unlock();
        throw new DBProviderException("Произошла ошибка при удалении элементов");
    }

    public Stack<Vehicle> reorder(){
        collection.sort(comparator.reversed());

        return collection;
    }

    public  Stack<Vehicle> removeAllByType(VehicleType type) throws NonExistingElementException, DBProviderException {
        locker.lock();
        var vehiclesToRemove = collection.stream().filter(vehicle -> vehicle.getType().equals(type)).toList();
        if (vehiclesToRemove.isEmpty()){
            locker.unlock();
            throw new NonExistingElementException("Элементов с таким типом ТС не существует");
        }

        if (DBProvider.removeVehiclesByType(type)){
            collection.removeAll(vehiclesToRemove);
            locker.unlock();
            return sortByCoords(collection);
        }
        locker.unlock();
        throw new DBProviderException("Произошла ошибка при удалении элементов");

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

    private synchronized Stack<Vehicle> sortByCoords(Stack<Vehicle> collection){
        return collection.stream().sorted(comparator).collect(Collectors.toCollection(Stack::new));
    }
}
