package Controller;

import View.GUIManager;
import View.MainGUILauncher;
import javafx.application.Application;

public class MainMenuController {
    private GUIManager guiManager;

    //FÖR IHOPKOPPLING MED GUIMANAGER, FUNKAR INTE JUST NU
   public void setGuiManager (GUIManager guiManager){
        this.guiManager = guiManager;
    }

    public MainMenuController(){
        //FÖR IHOPKOPPLING MED GUIMANAGER, FUNKAR INTE JUST NU
        //guiManager = new GUIManager(this);
    }

    //TESTMETOD FÖR IHOPKOPPLING MED GUI
    public void sendMessageToConsole(){
        System.out.println("Successfully sending through MainMenuController");
        guiManager.sendMessageToConsole();
    }
}
