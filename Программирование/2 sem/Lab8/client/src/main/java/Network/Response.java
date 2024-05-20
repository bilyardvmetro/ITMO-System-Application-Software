package Network;

import CollectionObject.Vehicle;

import java.io.Serializable;
import java.util.Stack;

public class Response implements Serializable {
    private String message;
    private Stack<Vehicle> collection;
    private boolean userAuthentication;

    public Response(String message, Stack<Vehicle> collection) {
        this.message = message;
        this.collection= collection;
    }

    public Response(String message, boolean userAuthentication) {
        this.message = message;
        this.userAuthentication = userAuthentication;
    }

    public String getMessage() {
        return message;
    }

    public Stack<Vehicle> getCollection() {
        return collection;
    }

    public boolean isUserAuthenticated() {
        return userAuthentication;
    }
}