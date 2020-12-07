package it.unito.ium_android.requests;

// Class of the json messages returned from server
public class jsonMessage<T> {
    private final String message;
    private final T data;

    public jsonMessage(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Gets the server message
    public String getMessage() {
        return this.message;
    }

    // Gets the server data
    public T getData() {
        return this.data;
    }
}
