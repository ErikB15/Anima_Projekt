package View;

import Controller.ApiController;
import javafx.application.Application;

public class Main {

    private static ApiController apiController;

    public static void main(String[] args) {

       // System.setProperty("prism.order", "es3"); // Hardware accellerated graphics api, "es2" for OpenGL
       //System.setProperty("prism.order", "d3d,sw");

       // Force hardware acceleration 
       //System.setProperty("prism.order", "d3d,es2,sw");
      // System.setProperty("prism.forceGPU", "true");


        apiController = new ApiController();
        apiController.initSteam();
        ApiController.hostLobby();


       Application.launch(MainGUILauncher.class, args);


        //Vi ropar på launch metoden (utan för vår kod, infår i fx paketet), som i sin tur ropar på start-metoden i "MainGUILauncher"


        
    }

            public static ApiController getApiController() {
            return apiController;
    }
}