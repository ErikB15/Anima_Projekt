package Controller;


import View.GUIManager;

public class MainMenuController {
    private static ApiController apiController;
    private static boolean isSteamInitialized = false;
    private GUIManager guiManager;
    private GameController gameController;



    //FÖR IHOPKOPPLING MED GUIMANAGER
   public void setGuiManager (GUIManager guiManager){
        this.guiManager = guiManager;
    }
    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }


    public MainMenuController(){
        
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
