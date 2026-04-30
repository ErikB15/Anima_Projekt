package View;

import Controller.GameController;
//import Controller.MainMenuController;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {


       Application.launch(MainGUILauncher.class, args);


        //Vi ropar på launch metoden (utan för vår kod, infår i fx paketet), som i sin tur ropar på start-metoden i "MainGUILauncher"

    }
}