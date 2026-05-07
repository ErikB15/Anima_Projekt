package View;

import Controller.GameController;
import Model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import Model.GameState;
import Model.Board;
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

    //Detta är spelkorten på PickCardScreen. Finns kanske ett smartare sätt att göra detta på
    @FXML private ImageView hand_0;
    @FXML private ImageView hand_1;
    @FXML private ImageView hand_2;

    //Array för att lägga till alla Image View i
    private ArrayList<ImageView> boardImageViews = new ArrayList<ImageView>();
    private ArrayList selectedCardsInPickCardphase = new ArrayList();

    @FXML private Label pickCardTurn;

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
    private Card cardToAttack;
    private Card cardToAttackWith;
    private boolean cardFromHandPicked = false;

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
    public void switchToPickCardScreen(MouseEvent event){
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
            gameController.startDraftPhase();

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
    public void switchToGameBoard(MouseEvent event){
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
            gameController.set();

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
        if (!isYourTurn) {
            return;
        }

        if(isYourTurn == true) {
            if (gameController.isCardPicked()) {

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
     *
     *
     * ANVÄNDS EJ LÄNGRE FÖR ATT DET NU FINNS EN "HANDLEBOARDCLICK" METOD LÄNGRE NER SOM HANTERAR OM DET SKA PLACERAS KORT ELLER KORT SOM SKA ATTACKERA. //ERIK
     */
    /*
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

     */
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
            switchTurnLabelInPickCard();
        } else {
            gameController.addCardToOpponent(card);
            System.out.println("card added to player 2 deck");
            playerOnesTurn = true;
            switchTurnLabelInPickCard();
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

        views.add((ImageView) scene.lookup("#card_0"));
        views.add((ImageView) scene.lookup("#card_1"));
        views.add((ImageView) scene.lookup("#card_2"));
        views.add((ImageView) scene.lookup("#card_3"));
        views.add((ImageView) scene.lookup("#card_4"));
        views.add((ImageView) scene.lookup("#card_5"));
        views.add((ImageView) scene.lookup("#card_6"));
        views.add((ImageView) scene.lookup("#card_7"));
        views.add((ImageView) scene.lookup("#card_8"));
        views.add((ImageView) scene.lookup("#card_9"));
        views.add((ImageView) scene.lookup("#card_10"));
        views.add((ImageView) scene.lookup("#card_11"));

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

    @FXML private void showGeneralRules() {
        showPane(generalRules);
    }

    @FXML private void showCardRules() {
        showPane(cardRules);
    }

    @FXML private void showPlayerRules() {
        showPane(playerRules);
    }

    @FXML private void showEffectsRules(){
        showPane(effectsRules);
    }

    private void makeDraggable(Image image){

    }

    public void killCard(){

    }

    public void endTurnInGuiInSinglePlayer(){

        System.out.println(gameController.getCurrentPlayerId() + " has ended their turn");

        if (gameController.getCurrentPlayerId() == PlayerID.PLAYER_TWO) {
            isYourTurn = true;
        } else {
            isYourTurn = false;
        }

        gameController.endTurnSinglePLayer();
    }

    public void enemyPlaceCard(int index, String imagePath){
        String fxID = ("p2board_" + index);
        Image newImage = new Image(imagePath);

        for(ImageView img : boardImageViews){

            if(fxID == img.getId()){
                img.setImage(newImage);
            }

        }

    }

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

    public void sendMessageToEventLog(){

    }

    public void init() {
        zoneMap.put(Zone.HAND, new ImageView[]{hand_0, hand_1, hand_2});
        zoneMap.put(Zone.PLAYER_BOARD, new ImageView[]{p1board_0, p1board_1, p1board_2, p1board_3});
        zoneMap.put(Zone.OPPONENT_BOARD, new ImageView[]{p2board_0, p2board_1, p2board_2, p2board_3});
    }

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

    public void renderHand(ArrayList<Card> hand) {
        views = zoneMap.get(Zone.HAND);

        for (int i = 0; i < views.length; i++) {
            if (i < hand.size()){
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

    public void switchTurnLabelInPickCard(){

        if(playerOnesTurn == true){
            pickCardTurn.setText("Player 1");

        } else{
            pickCardTurn.setText("Player 2");
        }
    }

    public void showWaiting()             {
        System.out.println("Väntar...");
    }

    public void enableCardButtons()       {
        isYourTurn = true;
    }

    public void updateBoard(String json)  {
        System.out.println("Spelläge: " + json);
    }

    public void showGameOver(String name) {
        sendMessageThroughGUI("Vinnare: " + name);
    }

    public void showError(String msg)     {
        sendMessageThroughGUI(msg);
    }

    public void showChat(String msg)      {
        System.out.println("Chatt: " + msg);
    }

    /**
     * Här hanterar vi valet av kort på motståndaren bräda. Om allt går bra så anropar vi attack metoden. Och uppdaterar gui
     *
     * @param event - eventtypen som skickas från guit.
     * @author Erik
     */
    public void pickedCardToAttack(MouseEvent event){
        if(isYourTurn == true) {

            if (!gameController.isAttackerPicked()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning!");
                alert.setContentText("Pick a card on your board first!");
                alert.show();

                return;
            }

            String cardID = event.getPickResult().getIntersectedNode().getId();

            String[] splitID = cardID.split("_");

            int defenderIndex = Integer.parseInt(splitID[1]);

            if ((defenderIndex <= 4) && (defenderIndex >= 0)) {

                gameController.setIndexToCardToAttack(defenderIndex);

                int attackerIndex = gameController.getIndexToCardToAttackWith();

                GameState gameState = gameController.getGameState();
                Board board = gameState.getBoard();

                cardToAttackWith = board.getCard(PlayerID.PLAYER_ONE, attackerIndex);
                cardToAttack = board.getCard(PlayerID.PLAYER_TWO, defenderIndex);

                gameController.attackCard(attackerIndex, defenderIndex);

                if (cardToAttackWith != null && !cardToAttackWith.isDead()) {
                    renderCard(Zone.PLAYER_BOARD, attackerIndex, cardToAttackWith.getImagePath());
                } else {
                    renderCard(Zone.PLAYER_BOARD, attackerIndex, null);
                }
                if (cardToAttack != null && !cardToAttack.isDead()) {
                    renderCard(Zone.OPPONENT_BOARD, defenderIndex, cardToAttack.getImagePath());
                } else {
                    renderCard(Zone.OPPONENT_BOARD, defenderIndex, null);
                }

            } else {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning!");
                alert.setContentText("INVALID NUMBER");
                alert.show();
            }
            gameController.resetAttackState();
        }
    }

    /**
     * Hanterar klick på spelarens bräde. Metoden avgör om klicket ska resultera i att ett kort placeras från handen eller att ett kort väljs för attack.
     * Om rutan är tom och ett kort från handen tidigare valts så placeras kortet på den platsen.
     * Om rutan innehåller ett kort så markeras det som attackkort beroende på tidigare val i GameController.
     * Så metoden är basiclly bara en mega if-sats om vad det är för typ av klick.
     *
     * @Param event - typen av event som skickas från gui.
     * @author Erik
     */
    public void handleBoardClick(MouseEvent event) {
        if (!isYourTurn) {
            return;
        }

        ImageView view = (ImageView) event.getPickResult().getIntersectedNode();

        if (view == null || view.getId() == null) {
            return;
        }

        String id = view.getId();
        String[] splitID = id.split("_");

        int index = Integer.parseInt(splitID[1]);

        GameState gameState = gameController.getGameState();
        Board board = gameState.getBoard();

        PlayerID currentPlayer = gameState.getCurrentPlayerId();
        Card cardOnBoard = board.getCard(currentPlayer, index);

        if (cardOnBoard == null) {
            if (!cardFromHandPicked) {
                return;
            }

            gameController.setIndexSpotToPlaceCard(index);
            cardFromHandPicked = false;
            return;
        }
        gameController.setIndexOfCardOnMyBoardToAttackWith(index);
    }

    public Card getCardToAttack(){
        return cardToAttack;
    }

    public Card getCardToAttackWith(){
        return cardToAttackWith;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }
}