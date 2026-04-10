package com.devops;

public class BrokenClass {
    public void doSomething() {
        String message = "Hello"    // missing semicolon
        int number = "not a number" // wrong type - string assigned to int
    }
}
