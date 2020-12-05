package it.unito.ium_android.requests;

public class Booking implements Comparable<Booking> {
    private final Teacher teacher;
    private final String course;
    private final int lessonSlot;
    Status status;

    public Booking(Teacher teacher, String course, int lessonSlot, Status status) {
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

    @Override
    public int compareTo(Booking o) {
        return o.lessonSlot - this.lessonSlot;
    }
}
