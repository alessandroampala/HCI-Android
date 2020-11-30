package it.unito.ium_android.requests;

public class Booking implements Comparable<Booking>{
    public String username;
    int teacherId;
    String course;
    int lessonSlot;
    Status status;

    public Booking(String username, int teacherId, String course, int lessonSlot, Status status) {
        this.username = username;
        this.teacherId = teacherId;
        this.course = course;
        this.lessonSlot = lessonSlot;
        this.status = status;
    }

    @Override
    public int compareTo(Booking o) {
        return o.lessonSlot - this.lessonSlot;
    }
}
