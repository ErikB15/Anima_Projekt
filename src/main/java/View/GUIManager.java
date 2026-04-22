package View;

import Controller.GameController;
import Controller.MainMenuController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;

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
    ImageView card_25;
    @FXML
    ImageView card_12;
    @FXML
    ImageView card_13;
    @FXML
    ImageView card_21;
    @FXML
    ImageView card_22;
    @FXML
    ImageView card_23;



    //FÖR IHOPKOPPLING MED CONTROLLER
    public GUIManager(){
        mainMenuController = new MainMenuController();
        mainMenuController.setGuiManager(this);

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

    public void switchToGameBoard(ActionEvent event){

        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("GameBoard.fxml"));
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

    //Denna metoden registrerar vilket nod-id som klickats på i "välja kort" och skickar detta till MainMenuController tillsammans med spelar-id.
    public void pickedCard(MouseEvent event){
        System.out.println("I have clicked the card!");
        //Här under tar jag ut IDt för varje nodelement(representerar kort) som klickas på i GUI.
        //nodens ID borde matcha med kortets ID sen.
        String cardID = event.getPickResult().getIntersectedNode().getId();
        String[] splitID;
        splitID = cardID.split("_");
        int  cardIDInt = Integer.parseInt(splitID[1]);
        //Här anropar jag en metod i en av controllers som får göra nått med det IDt.
        //0 är placeholder, vet inte hur vi gör med playerID än.
        mainMenuController.cardPickedInGui(cardIDInt, 0);

        //Försöker få bilden att ändras till kortets baksida. INTE KLAR
       // Image backsideCard = new Image(getClass().getResourceAsStream("CardBACKSIDE.png"));

       /* if(cardIDInt == 25){
            card_25.setImage(backsideCard);
        }*/






    }

    //TESTMETOD för sammankoppling med GUI
    public void sendMessageToConsole(){
        System.out.println("Successfully sending message through GUIManager");
    }

}
