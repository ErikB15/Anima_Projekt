package View;

import Controller.GameController;
import Model.Zone;
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
import javafx.stage.Stage;
import Model.Card;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


/**
 * GUIManager fungerar som kopplingslager mellan gui:t (JavaFX) och spel-logik (GameController).
 * Den ansvarar för att byta scener, ta emot användarinput från gui och uppdatera visuella delar.
 * baserat på spelmodellens tillstånd.
 */

public class GUIManager {
//Denna klass tänker jag att vi använder som Controller för GUI. Denna klass ska skicka info vidare till andra controllers när saker sker i GUI.

    //denna ska vara en check för vems tur det är. när det är den egna spelarens tur är denna true.
    private boolean isYourTurn = true;

    private Stage stage;
    private Scene scene;
    private Parent root;
    //Alla Controllers som GUI ska ha kontakt med går genom denna klass, därför behövs instanser här.
    //Finns instans av GUIManager i controller-klasserna också.
    //private MainMenuController mainMenuController;
    private GameController gameController;

    //boolean för att kontrollera ordningen av knapptryck i spelfas
    private boolean cardFromHandPicked = false;

    //Detta är spelkorten på PickCardScreen. Finns kanske ett smartare sätt att göra detta på
    @FXML private ImageView hand_0;
    @FXML private ImageView hand_1;
    @FXML private ImageView hand_2;

    //Array för att lägga till alla Image View i
    private ArrayList<ImageView> boardImageViews = new ArrayList<ImageView>();
    private ArrayList selectedCardsInPickCardphase = new ArrayList();

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
    private Map<Zone, ImageView[]> zoneMap = new HashMap<>();
    private ImageView[] views;
    private boolean playerOnesTurn = true;

    /**
     * Konstruktor som initialiserar GUIManager och skapar en koppling till GameController.
     * Sätter upp grundläggande kommunikation mellan gui:t och spel-logik.
     * @author: Erik, Elna
     */

    public GUIManager(){
        gameController = new GameController();
        gameController.setGuiManager(this);


        /*
        mainMenuController = new MainMenuController();
        mainMenuController.setGuiManager(this);
        mainMenuController.setGameController(gameController);
        */

    }

    /**
     * Byter scen till startskärmen.
     * Laddar FXML, kopplar ny controller till GameController och ersätter aktuell scen i Stage.
     *
     * @Param: event - MouseEvent från knapptryck i gui
     * @author: Erik, Elna
     */
    public void switchToStartScreen(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("StartScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);
            gameController.setGuiManager(controller);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Byter scen till anslutningsskärmen.
     * Laddar FXML och kopplar GameController till den nya GUI-instansen.
     *
     * @Param: event - MouseEvent från knapptryck i gui
     * @author: Erik, Elna
     */
    public void switchToConnectScreen(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ConnectScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);
            gameController.setGuiManager(controller);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller.sendMessageToConsole();

        } catch(Exception e){
            e.printStackTrace();
        }
    }



    /**
     * Byter scen till regler-skärmen för spelet.
     * Laddar FXML och visar spelregler i gui:t.
     *
     * @Param: event - MouseEvent från användarinput
     * @author: Erik, Elna
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
     * Byter till skärmen där spelaren väljer kort.
     * Laddar gui, kopplar controller och binder kortdata till ImageView.
     *
     * @Param: event - ActionEvent från knapptryck
     * @author: Erik, Elna
     */
    public void switchToPickCardScreen(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PickCardScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);
            gameController.setGuiManager(controller);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            //stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

