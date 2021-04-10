package graphical;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import logic.*;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

public class GameViewController implements Initializable {

  public Game game;

  private ArrayList<Player> players = new ArrayList<Player>();
  private int cPlayer = 0;

  private boolean actionTaken = false;

  ParseXMLView parser;

  private double boardWidth = 1066;
  private double boardHeight = 800;

  @FXML
  private HBox root;

  @FXML
  private TableView leaderboard;
  private TableColumn<String, Player> playerColumn;
  private TableColumn<String, Player> scoreColumn;

  @FXML
  private Label nameLabel, moneyLabel, creditLabel, rankLabel, locationLabel, practiceLabel;

  @FXML
  private VBox moveActionsContainer, workActionsContainer;

  @FXML
  private Pane boardView;

  @FXML
  private VBox movePane, rankUpPane, workPane;

  @FXML
  private TextField rankInput;

  private ArrayList<Button> dice = new ArrayList<Button>();
  private HashMap<String, ImageView> cards = new HashMap<>();
  private ArrayList<ImageView> shots = new ArrayList<>();

  WindowController wc;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    playerColumn = new TableColumn("Player");
    playerColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
    playerColumn.setResizable(false);
    playerColumn.setSortable(false);
    playerColumn.setEditable(false);
    playerColumn.setReorderable(false);


    scoreColumn = new TableColumn<>("Score");
    scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
    scoreColumn.setResizable(false);
    scoreColumn.setSortable(false);
    scoreColumn.setEditable(false);
    scoreColumn.setReorderable(false);


