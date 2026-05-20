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
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
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
    private Map<Zone, ImageView[]> zoneMap = new HashMap<>();
    private ImageView[] views;
    private boolean playerOnesTurn = true;
    private Card cardToAttack;
    private Card cardToAttackWith;
    private boolean cardFromHandPicked = false;
    private boolean yourTurnToPickCard = true;

    private ArrayList<ImageView> pickCardViews = new ArrayList<>();

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
     * Den ska byta menyn till game over menyn. Tror jag saknar något, för stage blir lika med null.
     * Antog att ni sätter stagen någonstans för att vi ska kunna byta senare men variabeln verkar alltid vara null?
     * Om jag fattat rätt efter typ 15 minuter av läsning så ska stage följa med de olika stages vi bytar till.
     * Så varje stage vi bytar till ska sparas i variabeln stage så vi inte alltid behöver en mouse event för att byta stage
     *
     * @author Jim Ström
     */
    public void switchToGameOverMenu() {
       /* try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource("GameOverScreen.fxml")
            );

            Parent root = loader.load();

            GUIManager controller = loader.getController();
            controller.setGameController(gameController);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try{
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GameOverScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void switchToGameOverBUTTON(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GameOverScreen.fxml"));
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

            controller.addPickCardViews(scene);

            gameController.bindCardsToView(controller.getCardImageView(scene));
            //Denna under ska kunnas ta bort om vi kopplar dem rätt.
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
            gameController.startPlayPhase();

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
        if (!isLocalPlayersTurn()) {
            return;
        }

        if(isLocalPlayersTurn()) {
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
        /**if (gameController.isMultiplayer()) {
            if (!isDraftTurn)
                return; //inte din tur, behövs för när vi kör multiplayer
            //beöver typ samma som nedan så vi har en för single en för multiplayer
        }
        */
        if (yourTurnToPickCard == false) return;
        ImageView clickedCard = (ImageView) event.getSource();
        Card card = (Card) clickedCard.getUserData();

        for (int i = 0; i < selectedCardsInPickCardphase.size(); i++){
            if (selectedCardsInPickCardphase.get(i) == clickedCard) {
                sendMessageThroughGUI("Card already chosen! Pick another card.");
                System.out.println("Card already chosen! Pick another card.");
                return;
            }
        }
        String hpID = event.getPickResult().getIntersectedNode().getId();
        String[] splitID = hpID.split("_");
        int hpIDInt = Integer.parseInt(splitID[1]);

        selectedCardsInPickCardphase.add(clickedCard);
        clickedCard.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));
        changeHP(hpIDInt, " ", null);

        gameController.addCardToPlayerOne(card);
        System.out.println("card added to player 1 deck");
        switchTurnLabelInPickCard();

        yourTurnToPickCard = false;
        opponentChooseCardInSinglePayer();
    }

    /**
     * Okej detta blir ett långt javadoc men det behövs nog för att förklara denna metod.
     *
     * Metoden hanterar motståndarens kortval i singleplayer under draft-fasen.
     * Metoden körs efter att spelaren själv har valt ett kort och ansvarar för att simulera att en dator väljer själv.
     *
     * Processen styrs av två tidsfördröjningar (PauseTransition) som separerar logiken i två steg.
     *
     * Sidenote om PauseTransition: Jag vill fördröja valet av kort och "animationerna" för att det ska vara lite mer äkta
     * och lättare för användaren att förstå vad som händer så inte allting händer på ett ögonblick.
     * Och man kan inte använde thread.sleep för då hänger hela programmet sig. Skillanden med thread.sleep och pausetransition är att
     * thread.sleep får hela trådan att pause och det vill vi inte. PauseTransition är mer som en fördröjning av en aktivtet på javaFX tråden.
     * Med det kan vi schemalägga upgifter.
     *
     * 1. Pick (fördröjning innan motståndaren väljer kort)
     *    - Skapar en artificiell paus för att simulera "tänkandet".
     *    - Efter fördröjningen skannas alla kort i pickCardViews.
     *    - En lista av tillgängliga kort byggs genom att filtrera bort redan valda kort
     *      (selectedCardsInPickCardphase).
     *    - Ett slumpmässigt kort väljs från den kvarvarande listan.
     *    - Om inga kort återstår avslutas metoden och turen återgår till spelaren.
     *
     * 2. Kortval och uppdatering av spelstatus
     *    - Det valda ImageView-objektet markeras som valt och läggs till i selectedCardsInPickCardphase.
     *    - Kortets visuella representation byts till baksidan (CardBACKSIDE.png) för att indikera att det är taget.
     *    - Kortets HP-etikett uppdateras via changeHP för att reflektera att kortet inte längre är tillgängligt.
     *    - Kortobjektet hämtas från ImageView via getUserData och skickas till GameController via addCardToOpponent,
     *      vilket lägger till kortet i motståndarens deck och tar bort det från gemensamma kortpoolen.
     *
     * Viktigt:
     * - All faktisk spel-logik (korttilldelning och borttagning från kortpool) hanteras i GameController.
     * - gui-uppdateringar sker stegvis för att undvika att spelaren och motståndaren väljer samtidigt.
     * - pick.play() är det som faktiaskt "startar" det som står inom .setOnFinished.
     * Tänk det lite som en run metod, vi skapar tasken sen senare så startar vi den.
     *
     * @author Erik
     */
    private void opponentChooseCardInSinglePayer() {

        PauseTransition Pick = new PauseTransition(Duration.seconds(1));

        Pick.setOnFinished(e -> {

            ImageView matchedView = null;

            List<ImageView> available = new ArrayList<>();

            for (ImageView view : pickCardViews) {
                if (!selectedCardsInPickCardphase.contains(view)) {
                    available.add(view);
                }
            }

            if (available.isEmpty()) {
                yourTurnToPickCard = true;
                return;
            }

            int randomIndex = (int) (Math.random() * available.size());
            matchedView = available.get(randomIndex);

            if (matchedView == null) {
                yourTurnToPickCard = true;
                return;
            }

            Card card = (Card) matchedView.getUserData();

            selectedCardsInPickCardphase.add(matchedView);

            matchedView.setImage(new Image(getClass().getResource("/CardBACKSIDE.png").toExternalForm()));

            String cardID = matchedView.getId();
            String[] splitID = cardID.split("_");
            int hpIDInt = Integer.parseInt(splitID[1]);

            changeHP(hpIDInt, " ", null);

            gameController.addCardToOpponent(card);

            switchTurnLabelInPickCard();
        });
        Pick.play();
        System.out.println("Card added to opponents deck");
        yourTurnToPickCard = true;
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

    public void sendMessageToEventLog(String message){

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

    public void switchTurnLabelInPickCard(){

        if(playerOnesTurn == true){
            pickCardTurn.setText("Player 1");

        } else{
            pickCardTurn.setText("Player 2");
        }
    }

    public void showWaiting() {
        System.out.println("Väntar...");
        // Ska visa text som säger att man väntar, t.ex eventloggen eller en ny label
    }

    public void enableCardButtons() {
        isYourTurn = true;
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
                Board board = gameState.getBoard();

                cardToAttackWith = board.getCard(PlayerID.PLAYER_ONE, attackerIndex);
                cardToAttack = board.getCard(PlayerID.PLAYER_TWO, defenderIndex);

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

    //behöver en hostgame knapp, här skapar vi då servern

    //samt en joingame knapp och metod där vi här connecttoserver genom gamecontroller metoden jag skapat där
}