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

import java.io.IOException;
import java.util.*;

import static java.lang.String.valueOf;


/**
 * GUIManager fungerar som kopplingslager mellan gui:t (JavaFX) och spel-logik (GameController).
 * Den ansvarar för att byta scener, ta emot användarinput från gui och uppdatera visuella delar.
 * baserat på spelmodellens tillstånd.
 */

public class GUIManager {

    private boolean isYourTurn = true;
    private PlayerID localRole = PlayerID.PLAYER_ONE;
    private boolean isDraftTurn = false;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private GameController gameController;

    @FXML private ImageView hand_0;
    @FXML private ImageView hand_1;
    @FXML private ImageView hand_2;

    @FXML private ImageView enemyIcon;

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

    private ArrayList<ImageView> boardImageViews = new ArrayList<>();

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
    private int cardToAttack;
    private int cardToAttackWith;
    private boolean attackCardPicked = false;
    private boolean cardFromHandPicked = false;

    @FXML
    private TextArea textArea;
    @FXML private Label turnNumber;

    private ArrayList<ImageView> pickCardViews = new ArrayList<>();

    /**
     * Metod som konstruktorn använder för att starta upp FXML-filerna. Anropas av Launch "Bakom kulisserna".
     * @author Elna
     */
    @FXML
    public void initialize(){

    }

    /**
     * Konstruktor som initialiserar GUIManager och skapar en koppling till GameController.
     * Sätter upp grundläggande kommunikation mellan gui:t och spel-logik.
     * @author: Erik, Elna
     */

    public GUIManager(){
        gameController = new GameController();
        gameController.setGuiManager(this);
    }

    /**
     * Byter scen till startskärmen.
     * Laddar FXML, kopplar ny controller till GameController och ersätter aktuell scen i Stage.
     * @Param: event - MouseEvent från knapptryck i GUI
     * @author: Elna, Erik
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
     * Metod som bytar till scen för connect.
     * @author Elna, Erik
     */

    @FXML
    public void switchToConnectScreen(){
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

            gameController.set();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Metod som bytar till gameOver screen, när gameController registrerar vinnare.
     * @author Jim, Elna, Erik
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
            showGameOver(gameController.getGameState().getWinner().getName());

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Byter till gameOver screen när någon avslutar en match innan vinnare utropats.
     * @param event knapptryck i GameBoard
     * @author Elna, Erik
     */

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
     * Byter till skärmen där spelaren väljer kort.
     * Laddar gui, kopplar controller och binder kortdata till ImageView.
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
     * @author: Erik, Elna
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
     * @Param: e - MouseEvent från GUI
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
     * Skickar varning till gui.
     * @param message String som ska användas som text till GUI
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
     * @author: Erik, Elna, Leo
     */

    public void pickedCard(MouseEvent event) {
        ImageView clickedCard = (ImageView) event.getSource();
        Card card = (Card) clickedCard.getUserData();

        String ID = event.getPickResult().getIntersectedNode().getId();
        String[] splitID = ID.split("_");
        int IDInt = Integer.parseInt(splitID[1]);

        if (gameController.isMultiplayer()) {
            if (!isDraftTurn) {
                sendMessageThroughGUI("Vänta på din tur att välja kort!");
                return;
            }
            if (card == null) return;

            isDraftTurn = false;

            clickedCard.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));
            changeHP(IDInt, " ", null);
            displayPickedCardDraft(card.getImagePath(), PlayerID.PLAYER_ONE);

            gameController.sendDraftPick(card.getCardID());
            return;
        }

        if (!isLocalPlayersTurn()){return;}


        System.out.println(IDInt);

