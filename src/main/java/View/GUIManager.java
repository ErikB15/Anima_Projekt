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
import Model.Card;

import java.io.IOException;
import java.util.ArrayList;

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
    @FXML private ImageView card_1;
    @FXML private ImageView card_2;
    @FXML private ImageView card_3;
    @FXML private ImageView card_4;
    @FXML private ImageView card_5;
    @FXML private ImageView card_6;
    private Card card;



    //FÖR IHOPKOPPLING MED CONTROLLER
    public GUIManager(){
        gameController = new GameController();
        gameController.setGuiManager(this);

        mainMenuController = new MainMenuController();
        mainMenuController.setGuiManager(this);
        mainMenuController.setGameController(gameController);
    }


    public void switchToStartScreen(ActionEvent event){

        try{
            setGameController(gameController);
            gameController.setGuiManager(this);

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

            gameController.bindCardsToView(getCardImageView(scene));

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
    public void pickedCard(MouseEvent event) {
        System.out.println("I have clicked the card!");

        ImageView clicked = (ImageView) event.getSource();
        Card card = (Card) clicked.getUserData();

        int cardIDInt = card.getCardID(); // eller från card direkt

        mainMenuController.cardPickedInGui(cardIDInt, 0, card.getCardAD(), card.getCardHP());
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

    public ArrayList<ImageView> getCardImageView(Scene scene) {

        ArrayList<ImageView> views = new ArrayList<>();

        views.add((ImageView) scene.lookup("#card_21"));
        views.add((ImageView) scene.lookup("#card_25"));
        views.add((ImageView) scene.lookup("#card_12"));
        views.add((ImageView) scene.lookup("#card_22"));
        views.add((ImageView) scene.lookup("#card_13"));
        views.add((ImageView) scene.lookup("#card_23"));

        return views;
    }



    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
