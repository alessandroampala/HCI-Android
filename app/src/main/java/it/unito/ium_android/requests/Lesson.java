package it.unito.ium_android.requests;

import java.io.Serializable;

public class Lesson implements Serializable {
    private final Teacher teacher;
    private final Course course;

    public Lesson(Teacher teacher, Course course) {
        this.teacher = teacher;
        this.course = course;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public Course getCourse() {
        return this.course;
    }
}
