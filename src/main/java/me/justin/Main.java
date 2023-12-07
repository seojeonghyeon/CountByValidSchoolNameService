package me.justin;

import me.justin.domain.MainController;

public class Main {
    public static void main(String[] args) {
        MainController mainController = MainController.getInstance();
        mainController.writeTextFile();
    }
}