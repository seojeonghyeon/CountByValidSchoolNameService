package me.justin;

import me.justin.modules.MainController;

public class Main {
    public static void main(String[] args) {
        MainController mainController = MainController.getInstance();
        mainController.writeTextFile();
    }
}