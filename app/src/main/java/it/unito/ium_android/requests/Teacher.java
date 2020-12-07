package it.unito.ium_android.requests;

// Class of the teachers
public class Teacher {
    private final int id;
    private final String name;
    private final String surname;

    public Teacher(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    // Returns the id of the teacher
    public int getId() {
        return id;
    }

    // Returns the teacher name
    public String getName() {
        return name;
    }

    // Returns the teacher surname
    public String getSurname() {
        return surname;
    }
}