    leaderboard.getColumns().add(playerColumn);
    leaderboard.getColumns().add(scoreColumn);
  }

  public void init(Game game, String[] players, String[] diceSelection, WindowController wc) {
    this.game = game;
    this.wc = wc;
    this.parser = ParseXMLView.getInstance();

    for (int i = 0; i < players.length; i++) {
      Player p = new Player(players[i], i, diceSelection[i]);
      this.players.add(p);
      leaderboard.getItems().add(this.players.get(i));

      Button b = new Button();
      ImageView iv = new ImageView(getClass().getResource("resources/dice/" + diceSelection[i] + game.getStartingRank() + ".png").toExternalForm());
      b.setPadding(Insets.EMPTY);
      b.setGraphic(iv);
      b.setId("diceTest");
      b.setPrefSize(calcBoardX(40), calcBoardY(40));
      b.relocate(calcBoardX(parser.getBoardArea("Trailers")[0] +
              ((i % 4) * 48)), calcBoardY(parser.getBoardArea("Trailers")[1] + (i / 4) * 48));
      dice.add(b);
      boardView.getChildren().add(b);
    }
    this.actionTaken = this.game.manageCommandUI(new Command(CommandType.CALCSCORE, null), this.actionTaken);


    playerColumn.setPrefWidth(leaderboard.getWidth() / 2);
    scoreColumn.setPrefWidth(leaderboard.getWidth() / 2 - 1);

    this.boardHeight = boardView.getHeight();
    this.boardWidth = boardView.getWidth();

    ArrayList<String> locations = parser.getRoomNames();
    for (int i = 0; i < locations.size(); i++) {
      if (!((locations.get(i).equals("Trailers"))||(locations.get(i).equals("Casting Office")))) {
        ImageView image = new ImageView(getClass().getResource("resources/CardBack.jpg").toExternalForm());
        image.setPreserveRatio(false);
        image.setX(calcBoardX(parser.getBoardArea(locations.get(i))[0]));
        image.setY(calcBoardY(parser.getBoardArea(locations.get(i))[1]));
        image.setFitWidth(calcBoardX(parser.getBoardArea(locations.get(i))[3]));
        image.setFitHeight(calcBoardY(parser.getBoardArea(locations.get(i))[2]));
        cards.put(locations.get(i), image);
        boardView.getChildren().add(image);
      }
    }

    startTurn(cPlayer);

    disablePane(rankUpPane);
    disablePane(workPane);

  }

  private void enablePane(Pane pane) {
    pane.setMouseTransparent(false);
    pane.setOpacity(1.0);
  }

  private void disablePane(Pane pane) {
    pane.setMouseTransparent(true);
    pane.setOpacity(0.5);
  }

  private void startTurn(int id) {
    this.cPlayer = id;
    this.actionTaken = false;

    disablePane(rankUpPane);
    disablePane(workPane);

    actionTaken = game.manageCommandUI(new Command(CommandType.WHO, null), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.ROOMSTAT, null), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.WHERE, null), actionTaken);
  }

  private void manageMoveButtons(String[] availableLocations) {
    moveActionsContainer.getChildren().clear();
    for (int i = 0; i < availableLocations.length; i++) {
      String loc = availableLocations[i];
      if (loc != null) {
        Button moveButton = new Button("Move to " + loc);
        moveButton.setId("button");
        moveButton.setOnAction(click -> {
          move(loc);
        });
        moveActionsContainer.getChildren().add(moveButton);
      }
    }
    moveActionsContainer.setSpacing(8);
  }

  private void move(String location) {
    actionTaken = game.manageCommandUI(new Command(CommandType.MOVE, location), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.ROOMSTAT, null), actionTaken);
  }

  private void manageWorkButtons(ArrayList<Role> roles) {
    workActionsContainer.getChildren().clear();
    if (roles != null) {
      for (int i = 0; i < roles.size(); i++) {
        String roleName = roles.get(i).getRoleName();
        Button takeRoleButton = new Button("Take role: \"" + roles.get(i).getRoleName() + "\"");
        takeRoleButton.setId("button");
        takeRoleButton.setOnAction(click -> {
          takeRole(roleName);
        });
        workActionsContainer.getChildren().add(takeRoleButton);
      }
    } else {
      Button actButton = new Button("Act");
      actButton.setId("button");
      actButton.setOnAction(click -> {
        act();
      });
      workActionsContainer.getChildren().add(actButton);

      Button rehearseButton = new Button("Rehearse");
      rehearseButton.setId("button");
      rehearseButton.setOnAction(click -> {
        rehearse();
      });
      workActionsContainer.getChildren().add(rehearseButton);
    }
    workActionsContainer.setSpacing(8);
  }

  private void act() {
    actionTaken = game.manageCommandUI(new Command(CommandType.ACT, null), actionTaken);
    actionTaken = this.game.manageCommandUI(new Command(CommandType.ROOMSTAT, null), actionTaken);
    disablePane(workPane);

  }

  private void rehearse() {
    actionTaken = game.manageCommandUI(new Command(CommandType.REHEARSE, null), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.WHO, null), actionTaken);
  }

  private void takeRole(String roleName) {
    actionTaken = game.manageCommandUI(new Command(CommandType.TAKEROLE, roleName), actionTaken);
  }

  @FXML
  private void rankChangeMoney() {
    actionTaken = game.manageCommandUI(new Command(CommandType.RANKUP, "money", rankInput.getText()), actionTaken);
  }

  @FXML
  private void rankChangeCredit() {
    actionTaken = game.manageCommandUI(new Command(CommandType.RANKUP, "credit", rankInput.getText()), actionTaken);
  }

  @FXML
  private void end() {
    actionTaken = game.manageCommandUI(new Command(CommandType.END, null), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.CALCSCORE, null), actionTaken);
    enablePane(movePane);
    cPlayer = (cPlayer + 1) % players.size();
    startTurn(cPlayer);
  }

  public int getSetupNumPlayers() {
    return 0;
  }

  public String getSetupPlayerName(int i) {
    return "";
  }

  public void setInvalidState(InvalidState state) {
    showPopUp("Error", state.toString(), false);

  }

  public void showIntro() {

  }

  public Command getActionInput(String name) {
    return new Command(CommandType.WHERE, null);
  }

  public void showPlayerState(String name, int money, int credit, int practiceCounter, int rank, String currentRole, String roleDescriptor, boolean isMainActor) {
    nameLabel.setText("Name: " + name);
    moneyLabel.setText("Money: " + money);
    creditLabel.setText("Credit: " + credit);
    practiceLabel.setText("Practice Chips: " + practiceCounter);
    rankLabel.setText("Rank: " + rank);

    players.get(cPlayer).setHasRole(!currentRole.isEmpty());
    players.get(cPlayer).setRank(rank);
    players.get(cPlayer).setIsMainActor(isMainActor);
  }

  public void updateLeaderboard(ArrayList<Integer> score){

    Comparator<Player> comparator = null;

    for (int i=0; i<score.size(); i++) {
      players.get(i).setScore(score.get(i));
      comparator = Comparator.comparingInt(Player::getScore);

      leaderboard.getItems().setAll(players);

    }
    FXCollections.sort(leaderboard.getItems(), comparator.reversed());
  }

  public void movePlayer(String requestLoc, String name) {
    actionTaken = game.manageCommandUI(new Command(CommandType.WHERE, null), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.ROOMSTAT, null), actionTaken);

    movePlayerIntoRoom(cPlayer, requestLoc, false);
    disablePane(movePane);
  }

  public void movePlayerIntoRoom(int player, String requestLoc, Boolean wrapScene) {

    actionTaken = game.manageCommandUI(new Command(CommandType.WHERE, null), actionTaken);
    actionTaken = game.manageCommandUI(new Command(CommandType.ROOMSTAT, null), actionTaken);

    ArrayList<Integer> playersInRoom = new ArrayList<>();

    for (int i = 0; i < players.size(); i++) {
      if (requestLoc.equals(players.get(i).getLocationName())) {
        playersInRoom.add(i);
      }
    }

    Button currentDie;
    switch(requestLoc) {
      case "Trailers":
        currentDie = dice.get(cPlayer);
        currentDie.relocate(calcBoardX(parser.getBoardArea("Trailers")[0] +
                ((cPlayer % 4) * 48)), calcBoardY(parser.getBoardArea("Trailers")[1] + (cPlayer / 4) * 48));
        disablePane(movePane);
        break;

      case "Casting Office":
        currentDie = dice.get(cPlayer);
        currentDie.relocate(calcBoardX(parser.getBoardArea("Casting Office")[0] +
                ((cPlayer % 4) * 48)), calcBoardY(parser.getBoardArea("Casting Office")[1] + (cPlayer / 4) * 48));
        disablePane(movePane);
        break;


      default:
        int numPlayersInRoomWithoutRole = 0;

        for(int i = 0; i < playersInRoom.size(); i++) {
          if(!(players.get(playersInRoom.get(i)).getHasRole()) || wrapScene) {
            currentDie = dice.get(playersInRoom.get(i));
            currentDie.relocate(calcBoardX(parser.getBoardArea(requestLoc)[0] +
                    ((numPlayersInRoomWithoutRole % 4) * 48)), calcBoardY(parser.getBoardArea(requestLoc)[1] +
                    (numPlayersInRoomWithoutRole / 4) * 48 + parser.getBoardArea(requestLoc)[2]));
            currentDie.toFront();
            numPlayersInRoomWithoutRole++;
          }
        }
        break;

    }
  }

  public void movePlayerIntoRole(String wantedRole, String name) {
    //Move player loc on screen
    //Manage work buttons
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
    if(players.get(cPlayer).getIsMainActor()) {
      String location = players.get(cPlayer).getLocationName();
      double xPos = calcBoardX(parser.getCardCord(wantedRole)[0] + parser.getBoardArea(location)[0] - 2);
      double yPos = calcBoardY(parser.getCardCord(wantedRole)[1] + parser.getBoardArea(location)[1] - 2);
      Button currentDie = dice.get(cPlayer);
      currentDie.relocate(xPos, yPos);
      currentDie.toFront();
    } else {
      double xPos = calcBoardX(parser.getBoardRoles(wantedRole)[0]);
      double yPos = calcBoardY(parser.getBoardRoles(wantedRole)[1]);
      Button currentDie = dice.get(cPlayer);
      currentDie.relocate(xPos, yPos);
    }
    disablePane(movePane);
    disablePane(workPane);
  }

  public void rehearsePlayer(int practiceCounter, String name) {
    //System.out.println("Rehearsed Player");
    showPopUp("Rehearsal!", "You've rehearsed and are rewarded a practice chip!", false);
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
    disablePane(workPane);
  }

  public void succeedAct(int moneyReceived, int creditReceived) {
    showPopUp("Succeeded Act", "You succeeded your act! You received " + moneyReceived + " dollars and "  + creditReceived + " credit", false);
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
    disablePane(workPane);
  }

  public void wrapScene(String sceneName, String roomName) {
    for(int i = 0; i < players.size(); i++) {
      if (players.get(i).getLocationName().equals(roomName)) {
        movePlayerIntoRoom(i, roomName, true);
      }
    }
    cards.get(roomName).setImage(null);
    disablePane(workPane);
  }
  public void thatsAWrap(){
    showPopUp("That's a wrap!", "You have successfully completed the scene!", false);
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
  }

  public void giveMoney(ArrayList<String> names, ArrayList<Integer> moneySum) {
    StringBuilder message = new StringBuilder();
    for (int i = 0; i<names.size(); i++){
      message.append(names.get(i));
      message.append(" has earned: ");
      message.append(moneySum.get(i));
      message.append(" dollars!\n");

    }
    String theMessage = message.toString();
    showPopUp("That's a wrap!", theMessage, false);
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
  }


  public void failAct(int playerRoll, int practiceCounter, int budget) {
    showPopUp("Failed Act", "...Better luck next time!", false);
    disablePane(workPane);
  }

  public void failActExtra(int playerRoll, int practiceCounter, int budget) {
    showPopUp("Failed Act", "Although you've failed, you've won 1 money!", false);
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
    disablePane(workPane);
  }

  public void rankUp(int moneySpent, int i, int rank) {
    showPopUp("Successful Rank Up", "Congratulations, you are now rank " + rank, false);
    this.game.manageCommandUI(new Command(CommandType.WHO, null), this.actionTaken);
    Player p = players.get(cPlayer);
    Button b = dice.get(cPlayer);
    b.setGraphic(new ImageView(getClass().getResource("resources/dice/" +p.getDice() + rank + ".png").toExternalForm()));
  }

  public void rankInfo(Upgrade[] upgrades) {
    //Probably doesn't need to be implemented;
  }

  public void endTurn(String name) {

  }

  public void showPlayerLocation(String name, String roomName, String scene, int id) {
    locationLabel.setText("Location: " + roomName);
    players.get(cPlayer).setLocationName(roomName);
    players.get(cPlayer).setSceneName(scene);
  }

  public void showRoomStat(String[] neighbors,
                           String roomName,
                           String scene,
                           String sceneDescriptor,
                           int sceneNo,
                           int budget,
                           ArrayList<Role> filledRoles,
                           ArrayList<Role> availableRoles,
                           int shotCounter) {

    ImageView roomCard = cards.get(roomName);
    if (roomCard != null && sceneNo > 0) {
      roomCard.setImage(new Image(getClass().getResource("resources/cards/" + parser.getCardImageName(sceneNo)).toExternalForm()));
    }

    int[][] shotLocation = this.parser.getBoardTakes(roomName);

    int numShots = 0;

    for(int i = 0; i < shotLocation.length; i++) {
      if (shotLocation[i][0] > 0) {
        numShots++;
      }
    }

    for(int i = 0; i < shotCounter; i ++){
      int shotIndex = numShots - 1 - i;
      ImageView iv = new ImageView(new Image(getClass().getResource("resources/shot.png").toExternalForm()));
      iv.setFitHeight(calcBoardY(shotLocation[shotIndex][2]));
      iv.setFitWidth(calcBoardX(shotLocation[shotIndex][3]));
      iv.relocate(calcBoardX(shotLocation[shotIndex][0]), calcBoardY(shotLocation[shotIndex][1]));
      boardView.getChildren().add(iv);
      shots.add(iv);
    }

    manageMoveButtons(neighbors);
    availableRoles.removeIf(role -> role.getRequiredRank() > players.get(cPlayer).getRank());
    if (availableRoles.size() > 0 && !players.get(cPlayer).getHasRole()) {
      enablePane(workPane);
      manageWorkButtons(availableRoles);
    } else if (players.get(cPlayer).getHasRole()) {
      enablePane(workPane);
      manageWorkButtons(null);
      disablePane(movePane);
    } else {
      disablePane(workPane);
    }

    if (roomName.equals("Casting Office")) {
      enablePane(rankUpPane);
    } else {
      disablePane(rankUpPane);
    }

  }

  public void endDay() {
    //move dice back
    for(int i= 0; i<players.size(); i++) {
      Button currentDie = dice.get(i);
      currentDie.relocate(calcBoardX(991 + ((i % 4) * 48)), calcBoardY(248 + (i / 4) * 48));
    }

    //remove shot counters
    shots.forEach(shot -> {
      boardView.getChildren().remove(shot);
    });

    //set all cards to blanks
    ArrayList<String> locations = parser.getRoomNames();
    for (int i = 0; i < locations.size(); i++) {
      if (!((locations.get(i).equals("Trailers"))||(locations.get(i).equals("Casting Office")))) {
        ImageView image = new ImageView(getClass().getResource("resources/CardBack.jpg").toExternalForm());
        image.setPreserveRatio(false);
        image.setX(calcBoardX(parser.getBoardArea(locations.get(i))[0]));
        image.setY(calcBoardY(parser.getBoardArea(locations.get(i))[1]));
        image.setFitWidth(calcBoardX(parser.getBoardArea(locations.get(i))[3]));
        image.setFitHeight(calcBoardY(parser.getBoardArea(locations.get(i))[2]));
        cards.put(locations.get(i), image);
        boardView.getChildren().add(image);
      }
    }

    showPopUp("End Of Day", "The day has ended! All players have been moved back to the trailers for the next day!", false);

  }

  public void endGame(String name, int calculateScore) {
    showPopUp("Congratulations!", name + "has won the game with " + calculateScore + " points", true);
  }

  public void showPopUp(String message, String description, Boolean endGame) {
    Popup popup = new Popup();
    VBox popupContent = new VBox();
    popupContent.setAlignment(Pos.TOP_CENTER);
    popupContent.setPrefHeight(200);
    popupContent.setPrefWidth(300);
    popupContent.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 1.0);\n" +
            "-fx-effect: dropshadow( gaussian , rgba(34,22,14,0.4) , 10,0,0,4);\n" +
            "-fx-background-radius: 10 10 10 10;");

    AnchorPane titleBar = new AnchorPane();
    titleBar.setStyle(
            "-fx-background-color: rgba(160, 65, 65, 0.9);\n" +
            "-fx-background-radius: 9 9 0 0;");
    titleBar.setMinHeight(25.0);
    titleBar.setPrefHeight(25.0);
    titleBar.setPrefWidth(200.0);

    Label title = new Label();
    title.setText("    "+message);
    title.setTextAlignment(TextAlignment.CENTER);
    title.setTextFill(Paint.valueOf("WHITE"));
    title.setFont(new Font("System Bold", 14.0));
    titleBar.getChildren().add(title);
    titleBar.setBottomAnchor(title, 0.0);
    titleBar.setLeftAnchor(title, 0.0);
    titleBar.setRightAnchor(title, 0.0);
    titleBar.setTopAnchor(title, 0.0);
    popupContent.getChildren().add(titleBar);

    VBox textContainer = new VBox();
    textContainer.setAlignment(Pos.CENTER);
    popupContent.setVgrow(textContainer, Priority.ALWAYS);
    popupContent.getChildren().add(textContainer);
    popupContent.setMargin(textContainer, new Insets(25));

    Label descriptionLabel = new Label();
    descriptionLabel.setText(description);
    descriptionLabel.setStyle("-fx-text-fill: #c3302e");
    title.setFont(new Font("System Bold", 12.0));
    descriptionLabel.setWrapText(true);
    descriptionLabel.setContentDisplay(ContentDisplay.CENTER);
    textContainer.setVgrow(descriptionLabel, Priority.ALWAYS);
    textContainer.getChildren().add(descriptionLabel);

    VBox bottomBar = new VBox();
    bottomBar.setAlignment(Pos.BOTTOM_CENTER);
    popupContent.setVgrow(bottomBar, Priority.SOMETIMES);

    Button okButton = new Button();
    okButton.setText("OK");
    okButton.setStyle(
            "    -fx-border-width: 0;\n" +
            "    -fx-background-color:rgba(160, 65, 65, 0.9);\n" +
            "    -fx-font-size: 12;\n" +
            "    -fx-text-fill: rgba(255, 255, 255, 1.0);\n" +
            "    -fx-effect: dropshadow( gaussian , rgba(14,2,0,0.3) , 10,0,0,4);");
    bottomBar.setMargin(okButton, new Insets(0, 0, 8, 0));
    okButton.setOnAction(click -> {
      if (!endGame) {
        root.setMouseTransparent(false);
        root.setEffect(null);
        popup.hide();
      } else {
        System.exit(0);
      }
    });

    bottomBar.getChildren().add(okButton);
    popupContent.getChildren().add(bottomBar);
    popup.getContent().add(popupContent);

    Effect blur = new GaussianBlur();

    root.setMouseTransparent(true);
    root.setEffect(blur);

    popup.show(wc.getStage());

  }

  private double calcBoardX(double x) {
    return (boardWidth/1200) * x;
  }

  private double calcBoardY(double y) {
    return (boardHeight/900) * y;
  }
}
