package it.unito.ium_android.requests;

// Class of the bookings
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

    // Returns the course
    public String getCourse() {
        return course;
    }

    // Returns the teacher
    public Teacher getTeacher() {
        return teacher;
    }

    // Returns the lesson slot
    public int getLessonSlot() {
        return lessonSlot;
    }

    // Compare function used during sort call
    @Override
    public int compareTo(Booking o) {
        return o.lessonSlot - this.lessonSlot;
    }
}
