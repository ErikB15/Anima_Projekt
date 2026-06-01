package View;

import Controller.GameController;
import Model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import Model.GameState;
import Model.Board;
import Model.Card;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.String.valueOf;


/**
 * GUIManager fungerar som kopplingslager mellan gui:t (JavaFX) och spel-logik (GameController).
 * Den ansvarar för att byta scener, ta emot användarinput från gui och uppdatera visuella delar.
 * baserat på spelmodellens tillstånd.
 */

public class GUIManager {
//Denna klass tänker jag att vi använder som Controller för GUI. Denna klass ska skicka info vidare till andra controllers när saker sker i GUI.

    //denna ska vara en check för vems tur det är. när det är den egna spelarens tur är denna true.
    private boolean isYourTurn = true;
    private PlayerID localRole = PlayerID.PLAYER_ONE; // vilken sida av brädet är "min"
    private boolean isDraftTurn = false; //är det min tur att välja kort
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

    @FXML private ImageView enemyIcon;

    //Detta är labels för Hp i spelet. //Elna
    @FXML private Label hp_0;
    @FXML private Label hp_1;
    @FXML private Label hp_2;
    @FXML private Label hp_3;
    @FXML private Label hp_4;
    @FXML private Label hp_5;
    @FXML private Label hp_6;
    @FXML private Label hp_7;
    @FXML private Label hp_8;
    @FXML private Label hp_9;
    @FXML private Label hp_10;
    @FXML private Label hp_11;
    @FXML private Label playerHP_1;
    @FXML private Label playerHP_2;



    //Array för att lägga till alla Labels för Hp i i
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
    @FXML private Pane matchRules;
    @FXML private Pane startMenu;
    boolean validChoice;

    private Map<Zone, ImageView[]> zoneMap = new HashMap<>();
    private ImageView[] views;
    private boolean playerOnesTurn = true;
    private int cardToAttack;
    private int cardToAttackWith;
    private boolean attackCardPicked = false;
    private boolean cardFromHandPicked = false;
    private boolean yourTurnToPickCard = true;
    @FXML
    private TextArea textArea;
    @FXML private Label turnNumber;

    private ArrayList<ImageView> pickCardViews = new ArrayList<>();

    @FXML
    public void initialize(){
            System.out.println("INIT GUIManager: " + this);
            System.out.println("textArea = " + textArea);
    }

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

