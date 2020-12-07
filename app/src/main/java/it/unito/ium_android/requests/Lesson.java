package it.unito.ium_android.requests;

import java.io.Serializable;

// Class of the lessons
public class Lesson implements Serializable {
    private final Teacher teacher;
    private final Course course;

    public Lesson(Teacher teacher, Course course) {
        this.teacher = teacher;
        this.course = course;
    }

    // Returns the teacher
    public Teacher getTeacher() {
        return this.teacher;
    }

    // Returns the Course
    public Course getCourse() {
        return this.course;
    }
}
