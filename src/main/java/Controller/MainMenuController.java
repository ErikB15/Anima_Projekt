package Controller;


import com.codedisaster.steamworks.SteamAPI;

import Controller.ApiController;
import View.GUIManager;
import View.MainGUILauncher;
import javafx.application.Application;

public class MainMenuController {
    private static ApiController apiController;
    private static boolean isSteamInitialized = false;
    private GUIManager guiManager;
    private GameController gameController;


    //Networking
    public void init() throws Exception {
        System.out.println("Starting pre-flight checks...");
        apiController = new ApiController();
        apiController.initSteam();
    }


    public static ApiController getApiController() {
        return apiController;
    }

    //FÖR IHOPKOPPLING MED GUIMANAGER
   public void setGuiManager (GUIManager guiManager){
        this.guiManager = guiManager;
    }
    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }

    public MainMenuController(){
        
        if (ApiController.getIsSteamInitialized() == false) {
            System.out.println("Starting Steam from MainMenuController...");
            try{
                SteamAPI.loadLibraries();
            } catch (Exception e) {
                System.out.println("Failed to load Steam libraries. Make sure the Steamworks SDK is in project.");
                e.printStackTrace();
            }
            apiController = new ApiController();
            apiController.initSteam();
            isSteamInitialized = true;
        }


    }


    //TESTMETOD FÖR IHOPKOPPLING MED GUI
    public void sendMessageToConsole(){
        System.out.println("Successfully sending through MainMenuController");
        guiManager.sendMessageToConsole();
    }

    //För att skicka info från GUI till controller. vet ej om det är denna infon som ska skickas sen, men testar nu.
    public void cardPickedInGui(int cardID, int playerID, int cardAD, int cardHP){
       System.out.println("CardID: " + cardID + ", playerID: " + playerID + ", Card attack damage: " + cardAD + ", Card HitPoints: " +  cardHP);
    }
}
