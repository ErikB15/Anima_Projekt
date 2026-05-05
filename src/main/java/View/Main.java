package View;

import Controller.ApiController;
import javafx.application.Application;

public class Main {

    private static ApiController apiController;

    public static void main(String[] args) {

        System.setProperty("prism.order", "sw");

        ApiController apiController = new ApiController();
        apiController.initSteam();


       Application.launch(MainGUILauncher.class, args);


        //Vi ropar på launch metoden (utan för vår kod, infår i fx paketet), som i sin tur ropar på start-metoden i "MainGUILauncher"


        
    }

            public static ApiController getApiController() {
            return apiController;
    }
}