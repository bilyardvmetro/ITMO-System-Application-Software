package Network;

import CollectionObject.Vehicle;

import java.io.Serializable;
import java.util.Stack;

public class Response implements Serializable {
    private String message;
    private Stack<Vehicle> collection;

    public Response(String message, Stack<Vehicle> collection) {
        this.message = message;
        this.collection = collection;
    }

    public String getMessage() {
        return message;
    }

    public Stack<Vehicle> getCollection() {
        return collection;
    }
}