    @FXML
    public void switchToStartScreen(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("StartScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.setStage(stage);

            gameController.setGuiManager(controller);

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

    @FXML
    public void switchToMainMenuScreen(MouseEvent event) {
        switchToStartScreen(event);
    }

    @FXML
    public void switchToConnectScreen(){
        System.out.println("MULTIPLAYER KNAPPEN KLICKAD!");
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ConnectScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);
            controller.setStage(stage);

            gameController.setGuiManager(controller);

            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller.sendMessageToConsole();

            gameController.set();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Den ska byta menyn till game over menyn. Tror jag saknar något, för stage blir lika med null.
     * Antog att ni sätter stagen någonstans för att vi ska kunna byta senare men variabeln verkar alltid vara null?
     * Om jag fattat rätt efter typ 15 minuter av läsning så ska stage följa med de olika stages vi bytar till.
     * Så varje stage vi bytar till ska sparas i variabeln stage så vi inte alltid behöver en mouse event för att byta stage
     *
     * @author Jim Ström
     */
    @FXML
    public void switchToGameOverMenu(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameOverScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            controller.setStage(stage);

            gameController.setGuiManager(controller);

            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToGameOverBUTTON(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameOverScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.setStage(stage);

            gameController.setGuiManager(controller);

            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

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
    @FXML
    public void switchToGameRulesScreen(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameRuleScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.setStage(stage);

            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Metod för att skicka in boolean till switchToPickedCardScreen om det är singleplayer eller inte.
     *
     * @throws IOException
     * @author Erik
     */
    @FXML
    private void openSinglePlayer() throws IOException {
        gameController.startSingleplayer();
    }

    /**
     * Metod för att skicka in boolean till switchToPickedCardScreen om det är singleplayer eller inte.
     *
     * //@param event - mousse clicked event
     * @throws IOException
     * @author Erik
     */
    @FXML
    private void openMultiPlayer() throws IOException {
        gameController.startMultiplayer();
    }

    /**
     * Byter till skärmen där spelaren väljer kort.
     * Laddar gui, kopplar controller och binder kortdata till ImageView.
     *
     * @Param: event - ActionEvent från knapptryck
     * @author: Erik, Elna
     */
    @FXML
    public void switchToPickCardScreen(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PickCardScreen.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            controller.setStage(stage);

            gameController.setGuiManager(controller);

            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller.addPickCardViews(scene);
            createBordersForPickCards();
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
    @FXML
    public void switchToGameBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameBoard.fxml"));
            root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            controller.setStage(stage);

            gameController.setGuiManager(controller);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller.init();

            controller.addImageViewToList();
            gameController.set();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stänger applikationen helt.
     *
     * @Param: e - MouseEvent från gui
     * @author: Elna
     */
    @FXML
    public void exitApplication(){
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
        if (!isLocalPlayersTurn()) {
            return;
        }

        if(isLocalPlayersTurn()) {

            if (gameController.isCardPicked()) {
                sendMessageThroughGUI("You have already picked a card. This will change your pick.");
            }

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
     * Skickar varning till gui.
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

        // MULTIPLAYER GREN
        if (gameController.isMultiplayer()) {
            System.out.println("(GUI) pickedCard klick. isDraftTurn=" + isDraftTurn + ", localRole=" + localRole);
            // Kolla om det är spelarens tur att välja (sätts av onDraftTurn till enableDraftPicking)
            if (!isDraftTurn) {
                sendMessageThroughGUI("Vänta på din tur att välja kort!");
                return;
            }
            if (card == null) return;

            // Förhindra dubbelklick innan servern svarar
            isDraftTurn = false;

            // Visa baksidan lokalt direkt (snabb feedback)
            clickedCard.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));

            // Skicka valet till servern. Servern lägger kortet i din deck och broadcastar GAME_STATE.
            gameController.sendDraftPick(card.getCardID());

            return; // VIKTIGT här, kör INTE singleplayer logiken nedanför
        }
        // SLUT MULTIPLAYER GREN

        if (!isLocalPlayersTurn()){return;}

        String ID = event.getPickResult().getIntersectedNode().getId();
        String[] splitID = ID.split("_");
        int IDInt = Integer.parseInt(splitID[1]);
        System.out.println(IDInt);

        gameController.chooseCardPhase(IDInt);
    }

    public void updateGuiAfterCardIsPicked(int IDInt){
        changeHP(IDInt, " ", null);


        ImageView view = pickCardViews.get(IDInt);
        Image newImage = new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm());
        view.setImage(newImage);
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

        startMenu.setVisible(false);
        startMenu.setManaged(false);

        generalRules.setVisible(false);
        generalRules.setManaged(false);

        cardRules.setVisible(false);
        cardRules.setManaged(false);

        playerRules.setVisible(false);
        playerRules.setManaged(false);

        effectsRules.setVisible(false);
        effectsRules.setManaged(false);

        matchRules.setVisible(false);
        matchRules.setManaged(false);

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
    @FXML private void showMatchRules(){showPane(matchRules);}

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
        gameController.endTurn();
        resetPlayerIcons();
    }

    public void enemyPlaceCard(int index, String imagePath){
        String fxID = ("p2board_" + index);
        Image newImage = new Image(imagePath);

        for(ImageView img : boardImageViews){

            if(fxID.equals(img.getId())){
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

    @FXML
    public void sendMessageToEventLog(String message){
        if (textArea == null) return;
        Platform.runLater(() -> textArea.appendText(message + "\n"));    }

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
            changeHP(index, " ", zone);
            return;
        }

        Image image = new Image(getClass().getResourceAsStream(imagePath));
        view.setImage(image);
        String hp = valueOf(getHPForCard(index, zone));
        changeHP(index, hp, zone);
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

                if(hand.size() >= i){
                    String hp = valueOf(getHPForCard(i, Zone.HAND));
                    changeHP(i, hp, Zone.HAND);
                }

            } else {
                views[i].setImage(null);
            }
        }
    }

    public void switchTurnLabelInPickCard(PlayerID id){

        if(id == PlayerID.PLAYER_ONE){
            pickCardTurn.setText("Player 2");

        } else if(id == PlayerID.PLAYER_TWO) {
            pickCardTurn.setText("Player 1");
        }
    }

    public void showWaiting() {
        System.out.println("Väntar...");
        // Ska visa text som säger att man väntar, t.ex eventloggen eller en ny label
    }

    public void enableCardButtons() {
        isYourTurn = true;
    }

    /**
     * Ritar om spelarens hand med givna bildvägar
     * Listan kan ha 0-3 element. Tomma platser rensar bilden där.
     * HP läses från lokal model (synkad från server via Controller).
     *
     * @param imagePaths bildvägar för korten i handen
     * @author Leo
     */
    public void updateMyHand(ArrayList<String> imagePaths) {
        for (int i = 0; i < 3; i++) {
            String path = (i < imagePaths.size()) ? imagePaths.get(i) : null;
            renderCard(Zone.HAND, i, path);
        }
    }

    /**
     * Ritar om min sida av brädet.
     * Listan ska ha 4 element. Null per plats = tom plats.
     *
     * @param imagePaths bildvägar för korten på brädet (4 element)
     * @author Leo
     */
    public void updateMyBoard(ArrayList<String> imagePaths) {
        for (int i = 0; i < 4; i++) {
            String path = (i < imagePaths.size()) ? imagePaths.get(i) : null;
            renderCard(Zone.PLAYER_BOARD, i, path);
        }
    }

    /**
     * Ritar om motståndarens sida av brädet.
     * Listan ska ha 4 element. Null per plats = tom plats.
     *
     * @param imagePaths bildvägar för motståndarens kort (4 element)
     * @author Leo
     */
    public void updateOpponentBoard(ArrayList<String> imagePaths) {
        for (int i = 0; i < 4; i++) {
            String path = (i < imagePaths.size()) ? imagePaths.get(i) : null;
            renderCard(Zone.OPPONENT_BOARD, i, path);
        }
    }

    public void updateBoard(String json) {
        System.out.println("Spelläge: " + json);
        // Tar emot hela spelläget från servern och ritar om behöver Gson import
    }

    public void showGameOver(String name) {
        sendMessageThroughGUI("Vinnare: " + name);
        // visa vem som vann
    }

    public void showError(String msg) {
        sendMessageThroughGUI(msg);
        // visa felmeddelnde
    }

    public void showChat(String msg) {
        System.out.println("Chatt: " + msg);
        //visa chatmeddelande
    }

    /**
     * Här hanterar vi valet av kort på motståndaren bräda. Om allt går bra så anropar vi attack metoden. Och uppdaterar gui
     *
     * @param event - eventtypen som skickas från guit.
     * @author Erik
     */
    public void pickedCardToAttack(MouseEvent event) {
        if(isLocalPlayersTurn()) {

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

            if ((defenderIndex < 4) && (defenderIndex >= 0)) {

                gameController.setIndexToCardToAttack(defenderIndex);

                int attackerIndex = gameController.getIndexToCardToAttackWith();

                GameState gameState = gameController.getGameState();
                //Board board = gameState.getBoard();

                cardToAttackWith = attackerIndex;
                cardToAttack = defenderIndex;

                gameController.attackCard(attackerIndex, defenderIndex);

                // Dessa checks är onödiga och görs istället i GameControllern. Sen anropar gameControllern GUI:et.
                //if (cardToAttackWith != null && !cardToAttackWith.isDead()) {
                //    renderCard(Zone.PLAYER_BOARD, attackerIndex, cardToAttackWith.getImagePath());
                //} else {
                //    renderCard(Zone.PLAYER_BOARD, attackerIndex, null);
                //}
                //if (cardToAttack != null && !cardToAttack.isDead()) {
                //    renderCard(Zone.OPPONENT_BOARD, defenderIndex, cardToAttack.getImagePath());
                //} else {
                //    renderCard(Zone.OPPONENT_BOARD, defenderIndex, null);
                //}

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
        if (!isLocalPlayersTurn()) {
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
            attackCardPicked = true;
            cardFromHandPicked = false;
            return;
        }
        gameController.setIndexOfCardOnMyBoardToAttackWith(index);
    }

    public int getCardToAttack(){
        return cardToAttack;
    }

    public int getCardToAttackWith(){
        return cardToAttackWith;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }

    public void changeHP(int index, String newValue, Zone zone){

        if(gameController.getGameState().getPhase() == GamePhase.DRAFT){

            switch(index){
                case 0:
                    hp_0.setText(newValue);
                    break;
                case 1:
                    hp_1.setText(newValue);
                    break;
                case 2:
                    hp_2.setText(newValue);
                    break;
                case 3:
                    hp_3.setText(newValue);
                    break;
                case 4:
                    hp_4.setText(newValue);
                    break;
                case 5:
                    hp_5.setText(newValue);
                    break;
                case 6:
                    hp_6.setText(newValue);
                    break;
                case 7:
                    hp_7.setText(newValue);
                    break;
                case 8:
                    hp_8.setText(newValue);
                    break;
                case 9:
                    hp_9.setText(newValue);
                    break;
                case 10:
                    hp_10.setText(newValue);
                    break;
                case 11:
                    hp_11.setText(newValue);
                    break;
                default: sendMessageThroughGUI("ERROR");
            }
        } else {
           if(zone == Zone.HAND){

               switch(index){
                   case 0:
                       hp_0.setText(newValue);
                       break;
                   case 1:
                       hp_1.setText(newValue);
                       break;
                   case 2:
                       hp_2.setText(newValue);
                       break;
                   default: sendMessageThroughGUI("ERROR");
               }

           } else if (zone == Zone.PLAYER_BOARD){

               switch(index){
                   case 0:
                       hp_3.setText(newValue);
                       break;
                   case 1:
                       hp_4.setText(newValue);
                       break;
                   case 2:
                       hp_5.setText(newValue);
                       break;
                   case 3:
                       hp_6.setText(newValue);
                       break;
                   default: sendMessageThroughGUI("ERROR");
               }

           } else {

               switch(index){
                   case 0:
                       hp_7.setText(newValue);
                       break;
                   case 1:
                       hp_8.setText(newValue);
                       break;
                   case 2:
                       hp_9.setText(newValue);
                       break;
                   case 3:
                       hp_10.setText(newValue);
                       break;
                   default: sendMessageThroughGUI("ERROR");
               }

           }
        }

    }

    public void changePlayerHP(){
        playerHP_1.setText(String.valueOf(gameController.getPlayerHP(1)));
        playerHP_2.setText(String.valueOf(gameController.getPlayerHP(2)));
    }


    public int getHPForCard(int index, Zone zone){

        if(zone == Zone.HAND){
            return gameController.getHPforCardHand(index);
        } else if (zone == Zone.PLAYER_BOARD){
            return gameController.getHPforCardBoard(index, 1);
        } else{
            return gameController.getHPforCardBoard(index, 2);
        }
    }

    //Avgör i updateboard vilken slots som ritas som "min sida"
    public void setLocalRole(PlayerID role) {
        System.out.println("(GUI) setLocalRole anropad med " + role + " på instance " + this);
        this.localRole = role;
    }

    /**
     * Metoden ska kunna kolla om det är den lokala (spelaren som kör programmet på sin dator just nu) spelarens tur,
     * och sedan returnerar den en boolean baserat på om det är deras tur eller inte.
     * @return En boolean som säger ja eller nej när en spelare klickar och frågar om det är deras tur.
     */
    public boolean isLocalPlayersTurn() {
        return gameController.getGameState().getCurrentPlayerId() == localRole;
    }

    // Ska anropas när det är din tur att välja kort i draft. Utan denna kan spelaren aldrig klicka på ett kort
    public void enableDraftPicking() {
        isDraftTurn = true;
    }

    /**
     * Uppdaterar PickCardScreen så att kort som inte finns kvar i poolen
     * visas med baksidan vilket menas, någon har valt dem
     *
     * Kortens ID hämtas från userData (sätts av bindCardsToView).
     * Om kortets ID inte finns i remainingCardIds då betyder det någon har valt det.
     *
     * @param remainingCardIds id för kort som FORTFARANDE finns i poolen
     * @author Leo
     */
    public void updateDraftPool(java.util.HashSet<Integer> remainingCardIds) {
        // scene fältet är inte satt på den nya GUIManager instansen (kopieras inte via setStage)
        // Hämta scenen från stage istället därför det blev problem
        Scene currentScene = (stage != null) ? stage.getScene() : scene;
        if (currentScene == null) return;

        for (int i = 0; i <= 11; i++) {
            ImageView view = (ImageView) currentScene.lookup("#card_" + i);
            if (view == null) continue;

            Card card = (Card) view.getUserData();
            if (card == null) continue;

            if (!remainingCardIds.contains(card.getCardID())) {
                // Kortet är valt, visa baksidan
                view.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));
            }
        }
    }

    public void addPickCardViews(Scene scene){
        pickCardViews.add((ImageView) scene.lookup("#card_0"));
        pickCardViews.add((ImageView) scene.lookup("#card_1"));
        pickCardViews.add((ImageView) scene.lookup("#card_2"));
        pickCardViews.add((ImageView) scene.lookup("#card_3"));
        pickCardViews.add((ImageView) scene.lookup("#card_4"));
        pickCardViews.add((ImageView) scene.lookup("#card_5"));
        pickCardViews.add((ImageView) scene.lookup("#card_6"));
        pickCardViews.add((ImageView) scene.lookup("#card_7"));
        pickCardViews.add((ImageView) scene.lookup("#card_8"));
        pickCardViews.add((ImageView) scene.lookup("#card_9"));
        pickCardViews.add((ImageView) scene.lookup("#card_10"));
        pickCardViews.add((ImageView) scene.lookup("#card_11"));
    }

    public void displayTurnRound(){
        if (turnNumber == null) return;
        turnNumber.setText(String.valueOf(gameController.getGameState().getTurnNumber()));
        //här kan man också skriva ut hur många kort som är tillåtna att placera var runda.
    }

    //behöver en hostgame knapp, här skapar vi då servern

    //samt en joingame knapp och metod där vi här connecttoserver genom gamecontroller metoden jag skapat där

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void joinButtonPressed(MouseEvent event){
        sendMessageThroughGUI("You Pressed Join!");
        try {
            // 1. Byt till PickCardScreen INNAN vi ansluter
            // (så den nya GUIManager instansen är aktiv när server meddelanden börjar komma)
            switchToPickCardScreen();

            // 2. Anslut till servern på localhost, server måste redan vara startad av host
            gameController.connectToServer("Player2");

        } catch (Exception e) {
            e.printStackTrace();
            sendMessageThroughGUI("Kunde inte ansluta till servern: " + e.getMessage());
        }
    }

    public void hostButtonPressed(MouseEvent event){
        sendMessageThroughGUI("You Pressed Host!");
        try {
            // 1. Starta GameServer i en bakgrundstråd så den inte blockerar GUI
            Network.GameServer server = new Network.GameServer();
            new Thread(server::start).start();

            // 2. Vänta lite så servern hinner sätta upp socket lyssnaren
            Thread.sleep(300);

            // 3. Byt till PickCardScreen INNAN vi ansluter
            // (så den nya GUIManager instansen är aktiv när server meddelanden börjar komma)
            switchToPickCardScreen();

            // 4. Anslut som spelare 1, servern svarar med WAITING tills spelare 2 ansluter
            gameController.connectToServer("Player1");

        } catch (Exception e) {
            e.printStackTrace();
            sendMessageThroughGUI("Kunde inte starta servern: " + e.getMessage());
        }
    }

    public void playerPressed(MouseEvent event) {
        String id = event.getPickResult().getIntersectedNode().getId();
        if(isLocalPlayersTurn() && attackCardPicked){
           if(id == enemyIcon.getId()){
               enemyIcon.setImage(new Image(getClass().getResource("/ProfileMan2UPSET.png").toExternalForm()));
               gameController.attackPlayer(cardToAttackWith);
               //gameController.addMassageInGui(5, );
               attackCardPicked = false;
               attackCardPicked = false;


           }
        }
    }

    public void resetPlayerIcons(){
        enemyIcon.setImage(new Image(getClass().getResource("/ProfileMan2.png").toExternalForm()));

    }

    public void onMouseMoveOnCardArea(MouseEvent event){
        ImageView card = (ImageView) event.getSource();
        Image img = card.getImage();

        String url = (img != null) ? img.getUrl() : null;

        boolean backside = url != null && url.contains("CardBACKSIDE.png");

        if (gameController.getGameState().getPhase() == GamePhase.DRAFT){
            validChoice = true;
            if (gameController.getCurrentPlayerId() == PlayerID.PLAYER_TWO || backside) {
                validChoice = false;
            }
        }

        if (gameController.getGameState().getPhase() == GamePhase.PLAY){
            validChoice = true;

            String id = card.getId();
            String[] splitID = id.split("_");
            int idInt = Integer.parseInt(splitID[1]);

            try {
                Card chosenCard = gameController.getGameState().getBoard().getCard(localRole, idInt);

                if (gameController.getGameState().getCardsPlayedThisTurn() >= gameController.getGameState().getMaxCardsToPlayPerTurn()) {
                    validChoice = false;
                }

                if (chosenCard.getAsleep()) {
                    validChoice = false;
                }

            } catch (NullPointerException n){ return;}
        }

        if (validChoice) {
            card.setStyle("-fx-effect: dropshadow(gaussian, green, 10, 0.7, 0, 0);");
        } else card.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.7, 0, 0);");

    }
    public void onMouseExitCardArea(MouseEvent event){

        ImageView card = (ImageView) event.getSource();

        card.setStyle("-fx-effect: dropshadow(gaussian, transparent, 0, 0.0, 0, 0);");
    }
    private void createBordersForPickCards() {

        for (ImageView card : pickCardViews) {
            card.setStyle("-fx-border-color: transparent;" + "-fx-border-width: 3;");
        }
    }
}