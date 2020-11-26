package it.unito.ium_android;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.service.autofill.AutofillService;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import it.unito.ium_android.R;

public class Request extends AsyncTask<String, String, String> {
    private Context context;
    private String className;

    public Request(Context context, String className) {
        this.context = context;
        this.className = className;
    }

    @Override
    protected String doInBackground(String... strings) {
        String concatStrings = "";

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(strings[1]).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod(strings[2]);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(strings[0].length()));
        connection.setDoInput(true);
        connection.setDoOutput(true);

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(strings[0]);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                concatStrings += decodedString;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return concatStrings;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        switch (this.className){
            case "login":
                login(s);
                break;
            case "getSessionLogin":
                sessionCheck(s);
                break;
        }

    }

    private void login(String s){
        jsonMessage<User> result = new Gson().fromJson(s, jsonMessage.class);

        if (result.getMessage().equals("Ok")) {
            Toast.makeText(context, "login fatto", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sessionCheck(String s){
        jsonMessage<User> result = new Gson().fromJson(s, jsonMessage.class);

        if (result.getMessage().equals("Sessione valida")) {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

class User {
    private String username;
    private String password;
    private boolean admin;

    public User(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean getAdmin() {
        return this.admin;
    }

    public void setPassword(String password) {
        this.password = User.this.password;
    }
}

class Course {
    private String name;

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

class Teacher {
    private int id;
    private String name;
    private String surname;

    public Teacher(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}

class Lesson {
    private Teacher teacher;
    private Course course;

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

class Booking {
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
}

class jsonMessage<T> {
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

enum Status {
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

