package View;

import Controller.GameController;
import Controller.MainMenuController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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

    //boolean för att kontrollera ordningen av knapptryck i spelfas
    private boolean cardFromHandPicked = false;

    //Detta är spelkorten på PickCardScreen. Finns kanske ett smartare sätt att göra detta på
    @FXML private ImageView hand_0;
    @FXML private ImageView hand_1;
    @FXML private ImageView hand_2;

    @FXML private ImageView p1board_0;
    @FXML private ImageView p1board_1;
    @FXML private ImageView p1board_2;
    @FXML private ImageView p1board_3;

    @FXML private ImageView p2board_0;
    @FXML private ImageView p2board_1;
    @FXML private ImageView p2board_2;
    @FXML private ImageView p2board_3;

    @FXML private Pane generalRules;
    @FXML private Pane cardRules;
    @FXML private Pane playerRules;
    @FXML private Pane effectsRules;



    //FÖR IHOPKOPPLING MED CONTROLLER
    public GUIManager(){
        gameController = new GameController();
        gameController.setGuiManager(this);

        mainMenuController = new MainMenuController();
        mainMenuController.setGuiManager(this);
        mainMenuController.setGameController(gameController);
    }

    /**
     * Bytar till startscen vid knapptryck
     * @param event
     * @author Elna N
     */
    public void switchToStartScreen(MouseEvent event){

        try{
            setGameController(gameController);
            gameController.setGuiManager(this);

            root = FXMLLoader.load(getClass().getClassLoader().getResource("StartScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            //stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Bytar till scen för att connecta vid knapptryck
     * @param event
     * @author Elna N
     */
    public void switchToConnectScreen(MouseEvent event){

        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("ConnectScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
           // stage.setFullScreen(true);
            stage.setResizable(false);
            mainMenuController.sendMessageToConsole();
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Bytar till scen för att gamerules vid knapptryck
     * @param event
     * @author Elna N / Erik
     */
    public void switchToGameRulesScreen(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameRuleScreen.fxml"));
            root = loader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
           // stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Bytar till scen för att välja kort vid knapptryck
     * @param event
     * @author Elna N / Erik
     */
    public void switchToPickCardScreen(ActionEvent event){
        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("PickCardScreen.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            //stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

            gameController.bindCardsToView(getCardImageView(scene));

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Bytar till skärm för spelplan vid knapptryck
     * @param event
     * @author Elna N.
     */
    public void switchToGameBoard(ActionEvent event){

        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource("GameBoard.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            //stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stänger ner applikation
     * @param e
     * @author Elna N.
     */
    public void exitApplication(MouseEvent e){
        Platform.exit();
    }


    /**
     * Denna metod skickar en indexpoint mellan 0-2 till controller. Används för handen
     * @param event
     * @author Elna N.
     */
    public void pickedCardIndexPoint(MouseEvent event){

        if(cardFromHandPicked == true) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning!");
            alert.setContentText("You have already chose a card!");
            alert.show();
        } else{

            String cardID = event.getPickResult().getIntersectedNode().getId();
            String[] splitID;
            splitID = cardID.split("_");
            int  cardIDInt = Integer.parseInt(splitID[1]);
            System.out.println(cardIDInt);

                if((cardIDInt < 3) && (cardIDInt >= 0)){
                    gameController.setIndexCardOnHandToMove(cardIDInt);
                    cardFromHandPicked = true;
                } else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Warning!");
                    alert.setContentText("INVALID NUMBER");
                    alert.show();
            }
        }



    }

    /**
     *  Denna metod skickar indexpoint mellan 0 - 3 till controller. Används för plats där spelaren ska lägga sitt kort.
     * @param event
     * @author Elna N.
     */

    public void pickedSpotToPlaceCardIndexPoint(MouseEvent event){

        if(cardFromHandPicked == false){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning!");
            alert.setContentText("The card you pick has to be from your hand!");
            alert.show();

        } else{

            String cardID = event.getPickResult().getIntersectedNode().getId();
            String[] splitID;
            splitID = cardID.split("_");
            int  cardIDInt = Integer.parseInt(splitID[1]);
            System.out.println(cardIDInt);

                if((cardIDInt >= 3) && (cardIDInt < 7)){
                    gameController.setIndexSpotToPlaceCard(cardIDInt);
                    cardFromHandPicked = false;
                } else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Warning!");
                    alert.setContentText("INVALID NUMBER");
                    alert.show();
                }
        }



    }

    /**
     * Metod som skickar en popup genom gUI
     * @param message - meddelande som ska visas
     * @author Elna N
     */
    public void sendMessageThroughGUI(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning!");
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Denna metoden registrerar vilket nod-id som klickats på i "välja kort" och skickar detta till MainMenuController tillsammans med spelar-id.
     * @author Elna N
     */

    public void pickedCard(MouseEvent event) {
        System.out.println("I have clicked the card!");

        ImageView clicked = (ImageView) event.getSource();
        Card card = (Card) clicked.getUserData();

        mainMenuController.cardPickedInGui(card.getCardID(), 0, card.getCardAD(), card.getCardCurrentHP());
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

    private void showPane(Pane pane) {
        generalRules.setVisible(false);
        generalRules.setManaged(false);

        cardRules.setVisible(false);
        cardRules.setManaged(false);

        playerRules.setVisible(false);
        playerRules.setManaged(false);

        effectsRules.setVisible(false);
        effectsRules.setManaged(false);

        pane.setVisible(true);
        pane.setManaged(true);
    }


    @FXML
    private void showGeneralRules() {
        showPane(generalRules);
    }

    @FXML
    private void showCardRules() {
        showPane(cardRules);
    }

    @FXML
    private void showPlayerRules() {
        showPane(playerRules);
    }

    @FXML
    private void showEffectsRules(){
        showPane(effectsRules);
    }

    private void makeDraggable(Image image){

    }


}
