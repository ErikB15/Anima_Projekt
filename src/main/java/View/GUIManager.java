package View;

import Controller.GameController;
import Controller.MainMenuController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class GUIManager {
//Denna klass tänker jag att vi använder som Controller för GUI. Denna klass ska skicka info vidare till andra controllers när saker sker i GUI.

    private Stage stage;
    private Scene scene;
    private Parent root;
    //Alla Controllers som GUI ska ha kontakt med går genom denna klass, därför behövs instanser här.
    //Finns instans av GUIManager i controller-klasserna också.
    private MainMenuController mainMenuController;
    private GameController gameController;

    //Detta är spelkorten på PickCardScreen. Finns kanske ett smartare sätt att göra detta på
    @FXML
    ImageView card11;
    @FXML
    ImageView card12;
    @FXML
    ImageView card13;
    @FXML
    ImageView card21;
    @FXML
    ImageView card22;
    @FXML
    ImageView card23;



    //FÖR IHOPKOPPLING MED CONTROLLER
    public GUIManager(){
        mainMenuController = new MainMenuController();
        mainMenuController.setGuiManager(this);
        //this.mainMenuController = mainMenuController;
    }

    public void switchToStartScreen(ActionEvent event){

        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("StartScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }


    }

    public void switchToConnectScreen(ActionEvent event){

        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("ConnectScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setResizable(false);
            mainMenuController.sendMessageToConsole();
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void switchToGameRulesScreen(ActionEvent event){
        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("GameRuleScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void switchToPickCardScreen(ActionEvent event){
        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("PickCardScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void exitApplication(ActionEvent e){
        Platform.exit();
    }

    //FÖR IHOPKOPPLING MED CONTROLLER, FUNKAR INTE JUST NU
    /*public void setMainMenuController(MainMenuController mainMenuController){
        this.mainMenuController = mainMenuController;
    }*/

    //TESTMETOD för sammankoppling med GUI
    public void sendMessageToConsole(){
        System.out.println("Successfully sending message through GUIManager");
    }

}
