package com.example.groupprojectapp2;

public class Model {
    private static Model instance;
    private String currentUser;

    // Private constructor to prevent instantiation
    private Model() {}

    // Public method to get the single instance of Model
    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}