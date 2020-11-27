package it.unito.ium_android.requests;

public class jsonMessage<T> {
    private String message;
    private T data;

    public jsonMessage(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }
}
