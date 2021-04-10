package graphical;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import logic.InvalidState;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerSetupController implements Initializable {

  int numPlayers;
  int[] tempDiceSelection;
  String[] diceSelection;
  String[] names;
  WindowController wc;

  ArrayList<ImageView> diceImages = new ArrayList<>();
  ArrayList<TextField> tempNames = new ArrayList<>();

  String[] usableDice = new String[]{"b","g", "o", "p", "r", "v", "w", "y"};
  @FXML
  private HBox root;
  @FXML
  VBox playerSetupPane;

  @FXML
  HBox topContainer, bottomContainer;

  public void setWindowController(WindowController wc) {
    this.wc = wc;
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    
  }

  public void init(int numPlayers){
    this.numPlayers = numPlayers;
    diceSelection = new String[numPlayers];
    names = new String[numPlayers];
    tempDiceSelection = new int[numPlayers];

    topContainer.setSpacing(50);
    bottomContainer.setSpacing(50);


    for(int i = 0; i < numPlayers; i++) {

      tempDiceSelection[i] = i;

      VBox containerPane = getContainerPane(i);

      if (i / 4 == 0) {
        topContainer.getChildren().add(containerPane);
      } else {
        bottomContainer.getChildren().add(containerPane);
        bottomContainer.setMargin(containerPane, new Insets(50, 0, 0 , 0));
      }
    }

  }

  public VBox getContainerPane(int id) {
    VBox vbox = new VBox();
    vbox.setId("actionPane");
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.setMaxHeight(300.0);
    vbox.setMaxWidth(200.0);
    vbox.setMinHeight(300.0);
    vbox.setMinWidth(225.0);
    vbox.setPrefHeight(300.0);
    vbox.setPrefWidth(200.0);
    topContainer.setHgrow(vbox, Priority.NEVER);

    AnchorPane titleBar = new AnchorPane();
    titleBar.setId("titleBar");
    titleBar.setMinHeight(25.0);
    titleBar.setPrefHeight(25.0);
    titleBar.setPrefWidth(200.0);

    Label title = new Label();
    title.setText("    Player " + (id + 1));
    title.setTextAlignment(TextAlignment.CENTER);
    title.setTextFill(Paint.valueOf("WHITE"));
    title.setFont(new Font("System Bold", 14.0));
    titleBar.getChildren().add(title);
    titleBar.setBottomAnchor(title, 0.0);
    titleBar.setLeftAnchor(title, 0.0);
    titleBar.setRightAnchor(title, 0.0);
    titleBar.setTopAnchor(title, 0.0);

    vbox.getChildren().add(titleBar);

    Label choosePlayerDiceLabel = new Label("Choose Player Dice");
    choosePlayerDiceLabel.setId("menuLabel");
    choosePlayerDiceLabel.setFont(new Font("System Bold", 12));
    vbox.setMargin(choosePlayerDiceLabel, new Insets(8,16,8,16));

    vbox.getChildren().add(choosePlayerDiceLabel);


    HBox playerDiceSelectionContainer = new HBox();
    playerDiceSelectionContainer.setAlignment(Pos.CENTER);
    playerDiceSelectionContainer.setMinHeight(35.0);
    playerDiceSelectionContainer.setPrefHeight(35.0);
    playerDiceSelectionContainer.setPrefWidth(200.0);
    playerDiceSelectionContainer.setSpacing(8);
    VBox.setMargin(playerDiceSelectionContainer, new Insets(0, 0 ,8, 0));

    Button leftButton = new Button();
    leftButton.setId("menuButton");
    leftButton.setMnemonicParsing(false);
    leftButton.setText("<");
    leftButton.setOnAction(click -> {
      if (tempDiceSelection[id] > 0 && tempDiceSelection[id] <= 7) {
        tempDiceSelection[id] = tempDiceSelection[id] - 1;
        diceImages.get(id).setImage(new Image(getClass().getResource("resources/dice/" + usableDice[tempDiceSelection[id]] + "1.png").toExternalForm()));
      }
    });
    playerDiceSelectionContainer.getChildren().add(leftButton);

    Image diceImage = new Image(getClass().getResource("resources/dice/" + usableDice[tempDiceSelection[id]] +"1.png").toExternalForm());
    ImageView dice = new ImageView(diceImage);
    dice.setFitWidth(30);
    dice.setFitHeight(30);
    dice.setPickOnBounds(true);
    dice.setPreserveRatio(true);
    dice.setId("menuButton");
    diceImages.add(dice);
    playerDiceSelectionContainer.getChildren().add(dice);

    Button rightButton = new Button();
    rightButton.setId("menuButton");
    rightButton.setMnemonicParsing(false);
    rightButton.setText(">");
    rightButton.setOnAction(click -> {
      if (tempDiceSelection[id] >= 0 && tempDiceSelection[id] < 7) {
        tempDiceSelection[id] = tempDiceSelection[id] + 1;
        diceImages.get(id).setImage(new Image(getClass().getResource("resources/dice/" + usableDice[tempDiceSelection[id]] + "1.png").toExternalForm()));
      }
    });
    playerDiceSelectionContainer.getChildren().add(rightButton);

    vbox.getChildren().add(playerDiceSelectionContainer);

    Label choosePlayerNameLabel = new Label("Choose Player Name");
    choosePlayerNameLabel.setId("menuLabel");
    choosePlayerNameLabel.setFont(new Font("System Bold", 12));
    vbox.setMargin(choosePlayerNameLabel, new Insets(0,16,8,16));

    vbox.getChildren().add(choosePlayerNameLabel);

    TextField nameInput = new TextField();
    nameInput.setPromptText("Name");
    vbox.setMargin(nameInput, new Insets(0,16,8,16));
    tempNames.add(nameInput);

    vbox.getChildren().add(nameInput);


    return vbox;
  }

  public void playGame() {
    for (int i = 0; i < numPlayers; i++) {
      names[i] = tempNames.get(i).getText();
      Pattern r = Pattern.compile("^[a-zA-Z0-9 ]*$");
      Matcher m = r.matcher(names[i]);
      if (names[i].isEmpty() || !m.find()) {
        showPopUp("Error!", "Invalid name provided");
        return;
      }
      diceSelection[i] = usableDice[tempDiceSelection[i]];
    }
    wc.switchToGameView(numPlayers, diceSelection, names);
  }


  public void showPopUp(String message, String description) {
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
      root.setMouseTransparent(false);
      root.setEffect(null);
      popup.hide();
    });

    bottomBar.getChildren().add(okButton);
    popupContent.getChildren().add(bottomBar);
    popup.getContent().add(popupContent);

    Effect blur = new GaussianBlur();

    root.setMouseTransparent(true);
    root.setEffect(blur);

    popup.show(wc.getStage());

  }
}
