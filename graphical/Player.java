package graphical;

public class Player {

  private String playerName = null;
  private int score;
  private int id;
  //For internal purposes -- maybe move to game
  private int rank;
  private String sceneName = "";
  private String dice;
  private boolean hasRole;
  private boolean isMainActor = false;

  private String locationName = "";

  public Player() {
  }

  public Player(String playerName, int id, String dice) {
    this.playerName = playerName;
    this.score = 0;
    this.rank = 1;
    this.id = id;
    this.dice = dice;
    hasRole = false;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String name) {
    this.playerName = name;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDice() {
    return dice;
  }

  public boolean getHasRole() {
    return hasRole;
  }

  public void setHasRole(boolean hasRole) {
    this.hasRole = hasRole;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public void setIsMainActor(boolean isMainActor) {
    this.isMainActor = isMainActor;
  }

  public boolean getIsMainActor() {
    return this.isMainActor;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getSceneName() {
    return sceneName;
  }

  public void setSceneName(String sceneName) {
    this.sceneName = sceneName;
  }



}