            gameController.bindCardsToView(controller.getCardImageView(scene));

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Byter till själva spelbrädet och startar spelet visuellt.
     * Initierar gui och startar GameController-logik.
     *
     * @Param: event - ActionEvent från knapptryck
     * @author: Erik, ELna
     */
    public void switchToGameBoard(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameBoard.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();

            controller.setGameController(gameController);
            gameController.setGuiManager(controller);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller.init();
            gameController.startGame();

            addImageViewToList();
            //enemyPlaceCard(1, "CardBACKSIDE.png");



        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stänger applikationen helt.
     *
     * @Param: e - MouseEvent från gui
     * @author: Elna
     */
    public void exitApplication(MouseEvent e){
        Platform.exit();
    }

    /**
     * Hanterar klick på ett kort i handen.
     * Validerar om ett kort redan är valt och skickar index till GameController.
     *
     * @Param: event - MouseEvent från klick på ImageView
     * @author: Elna
     */
    public void pickedCardIndexPoint(MouseEvent event){

        if(isYourTurn == true) {

            if (cardFromHandPicked == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning!");
                alert.setContentText("You have already chose a card!");
                alert.show();
            } else {

                String cardID = event.getPickResult().getIntersectedNode().getId();
                String[] splitID;
                splitID = cardID.split("_");
                int cardIDInt = Integer.parseInt(splitID[1]);
                System.out.println(cardIDInt);

                if ((cardIDInt < 3) && (cardIDInt >= 0)) {
                    gameController.setIndexCardOnHandToMove(cardIDInt);
                    cardFromHandPicked = true;
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Warning!");
                    alert.setContentText("INVALID NUMBER");
                    alert.show();
                }
            }
        }
    }

    /**
     * Hanterar klick på en spelplats på brädet.
     * Validerar att ett kort först valts och skickar sedan platsindex till GameController.
     *
     * @Param: event - MouseEvent från klick på brädets UI
     * @author: Elna
     */
    public void pickedSpotToPlaceCardIndexPoint(MouseEvent event){

        if(isYourTurn == true) {


            if (cardFromHandPicked == false) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning!");
                alert.setContentText("The card you pick has to be from your hand!");
                alert.show();
                return;
            }

            String cardID = event.getPickResult().getIntersectedNode().getId();
            String[] splitID = cardID.split("_");
            int cardIDInt = Integer.parseInt(splitID[1]);

            if ((cardIDInt <= 3) && (cardIDInt >= 0)) {
                gameController.setIndexSpotToPlaceCard(cardIDInt);
                cardFromHandPicked = false;
                System.out.println(cardIDInt);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning!");
                alert.setContentText("INVALID NUMBER");
                alert.show();
            }

            isYourTurn = false;
        }
    }
    /**
     * Skickar varning till gui, används ej.
     * @param message
     * @author: Elna
     */
    public void sendMessageThroughGUI(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning!");
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Hanterar klick på kort i pick-fasen.
     * Hämtar kort från gui och skickar det till GameController för att läggas i spelarens deck.
     * Vi kontrollerar också om kortet redan är valt, detta uppnås genom att vi lägger in dett klickade imageview objektet
     * i en array. Vi går igenom array innan vi lägger till kortet i decket och skulle kortet redan ha avrit valt så retunerar vi.
     * Om kort-valet är gilltig så gör vi dessutom om bilden till bakssidan så man ser att den är vald.
     *
     * @Param: event - MouseEvent från ImageView
     * @author: Erik, Elna
     */
    public void pickedCard(MouseEvent event) {
        ImageView clickedCard = (ImageView) event.getSource();
        Card card = (Card) clickedCard.getUserData();

        for (int i = 0; i < selectedCardsInPickCardphase.size(); i++){
            if (selectedCardsInPickCardphase.get(i) == clickedCard) {
                System.out.println("Card already chosen! Pick another card.");
                return;
            }
        }

        selectedCardsInPickCardphase.add(clickedCard);
        clickedCard.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));

