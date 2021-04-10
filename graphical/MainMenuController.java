package graphical;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

  @FXML
  public Label numLabel;

  public WindowController wc;

  public void setWindowController(WindowController wc) {
    this.wc = wc;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }

  @FXML
  public void clickRight() {
    int currentNum = Integer.parseInt(numLabel.getText());
    if (currentNum >= 2 && currentNum < 8) {
      currentNum += 1;
    }
    numLabel.setText("" + currentNum);
  }

  @FXML
  public void clickLeft() {
    int currentNum = Integer.parseInt(numLabel.getText());
    if (currentNum > 2 && currentNum <= 8) {
      currentNum -= 1;
    }
    numLabel.setText("" + currentNum);
  }

  @FXML
  public void play() {
    wc.switchToPlayerSetup(Integer.parseInt(numLabel.getText()));
  }


}
