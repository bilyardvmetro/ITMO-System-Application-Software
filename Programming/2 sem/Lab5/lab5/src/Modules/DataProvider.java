package Modules;

import CollectionObject.Vehicle;

import java.util.Stack;

public interface DataProvider {

    void save(Stack<Vehicle> collection);

    void load(String arguments);
}