        if (playerOnesTurn == true){
            gameController.addCardToPlayerOne(card);
            System.out.println("card added to player 1 deck");
            playerOnesTurn = false;
        } else {
            gameController.addCardToOpponent(card);
            System.out.println("card added to player 2 deck");
            playerOnesTurn = true;
        }
    }


    /**
     * TESTMETOD för sammankoppling med GUI
     * @author: Elna
     */
    public void sendMessageToConsole(){
        System.out.println("Successfully sending message through GUIManager");
    }

    /**
     * Returnerar alla ImageView-komponenter som representerar kort i pick-card-scenen.
     * Används för att binda kortdata till UI.
     *
     * @Param: scene - aktuell JavaFX Scene där korten finns
     * @return: lista av ImageView som representerar kort
     * @author: Erik
     */
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


    /**
     * Kopplar GameController till GUIManager.
     * Används för att möjliggöra kommunikation mellan gui och spel-logik.
     *
     * @Param: gameController - instans av GameController
     * @author: Erik
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * I gameRules guit kan man välja olika tabbar av information, alla dessa Show-metoder visar bara den specifika panen i gameRules guit.
     * @param pane
     * @author: Erik
     */
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


    /**
     * Visar allmänna spelregler i gui.
     * @author: Erik
     */
    @FXML
    private void showGeneralRules() {
        showPane(generalRules);
    }
    /**
     * Visar allmänna kort-regler i gui.
     * @author: Erik
     */
    @FXML
    private void showCardRules() {
        showPane(cardRules);
    }
    /**
     * Visar allmänna spelareregler gällande spelare i gui.
     * @author: Erik
     */
    @FXML
    private void showPlayerRules() {
        showPane(playerRules);
    }

    /**
     * Visar allmänna regler för effekter i gui.
     * @author: Erik
     */
    @FXML
    private void showEffectsRules(){
        showPane(effectsRules);
    }

    /**
     * Ska göra komponenter draggable, om vi vill ha det sen
     * @param image
     * @author Elna
     */
    private void makeDraggable(Image image){

    }

    /**
     * Metod för att ta bort bilderna på korten i imageView i gameboard
     * @authe Erik
     */
    public void killCard(){

    }

    /**
     * Metoden existerar för att begränsa vem som kan samtala med gui och skickar endast vidare ansvaret av logiken till gameControllern.
     * @author Erik
     */
    public void endTurnInGui(){
        System.out.println(gameController.getCurrentPlayerId() + " has ended their turn");
        gameController.endTurn();
    }

    /**
     * Metod för att visa bilden när ett kort ska placeras på en imageview.
     * BORDE ANROPAS FRÅN GAMECONTROLLER NÄR MOTSTÅNDARE LÄGGER KORT
     * @author: Elna, Erik
     */
    public void enemyPlaceCard(int index, String imagePath){
        String fxID = ("p2board_" + index);
        Image newImage = new Image(imagePath);

        for(ImageView img : boardImageViews){

            if(fxID == img.getId()){
                img.setImage(newImage);
            }

        }

    }

    /**
     * Metod för att lägga till alla ImageView som representerar board i en array.
     * Det förenklar när man ska lägga ut bilder på plan att kunna loopa igenom alla ImageView.
     * @author Elna
     */
    public void addImageViewToList(){
         boardImageViews.add(p2board_0);
         boardImageViews.add(p2board_1);
         boardImageViews.add(p2board_2);
         boardImageViews.add(p2board_3);

         boardImageViews.add(p1board_0);
         boardImageViews.add(p1board_1);
         boardImageViews.add(p1board_2);
         boardImageViews.add(p1board_3);
    }


    /**
     * Metod för att visuelt representera en attack i spelet. Om det är animation, smårörelse eller vad bestäms senare.
     * @auther: Erik
     */
    public void attack(){

    }

    /**
     * Metod för att skriva meddelande i eventlog. Ska skriva ut allt som händer på brädan.
     * @auther: Erik
     */
    public void sendMessageToEventLog(){

    }


    /**
     * Initierar kopplingen mellan spelzoner (t.ex. hand och bräda) och deras motsvarande ImageView-komponenter i Gui.
     * Måste anropas efter att FXML har laddats, eftersom alla @FXML-fält annars är null.
     * Används som intern mapping för att kunna uppdatera rätt UI-komponent baserat på spelstatus.
     * @author: Erik
     */
    public void init() {
        zoneMap.put(Zone.HAND, new ImageView[]{hand_0, hand_1, hand_2});
        zoneMap.put(Zone.PLAYER_BOARD, new ImageView[]{p1board_0, p1board_1, p1board_2, p1board_3});
        //zoneMap.put(Zone.OPPONENT_BOARD, new ImageView[]{card_4, card_5, card_6}); // ändra senare när moståndare är aktuellt.
    }

    /**
     * Uppdaterar den visuella representationen av ett kort i Gui.
     * Metoden hittar rätt ImageView utifrån given zon och position, och sätter eller tar bort bild beroende på imagePath.
     * Om imagePath är null rensas platsen, annars laddas och visar bilden.
     * @param zone - vilken zon det är, hand, deck, brädan
     * @param index - vilket specifikt index vi pratar om
     * @param imagePath - bilden på kortet
     * @auther: Erik
     */
    public void renderCard(Zone zone, int index, String imagePath) {
        ImageView[] views = zoneMap.get(zone);

        if (views == null || index < 0 || index >= views.length) {
            return;
        }

        ImageView view = views[index];

        if (imagePath == null) {
            view.setImage(null);
            return;
        }

        Image image = new Image(getClass().getResourceAsStream(imagePath));
        view.setImage(image);
    }

    /**
     * Renderar spelarens hand visuellt i gui.
     * Uppdaterar ImageView-komponenter baserat på kort i handen.
     *
     * @Param: hand - lista av kort som spelaren har i handen
     * @auther: Erik
     */

    public void renderHand(ArrayList<Card> hand) {
        views = zoneMap.get(Zone.HAND);

        for (int i = 0; i < views.length; i++) {
            if (i < hand.size()) {
                InputStream stream = getClass().getResourceAsStream(hand.get(i).getImagePath());

                if (stream == null) {
                    System.out.println("Missing image: " + hand.get(i).getImagePath());
                    continue;
                }

                views[i].setImage(new Image(stream));
            } else {
                views[i].setImage(null);
            }
        }
    }
}