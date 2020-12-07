package it.unito.ium_android.requests;

// Class of the courses
public class Course {
    private final String name;

    public Course(String name) {
        this.name = name;
    }

    // Returns the course name
    public String getName() {
        return this.name;
    }
}
