package it.unito.ium_android.requests;

public class Booking implements Comparable<Booking> {
    private String username;
    private int teacherId;
    private Teacher teacher = null;
    private String course;
    private int lessonSlot;
    Status status;

    public Booking(String username, int teacherId, String course, int lessonSlot, Status status) {
        this.username = username;
        this.teacherId = teacherId;
        this.course = course;
        this.lessonSlot = lessonSlot;
        this.status = status;
    }

    public Booking(String username, Teacher teacher, String course, int lessonSlot, Status status) {
        this.username = username;
        this.teacher = teacher;
        this.course = course;
        this.lessonSlot = lessonSlot;
        this.status = status;
    }

    public String getCourse() {
        return course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public int getLessonSlot() {
        return lessonSlot;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(Booking o) {
        return o.lessonSlot - this.lessonSlot;
    }
}
