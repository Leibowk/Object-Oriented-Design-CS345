package graphical;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Game;

import java.io.IOException;

public class WindowController extends Application {

  private FXMLLoader loader;
  private Stage stage;
  private Scene currentScene;
  private String sceneName;
  private GameViewController gameViewController;
  private MainMenuController menuViewController;
  private PlayerSetupController playerSetupViewController;
  private Game game;

  public void init(String[] args) {
    Application.launch(args);
  }

  public void switchScene(String scene){
    this.sceneName = scene;
    this.loader = new FXMLLoader();
    this.loader.setLocation(getClass().getResource("resources/ui/layout/" + scene + ".fxml"));

    HBox root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }

    this.currentScene = new Scene(root);
    this.currentScene.setFill(Color.TRANSPARENT);

    this.stage.setScene(this.currentScene);

    if (scene.equals("game")) {
      this.gameViewController = loader.getController();
    }
    if (scene.equals("menu")) {
      this.menuViewController = loader.getController();
      this.menuViewController.setWindowController(this);
    }

  }

  public void switchToPlayerSetup(int numPlayers){
    this.sceneName = "playerSetup";
    this.loader = new FXMLLoader();
    this.loader.setLocation(getClass().getResource("resources/ui/layout/playerSetup.fxml"));

    Parent root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }

    this.currentScene = new Scene(root);
    this.currentScene.setFill(Color.TRANSPARENT);

    this.stage.setScene(this.currentScene);

    this.playerSetupViewController = loader.getController();
    this.playerSetupViewController.setWindowController(this);
    this.playerSetupViewController.init(numPlayers);

  }

  public void switchToGameView(int numPlayers, String[] dice, String[] names) {
    this.sceneName = "game";
    this.loader = new FXMLLoader();
    this.loader.setLocation(getClass().getResource("resources/ui/layout/game.fxml"));

    Parent root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }

    this.currentScene = new Scene(root);
    this.currentScene.setFill(Color.TRANSPARENT);

    this.stage.setScene(this.currentScene);

    this.gameViewController = loader.getController();
    this.game = new Game(this.gameViewController);
    this.game.setupGame(numPlayers, names);
    this.gameViewController.init(this.game, names, dice, this);
  }

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    switchScene("menu");
    stage.setTitle("Deadwood");
    Image icon = new Image("graphical/resources/deadwoodIcon.png");
    stage.getIcons().add(icon);
    //stage.setResizable(false);
    //stage.setFullScreen(false);
    stage.show();
  }

  public Stage getStage() {
    return this.stage;
  }
}
