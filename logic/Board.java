package logic;

import java.util.Random;
import java.awt.Point;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;


public class Board {
    private Room board[][] = new Room[4][3];
    private HashMap<Integer, Point> playerLocations;
    private int roomsFinished = 0;
    private int numRooms;
    private Card theCards[];
    private int numCards = 40;
    private int cardIndex = 0;
    private NodeList set;
    private NodeList card;
    private Document doc = null;
    private Document doc2 = null;
    private parseXML parsee = new parseXML();


    // Setting up the board at the start of the game
    public Board(int numPlayers) {
        int k = 0;

        // Default value of rooms -- should pull from xml later
        this.numRooms = 10;

        // Putting the initial Rooms onto the board
        try {

            doc = parsee.getDocFromFile("src/logic/board.xml");
            doc2 = parsee.getDocFromFile("src/logic/cards.xml");
            Element root = doc.getDocumentElement();
            Element root2 = doc2.getDocumentElement();
            set = root.getElementsByTagName("set");
            card = root2.getElementsByTagName("card");

        } catch (Exception e) {
            System.out.println("Error = " + e);
        }

        // Getting the cards
        theCards = new Card[numCards];
        for (int b = 0; b < numCards; b++) {
            theCards[b] = new Card(card, b);
        }
        shuffleCards();

        // Setting up the board        
        int q= 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // putting rooms in normal order
                board[i][j] = new Room(set, q);
                q++;
            }
        }

        giveCardsToRooms();
        playerLocations = new HashMap<>();
        resetPlayers(numPlayers);
    }

    //get players in room 
    public int[] getPlayersInRoom(int player){
        int[] playersInRoom;
        int num=0;
        Point theLocation = getPlayerLocation(player);
        //get size of new array
        for(int i=0; i<playerLocations.size();i++){
            Point theirLoc = getPlayerLocation(i);
            if(theLocation.equals(theirLoc)){
                num++;
            }
        }
        playersInRoom = new int[num];
        num=0;
        //put players in array
        for(int i=0; i<playerLocations.size();i++){
            Point theirLoc = getPlayerLocation(i);
            if(theLocation.equals(theirLoc)){
                playersInRoom[num]=i;
                num++;
            }
        }
        return playersInRoom;
    }
    
    public Upgrade[] getRankUpInfo(){
        Point casting = findRoom("Casting Office");
        return board[(int) casting.getX()][(int) casting.getY()].getUpgrades();
    }

    // Function to finish a room.
    //add to rooms finished
    public void finishRoom(int player) {
        Point theLocation = getPlayerLocation(player);
        Room currentRoom = board[(int) theLocation.getX()][(int) theLocation.getY()];
        currentRoom.scrapCard();
        roomsFinished++;
    }
    
    public void successfulAct(int player){
        Point theLocation = getPlayerLocation(player);
        Room currentRoom = board[(int) theLocation.getX()][(int) theLocation.getY()];
        currentRoom.increaseShotCounter();
    }

    //returns if the room of a certain player has a current scene
    public boolean roomFinished(int player){
        Point theLocation = getPlayerLocation(player);
        Room room = board[(int) theLocation.getX()][(int) theLocation.getY()];
        return room.roomFinished();
    }

    //returns room of current player
    //may not need to be used
    public Room getRoom(int player){
        Point theLocation = getPlayerLocation(player);
        return board[(int) theLocation.getX()][(int) theLocation.getY()];
    }

    public boolean dayShouldEnd() { return (numRooms - roomsFinished) <= 1; }

    //Provides the neighbors in the order of left, below, right, above. If room has no neighbor a null is provided. 
    public String[] getNeighbors(int player){
        Point theLocation = getPlayerLocation(player);
        return board[(int)theLocation.getX()][(int)theLocation.getY()].getNeighbors();
    }

    //method to move player if possible. 
    public boolean playerMove(int player, String location){
        Point theLocation = getPlayerLocation(player);
        String neighbors[] = board[(int)theLocation.getX()][(int)theLocation.getY()].getNeighbors();
        for(int i=0; i<4; i++){
            if(neighbors[i] != null && neighbors[i].equals(location)){
                updatePlayerLoc(location, player);
                return true;
                }
        }
        return false;
    }

    //get array of all roles in same room as player.
    public Role[] getRoleInfo(int player){
        Point theLocation = getPlayerLocation(player);
        return board[(int)theLocation.getX()][(int)theLocation.getY()].getRoles();
    }

    //check if the role exists and if not filled. if so provides role. else returns null
    public Role provideRole(int player, String role){
        Point theLocation = getPlayerLocation(player);
        Role[] roles = board[(int)theLocation.getX()][(int)theLocation.getY()].getRoles();
        if (roles != null) {
            for(int i=0; i<roles.length; i++){
                if(roles[i].getRoleName().equals(role)&& !roles[i].isFilled()){
                    return roles[i];
                }
            }
        }
        return null;
    }

    //Returns true if player located in casting office
    public boolean canRankUp(int player){
        Point theLocation = getPlayerLocation(player);
        if(theLocation.equals(findRoom("Casting Office"))){
            return true;
        }
        return false;
    }

    public int getNumRoomsFinished() {
        return roomsFinished;
    }

    public void resetForDay() {
        resetPlayers(playerLocations.size());
        this.roomsFinished = 0;
        resetRooms();
    }

    

    //Private internal helper functions
    //
    //
    //
    //
    
    //Function to help reset day. Goes through rooms and sets all off-card roles as available.
    private void resetRooms(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].resetSup();
                board[i][j].resetShotCounter();
            }
        }
        giveCardsToRooms();
    }

    //Function to shuffle all the cards.
    private void shuffleCards(){
        Random random = new Random();
        for (int j = theCards.length - 1; j >= 0; j--) {
              int n = random.nextInt(j + 1);
              Card temp = theCards[j];
              theCards[j] = theCards[n];
              theCards[n] = temp;
        }
    }

    //getting player location internally
    private Point getPlayerLocation(int i){
        return this.playerLocations.get(i);
    }

    private void giveCardsToRooms() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // if room isn't casting office or trailers give it a card
                String name = board[i][j].getRoomName();
                if ((!name.equals("Casting Office")) && !name.equals("Trailers")) {
                    board[i][j].setCard(theCards[cardIndex]);
                    cardIndex++;
                }
            }
        }
    }


    //Puts player in proper room
    private void updatePlayerLoc(String roomName, int player){
        Point location = findRoom(roomName);
        playerLocations.put(player, location);
    
    }

    //Moves all player locations back to the trailers
    private void resetPlayers(int numPlayers){
        Point trailers = findRoom("Trailers");
        for(int k=0; k<numPlayers; k++){
            playerLocations.put(k,trailers);
        }
    }

    //Function to find room by String name. returns point.
    private Point findRoom(String name){
        Point myPoint = null;
        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = board[i].length - 1; j >= 0; j--) {
                if(board[i][j].getRoomName().equals(name)){
                    myPoint = new Point(i, j);
                }
            }
        }
        return myPoint;
    }
    
    
}