       displayPickedCardDraft(card.getImagePath(), gameController.getCurrentPlayerId());
        gameController.chooseCardPhase(IDInt);
    }

    /**
     * Updaterar GUI med ett korts baksida när det varlts i pickCardPhase
     * @param IDInt indexplats på det kort som valts
     * @author Erik
     */
    public void updateGuiAfterCardIsPicked(int IDInt){
        changeHP(IDInt, " ", null);
        ImageView view = pickCardViews.get(IDInt);
        Image newImage = new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm());
        view.setImage(newImage);
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

    /**
     * Sättmetod för GameController
     * @param gameController Ett gamecontroller - objekt
     * @author Erik
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Privat metod som bara används i game-rules screen.
     * @param pane -en panel i game rules screen
     * @author Erik
     */
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

    /**
     * Privat metod som bara används i game-rules screen.
     * @author Erik
     */
    @FXML private void showGeneralRules() {
        showPane(generalRules);
    }

    /**
     * Privat metod som bara används i game-rules screen.
     * @author Erik
     */
    @FXML private void showCardRules() {
        showPane(cardRules);
    }

    /**
     * Privat metod som bara används i game-rules screen.
     * @author Erik
     */
    @FXML private void showPlayerRules() {
        showPane(playerRules);
    }

    /**
     * Privat metod som bara används i game-rules screen.
     * @author Erik
     */
    @FXML private void showEffectsRules(){
        showPane(effectsRules);
    }

    /**
     * Privat metod som bara används i game-rules screen.
     * @author Erik
     */
    @FXML private void showMatchRules(){showPane(matchRules);}


    /**
     * Metod för att avsluta runda i GUI när spelare trycker på knapp.
     * @author Elna, Erik, Jim
     */
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

    /**
     * Lägger till imageViews för spelbräda i en lista för att lättare kunna iterera igenom.
     * @author Elna
     */
    private void addImageViewToList(){
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
     * Metod för att skicka meddelande till  eventlog
     * @param message meddelande som ska visas för spelare
     * @author Erik, Jim
     */
    @FXML
    public void sendMessageToEventLog(String message){
        if (textArea == null) return;
        Platform.runLater(() -> textArea.appendText(message + "\n"));    }

    /**
     * Metod för att lägga imageViews i olika zoner.
     * @author Erik
     */
    public void init() {
        zoneMap.put(Zone.HAND, new ImageView[]{hand_0, hand_1, hand_2});
        zoneMap.put(Zone.PLAYER_BOARD, new ImageView[]{p1board_0, p1board_1, p1board_2, p1board_3});
        zoneMap.put(Zone.OPPONENT_BOARD, new ImageView[]{p2board_0, p2board_1, p2board_2, p2board_3});
    }

    /**
     * Metod för att rendera kort från logik ut i GUI
     * @param zone Vilken zon kortet ska läggas i
     * @param index Vilken indexplats kortet ska ut på
     * @param imagePath Vilken path bilden har som ska renderas
     * @author Elna, Erik
     */
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

    /**
     * byter label i GUI för vilken spelares tur det är att välja kort.
     * @param id - id för spelare vars tur det är
     * @author Elna
     */
    public void switchTurnLabelInPickCard(PlayerID id){

        if(id == PlayerID.PLAYER_ONE){
            pickCardTurn.setText("Player 2");

        } else if(id == PlayerID.PLAYER_TWO) {
            pickCardTurn.setText("Player 1");
        }
    }

    /**
     * Anropas när spelaren anslutit och väntar på att en motståndare ska ansluta.
     *
     * @author Leo
     */
    public void showWaiting() {
        System.out.println("Väntar på motståndare...");
    }

    /**
     * Aktiverar spelarens möjlighet att agera när det blivit deras tur.
     *
     * @author Leo
     */
    public void enableCardButtons() {
        isYourTurn = true;
    }

    /**
     * Ritar om spelarens hand med givna bildvägar.
     * Listan kan ha upp till 3 element. Tomma platser rensar bilden där.
     * HP läses från den lokala modellen som synkats från servern via Controller.
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

    /**
     * Visar vinnaren på spelbrädets befintliga scen.
     * Används av singleplayer-flödet, där vinnaren hämtas från det lokala spelläget.
     *
     * @param name namnet på spelaren som vann
     * @author Leo, Elna
     */
    public void showGameOver(String name) {
        Scene currentScene = (stage != null) ? stage.getScene() : scene;
        if (currentScene == null) return;

        Label label = (Label) currentScene.lookup("#winner");
        if (label != null) {
            label.setText(name);
        }
    }

    /**
     * Hanterar val av kort på motståndaren bräda. Om allt går bra så anropar vi attack metoden. Och uppdaterar GUI
     *
     * @param event - eventtypen som skickas från guit.
     * @author Erik, Elna
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

                cardToAttackWith = attackerIndex;
                cardToAttack = defenderIndex;

                gameController.attackCard(attackerIndex, defenderIndex);

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
     * @author Erik, Elna, Jim
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

    /**
     * Ändrar HP på kort beroende på dess värde i logik
     * @param index -vilket index kort ligger på
     * @param newValue - Nytt värde på HP
     * @param zone - Vilken zon kortet ligger i
     * @author Elna
     */
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

    /**
     * Updaterar HP för spelare i gameBoard
     * @author Elna
     */
    public void changePlayerHP(){
        playerHP_1.setText(String.valueOf(gameController.getPlayerHP(1)));
        playerHP_2.setText(String.valueOf(gameController.getPlayerHP(2)));
    }


    /**
     * Hämtar HP för kort från Kortobjekt i controller
     * @param index - vilken index i array för kort som relevant hp ska hämtas från
     * @param zone - Vilken zon kortet som ska hämtas ligger i
     * @return returnerar HP i int
     * @author Elna
     */
    public int getHPForCard(int index, Zone zone){

        if(zone == Zone.HAND){
            return gameController.getHPforCardHand(index);
        } else if (zone == Zone.PLAYER_BOARD){
            return gameController.getHPforCardBoard(index, 1);
        } else{
            return gameController.getHPforCardBoard(index, 2);
        }
    }

    /**
     * Sätter vilken roll den lokala spelaren har.
     * Avgör i renderingen vilken sida av brädet som ritas som spelarens egen sida.
     *
     * @param role den lokala spelarens roll (PLAYER_ONE eller PLAYER_TWO)
     * @author Leo
     */
    public void setLocalRole(PlayerID role) {
        this.localRole = role;
    }

    /**
     * Metoden ska kunna kolla om det är den lokala (spelaren som kör programmet på sin dator just nu) spelarens tur,
     * och sedan returnerar den en boolean baserat på om det är deras tur eller inte.
     * @return En boolean som säger ja eller nej när en spelare klickar och frågar om det är deras tur.
     * @author Jim
     */
    public boolean isLocalPlayersTurn() {
        return gameController.getGameState().getCurrentPlayerId() == localRole;
    }

    /**
     * Aktiverar kortvalet när det är spelarens tur att välja kort i draft-fasen.
     * Utan detta kan spelaren inte klicka på något kort.
     *
     * @author Leo
     */
    public void enableDraftPicking() {
        isDraftTurn = true;
    }

    /**
     * Uppdaterar PickCardScreen så att kort som inte längre finns i poolen
     * visas med baksidan uppåt, vilket betyder att någon redan har valt dem.
     *
     * Kortens id hämtas från userData som sätts av bindCardsToView. Om ett korts
     * id inte finns i remainingCardIds har det valts och vänds till baksidan.
     *
     * @param remainingCardIds id för de kort som fortfarande finns kvar i poolen
     * @author Leo
     */
    public void updateDraftPool(java.util.HashSet<Integer> remainingCardIds) {
        Scene currentScene = (stage != null) ? stage.getScene() : scene;
        if (currentScene == null) return;

        for (int i = 0; i <= 11; i++) {
            ImageView view = (ImageView) currentScene.lookup("#card_" + i);
            if (view == null) continue;

            Card card = (Card) view.getUserData();
            if (card == null) continue;

            if (!remainingCardIds.contains(card.getCardID())) {
                view.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));
                changeHP(i, " ", null);
            }

        }
    }

    /**
     * Lägger till imageviews till scen i pickCardScreen
     * @param scene scene dessa ska läggas till i
     * @author Erik
     */
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

    /**
     * Visar vilken runda det är i GUI
     * @author Jim
     */
    public void displayTurnRound(){
        if (turnNumber == null) return;
        turnNumber.setText(String.valueOf(gameController.getGameState().getTurnNumber()));
    }

    /**
     * Setter för stage
     * @param stage
     * @author Erik
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Ansluter till en redan startad server som spelare 2.
     * Försöker ansluta först och byter scen till PickCardScreen bara om
     * anslutningen lyckas, så att en misslyckad anslutning inte lämnar spelaren
     * på en tom skärm.
     *
     * @param event MouseEvent från knapptryck i gui
     * @author Leo
     */
    public void joinButtonPressed(MouseEvent event){
        try {
            gameController.connectToServer("Player2");
            switchToPickCardScreen();
        } catch (Exception e) {
            sendMessageThroughGUI("Kunde inte ansluta till servern: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Startar en server och ansluter som spelare 1 (host).
     * Servern startas i en bakgrundstråd så att den inte blockerar gui-tråden.
     * Scenbytet sker innan anslutningen så att den nya GUIManager-instansen är
     * aktiv när servermeddelanden börjar komma in.
     *
     * @param event MouseEvent från knapptryck i gui
     * @author Leo
     */
    public void hostButtonPressed(MouseEvent event){
        try {
            Network.GameServer server = new Network.GameServer();
            new Thread(server::start).start();

            Thread.sleep(300);

            switchToPickCardScreen();
            gameController.connectToServer("Player1");

        } catch (Exception e) {
            e.printStackTrace();
            sendMessageThroughGUI("Kunde inte starta servern: " + e.getMessage());
        }
    }

    /**
     * Hanterar klick på motståndarens spelarikon för en direkt attack.
     * Kräver att det är spelarens tur och att ett attackerande kort redan valts.
     *
     * @param event MouseEvent från klick på ikonen
     * @author Leo, Elna
     */
    public void playerPressed(MouseEvent event) {
        String id = event.getPickResult().getIntersectedNode().getId();

        if (isLocalPlayersTurn() && gameController.isAttackerPicked()) {
            if (id == enemyIcon.getId()) {
                enemyIcon.setImage(new Image(getClass().getResource("/ProfileMan2UPSET.png").toExternalForm()));

                int attackerIndex = gameController.getIndexToCardToAttackWith();
                gameController.attackPlayer(attackerIndex);
                gameController.resetAttackState();
            }
        }
    }

    /**
     * Återställer spelare om han tryckts på under en turn
     * @author Elna
     */
    public void resetPlayerIcons(){
        enemyIcon.setImage(new Image(getClass().getResource("/ProfileMan2.png").toExternalForm()));

    }

    /**
     * Anropas när kort mus rör sig över kort
     * @param event
     * @author Erik, Daniel
     */
    public void onMouseMoveOnCardArea(MouseEvent event) {

        ImageView card = (ImageView) event.getSource();

        card.setScaleX(1.15); 
        card.setScaleY(1.15);
        card.toFront();
        bringHPLabelsToFront();

        Label hpLabel = getHpLabelForCard(card.getId());
             if (hpLabel != null) {
                hpLabel.setScaleX(1.4); 
                hpLabel.setScaleY(1.4);


                double centerX = card.getLayoutX() + (card.getFitWidth() / 2);
                double centerY = card.getLayoutY() + (card.getFitHeight() / 2);



                hpLabel.setTranslateX((hpLabel.getLayoutX() - centerX) * 0.15);
                hpLabel.setTranslateY((hpLabel.getLayoutY() - centerY) * 0.15);
        }

        Image img = card.getImage();

        String url = (img != null) ? img.getUrl() : null;
        boolean backside = url != null && url.contains("CardBACKSIDE.png");

        validChoice = true;

        GameState state = gameController.getGameState();
        if (state == null) {
            return;
        }

        GamePhase phase = state.getPhase();

        if (phase == GamePhase.DRAFT) {

            if (gameController.getCurrentPlayerId() == PlayerID.PLAYER_TWO || backside) {
                validChoice = false;
            }

        } else if (phase == GamePhase.PLAY) {

            String id = card.getId();

            if (id == null || !id.contains("_")) {
                return;
            }

            String[] splitID = id.split("_");

            if (splitID.length < 2) {
                return;
            }

            int idInt;

            try {
                idInt = Integer.parseInt(splitID[1]);
            } catch (NumberFormatException e) {
                return;
            }

            if (id.startsWith("hand_")) {

                if (state.getCardsPlayedThisTurn() >= state.getMaxCardsToPlayPerTurn()) {
                    validChoice = false;
                }

            }

            else if (id.startsWith("p1board_")) {

                Board board = state.getBoard();

                if (board == null) {
                    return;
                }

                Card chosenCard = board.getCard(PlayerID.PLAYER_ONE, idInt);

                if (chosenCard == null) {
                    return;
                }

                if (chosenCard.getAsleep()) {
                    validChoice = false;
                }

                if (chosenCard.getHasAttackedThisTurn()) {
                    validChoice = false;
                }
            }

            else if (id.startsWith("p2board_")) {

                Board board = state.getBoard();

                if (board == null) {
                    return;
                }

                Card chosenCard = board.getCard(PlayerID.PLAYER_TWO, idInt);

                if (chosenCard == null) {
                    return;
                }

                if (gameController.isAttackerPicked()) {
                    validChoice = true;
                } else {
                    validChoice = false;
                }
            }
        }

        if (validChoice) {
            card.setStyle("-fx-effect: dropshadow(gaussian, green, 10, 0.7, 0, 0);");
        } else {
            card.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.7, 0, 0);");
        }
    }

    public void onMouseExitCardArea(MouseEvent event){

        ImageView card = (ImageView) event.getSource();


        card.setScaleX(1.0); 
        card.setScaleY(1.0);
        card.setScaleZ(1.0);
        bringHPLabelsToFront();

        Label hpLabel = getHpLabelForCard(card.getId());
            if (hpLabel != null) {
                hpLabel.setScaleX(1.0);
                hpLabel.setScaleY(1.0);
                hpLabel.setTranslateX(0);
                hpLabel.setTranslateY(0);
            } 

        card.setStyle("-fx-effect: dropshadow(gaussian, transparent, 0, 0.0, 0, 0);");
    }
    private void createBordersForPickCards() {

        for (ImageView card : pickCardViews) {
            card.setStyle("-fx-border-color: transparent;" + "-fx-border-width: 3;");
        }
    }

    /**
     * Metod för att visa kort i diverse spelares hand i pick card phase
     * @param imagePath
     * @param id
     * @author Elna
     */
    public void displayPickedCardDraft(String imagePath, PlayerID id){
        Scene currentScene = (stage != null) ? stage.getScene() : scene;
        if (currentScene == null) return;

        if(id == PlayerID.PLAYER_ONE){

            for (int i = 0; i < 6; i++) {
                ImageView view = (ImageView) currentScene.lookup("#player_" + i);

                if (view.getImage() == null){
                    view.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
                    break;
                }
            }
        } else{
            for (int i = 0; i < 6; i++) {
                ImageView view = (ImageView) currentScene.lookup("#enemy_" + i);

                if (view.getImage() == null){
                    view.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
                    break;
                }
            }
        }

    }

    /**
     * Uppdaterar texten som visar vems tur det är att välja kort i draft-fasen.
     *
     * @param text texten som ska visas, till exempel "Player 1" eller "Player 2"
     * @author Leo
     */
    public void updatePickCardTurn(String text) {
        if (pickCardTurn != null) {
            pickCardTurn.setText(text);
        }
    }

    /**
     * Byter till GameOverScreen och visar vinnaren i multiplayer.
     * Vinnarnamnet kommer från servern som parameter eftersom det lokala
     * spelläget inte har någon vinnare satt i multiplayer.
     *
     * @param winnerName namnet på vinnaren, skickat av servern
     * @author Leo
     */
    public void showGameOverMultiplayer(String winnerName) {
        try {
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

            Label label = (Label) scene.lookup("#winner");
            if (label != null) {
                label.setText(winnerName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Metod för att flytta alla HP labels framför andra komponenter efter toFront metoden som körs vid hover
 * @author Daniel
 */
    private void bringHPLabelsToFront() {

    Label[] hpLabels = {hp_0, hp_1, hp_2, hp_3, hp_4, hp_5, hp_6, hp_7, hp_8, hp_9, hp_10, hp_11, playerHP_1, playerHP_2};
    

    for (Label hp : hpLabels) {
        if (hp != null) {
            hp.toFront();
            hp.setMouseTransparent(true);   // Stäng av mus-kollision för text 
        }
    }
}


/**
     * Hjälpmetod för att hitta vilken HP-text (Label) som tillhör en specifik kortplats (ImageView).
     * Används för att veta vilken siffra som ska förstoras när ett specifikt kort hovras.
     * @author Daniel
     */
private Label getHpLabelForCard(String cardId) {
    if (cardId == null || !cardId.contains("_")) return null;
    
    String[] splitID = cardId.split("_");
    int index;
    try {
        index = Integer.parseInt(splitID[1]);
    } catch (NumberFormatException e) {
        return null;
    }

    if (cardId.startsWith("hand_")) {   // Jämför prefix
        switch(index) {
            case 0: return hp_0; 
            case 1: return hp_1; 
            case 2: return hp_2;
        }
    } else if (cardId.startsWith("p1board_")) {
        switch(index) {
            case 0: return hp_3; 
            case 1: return hp_4; 
            case 2: return hp_5; 
            case 3: return hp_6;
        }
    } else if (cardId.startsWith("p2board_")) {
        switch(index) {
            case 0: 
            return hp_7; 
            case 1: return hp_8;
            case 2: return hp_9; 
            case 3: return hp_10;
        }
    } else if (cardId.startsWith("card_")) { // För PickCardScreen 
        switch(index){
            case 0: return hp_0; 
            case 1: return hp_1;
            case 2: return hp_2;
            case 3: return hp_3;
            case 4: return hp_4; 
            case 5: return hp_5; 
            case 6: return hp_6; 
            case 7: return hp_7;
            case 8: return hp_8; 
            case 9: return hp_9; 
            case 10: return hp_10; 
            case 11: return hp_11;
        }
    }
    return null;
}


}