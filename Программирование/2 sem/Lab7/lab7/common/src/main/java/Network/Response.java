package Network;

import java.io.Serializable;

public class Response implements Serializable {
    private String message;
    private String collectionToStr;
    private boolean userAuthentication;

    public Response(String message, String collection) {
        this.message = message;
        this.collectionToStr = collection;
    }

    public Response(String message, boolean userAuthentication) {
        this.message = message;
        this.userAuthentication = userAuthentication;
    }

    public String getMessage() {
        return message;
    }

    public String getCollection() {
        return collectionToStr;
    }

    public boolean isUserAuthenticated() {
        return userAuthentication;
    }
}
