package View;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("hejsan");

        //Vi ropar på launch metoden (utan för vår kod, infår i fx paketet), som i sin tur ropar på start-metoden i "MyApplication"
        Application.launch(MyApplication.class, args);
    }
}