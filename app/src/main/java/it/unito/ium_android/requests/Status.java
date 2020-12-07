package it.unito.ium_android.requests;

// Enum that refers the status of the booking
public enum Status {
    ACTIVE,
    DONE,
    CANCELED;

    public static Status fromString(String status) {
        switch (status) {
            case "active":
                return Status.ACTIVE;
            case "done":
                return Status.DONE;
            case "canceled":
                return Status.CANCELED;
        }
        return null;
    }
}
